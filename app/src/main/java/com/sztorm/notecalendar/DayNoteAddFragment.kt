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
import kotlinx.android.synthetic.main.fragment_day_note_add.*
import kotlinx.android.synthetic.main.fragment_day_note_add.view.*

/**
 * A simple [Fragment] subclass.
 * Use the [DayNoteAddFragment.createInstance] factory method to create an instance of this
 * fragment.
 */
@RequiresApi(Build.VERSION_CODES.O)
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
    }

    private fun handleBtnNoteSaveClickEvent() = mView.btnNoteSave.setOnClickListener {
        val noteData = NoteData(
            mainActivity.viewedDate.toString(),
            txtNoteAdd.text.toString())
        mainActivity.noteRepository.add(noteData)
        dayFragment.setFragment(DayNoteFragment.createInstance(dayFragment, noteData))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mView = inflater.inflate(R.layout.fragment_day_note_add, container, false)
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