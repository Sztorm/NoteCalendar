package com.sztorm.notecalendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.sztorm.notecalendar.components.AnimatedNoteVisibility
import com.sztorm.notecalendar.components.ThemedButton
import com.sztorm.notecalendar.components.ThemedIconButton
import com.sztorm.notecalendar.components.ThemedNote
import com.sztorm.notecalendar.databinding.FragmentDayNoteBinding
import com.sztorm.notecalendar.repositories.NoteRepository
import timber.log.Timber
import java.time.LocalDate

enum class DayNoteState {
    Empty,
    Reading,
    Editing,
    Adding
}

class DayNoteFragment() : Fragment() {
    private lateinit var binding: FragmentDayNoteBinding
    private lateinit var dayFragment: DayFragment
    private var args: Arguments? = null

    constructor(dayFragment: DayFragment, args: Arguments? = null) : this() {
        this.dayFragment = dayFragment
        this.args = args
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDayNoteBinding.inflate(inflater, container, false)
        binding.composeView.setContent {
            MaterialTheme {
                DayLayout(dayFragment, activity as MainActivity, args)
            }
        }
        return binding.root
    }
}

fun getInitialNoteState(isNotePresent: Boolean, isCreateOrEditRequested: Boolean) =
    when (Pair(isNotePresent, isCreateOrEditRequested)) {
        Pair(true, true) -> DayNoteState.Editing
        Pair(true, false) -> DayNoteState.Reading
        Pair(false, true) -> DayNoteState.Adding
        else -> DayNoteState.Empty
    }

@Composable
fun DayLayout(
    dayFragment: DayFragment,
    mainActivity: MainActivity,
    args: Arguments? = null
) {
    val noteState = remember {
        mutableStateOf(
            getInitialNoteState(dayFragment.note != null, args is CreateOrEditNoteRequest)
        )
    }
    AnimatedNoteVisibility(visible = noteState.value == DayNoteState.Adding) {
        DayNoteAddLayout(
            dayFragment,
            mainActivity,
            noteState
        )
    }
    AnimatedVisibility(
        visible = noteState.value == DayNoteState.Empty,
        enter = slideInHorizontally(
            animationSpec = tween(durationMillis = 500),
            initialOffsetX = { -it }
        ),
        exit = fadeOut(
            animationSpec = tween(durationMillis = 400)
        ) + slideOutVertically(
            animationSpec = tween(durationMillis = 500),
            targetOffsetY = { (it * 0.1f).toInt() }
        )
    ) {
        DayNoteEmptyLayout(dayFragment, mainActivity, noteState)
    }
    AnimatedNoteVisibility(
        visible = noteState.value == DayNoteState.Reading ||
            noteState.value == DayNoteState.Editing
    ) {
        DayNoteLayout(
            dayFragment,
            mainActivity,
            noteState
        )
    }
}

@Composable
fun DayNoteEmptyLayout(
    dayFragment: DayFragment,
    mainActivity: MainActivity,
    noteState: MutableState<DayNoteState>
) {
    val themeValues = mainActivity.themePainter.values

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        ThemedButton(
            onClick = { noteState.value = DayNoteState.Adding },
            themeValues = themeValues,
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 0.dp),
            text = stringResource(R.string.AddNote),
            icon = painterResource(R.drawable.icon_note_add)
        )
        //if (args is UndoNoteDeleteOption) {
        val undoNote = dayFragment.undoNote

        if (undoNote != null) {
            ThemedButton(
                onClick = {
                    //val note = args.note
                    val note = undoNote

                    NoteRepository.add(note)

                    if (mainActivity.notificationManager.tryScheduleNotification(
                            ScheduleNoteNotificationArguments(note = note)
                        )
                    ) {
                        Timber.i("${LogTags.NOTIFICATIONS} Scheduled notification after note save")
                    }
                    dayFragment.note = note
                    noteState.value = DayNoteState.Reading
                    //dayFragment.setFragment(DayNoteFragment(dayFragment))
                },
                themeValues = themeValues,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 0.dp),
                text = stringResource(R.string.UndoDeletion),
                icon = painterResource(R.drawable.icon_undo)
            )
        }
    }
}

