package com.sztorm.notecalendar

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.sztorm.notecalendar.components.ThemedButton
import com.sztorm.notecalendar.components.ThemedIconButton
import com.sztorm.notecalendar.components.ThemedNote
import com.sztorm.notecalendar.databinding.FragmentDayBinding
import com.sztorm.notecalendar.helpers.DateHelper.Companion.toLocalizedString
import com.sztorm.notecalendar.helpers.DateHelper.Companion.toLocalizedStringGenitiveCase
import com.sztorm.notecalendar.repositories.NoteRepository
import timber.log.Timber
import java.time.LocalDate

enum class DayNoteState {
    Empty,
    Reading,
    Editing,
    Adding
}

enum class DayNoteTransitionState {
    Empty,
    Reading,
    Adding
}

class DayFragment : Fragment {
    private lateinit var binding: FragmentDayBinding
    private lateinit var swipeListener: SwipeListener
    private val initArgs: Arguments?
    var note: NoteData? = null
    var undoNote: NoteData? = null

    constructor() : super() {
        initArgs = null
    }

    constructor(args: Arguments?) : super() {
        initArgs = args
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val mainActivity = activity as MainActivity
        note = NoteRepository.getByDate(mainActivity.sharedData.viewedDate)
        binding = FragmentDayBinding.inflate(inflater, container, false)
        binding.composeView.setContent {
            MaterialTheme {
                DayLayout(this, mainActivity, initArgs)
            }
        }
        swipeListener = SwipeListener(mainActivity, binding.root.context)
        @SuppressLint("ClickableViewAccessibility")
        binding.root.setOnTouchListener(swipeListener)

        return binding.root
    }

    class SwipeListener(
        private val mainActivity: MainActivity, context: Context
    ) : OnSwipeListener(context) {
        override fun onSwipeLeft() {
            super.onSwipeLeft()
            val sharedData = mainActivity.sharedData
            sharedData.viewedDate = sharedData.viewedDate.plusDays(1)
            mainActivity.setMainFragment(
                MainFragmentType.DAY, R.anim.anim_in_left, R.anim.anim_out_left
            )
        }

        override fun onSwipeRight() {
            super.onSwipeRight()
            val sharedData = mainActivity.sharedData
            sharedData.viewedDate = sharedData.viewedDate.minusDays(1)
            mainActivity.setMainFragment(
                MainFragmentType.DAY, R.anim.anim_in_right, R.anim.anim_out_right
            )
        }
    }
}

