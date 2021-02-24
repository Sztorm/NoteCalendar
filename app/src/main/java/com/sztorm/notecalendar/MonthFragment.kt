package com.sztorm.notecalendar

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.fragment_month.view.*
import java.time.LocalDate
import com.sztorm.notecalendar.DateHelper.Companion.toDate

/**
 * A simple [Fragment] subclass.
 * Use the [MonthFragment.createInstance] factory method to create an instance of this fragment.
 */
@RequiresApi(Build.VERSION_CODES.O)
class MonthFragment : Fragment() {
    private lateinit var mView: View
    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mView = inflater.inflate(R.layout.fragment_month, container, false)
        val viewedDate: LocalDate = mainActivity.viewedDate
        val date = viewedDate.toDate()

        mView.calendarView.setDate(date.time, false, false)
        mView.calendarView.setOnDateChangeListener { calendarView, i, i2, i3 ->
            mainActivity.viewedDate = LocalDate.of(i, i2 + 1, i3)
            mainActivity.setFragment(DayFragment)
        }
        return mView
    }

    companion object : InstanceCreator<MonthFragment> {
        @JvmStatic
        override fun createInstance(): MonthFragment = MonthFragment()
    }
}