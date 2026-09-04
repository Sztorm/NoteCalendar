package com.sztorm.notecalendar.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sztorm.notecalendar.AppNotificationManager
import com.sztorm.notecalendar.AppPermission
import com.sztorm.notecalendar.AppPermissionManager
import com.sztorm.notecalendar.ILogger
import com.sztorm.notecalendar.NoteData
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.ReminderNote
import com.sztorm.notecalendar.components.DayNote
import com.sztorm.notecalendar.components.InfiniteHorizontalPager
import com.sztorm.notecalendar.components.TimePickerDialog
import com.sztorm.notecalendar.components.TimedContent
import com.sztorm.notecalendar.getLocalizedGenitiveCaseName
import com.sztorm.notecalendar.getLocalizedName
import com.sztorm.notecalendar.preferences.ThemeColors
import com.sztorm.notecalendar.remainingDurationFromNow
import com.sztorm.notecalendar.repositories.NoteRepository
import com.sztorm.notecalendar.viewmodels.DayScreenEvent
import com.sztorm.notecalendar.viewmodels.DayScreenNote
import com.sztorm.notecalendar.viewmodels.DayScreenState
import com.sztorm.notecalendar.viewmodels.DayScreenViewModel
import com.sztorm.notecalendar.viewmodels.DayScreenViewModelFactory
import com.sztorm.notecalendar.viewmodels.MainEvent
import com.sztorm.notecalendar.viewmodels.MainViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import kotlin.time.Duration as KotlinDuration

enum class DayActionType {
    None,
    EditingNote,
    AddingNote,
    EditingReminder,
}

