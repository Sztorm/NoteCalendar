package com.sztorm.notecalendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.sztorm.notecalendar.components.ThemedButton
import com.sztorm.notecalendar.databinding.FragmentDayNoteEmptyBinding
import com.sztorm.notecalendar.repositories.NoteRepository
import timber.log.Timber

class DayNoteEmptyFragment() : Fragment() {
    private lateinit var binding: FragmentDayNoteEmptyBinding
    private lateinit var dayFragment: DayFragment
    private var args: Arguments? = null

    constructor(dayFragment: DayFragment, args: Arguments? = null) : this() {
        this.dayFragment = dayFragment
        this.args = args
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDayNoteEmptyBinding.inflate(inflater, container, false)
        binding.composeView.setContent {
            MaterialTheme {
                DayNoteEmptyLayout(args, dayFragment, activity as MainActivity)
            }
        }
        if (args is CreateOrEditNoteRequest) {
            dayFragment.setFragment(DayNoteAddFragment(dayFragment))
            this.args = null
        }

        return binding.root
    }
}

@Composable
fun DayNoteEmptyLayout(
    args: Arguments?, dayFragment: DayFragment, mainActivity: MainActivity
) {
    val themeValues = mainActivity.themePainter.values

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        ThemedButton(
            onClick = { dayFragment.setFragment(DayNoteAddFragment(dayFragment)) },
            themeValues = themeValues,
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 0.dp),
            text = stringResource(R.string.AddNote),
            icon = painterResource(R.drawable.icon_note_add)
        )
        if (args is UndoNoteDeleteOption) {
            ThemedButton(
                onClick = {
                    val note = args.note

                    NoteRepository.add(note)

                    if (mainActivity.notificationManager.tryScheduleNotification(
                            ScheduleNoteNotificationArguments(note = note)
                        )
                    ) {
                        Timber.i("${LogTags.NOTIFICATIONS} Scheduled notification after note save")
                    }
                    dayFragment.setFragment(DayNoteFragment(dayFragment, note))
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

