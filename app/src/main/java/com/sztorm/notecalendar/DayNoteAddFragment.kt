package com.sztorm.notecalendar

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sztorm.notecalendar.databinding.FragmentDayNoteAddBinding
import com.sztorm.notecalendar.helpers.ViewHelper.Companion.hideKeyboard
import com.sztorm.notecalendar.helpers.ViewHelper.Companion.showKeyboard
import com.sztorm.notecalendar.repositories.NoteRepository
import timber.log.Timber

class DayNoteAddFragment() : Fragment() {
    private lateinit var binding: FragmentDayNoteAddBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var dayFragment: DayFragment

    constructor(dayFragment: DayFragment) : this() {
        this.dayFragment = dayFragment
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDayNoteAddBinding.inflate(inflater, container, false)
        setTheme()
        setBtnNoteCancelClickListener()
        setBtnNoteSaveClickListener()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtNoteAdd.requestFocus()
        binding.txtNoteAdd.showKeyboard()
    }

    private fun setBtnNoteCancelClickListener() = binding.btnNoteCancel.setOnClickListener {
        binding.txtNoteAdd.text.clear()
        binding.root.hideKeyboard()
        dayFragment.setFragment(DayNoteEmptyFragment(dayFragment))
    }

    private fun setBtnNoteSaveClickListener() = binding.btnNoteSave.setOnClickListener {
        binding.root.hideKeyboard()
        val noteData = NoteData(
            date = mainActivity.viewedDate.toString(),
            text = binding.txtNoteAdd.text.toString()
        )
        NoteRepository.add(noteData)
        if (mainActivity.tryScheduleNoteNotification(
                ScheduleNoteNotificationArguments(note = noteData)
            )
        ) {
            Timber.i("${LogTags.NOTIFICATIONS} Scheduled notification after note save")
        }
        dayFragment.setFragment(DayNoteFragment(dayFragment, noteData))
    }

    private fun setTheme() {
        val themePainter: ThemePainter = mainActivity.themePainter

        binding.layoutNoteBottom.setBackgroundColor(themePainter.values.noteColor)
        themePainter.paintNote(binding.layoutNoteUpper)
        themePainter.paintButton(binding.btnNoteSave)
        themePainter.paintButton(binding.btnNoteCancel)
        themePainter.paintEditText(binding.txtNoteAdd)
    }
}