package com.sztorm.notecalendar

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sztorm.notecalendar.helpers.ViewHelper.Companion.hideKeyboard
import kotlinx.android.synthetic.main.fragment_day_note.view.*
import java.time.LocalDate

/**
 * A simple [Fragment] subclass.
 * Use the [DayNoteFragment.createInstance] factory method to
 * create an instance of this fragment.
 */
class DayNoteFragment : Fragment() {
    private lateinit var mView: View
    private lateinit var mainActivity: MainActivity
    private lateinit var dayFragment: DayFragment
    private var note: NoteData? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }

    private fun setEditMode() {
        mView.btnNoteEditSave.visibility = View.VISIBLE
        mView.btnNoteEditCancel.visibility = View.VISIBLE
        mView.btnNoteEditText.visibility = View.GONE
        mView.btnNoteDeleteText.visibility = View.GONE
        mView.layoutNoteEditBottom.visibility = View.VISIBLE
        mView.layoutNoteBottom.visibility = View.GONE
    }

    private fun setViewMode() {
        mView.btnNoteEditSave.visibility = View.GONE
        mView.btnNoteEditCancel.visibility = View.GONE
        mView.btnNoteEditText.visibility = View.VISIBLE
        mView.btnNoteDeleteText.visibility = View.VISIBLE
        mView.layoutNoteEditBottom.visibility = View.GONE
        mView.layoutNoteBottom.visibility = View.VISIBLE
    }

    private fun handleBtnNoteEditTextClickEvent() = mView.btnNoteEditText.setOnClickListener {
        mView.txtNoteEdit.text.clear()

        if (note !== null) {
            mView.txtNoteEdit.text.append(note?.text)
        }
        setEditMode()
    }

    private fun handleBtnNoteEditSaveClickEvent() = mView.btnNoteEditSave.setOnClickListener {
        val editedText: String = mView.txtNoteEdit.text.toString()
        val editedNote: NoteData? = mainActivity.noteRepository.getByDate(mainActivity.viewedDate)

        if (editedNote != null) {
            editedNote.date = mainActivity.viewedDate.toString()
            editedNote.text = editedText
            editedNote.save()
            note = editedNote
            mainActivity.tryScheduleNoteNotification(ScheduleNoteNotificationArguments(note = note))
        }
        mView.lblNote.text = editedText

        setViewMode()
        mView.hideKeyboard()
    }

    private fun handleBtnNoteEditCancelClickEvent() = mView.btnNoteEditCancel.setOnClickListener {
        setViewMode()
        mView.txtNoteEdit.text.clear()
        mView.hideKeyboard()
    }

    private fun handleBtnNoteDeleteTextClickEvent() = mView.btnNoteDeleteText.setOnClickListener {
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

        mView.layoutNoteEditBottom.setBackgroundColor(noteColor)
        mView.layoutNoteBottom.setBackgroundColor(noteColor)
        mView.lblNote.setTextColor(themePainter.values.noteTextColor)
        themePainter.paintNote(mView.layoutNoteUpper)
        themePainter.paintOutlinedButton(mView.btnNoteEditText)
        themePainter.paintOutlinedButton(mView.btnNoteDeleteText)
        themePainter.paintButton(mView.btnNoteEditSave)
        themePainter.paintButton(mView.btnNoteEditCancel)
        themePainter.paintEditText(mView.txtNoteEdit)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mView = inflater.inflate(R.layout.fragment_day_note, container, false)
        mView.lblNote.text = note?.text

        setTheme()
        handleBtnNoteEditTextClickEvent()
        handleBtnNoteEditSaveClickEvent()
        handleBtnNoteEditCancelClickEvent()
        handleBtnNoteDeleteTextClickEvent()

        return mView
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