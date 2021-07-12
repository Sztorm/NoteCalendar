package com.sztorm.notecalendar

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.sztorm.notecalendar.helpers.ViewHelper.Companion.hideKeyboard
import kotlinx.android.synthetic.main.fragment_day_note_add.*
import kotlinx.android.synthetic.main.fragment_day_note_add.view.*

/**
 * A simple [Fragment] subclass.
 * Use the [DayNoteAddFragment.createInstance] factory method to create an instance of this
 * fragment.
 */
class DayNoteAddFragment : Fragment() {
    private lateinit var mView: View
    private lateinit var mainActivity: MainActivity
    private lateinit var dayFragment: DayFragment

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }

    private fun handleBtnNoteAddTextClickEvent() = mView.btnNoteAddText.setOnClickListener {
        mView.btnNoteAddText.isVisible = false
        mView.layoutSaveCancelNote.isVisible = true
        mView.txtNoteAdd.isVisible = true
    }

    private fun handleBtnNoteCancelClickEvent() = mView.btnNoteCancel.setOnClickListener {
        txtNoteAdd.text.clear()

        mView.txtNoteAdd.isVisible = false
        mView.layoutSaveCancelNote.isVisible = false
        mView.btnNoteAddText.isVisible = true
        mView.hideKeyboard()
    }

    private fun handleBtnNoteSaveClickEvent() = mView.btnNoteSave.setOnClickListener {
        val noteData = NoteData(
            mainActivity.viewedDate.toString(),
            txtNoteAdd.text.toString())
        mainActivity.noteRepository.add(noteData)
        mainActivity.scheduleNoteNotification(ScheduleNoteNotificationArguments(note = noteData))
        dayFragment.setFragment(DayNoteFragment.createInstance(dayFragment, noteData))
    }

    private fun setTheme() {
        val themePainter: ThemePainter = mainActivity.themePainter
        themePainter.paintNote(mView)
        themePainter.paintButton(mView.btnNoteAddText)
        themePainter.paintButton(mView.btnNoteSave)
        themePainter.paintButton(mView.btnNoteCancel)
        themePainter.paintEditText(mView.txtNoteAdd)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mView = inflater.inflate(R.layout.fragment_day_note_add, container, false)
        setTheme()
        handleBtnNoteAddTextClickEvent()
        handleBtnNoteCancelClickEvent()
        handleBtnNoteSaveClickEvent()

        return mView
    }

    companion object : DayNoteAddCreator {
        @JvmStatic
        override fun createInstance(): DayNoteAddFragment = DayNoteAddFragment()

        @JvmStatic
        override fun createInstance(dayFragment: DayFragment): DayNoteAddFragment {
            val result = DayNoteAddFragment()
            result.dayFragment = dayFragment
            return result
        }
    }
}

interface DayNoteAddCreator: InstanceCreator<DayNoteAddFragment> {
    fun createInstance(dayFragment: DayFragment): DayNoteAddFragment
}