package com.sztorm.notecalendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class WeekDayFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_week_day, container, false)

    companion object : InstanceCreator<WeekDayFragment> {
        @JvmStatic
        override fun createInstance(): WeekDayFragment = WeekDayFragment()
    }
}