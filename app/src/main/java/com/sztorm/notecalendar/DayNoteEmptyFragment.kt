package com.sztorm.notecalendar

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sztorm.notecalendar.databinding.FragmentDayNoteEmptyBinding

class DayNoteEmptyFragment(private val dayFragment: DayFragment) : Fragment() {
    private lateinit var binding: FragmentDayNoteEmptyBinding
    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDayNoteEmptyBinding.inflate(inflater, container, false)
        setTheme()
        setBtnNoteAddClickListener()

        return binding.root
    }

    private fun setTheme() {
        val themePainter: ThemePainter = mainActivity.themePainter
        themePainter.paintButton(binding.btnNoteAdd)
    }

    private fun setBtnNoteAddClickListener() = binding.btnNoteAdd.setOnClickListener {
        dayFragment.setFragment(DayNoteAddFragment(dayFragment))
    }
}