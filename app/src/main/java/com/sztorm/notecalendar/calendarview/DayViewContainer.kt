package com.sztorm.notecalendar.calendarview

import android.view.View
import android.widget.TextView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.ui.ViewContainer
import com.sztorm.notecalendar.DayFragment
import com.sztorm.notecalendar.MainActivity
import kotlinx.android.synthetic.main.calendar_day.view.*

class DayViewContainer(view: View, mainActivity: MainActivity) : ViewContainer(view) {
    val textView: TextView = view.cvDayText
    lateinit var day: CalendarDay

    init {
        view.setOnClickListener {
            mainActivity.setMainFragment(DayFragment, date = day.date)
        }
    }
}