@Composable
fun DayLayout(
    dayFragment: DayFragment,
    mainActivity: MainActivity,
    args: Arguments? = null
) {
    val date: LocalDate = mainActivity.sharedData.viewedDate
    val themeValues: ThemeValues = mainActivity.themePainter.values
    val noteState = remember {
        mutableStateOf(
            when (Pair(dayFragment.note != null, args is CreateOrEditNoteRequest)) {
                Pair(true, true) -> DayNoteState.Editing
                Pair(true, false) -> DayNoteState.Reading
                Pair(false, true) -> DayNoteState.Adding
                else -> DayNoteState.Empty
            }
        )
    }
    val noteTransitionState = when (noteState.value) {
        DayNoteState.Empty -> DayNoteTransitionState.Empty
        DayNoteState.Adding -> DayNoteTransitionState.Adding
        else -> DayNoteTransitionState.Reading
    }
    val focusRequester = remember { FocusRequester() }
    Column {
        BasicText(
            text = date.dayOfMonth.toString(),
            style = LocalTextStyle.current.copy(
                color = Color(themeValues.textColor),
                textAlign = TextAlign.Center,
            ),
            autoSize = TextAutoSize.StepBased(maxFontSize = 300.sp, stepSize = 0.1.sp),
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.44f)
                .padding(5.dp)
        )
        BasicText(
            text = date.month.toLocalizedStringGenitiveCase(mainActivity),
            style = LocalTextStyle.current.copy(
                color = Color(themeValues.textColor),
                textAlign = TextAlign.Center,
            ),
            autoSize = TextAutoSize.StepBased(stepSize = 0.1.sp),
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.18f)
                .padding(5.dp)
        )
        BasicText(
            text = date.dayOfWeek.toLocalizedString(mainActivity),
            style = LocalTextStyle.current.copy(
                color = Color(themeValues.textColor),
                textAlign = TextAlign.Center,
            ),
            autoSize = TextAutoSize.StepBased(stepSize = 0.1.sp),
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.10f)
                .padding(5.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(10.dp)
        ) {
            val inTransition = updateTransition(
                targetState = noteTransitionState != DayNoteTransitionState.Empty
            )
            val inScaleY by inTransition.animateFloat(
                transitionSpec = { tween(durationMillis = 500) },
                targetValueByState = { expanded ->
                    if (expanded) 1f else 0f
                }
            )
            AnimatedContent(
                targetState = noteTransitionState,
                transitionSpec = {
                    when (targetState) {
                        DayNoteTransitionState.Empty -> {
                            slideInHorizontally(
                                animationSpec = tween(durationMillis = 500),
                                initialOffsetX = { -it }
                            ) togetherWith
                                fadeOut(animationSpec = tween(durationMillis = 400)) +
                                slideOutVertically(
                                    animationSpec = tween(durationMillis = 500),
                                    targetOffsetY = { (it * 0.1f).toInt() }
                                )
                        }

                        DayNoteTransitionState.Adding -> {
                            fadeIn(animationSpec = tween(durationMillis = 400)) togetherWith
                                ExitTransition.None
                        }

                        else -> {
                            EnterTransition.None togetherWith
                                fadeOut(animationSpec = tween(durationMillis = 400)) +
                                slideOutVertically(
                                    animationSpec = tween(durationMillis = 500),
                                    targetOffsetY = { it }
                                )
                        }
                    }
                },
                modifier = Modifier.graphicsLayer(scaleX = 1f)
            ) { targetState ->
                when (targetState) {
                    DayNoteTransitionState.Empty -> {
                        DayNoteEmptyLayout(
                            dayFragment,
                            mainActivity,
                            noteState
                        )
                    }

                    DayNoteTransitionState.Reading -> {
                        DayNoteLayout(
                            dayFragment,
                            mainActivity,
                            focusRequester,
                            noteState,
                            modifier = Modifier.graphicsLayer(scaleY = inScaleY)
                        )
                    }

                    DayNoteTransitionState.Adding -> {
                        DayNoteAddLayout(
                            dayFragment,
                            mainActivity,
                            focusRequester,
                            noteState,
                            modifier = Modifier.graphicsLayer(scaleY = inScaleY)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DayNoteEmptyLayout(
    dayFragment: DayFragment,
    mainActivity: MainActivity,
    noteState: MutableState<DayNoteState>,
    modifier: Modifier = Modifier
) {
    val themeValues = mainActivity.themePainter.values
    Row(
        modifier = modifier.fillMaxSize()
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
        val undoNote = dayFragment.undoNote

        if (undoNote != null) {
            ThemedButton(
                onClick = {
                    val note = undoNote
                    dayFragment.undoNote = null

                    NoteRepository.add(note)

                    if (mainActivity.notificationManager.tryScheduleNotification(
                            ScheduleNoteNotificationArguments(note = note)
                        )
                    ) {
                        Timber.i("${LogTags.NOTIFICATIONS} Scheduled notification after note save")
                    }
                    dayFragment.note = note
                    noteState.value = DayNoteState.Reading
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
    focusRequester: FocusRequester,
    noteState: MutableState<DayNoteState>,
    modifier: Modifier = Modifier
) {
    val themeValues = mainActivity.themePainter.values
    val focusManager = LocalFocusManager.current
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
                    focusManager.clearFocus()
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
                    dayFragment.undoNote = null
                    noteState.value = DayNoteState.Reading
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
                    focusManager.clearFocus()
                    noteTextFieldState = TextFieldState()
                    noteState.value = DayNoteState.Empty
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
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun DayNoteLayout(
    dayFragment: DayFragment,
    mainActivity: MainActivity,
    focusRequester: FocusRequester,
    noteState: MutableState<DayNoteState>,
    modifier: Modifier = Modifier
) {
    val themeValues = mainActivity.themePainter.values
    val focusManager = LocalFocusManager.current
    var noteTextFieldState by remember {
        mutableStateOf(TextFieldState(dayFragment.note?.text ?: ""))
    }
    ThemedNote(
        themeValues = themeValues,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
        ) {
            if (noteState.value == DayNoteState.Editing) {
                ThemedButton(
                    onClick = {
                        noteState.value = DayNoteState.Reading
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
                        focusManager.clearFocus()
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
                        noteState.value = DayNoteState.Reading
                        noteTextFieldState = TextFieldState(dayFragment.note?.text ?: "")
                        focusManager.clearFocus()
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
                        noteState.value = DayNoteState.Editing
                        focusRequester.requestFocus()
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

                        if (note != null) {
                            NoteRepository.delete(note)
                            if (mainActivity.notificationManager
                                    .tryCancelScheduledNotification(LocalDate.parse(note.date))
                            ) {
                                Timber.i("${LogTags.NOTIFICATIONS} Canceled notification after note deletion")
                            }
                            dayFragment.undoNote = note
                        }
                        noteState.value = DayNoteState.Empty
                        focusManager.clearFocus()
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
                readOnly = noteState.value == DayNoteState.Reading,
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
}