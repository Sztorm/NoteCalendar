package com.sztorm.notecalendar.calendarview

import android.widget.TextView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.ui.ViewContainer
import com.sztorm.notecalendar.CreateOrEditNoteRequest
import com.sztorm.notecalendar.MainActivity
import com.sztorm.notecalendar.MainFragmentType
import com.sztorm.notecalendar.databinding.CalendarDayBinding

class DayViewContainer(binding: CalendarDayBinding, mainActivity: MainActivity) :
    ViewContainer(binding.root) {
    private lateinit var day: CalendarDay
    val textView: TextView = binding.cvDayText

    init {
        binding.root.setOnClickListener {
            mainActivity.viewedDate = day.date
            mainActivity.setMainFragment(MainFragmentType.DAY)
        }
        binding.root.setOnLongClickListener {
            mainActivity.viewedDate = day.date
            mainActivity.setMainFragment(MainFragmentType.DAY, args = CreateOrEditNoteRequest)
            true
        }
    }

    fun reinit(day: CalendarDay) {
        this.day = day
    }
}