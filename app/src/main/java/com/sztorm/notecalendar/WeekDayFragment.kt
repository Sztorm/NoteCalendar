package com.sztorm.notecalendar

import androidx.fragment.app.Fragment

class WeekDayFragment : Fragment() {
    companion object : InstanceCreator<WeekDayFragment> {
        @JvmStatic
        override fun createInstance(): WeekDayFragment = WeekDayFragment()
    }
}