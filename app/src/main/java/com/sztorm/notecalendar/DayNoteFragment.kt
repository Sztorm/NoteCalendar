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

class DayNoteFragment() : Fragment() {
    private lateinit var binding: FragmentDayNoteBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var dayFragment: DayFragment
    private var note: NoteData? = null
    private var isEditRequested: Boolean = false

    constructor(dayFragment: DayFragment, note: NoteData?, args: Arguments? = null) : this() {
        this.dayFragment = dayFragment
        this.note = note

        if (args is CreateOrEditNoteRequest) {
            isEditRequested = true
        }
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

    override fun onStart() {
        super.onStart()

        if (isEditRequested) {
            setEditUI()
            isEditRequested = false
        }
    }

    private fun setEditUI() {
        binding.txtNoteEdit.text.clear()

        if (note !== null) {
            binding.txtNoteEdit.text.append(note?.text)
        }
        binding.btnNoteEditSave.visibility = View.VISIBLE
        binding.btnNoteEditCancel.visibility = View.VISIBLE
        binding.btnNoteEditText.visibility = View.GONE
        binding.btnNoteDeleteText.visibility = View.GONE
        binding.spacerCenter.visibility = View.GONE
        binding.spacerEnd.visibility = View.GONE
        binding.layoutNoteEditBottom.visibility = View.VISIBLE
        binding.layoutNoteBottom.visibility = View.GONE
        binding.txtNoteEdit.requestFocus()
        binding.txtNoteEdit.showKeyboard()
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
        binding.txtNoteEdit.text.clear()
        binding.root.hideKeyboard()
    }

    private fun setBtnNoteEditTextClickListener() = binding.btnNoteEditText.setOnClickListener {
        setEditUI()
    }

    private fun setBtnNoteEditSaveClickListener() = binding.btnNoteEditSave.setOnClickListener {
        val editedText: String = binding.txtNoteEdit.text.toString()
        val editedNote: NoteData? = NoteRepository.getByDate(mainActivity.sharedData.viewedDate)

        if (editedNote != null) {
            editedNote.date = mainActivity.sharedData.viewedDate.toString()
            editedNote.text = editedText
            editedNote.save()
            note = editedNote
            if (mainActivity.notificationManager.tryScheduleNotification(
                    ScheduleNoteNotificationArguments(note = note)
                )
            ) {
                Timber.i("${LogTags.NOTIFICATIONS} Scheduled notification after note edit")
            }
        }
        binding.lblNote.text = editedText

        setViewUI()
    }

    private fun setBtnNoteEditCancelClickListener() = binding.btnNoteEditCancel.setOnClickListener {
        setViewUI()
    }

    private fun setBtnNoteDeleteTextClickListener() = binding.btnNoteDeleteText.setOnClickListener {
        val note: NoteData? = this.note

        if (note == null) {
            dayFragment.setFragment(DayNoteEmptyFragment(dayFragment))
        }
        else {
            dayFragment.setFragment(DayNoteEmptyFragment(dayFragment, UndoNoteDeleteOption(note)))
        }
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