@Composable
fun DayNoteAddLayout(
    dayFragment: DayFragment,
    mainActivity: MainActivity,
    noteState: MutableState<DayNoteState>,
    modifier: Modifier = Modifier
) {
    val themeValues = mainActivity.themePainter.values
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var noteTextFieldState by remember { mutableStateOf(TextFieldState()) }

    ThemedNote(
        themeValues = themeValues,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
        ) {
            ThemedButton(
                onClick = {
                    keyboardController?.hide()
                    val noteData = NoteData(
                        date = mainActivity.sharedData.viewedDate.toString(),
                        text = noteTextFieldState.text.toString()
                    )
                    NoteRepository.add(noteData)
                    if (mainActivity.notificationManager.tryScheduleNotification(
                            ScheduleNoteNotificationArguments(note = noteData)
                        )
                    ) {
                        Timber.i("${LogTags.NOTIFICATIONS} Scheduled notification after note save")
                    }
                    dayFragment.note = noteData
                    noteState.value = DayNoteState.Reading
                    //dayFragment.setFragment(DayNoteFragment(dayFragment))
                },
                themeValues = themeValues,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(start = 10.dp, top = 10.dp, end = 0.dp, bottom = 10.dp),
                text = stringResource(R.string.Save),
                icon = painterResource(R.drawable.icon_add)
            )
            ThemedButton(
                onClick = {
                    keyboardController?.hide()
                    noteTextFieldState = TextFieldState()
                    noteState.value = DayNoteState.Empty
                    //dayFragment.setFragment(DayNoteEmptyFragment(dayFragment))
                },
                themeValues = themeValues,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(start = 5.dp, top = 10.dp, end = 0.dp, bottom = 10.dp),
                text = stringResource(R.string.Cancel),
                icon = painterResource(R.drawable.icon_cancel)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp, top = 0.dp, end = 10.dp, bottom = 10.dp)
        ) {
            BasicTextField(
                state = noteTextFieldState,
                lineLimits = TextFieldLineLimits.MultiLine(),
                textStyle = TextStyle(
                    color = Color(themeValues.noteTextColor),
                    fontSize = 24.sp,
                    lineHeight = 26.sp,
                ),
                cursorBrush = SolidColor(Color(themeValues.secondaryColor)),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .then(if (noteState.value == DayNoteState.Empty) Modifier.focusProperties {
                        canFocus = false
                    } else Modifier)
                    .focusRequester(focusRequester)
            )
        }
    }
    if (noteState.value == DayNoteState.Adding) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }
}

@Composable
fun DayNoteLayout(
    dayFragment: DayFragment,
    mainActivity: MainActivity,
    noteState: MutableState<DayNoteState>
) {
    val themeValues = mainActivity.themePainter.values
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var noteTextFieldState by remember {
        mutableStateOf(TextFieldState(dayFragment.note?.text ?: ""))
    }
    var isInEditableState by remember { mutableStateOf(noteState.value == DayNoteState.Editing) }

    ThemedNote(
        themeValues = themeValues,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
        ) {
            if (isInEditableState) {
                ThemedButton(
                    onClick = {
                        isInEditableState = false
                        val editedNote: NoteData? = NoteRepository
                            .getByDate(mainActivity.sharedData.viewedDate)
                        if (editedNote != null) {
                            editedNote.date = mainActivity.sharedData.viewedDate.toString()
                            editedNote.text = noteTextFieldState.text.toString()
                            editedNote.save()
                            dayFragment.note = editedNote

                            if (mainActivity.notificationManager.tryScheduleNotification(
                                    ScheduleNoteNotificationArguments(note = editedNote)
                                )
                            ) {
                                Timber.i("${LogTags.NOTIFICATIONS} Scheduled notification after note edit")
                            }
                        }
                        keyboardController?.hide()
                    },
                    themeValues = themeValues,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(start = 10.dp, top = 10.dp, end = 0.dp, bottom = 10.dp),
                    text = stringResource(R.string.Save),
                    icon = painterResource(R.drawable.icon_add)
                )
                ThemedButton(
                    onClick = {
                        isInEditableState = false
                        noteTextFieldState = TextFieldState(dayFragment.note?.text ?: "")
                        keyboardController?.hide()
                    },
                    themeValues = themeValues,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(start = 5.dp, top = 10.dp, end = 0.dp, bottom = 10.dp),
                    text = stringResource(R.string.Cancel),
                    icon = painterResource(R.drawable.icon_cancel)
                )
            } else {
                ThemedIconButton(
                    onClick = {
                        isInEditableState = true
                    },
                    themeValues = themeValues,
                    icon = painterResource(R.drawable.icon_edit),
                    contentDescription = stringResource(R.string.EditNote),
                    modifier = Modifier
                        .padding(start = 10.dp, top = 10.dp, end = 5.dp, bottom = 0.dp)
                        .size(48.dp)
                )
                ThemedIconButton(
                    onClick = {
                        val note = dayFragment.note

                        if (note == null) {
                            noteState.value = DayNoteState.Empty
                            //dayFragment.setFragment(DayNoteEmptyFragment(dayFragment))
                        } else {
                            NoteRepository.delete(note)
                            if (mainActivity.notificationManager
                                    .tryCancelScheduledNotification(LocalDate.parse(note.date))
                            ) {
                                Timber.i("${LogTags.NOTIFICATIONS} Canceled notification after note deletion")
                            }
                            //dayFragment.setFragment(
                            //    DayNoteEmptyFragment(
                            //        dayFragment,
                            //        UndoNoteDeleteOption(note)
                            //    )
                            //)
                            dayFragment.undoNote = note
                            noteState.value = DayNoteState.Empty
                        }
                    },
                    themeValues = themeValues,
                    icon = painterResource(R.drawable.icon_delete),
                    contentDescription = stringResource(R.string.DeleteNote),
                    modifier = Modifier
                        .padding(start = 5.dp, top = 10.dp, end = 5.dp, bottom = 0.dp)
                        .size(48.dp)
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp, top = 0.dp, end = 10.dp, bottom = 10.dp)
        ) {
            BasicTextField(
                state = noteTextFieldState,
                readOnly = !isInEditableState,
                lineLimits = TextFieldLineLimits.MultiLine(),
                textStyle = TextStyle(
                    color = Color(themeValues.noteTextColor),
                    fontSize = 24.sp,
                    lineHeight = 26.sp,
                ),
                cursorBrush = SolidColor(Color(themeValues.secondaryColor)),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .focusRequester(focusRequester)
            )
        }
    }
    LaunchedEffect(isInEditableState) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }
}