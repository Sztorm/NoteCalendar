package com.sztorm.notecalendar

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sztorm.notecalendar.databinding.FragmentDayNoteAddBinding
import com.sztorm.notecalendar.helpers.ViewHelper.Companion.hideKeyboard
import com.sztorm.notecalendar.repositories.NoteRepository
import timber.log.Timber

class DayNoteAddFragment(private val dayFragment: DayFragment) : Fragment() {
    private lateinit var binding: FragmentDayNoteAddBinding
    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }

    private fun handleBtnNoteAddTextClickEvent() = binding.btnNoteAddText.setOnClickListener {
        binding.btnNoteAddText.visibility = View.GONE
        binding.btnNoteSave.visibility = View.VISIBLE
        binding.btnNoteCancel.visibility = View.VISIBLE
        binding.txtNoteAdd.visibility = View.VISIBLE
    }

    private fun handleBtnNoteCancelClickEvent() = binding.btnNoteCancel.setOnClickListener {
        binding.txtNoteAdd.text.clear()

        binding.txtNoteAdd.visibility = View.GONE
        binding.btnNoteSave.visibility = View.INVISIBLE
        binding.btnNoteCancel.visibility = View.INVISIBLE
        binding.btnNoteAddText.visibility = View.VISIBLE
        binding.root.hideKeyboard()
    }

    private fun handleBtnNoteSaveClickEvent() = binding.btnNoteSave.setOnClickListener {
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
        themePainter.paintButton(binding.btnNoteAddText)
        themePainter.paintButton(binding.btnNoteSave)
        themePainter.paintButton(binding.btnNoteCancel)
        themePainter.paintEditText(binding.txtNoteAdd)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDayNoteAddBinding.inflate(inflater, container, false)
        setTheme()
        handleBtnNoteAddTextClickEvent()
        handleBtnNoteCancelClickEvent()
        handleBtnNoteSaveClickEvent()

        return binding.root
    }
}