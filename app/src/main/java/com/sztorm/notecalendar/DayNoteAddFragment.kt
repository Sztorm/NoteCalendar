package com.sztorm.notecalendar

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sztorm.notecalendar.helpers.ViewHelper.Companion.hideKeyboard
import kotlinx.android.synthetic.main.fragment_day_note_add.*
import kotlinx.android.synthetic.main.fragment_day_note_add.view.*
import kotlinx.android.synthetic.main.fragment_day_note_add.view.layoutNoteBottom
import kotlinx.android.synthetic.main.fragment_day_note_add.view.layoutNoteUpper

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
        mView.btnNoteAddText.visibility = View.GONE
        mView.btnNoteSave.visibility = View.VISIBLE
        mView.btnNoteCancel.visibility = View.VISIBLE
        mView.txtNoteAdd.visibility = View.VISIBLE
    }

    private fun handleBtnNoteCancelClickEvent() = mView.btnNoteCancel.setOnClickListener {
        txtNoteAdd.text.clear()

        mView.txtNoteAdd.visibility = View.GONE
        mView.btnNoteSave.visibility = View.INVISIBLE
        mView.btnNoteCancel.visibility = View.INVISIBLE
        mView.btnNoteAddText.visibility = View.VISIBLE
        mView.hideKeyboard()
    }

    private fun handleBtnNoteSaveClickEvent() = mView.btnNoteSave.setOnClickListener {
        val noteData = NoteData(
            mainActivity.viewedDate.toString(),
            txtNoteAdd.text.toString())
        mainActivity.noteRepository.add(noteData)
        mainActivity.tryScheduleNoteNotification(ScheduleNoteNotificationArguments(note = noteData))
        dayFragment.setFragment(DayNoteFragment.createInstance(dayFragment, noteData))
    }

    private fun setTheme() {
        val themePainter: ThemePainter = mainActivity.themePainter

        mView.layoutNoteBottom.setBackgroundColor(themePainter.values.noteColor)
        themePainter.paintNote(mView.layoutNoteUpper)
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