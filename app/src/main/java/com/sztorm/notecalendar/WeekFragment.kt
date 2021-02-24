package com.sztorm.notecalendar

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment

@RequiresApi(Build.VERSION_CODES.O)
class WeekFragment : Fragment() {
    private lateinit var mView: View
    private lateinit var mainActivity: MainActivity
    private val weekDayFragments: Array<WeekDayFragment?> = Array(DAYS_IN_WEEK) { null }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        mView = inflater.inflate(R.layout.fragment_week, container, false)
        var nextDate = mainActivity.viewedDate
        nextDate = nextDate.minusDays(3)
        val transaction = childFragmentManager.beginTransaction()

        for (i: Int in 0..6) {
            val fragment = WeekDayFragment.createInstance(nextDate)
            weekDayFragments[i] = fragment
            transaction.add(R.id.weekDayFragmentContainer, fragment)
            nextDate = nextDate.plusDays(1)
        }
        transaction.commit()
        return mView
    }

    companion object : InstanceCreator<WeekFragment> {
        const val DAYS_IN_WEEK = 7

        @JvmStatic
        override fun createInstance(): WeekFragment = WeekFragment()
    }
}