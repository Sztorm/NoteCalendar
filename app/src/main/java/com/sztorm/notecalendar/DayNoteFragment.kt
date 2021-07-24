package com.sztorm.notecalendar

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sztorm.notecalendar.databinding.FragmentDayNoteBinding
import com.sztorm.notecalendar.helpers.ViewHelper.Companion.hideKeyboard
import java.time.LocalDate

/**
 * A simple [Fragment] subclass.
 * Use the [DayNoteFragment.createInstance] factory method to
 * create an instance of this fragment.
 */
class DayNoteFragment : Fragment() {
    private lateinit var binding: FragmentDayNoteBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var dayFragment: DayFragment
    private var note: NoteData? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }

    private fun setEditMode() {
        binding.btnNoteEditSave.visibility = View.VISIBLE
        binding.btnNoteEditCancel.visibility = View.VISIBLE
        binding.btnNoteEditText.visibility = View.GONE
        binding.btnNoteDeleteText.visibility = View.GONE
        binding.layoutNoteEditBottom.visibility = View.VISIBLE
        binding.layoutNoteBottom.visibility = View.GONE
    }

    private fun setViewMode() {
        binding.btnNoteEditSave.visibility = View.GONE
        binding.btnNoteEditCancel.visibility = View.GONE
        binding.btnNoteEditText.visibility = View.VISIBLE
        binding.btnNoteDeleteText.visibility = View.VISIBLE
        binding.layoutNoteEditBottom.visibility = View.GONE
        binding.layoutNoteBottom.visibility = View.VISIBLE
    }

    private fun handleBtnNoteEditTextClickEvent() = binding.btnNoteEditText.setOnClickListener {
        binding.txtNoteEdit.text.clear()

        if (note !== null) {
            binding.txtNoteEdit.text.append(note?.text)
        }
        setEditMode()
    }

    private fun handleBtnNoteEditSaveClickEvent() = binding.btnNoteEditSave.setOnClickListener {
        val editedText: String = binding.txtNoteEdit.text.toString()
        val editedNote: NoteData? = mainActivity.noteRepository.getByDate(mainActivity.viewedDate)

        if (editedNote != null) {
            editedNote.date = mainActivity.viewedDate.toString()
            editedNote.text = editedText
            editedNote.save()
            note = editedNote
            mainActivity.tryScheduleNoteNotification(ScheduleNoteNotificationArguments(note = note))
        }
        binding.lblNote.text = editedText

        setViewMode()
        binding.root.hideKeyboard()
    }

    private fun handleBtnNoteEditCancelClickEvent() = binding.btnNoteEditCancel.setOnClickListener {
        setViewMode()
        binding.txtNoteEdit.text.clear()
        binding.root.hideKeyboard()
    }

    private fun handleBtnNoteDeleteTextClickEvent() = binding.btnNoteDeleteText.setOnClickListener {
        val possibleNote: NoteData? = note

        if (possibleNote !== null) {
            mainActivity.noteRepository.delete(possibleNote)
            mainActivity.tryCancelScheduledNotification(LocalDate.parse(possibleNote.date))
        }
        dayFragment.setFragment(DayNoteAddFragment.createInstance(dayFragment))
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

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDayNoteBinding.inflate(inflater, container, false)
        binding.lblNote.text = note?.text

        setTheme()
        handleBtnNoteEditTextClickEvent()
        handleBtnNoteEditSaveClickEvent()
        handleBtnNoteEditCancelClickEvent()
        handleBtnNoteDeleteTextClickEvent()

        return binding.root
    }

    companion object : DayNoteCreator {
        @JvmStatic
        override fun createInstance(): DayNoteFragment = DayNoteFragment()

        @JvmStatic
        override fun createInstance(dayFragment: DayFragment, note: NoteData): DayNoteFragment {
            val result = DayNoteFragment()
            result.dayFragment = dayFragment
            result.note = note
            return result
        }
    }
}

interface DayNoteCreator: InstanceCreator<DayNoteFragment> {
    fun createInstance(dayFragment: DayFragment, note: NoteData): DayNoteFragment
}