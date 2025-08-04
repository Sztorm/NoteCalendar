package com.sztorm.notecalendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
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
import com.sztorm.notecalendar.components.ThemedButton
import com.sztorm.notecalendar.components.ThemedIconButton
import com.sztorm.notecalendar.components.ThemedNote
import com.sztorm.notecalendar.databinding.FragmentDayNoteBinding
import com.sztorm.notecalendar.repositories.NoteRepository
import timber.log.Timber
import java.time.LocalDate

class DayNoteFragment() : Fragment() {
    private lateinit var binding: FragmentDayNoteBinding
    private lateinit var dayFragment: DayFragment
    private var _note: NoteData? = null
    private var isEditRequested: Boolean = false

    val note: NoteData?
        get() = _note

    constructor(dayFragment: DayFragment, note: NoteData?, args: Arguments? = null) : this() {
        this.dayFragment = dayFragment
        this._note = note

        if (args is CreateOrEditNoteRequest) {
            isEditRequested = true
        }
    }

    fun setNote(note: NoteData?) {
        this._note = note
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDayNoteBinding.inflate(inflater, container, false)
        binding.composeView.setContent {
            MaterialTheme {
                DayNoteLayout(dayFragment, this, activity as MainActivity, isEditRequested)
            }
        }
        return binding.root
    }
}

@Composable
fun DayNoteLayout(
    dayFragment: DayFragment,
    dayNoteFragment: DayNoteFragment,
    mainActivity: MainActivity,
    isEditRequested: Boolean
) {
    val themeValues = mainActivity.themePainter.values
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var noteTextFieldState by remember {
        mutableStateOf(TextFieldState(dayNoteFragment.note?.text ?: ""))
    }
    var isInEditableState by remember { mutableStateOf(isEditRequested) }

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
                            dayNoteFragment.setNote(editedNote)

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
                        noteTextFieldState = TextFieldState(dayNoteFragment.note?.text ?: "")
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
                        val note = dayNoteFragment.note

                        if (note == null) {
                            dayFragment.setFragment(DayNoteEmptyFragment(dayFragment))
                        } else {
                            NoteRepository.delete(note)
                            if (mainActivity.notificationManager
                                    .tryCancelScheduledNotification(LocalDate.parse(note.date))
                            ) {
                                Timber.i("${LogTags.NOTIFICATIONS} Canceled notification after note deletion")
                            }
                            dayFragment.setFragment(
                                DayNoteEmptyFragment(
                                    dayFragment,
                                    UndoNoteDeleteOption(note)
                                )
                            )
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