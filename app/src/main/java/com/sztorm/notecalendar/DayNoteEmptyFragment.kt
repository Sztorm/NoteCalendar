package com.sztorm.notecalendar

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sztorm.notecalendar.databinding.FragmentDayNoteEmptyBinding
import com.sztorm.notecalendar.repositories.NoteRepository
import timber.log.Timber

class DayNoteEmptyFragment() : Fragment() {
    private lateinit var binding: FragmentDayNoteEmptyBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var dayFragment: DayFragment
    private var args: Arguments? = null

    constructor(dayFragment: DayFragment, args: Arguments? = null) : this() {
        this.dayFragment = dayFragment
        this.args = args
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDayNoteEmptyBinding.inflate(inflater, container, false)
        setTheme()
        setBtnNoteAddClickListener()
        setBtnNoteUndoDeletionClickListener()
        handleArgs(args)

        return binding.root
    }

    private fun handleArgs(args: Arguments?) {
        when (args) {
            is CreateOrEditNoteRequest -> {
                dayFragment.setFragment(DayNoteAddFragment(dayFragment))
                this.args = null
            }

            is UndoNoteDeleteOption -> {
                binding.btnUndoDeletion.visibility = View.VISIBLE
            }
        }
    }

    private fun setTheme() {
        val themePainter: ThemePainter = mainActivity.themePainter
        themePainter.paintButton(binding.btnNoteAdd)
        themePainter.paintButton(binding.btnUndoDeletion)
    }

    private fun setBtnNoteAddClickListener() =
        binding.btnNoteAdd.setOnClickListener {
            dayFragment.setFragment(DayNoteAddFragment(dayFragment))
        }

    private fun setBtnNoteUndoDeletionClickListener() =
        binding.btnUndoDeletion.setOnClickListener {
            val args = this.args

            if (args is UndoNoteDeleteOption) {
                val note = args.note

                NoteRepository.add(note)

                if (mainActivity.notificationManager.tryScheduleNotification(
                        ScheduleNoteNotificationArguments(note = note)
                    )
                ) {
                    Timber.i("${LogTags.NOTIFICATIONS} Scheduled notification after note save")
                }
                dayFragment.setFragment(DayNoteFragment(dayFragment, note))
            }
        }
}