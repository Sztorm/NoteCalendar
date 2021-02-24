package com.sztorm.notecalendar

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_day_note.view.*

/**
 * A simple [Fragment] subclass.
 * Use the [DayNoteFragment.createInstance] factory method to
 * create an instance of this fragment.
 */
@RequiresApi(Build.VERSION_CODES.O)
class DayNoteFragment : Fragment() {
    private lateinit var mView: View
    private lateinit var mainActivity: MainActivity
    private lateinit var dayFragment: DayFragment
    private lateinit var note: NoteData

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }

    private fun handleBtnNoteEditTextClickEvent() = mView.btnNoteEditText.setOnClickListener {
        mView.layoutLblNote.isVisible = false
        mView.layoutEditDeleteNote.isVisible = false
        mView.txtNoteEdit.text.clear()
        mView.txtNoteEdit.text.append(note.text)
        mView.txtNoteEdit.isVisible = true
        mView.layoutSaveCancelNote.isVisible = true
    }

    private fun handleBtnNoteEditSaveClickEvent() = mView.btnNoteEditSave.setOnClickListener {
        val editedText = mView.txtNoteEdit.text.toString()
        /*val noteData = NoteData(
                mainActivity.viewedDate.toString(),
                editedText)*/
        //mainActivity.noteRepository.update(noteData)
        val editedNote = mainActivity.noteRepository.getByDate(mainActivity.viewedDate)

        if (editedNote != null) {
            editedNote.date = mainActivity.viewedDate.toString()
            editedNote.text = editedText
            editedNote.save()
            note = editedNote
        }
        mView.lblNote.text = editedText
        mView.txtNoteEdit.isVisible = false
        mView.layoutSaveCancelNote.isVisible = false
        mView.layoutEditDeleteNote.isVisible = true
        mView.layoutLblNote.isVisible = true
    }

    private fun handleBtnNoteEditCancelClickEvent() = mView.btnNoteEditCancel.setOnClickListener {
        mView.txtNoteEdit.isVisible = false
        mView.txtNoteEdit.text.clear()
        mView.layoutSaveCancelNote.isVisible = false
        mView.layoutEditDeleteNote.isVisible = true
        mView.layoutLblNote.isVisible = true
    }

    private fun handleBtnNoteDeleteTextClickEvent() = mView.btnNoteDeleteText.setOnClickListener {
        mainActivity.noteRepository.delete(note)
        dayFragment.setFragment(DayNoteAddFragment.createInstance(dayFragment))
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mView = inflater.inflate(R.layout.fragment_day_note, container, false)
        mView.lblNote.text = note.text
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