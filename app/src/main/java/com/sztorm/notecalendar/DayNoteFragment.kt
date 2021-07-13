package com.sztorm.notecalendar

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
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

    private fun handleBtnNoteEditTextClickEvent() = mView.btnNoteEditText.setOnClickListener {
        mView.layoutLblNote.isVisible = false
        mView.layoutEditDeleteNote.isVisible = false
        mView.txtNoteEdit.text.clear()

        if (note !== null) {
            mView.txtNoteEdit.text.append(note?.text)
        }
        mView.txtNoteEdit.isVisible = true
        mView.layoutSaveCancelNote.isVisible = true
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
        mView.txtNoteEdit.isVisible = false
        mView.layoutSaveCancelNote.isVisible = false
        mView.layoutEditDeleteNote.isVisible = true
        mView.layoutLblNote.isVisible = true
        mView.hideKeyboard()
    }

    private fun handleBtnNoteEditCancelClickEvent() = mView.btnNoteEditCancel.setOnClickListener {
        mView.txtNoteEdit.isVisible = false
        mView.txtNoteEdit.text.clear()
        mView.layoutSaveCancelNote.isVisible = false
        mView.layoutEditDeleteNote.isVisible = true
        mView.layoutLblNote.isVisible = true
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
        val themeValues: ThemeValues = themePainter.values

        themePainter.paintNote(mView)
        themePainter.paintOutlinedButton(mView.btnNoteEditText)
        themePainter.paintOutlinedButton(mView.btnNoteDeleteText)
        themePainter.paintButton(mView.btnNoteEditSave)
        themePainter.paintButton(mView.btnNoteEditCancel)
        themePainter.paintEditText(mView.txtNoteEdit)
        mView.lblNote.setTextColor(themeValues.noteTextColor)
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