@Composable
fun DayScreen(
    logger: ILogger,
    mainViewModel: MainViewModel,
    permissionManager: AppPermissionManager,
    notificationManager: AppNotificationManager,
    noteRepository: NoteRepository,
    isCreateOrEditRequested: Boolean = false
) {
    val currentDate = remember { mainViewModel.state.dayScreenDate }
    val initialDayNote = noteRepository
        .getBy(currentDate)
        ?.let { noteData ->
            DayScreenNote(
                date = currentDate,
                textValue = TextFieldValue(text = noteData.text),
                reminderDateTime = noteData.reminderDateTime
                    .ifEmpty { null }
                    ?.let { OffsetDateTime.parse(it) }
            )
        }
    val viewModel = viewModel<DayScreenViewModel>(
        factory = DayScreenViewModelFactory(
            initialState = DayScreenState(
                note = initialDayNote,
                prevNote = noteRepository
                    .getBy(currentDate.minusDays(1))
                    ?.let { noteData ->
                        DayScreenNote(
                            date = currentDate.minusDays(1),
                            textValue = TextFieldValue(text = noteData.text),
                            reminderDateTime = noteData.reminderDateTime
                                .ifEmpty { null }
                                ?.let { OffsetDateTime.parse(it) }
                        )
                    },
                nextNote = noteRepository
                    .getBy(currentDate.plusDays(1))
                    ?.let { noteData ->
                        DayScreenNote(
                            date = currentDate.plusDays(1),
                            textValue = TextFieldValue(text = noteData.text),
                            reminderDateTime = noteData.reminderDateTime
                                .ifEmpty { null }
                                ?.let { OffsetDateTime.parse(it) }
                        )
                    },
                actionType = DayActionType.None,
                noteBackup = null,
                isReminderDialogOpen = false,
                remainingReminderTime = initialDayNote?.reminderDateTime?.remainingDurationFromNow(),
                haveReminderPermissionsBeenDenied = false
            )
        )
    )
    val focusRequester = remember { FocusRequester() }

    Box(modifier = Modifier.fillMaxSize()) {
        InfiniteHorizontalPager(
            verticalAlignment = Alignment.Top,
            key = { currentDate.plusDays(it.toLong()) },
            onPageChange = { page ->
                val date = currentDate.plusDays(page.toLong())
                val currentNoteData = noteRepository.getBy(date)
                mainViewModel.onEvent(MainEvent.DayScreenDateChange(date))
                viewModel.onEvent(
                    DayScreenEvent.DateChange(
                        note = currentNoteData?.let { noteData ->
                            DayScreenNote(
                                date = date,
                                textValue = TextFieldValue(text = noteData.text),
                                reminderDateTime = noteData.reminderDateTime
                                    .ifEmpty { null }
                                    ?.let { OffsetDateTime.parse(it) }
                            )
                        },
                        prevNote = noteRepository
                            .getBy(date.minusDays(1))?.let { noteData ->
                                DayScreenNote(
                                    date = date.minusDays(1),
                                    textValue = TextFieldValue(text = noteData.text),
                                    reminderDateTime = noteData.reminderDateTime
                                        .ifEmpty { null }
                                        ?.let { OffsetDateTime.parse(it) }
                                )
                            },
                        nextNote = noteRepository
                            .getBy(date.plusDays(1))?.let { noteData ->
                                DayScreenNote(
                                    date = date.plusDays(1),
                                    textValue = TextFieldValue(text = noteData.text),
                                    reminderDateTime = noteData.reminderDateTime
                                        .ifEmpty { null }
                                        ?.let { OffsetDateTime.parse(it) }
                                )
                            },
                    )
                )
                viewModel.onEvent(
                    DayScreenEvent.ActionTypeChange(DayActionType.None)
                )
            }
        ) {
            val date = currentDate.plusDays(it.toLong())

            DayPageLayout(
                logger = logger,
                modifier = Modifier.fillMaxSize(),
                mainViewModel = mainViewModel,
                viewModel = viewModel,
                focusRequester = focusRequester,
                date = date
            )
        }
        ActionButtons(
            logger = logger,
            mainViewModel = mainViewModel,
            viewModel = viewModel,
            noteRepository = noteRepository,
            permissionManager = permissionManager,
            notificationManager = notificationManager,
            focusRequester = focusRequester
        )
        LaunchedEffect(Unit) {
            if (isCreateOrEditRequested) {
                val note = viewModel.state.note

                if (note == null) {
                    viewModel.onEvent(
                        DayScreenEvent.ActionTypeChange(DayActionType.AddingNote)
                    )
                    viewModel.onEvent(
                        DayScreenEvent.NoteChange(
                            DayScreenNote(
                                date = mainViewModel.state.dayScreenDate,
                                textValue = TextFieldValue()
                            )
                        )
                    )
                } else {
                    viewModel.onEvent(
                        DayScreenEvent.ActionTypeChange(DayActionType.EditingNote)
                    )
                    viewModel.onEvent(
                        DayScreenEvent.NoteChange(
                            DayScreenNote(
                                date = note.date,
                                textValue = note.textValue.copy(
                                    selection = TextRange(note.textValue.text.length)
                                ),
                                reminderDateTime = note.reminderDateTime
                            )
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.DayDateText(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    date: LocalDate
) {
    val themeColors = viewModel.state.themeColors

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
            .weight(0.6f)
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
        )
        BasicText(
            text = date.dayOfMonth.toString(),
            style = LocalTextStyle.current.copy(
                color = themeColors.textColor,
                textAlign = TextAlign.Center,
                fontSize = 140.sp
            ),
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .weight(4f)
        )
        BasicText(
            text = date.month.getLocalizedGenitiveCaseName(),
            style = LocalTextStyle.current.copy(
                color = themeColors.textColor,
                textAlign = TextAlign.Center,
                fontSize = 36.sp
            ),
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.5f)
        )
        BasicText(
            text = date.dayOfWeek.getLocalizedName(),
            style = LocalTextStyle.current.copy(
                color = themeColors.textColor,
                textAlign = TextAlign.Center,
                fontSize = 24.sp
            ),
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

@Composable
private fun ActionButton(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    onClick: () -> Unit,
    themeColors: ThemeColors,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn() + expandIn(),
        exit = shrinkOut() + scaleOut()
    ) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = themeColors.primaryColor,
            contentColor = themeColors.buttonTextColor,
            modifier = modifier.padding(8.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun ActionButton(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    onClick: () -> Unit,
    themeColors: ThemeColors,
    icon: Painter,
    contentDescription: String,
) = ActionButton(
    modifier = modifier,
    visible = visible,
    onClick = onClick,
    themeColors = themeColors,
    content = {
        Icon(painter = icon, contentDescription = contentDescription)
    }
)

private fun showNotificationPermissionDeniedToast(context: Context) = Toast
    .makeText(
        context,
        context.getString(R.string.Message_NotificationPermissionDenied),
        Toast.LENGTH_SHORT
    ).show()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BoxScope.ActionButtons(
    @Suppress("unused") logger: ILogger,
    mainViewModel: MainViewModel,
    viewModel: DayScreenViewModel,
    noteRepository: NoteRepository,
    permissionManager: AppPermissionManager,
    notificationManager: AppNotificationManager,
    focusRequester: FocusRequester
) {
    val themeColors = mainViewModel.state.themeColors
    val dialogColors = CardDefaults.cardColors().copy(
        containerColor = themeColors.backgroundColor,
        contentColor = themeColors.backgroundColor,
    )
    val timePickerColors = TimePickerDefaults.colors().copy(
        clockDialColor = themeColors.backgroundColorVariant,
        selectorColor = themeColors.primaryColor,
        containerColor = themeColors.textColor,
        periodSelectorBorderColor = themeColors.textColor,
        clockDialSelectedContentColor = themeColors.buttonTextColor,
        clockDialUnselectedContentColor = themeColors.textColor,
        periodSelectorSelectedContainerColor = themeColors.primaryColor,
        periodSelectorUnselectedContainerColor = themeColors.backgroundColorVariant,
        periodSelectorSelectedContentColor = themeColors.buttonTextColor,
        periodSelectorUnselectedContentColor = themeColors.textColor,
        timeSelectorSelectedContainerColor = themeColors.primaryColor,
        timeSelectorUnselectedContainerColor = themeColors.backgroundColorVariant,
        timeSelectorSelectedContentColor = themeColors.buttonTextColor,
        timeSelectorUnselectedContentColor = themeColors.textColor,
    )
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val deleteReminder = {
        val note = viewModel.state.note
        val noteData = note?.let { noteRepository.getBy(it.date) }

        if (note != null && noteData != null) {
            noteRepository.update(noteData.copy(reminderDateTime = ""))
            viewModel.onEvent(
                DayScreenEvent.NoteChange(
                    note.copy(reminderDateTime = null)
                )
            )
        }
    }
    val onReminderDialogConfirm = { localTime: LocalTime ->
        val offsetNow = OffsetDateTime.now()
        val localNow = LocalDateTime.now()
        val reminderDateTime = OffsetDateTime.of(
            if (localTime > localNow.toLocalTime()) localNow.toLocalDate()
            else localNow.toLocalDate().plusDays(1),
            localTime,
            offsetNow.offset
        )
        val note = viewModel.state.note

        if (note != null) {
            val noteData = noteRepository.getBy(note.date)

            if (noteData != null) {
                notificationManager.tryScheduleNotification(
                    note = ReminderNote(note.date, note.textValue.text, reminderDateTime),
                    permissionManager = permissionManager,
                    coroutineScope = coroutineScope
                ) { isSuccess ->
                    if (isSuccess) {
                        viewModel.onEvent(
                            DayScreenEvent.ReminderPermissionsChange(false)
                        )
                        noteRepository
                            .update(noteData.copy(reminderDateTime = reminderDateTime.toString()))
                        viewModel.onEvent(
                            DayScreenEvent.NoteChange(
                                note.copy(reminderDateTime = reminderDateTime)
                            )
                        )
                    } else {
                        if (viewModel.state.haveReminderPermissionsBeenDenied) {
                            permissionManager.requestNotifiactionsSettingsPermission { isGranted ->
                                if (isGranted) {
                                    notificationManager.tryScheduleNotification(
                                        note = ReminderNote(
                                            note.date,
                                            note.textValue.text,
                                            reminderDateTime
                                        ),
                                        permissionManager = permissionManager,
                                        coroutineScope = coroutineScope,
                                        onCompletion = {}
                                    )
                                    viewModel.onEvent(
                                        DayScreenEvent
                                            .ReminderPermissionsChange(false)
                                    )
                                    noteRepository.update(
                                        noteData.copy(
                                            reminderDateTime = reminderDateTime.toString()
                                        )
                                    )
                                    viewModel.onEvent(
                                        DayScreenEvent.NoteChange(
                                            note.copy(reminderDateTime = reminderDateTime)
                                        )
                                    )
                                } else {
                                    showNotificationPermissionDeniedToast(context)
                                }
                            }
                        } else {
                            showNotificationPermissionDeniedToast(context)
                            viewModel.onEvent(
                                DayScreenEvent.ReminderPermissionsChange(true)
                            )
                        }
                    }
                }
            }
        }
        viewModel.onEvent(DayScreenEvent.ReminderDialogStateChange(false))
        viewModel.onEvent(
            DayScreenEvent.ActionTypeChange(DayActionType.None)
        )
    }
    val onReminderDialogDismiss = { _: LocalTime ->
        viewModel.onEvent(DayScreenEvent.ReminderDialogStateChange(false))
    }
    val onAddOrEditReminderClick = {
        val note = viewModel.state.note

        if (note != null) {
            if (note.reminderDateTime == null) {
                viewModel.onEvent(DayScreenEvent.ReminderDialogStateChange(true))
            } else {
                viewModel.onEvent(
                    DayScreenEvent.ActionTypeChange(
                        DayActionType.EditingReminder
                    )
                )
            }
        }
    }
    val onDeleteReminderClick = {
        deleteReminder()
        val note = viewModel.state.note

        if (note != null) {
            notificationManager.cancelScheduledNotification(note.date)
        }
        viewModel.onEvent(
            DayScreenEvent.ActionTypeChange(DayActionType.None)
        )
    }
    val onCancelReminderEditClick = {
        viewModel.onEvent(
            DayScreenEvent.ActionTypeChange(DayActionType.None)
        )
    }
    val onEditReminderDialogClick = {
        viewModel.onEvent(
            DayScreenEvent.ReminderDialogStateChange(true)
        )
    }
    val onUndoNoteDeletionClick = {
        val note = viewModel.state.noteBackup

        if (note != null) {
            val noteData = noteRepository.getBy(note.date)

            if (noteData == null) {
                noteRepository.add(
                    NoteData(
                        date = mainViewModel.state.dayScreenDate.toString(),
                        text = note.textValue.text
                    )
                )
            } else {
                noteRepository.update(noteData.copy(text = note.textValue.text))
            }
            note.toReminderNoteOrNull()?.let {
                notificationManager.tryScheduleNotification(
                    note = it,
                    permissionManager = permissionManager,
                    coroutineScope = coroutineScope,
                    onCompletion = {}
                )
            }
        }
        viewModel.onEvent(
            DayScreenEvent.NoteChange(note)
        )
    }
    val onAddOrEditNoteClick = {
        val note = viewModel.state.note

        if (note == null) {
            viewModel.onEvent(
                DayScreenEvent.ActionTypeChange(DayActionType.AddingNote)
            )
            viewModel.onEvent(
                DayScreenEvent.NoteChange(
                    DayScreenNote(
                        date = mainViewModel.state.dayScreenDate,
                        textValue = TextFieldValue()
                    )
                )
            )
        } else {
            viewModel.onEvent(
                DayScreenEvent.ActionTypeChange(DayActionType.EditingNote)
            )
            viewModel.onEvent(
                DayScreenEvent.NoteChange(
                    DayScreenNote(
                        date = note.date,
                        textValue = note.textValue.copy(
                            selection = TextRange(note.textValue.text.length)
                        ),
                        reminderDateTime = note.reminderDateTime
                    )
                )
            )
        }
    }
    val onDeleteNoteClick = {
        val note = viewModel.state.note
        val noteData = note?.let { noteRepository.getBy(it.date) }

        if (noteData != null) {
            noteRepository.delete(noteData)
            notificationManager.cancelScheduledNotification(note.date)
        }
        viewModel.onEvent(DayScreenEvent.NoteChange(null))
        viewModel.onEvent(
            DayScreenEvent.ActionTypeChange(DayActionType.None)
        )
        focusRequester.freeFocus()
        keyboardController?.hide()
        focusManager.clearFocus()
    }
    val onCancelNoteEditClick = {
        val note = viewModel.state.note
        val noteMode = viewModel.state.actionType

        if (noteMode == DayActionType.EditingNote && note != null) {
            val noteData = noteRepository.getBy(note.date)

            if (noteData != null) {
                viewModel.onEvent(
                    DayScreenEvent.NoteChange(
                        DayScreenNote(
                            date = note.date,
                            textValue = TextFieldValue(noteData.text),
                            reminderDateTime = note.reminderDateTime
                        )
                    )
                )
            }
        } else if (noteMode == DayActionType.AddingNote) {
            viewModel.onEvent(DayScreenEvent.NoteChange(null))
        }
        viewModel.onEvent(
            DayScreenEvent.ActionTypeChange(DayActionType.None)
        )
        focusRequester.freeFocus()
        keyboardController?.hide()
        focusManager.clearFocus()
    }
    val onAcceptNoteEditClick = {
        val note = viewModel.state.note

        if (note != null) {
            val noteData = note.let { noteRepository.getBy(it.date) }

            if (noteData == null) {
                noteRepository.add(
                    NoteData(
                        date = mainViewModel.state.dayScreenDate.toString(),
                        text = note.textValue.text
                    )
                )
            } else {
                noteRepository.update(noteData.copy(text = note.textValue.text))
                note.toReminderNoteOrNull()?.let {
                    notificationManager.tryScheduleNotification(
                        note = it,
                        permissionManager = permissionManager,
                        coroutineScope = coroutineScope,
                        onCompletion = {}
                    )
                }
            }
            viewModel.onEvent(
                DayScreenEvent.NoteChange(
                    DayScreenNote(note.date, note.textValue, note.reminderDateTime)
                )
            )
            viewModel.onEvent(
                DayScreenEvent.ActionTypeChange(DayActionType.None)
            )
        }
        focusRequester.freeFocus()
        keyboardController?.hide()
        focusManager.clearFocus()
    }
    TimePickerDialog(
        title = stringResource(R.string.SetReminder),
        initialTime = viewModel.state.note?.reminderDateTime?.toLocalTime()
            ?: OffsetDateTime.now().toLocalTime(),
        isOpen = viewModel.state.isReminderDialogOpen,
        titleColor = themeColors.textColor,
        buttonColor = themeColors.primaryColor,
        dialogColors = dialogColors,
        timePickerColors = timePickerColors,
        onConfirm = onReminderDialogConfirm,
        onDismiss = onReminderDialogDismiss
    )
    Row(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(8.dp)
    ) {
        val reminderDateTime = viewModel.state.note?.reminderDateTime

        if (reminderDateTime != null) {
            if (reminderDateTime > OffsetDateTime.now()) {
                TimedContent(reminderDateTime) { remainingTime ->
                    viewModel.onEvent(
                        DayScreenEvent.ReminderRemainingTimeChange(remainingTime)
                    )
                    if (remainingTime == KotlinDuration.ZERO ||
                        !permissionManager.isGranted(AppPermission.PostNotifications)
                    ) {
                        deleteReminder()

                        if (viewModel.state.actionType == DayActionType.EditingReminder) {
                            viewModel.onEvent(
                                DayScreenEvent.ActionTypeChange(
                                    DayActionType.None
                                )
                            )
                        }
                        viewModel.onEvent(
                            DayScreenEvent.ReminderRemainingTimeChange(null)
                        )
                    }
                }
            } else {
                deleteReminder()
            }
        }
        ActionButton(
            onClick = onDeleteReminderClick,
            visible = viewModel.state.actionType == DayActionType.EditingReminder &&
                viewModel.state.note?.reminderDateTime != null,
            themeColors = themeColors,
            icon = painterResource(R.drawable.icon_outline_rounded_delete_forever),
            contentDescription = stringResource(R.string.DeleteReminder)
        )
        ActionButton(
            onClick = onCancelReminderEditClick,
            visible = viewModel.state.actionType == DayActionType.EditingReminder &&
                viewModel.state.note?.reminderDateTime != null,
            themeColors = themeColors,
            icon = painterResource(R.drawable.icon_outline_rounded_notifications_edit_off),
            contentDescription = stringResource(R.string.Cancel)
        )
        ActionButton(
            onClick = onEditReminderDialogClick,
            visible = viewModel.state.actionType == DayActionType.EditingReminder &&
                viewModel.state.note?.reminderDateTime != null,
            themeColors = themeColors,
            icon = painterResource(R.drawable.icon_outline_rounded_edit_notifications),
            contentDescription = stringResource(R.string.OpenReminderDialog)
        )
        ActionButton(
            onClick = onAddOrEditReminderClick,
            visible = viewModel.state.actionType == DayActionType.None &&
                viewModel.state.note != null,
            themeColors = themeColors,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(
                        if (reminderDateTime == null) R.drawable.icon_outline_rounded_notification_add
                        else R.drawable.icon_outline_rounded_notifications
                    ),
                    contentDescription =
                        if (reminderDateTime == null) stringResource(R.string.AddReminder)
                        else stringResource(R.string.EditReminder)
                )
                val remainingTime = viewModel.state.remainingReminderTime

                if (viewModel.state.note?.reminderDateTime != null && remainingTime != null) {
                    Text(text = remainingTime.toComponents { hours, minutes, seconds, _ ->
                        "%02d:%02d:%02d"
                            .format(Locale.current.platformLocale, hours, minutes, seconds)
                    })
                }
            }
        }
        ActionButton(
            onClick = onUndoNoteDeletionClick,
            visible = viewModel.state.actionType == DayActionType.None &&
                viewModel.state.note == null &&
                viewModel.state.noteBackup != null,
            themeColors = themeColors,
            icon = painterResource(R.drawable.icon_rounded_undo),
            contentDescription = stringResource(R.string.UndoDeletion)
        )
        ActionButton(
            onClick = onAddOrEditNoteClick,
            visible = viewModel.state.actionType == DayActionType.None,
            themeColors = themeColors,
            icon = painterResource(
                if (viewModel.state.note == null) R.drawable.icon_rounded_plus
                else R.drawable.icon_outline_rounded_edit
            ),
            contentDescription =
                if (viewModel.state.note == null) stringResource(R.string.AddNote)
                else stringResource(R.string.EditNote)
        )
        ActionButton(
            onClick = onDeleteNoteClick,
            visible = viewModel.state.actionType == DayActionType.EditingNote,
            themeColors = themeColors,
            icon = painterResource(R.drawable.icon_outline_rounded_delete),
            contentDescription = stringResource(R.string.DeleteNote)
        )
        ActionButton(
            onClick = onCancelNoteEditClick,
            visible = viewModel.state.actionType == DayActionType.EditingNote ||
                viewModel.state.actionType == DayActionType.AddingNote,
            themeColors = themeColors,
            icon = painterResource(R.drawable.icon_outline_rounded_edit_off),
            contentDescription = stringResource(R.string.Cancel)
        )
        ActionButton(
            onClick = onAcceptNoteEditClick,
            visible = viewModel.state.actionType == DayActionType.EditingNote ||
                viewModel.state.actionType == DayActionType.AddingNote,
            themeColors = themeColors,
            icon = painterResource(R.drawable.icon_rounded_check),
            contentDescription = stringResource(R.string.Accept)
        )
    }
}

@Composable
fun DayPageLayout(
    @Suppress("unused") logger: ILogger,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    viewModel: DayScreenViewModel,
    focusRequester: FocusRequester,
    date: LocalDate
) {
    val themeColors = mainViewModel.state.themeColors
    val dayScreenDate = mainViewModel.state.dayScreenDate
    val note = when {
        date > dayScreenDate -> viewModel.state.nextNote
        date < dayScreenDate -> viewModel.state.prevNote
        else -> viewModel.state.note
    }
    val actionType =
        if (date == dayScreenDate) viewModel.state.actionType
        else DayActionType.None
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = modifier) {
        DayDateText(
            viewModel = mainViewModel,
            date = date
        )
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            AnimatedVisibility(
                visible = note != null ||
                    actionType == DayActionType.EditingNote ||
                    actionType == DayActionType.AddingNote,
                enter = scaleIn() + expandIn(),
                exit = shrinkOut() + scaleOut()
            ) {
                DayNote(
                    color = themeColors.noteColor,
                    bendTint = themeColors.noteColorVariant,
                    bendWidth = with(LocalDensity.current) { 32.dp.toPx() },
                    bendShadowWidth = with(LocalDensity.current) { 1.dp.toPx() },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(8.dp)
                    ) {
                        if (note != null) {
                            BasicTextField(
                                value = note.textValue,
                                onValueChange = {
                                    val actionType = viewModel.state.actionType

                                    if (actionType == DayActionType.AddingNote ||
                                        actionType == DayActionType.EditingNote
                                    ) {
                                        viewModel.onEvent(
                                            DayScreenEvent.NoteChange(
                                                DayScreenNote(
                                                    date = date,
                                                    textValue = it,
                                                    reminderDateTime = note.reminderDateTime
                                                )
                                            )
                                        )
                                    }
                                },
                                readOnly = actionType == DayActionType.None,
                                minLines = 1,
                                maxLines = Int.MAX_VALUE,
                                textStyle = TextStyle(
                                    color = themeColors.noteTextColor,
                                    fontSize = mainViewModel.state.noteFontSize.value,
                                    lineHeight = mainViewModel.state.noteFontSize.value * 1.33f,
                                ),
                                cursorBrush = SolidColor(themeColors.secondaryColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(end = 24.dp)
                                    .then(
                                        when (date == dayScreenDate) {
                                            true -> Modifier.focusRequester(focusRequester)
                                            false -> Modifier
                                        }
                                    )
                            )
                        }
                        Spacer(modifier = Modifier.height(72.dp))
                        LaunchedEffect(actionType) {
                            when (actionType) {
                                DayActionType.EditingNote, DayActionType.AddingNote -> {
                                    focusRequester.requestFocus()
                                    keyboardController?.show()
                                }

                                else -> {
                                    focusRequester.freeFocus()
                                    keyboardController?.hide()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}