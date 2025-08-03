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
import com.sztorm.notecalendar.components.ThemedNote
import com.sztorm.notecalendar.databinding.FragmentDayNoteAddBinding
import com.sztorm.notecalendar.repositories.NoteRepository
import timber.log.Timber

class DayNoteAddFragment() : Fragment() {
    private lateinit var binding: FragmentDayNoteAddBinding
    private lateinit var dayFragment: DayFragment

    constructor(dayFragment: DayFragment) : this() {
        this.dayFragment = dayFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDayNoteAddBinding.inflate(inflater, container, false)
        binding.composeView.setContent {
            MaterialTheme {
                DayNoteAddLayout(dayFragment, activity as MainActivity)
            }
        }
        return binding.root
    }
}

@Composable
fun DayNoteAddLayout(
    dayFragment: DayFragment,
    mainActivity: MainActivity
) {
    val themeValues = mainActivity.themePainter.values
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var noteTextFieldState by remember { mutableStateOf(TextFieldState()) }

    ThemedNote(
        themeValues = themeValues,
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
                    dayFragment.setFragment(DayNoteFragment(dayFragment, noteData))
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
                    noteTextFieldState = TextFieldState()
                    keyboardController?.hide()
                    dayFragment.setFragment(DayNoteEmptyFragment(dayFragment))
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
                    .focusRequester(focusRequester)
            )
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }
}