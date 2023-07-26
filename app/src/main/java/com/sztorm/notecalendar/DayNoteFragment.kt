package com.sztorm.notecalendar

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sztorm.notecalendar.databinding.FragmentDayNoteBinding
import com.sztorm.notecalendar.helpers.ViewHelper.Companion.hideKeyboard
import com.sztorm.notecalendar.helpers.ViewHelper.Companion.showKeyboard
import com.sztorm.notecalendar.repositories.NoteRepository
import timber.log.Timber
import java.time.LocalDate

class DayNoteFragment() : Fragment() {
    private lateinit var binding: FragmentDayNoteBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var dayFragment: DayFragment
    private var note: NoteData? = null

    constructor(dayFragment: DayFragment, note: NoteData?) : this() {
        this.dayFragment = dayFragment
        this.note = note
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDayNoteBinding.inflate(inflater, container, false)
        binding.lblNote.text = note?.text

        setTheme()
        setBtnNoteEditTextClickListener()
        setBtnNoteEditSaveClickListener()
        setBtnNoteEditCancelClickListener()
        setBtnNoteDeleteTextClickListener()

        return binding.root
    }

    private fun setEditUI() {
        binding.btnNoteEditSave.visibility = View.VISIBLE
        binding.btnNoteEditCancel.visibility = View.VISIBLE
        binding.btnNoteEditText.visibility = View.GONE
        binding.btnNoteDeleteText.visibility = View.GONE
        binding.spacerCenter.visibility = View.GONE
        binding.spacerEnd.visibility = View.GONE
        binding.layoutNoteEditBottom.visibility = View.VISIBLE
        binding.layoutNoteBottom.visibility = View.GONE
    }

    private fun setViewUI() {
        binding.btnNoteEditSave.visibility = View.GONE
        binding.btnNoteEditCancel.visibility = View.GONE
        binding.btnNoteEditText.visibility = View.VISIBLE
        binding.btnNoteDeleteText.visibility = View.VISIBLE
        binding.spacerCenter.visibility = View.VISIBLE
        binding.spacerEnd.visibility = View.VISIBLE
        binding.layoutNoteEditBottom.visibility = View.GONE
        binding.layoutNoteBottom.visibility = View.VISIBLE
    }

    private fun setBtnNoteEditTextClickListener() = binding.btnNoteEditText.setOnClickListener {
        binding.txtNoteEdit.text.clear()

        if (note !== null) {
            binding.txtNoteEdit.text.append(note?.text)
        }
        setEditUI()
        binding.txtNoteEdit.requestFocus()
        binding.txtNoteEdit.showKeyboard()
    }

    private fun setBtnNoteEditSaveClickListener() = binding.btnNoteEditSave.setOnClickListener {
        val editedText: String = binding.txtNoteEdit.text.toString()
        val editedNote: NoteData? = NoteRepository.getByDate(mainActivity.viewedDate)

        if (editedNote != null) {
            editedNote.date = mainActivity.viewedDate.toString()
            editedNote.text = editedText
            editedNote.save()
            note = editedNote
            if (mainActivity.tryScheduleNoteNotification(
                    ScheduleNoteNotificationArguments(note = note)
                )
            ) {
                Timber.i("${LogTags.NOTIFICATIONS} Scheduled notification after note edit")
            }
        }
        binding.lblNote.text = editedText

        setViewUI()
        binding.root.hideKeyboard()
    }

    private fun setBtnNoteEditCancelClickListener() = binding.btnNoteEditCancel.setOnClickListener {
        setViewUI()
        binding.txtNoteEdit.text.clear()
        binding.root.hideKeyboard()
    }

    private fun setBtnNoteDeleteTextClickListener() = binding.btnNoteDeleteText.setOnClickListener {
        val possibleNote: NoteData? = note

        if (possibleNote !== null) {
            NoteRepository.delete(possibleNote)
            if (mainActivity.tryCancelScheduledNotification(LocalDate.parse(possibleNote.date))) {
                Timber.i("${LogTags.NOTIFICATIONS} Canceled notification after note deletion")
            }
        }
        dayFragment.setFragment(DayNoteEmptyFragment(dayFragment))
    }

    private fun setTheme() {
        val themePainter: ThemePainter = mainActivity.themePainter
        val noteColor: Int = themePainter.values.noteColor

        binding.layoutNoteEditBottom.setBackgroundColor(noteColor)
        binding.layoutNoteBottom.setBackgroundColor(noteColor)
        binding.lblNote.setTextColor(themePainter.values.noteTextColor)
        themePainter.paintNote(binding.layoutNoteUpper)
        themePainter.paintOutlinedButton(binding.btnNoteEditText)
        themePainter.paintOutlinedButton(binding.btnNoteDeleteText)
        themePainter.paintButton(binding.btnNoteEditSave)
        themePainter.paintButton(binding.btnNoteEditCancel)
        themePainter.paintEditText(binding.txtNoteEdit)
    }
}