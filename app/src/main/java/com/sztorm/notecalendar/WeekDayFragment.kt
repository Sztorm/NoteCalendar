package com.sztorm.notecalendar

import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.sztorm.notecalendar.DateHelper.Companion.toLocalizedString
import kotlinx.android.synthetic.main.fragment_week_day.view.*
import java.time.DayOfWeek
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class WeekDayFragment : Fragment() {
    private lateinit var mView: View
    private lateinit var mainActivity: MainActivity
    lateinit var date: LocalDate
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = activity as MainActivity
    }

    private fun getTextColorOf(dayOfWeek: DayOfWeek): Int {
        val typedValue = TypedValue()
        mainActivity.theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        val colorPrimary: Int = ContextCompat.getColor(mView.context, typedValue.resourceId)

        mainActivity.theme.resolveAttribute(R.attr.colorSecondary, typedValue, true)
        val colorSecondary: Int = ContextCompat.getColor(mView.context, typedValue.resourceId)

        mainActivity.theme.resolveAttribute(R.attr.colorText, typedValue, true)
        val colorText: Int = ContextCompat.getColor(mView.context, typedValue.resourceId)

        return when(dayOfWeek) {
            DayOfWeek.SUNDAY -> colorPrimary
            DayOfWeek.SATURDAY -> colorSecondary
            else -> colorText
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mView = inflater.inflate(R.layout.fragment_week_day, container, false)
        val textColor: Int = getTextColorOf(date.dayOfWeek)
        mView.lblWeekDayOfMonth.setTextColor(textColor)
        mView.lblWeekDayOfMonth.text = date.dayOfMonth.toString()

        mView.lblWeekDayOfWeek.setTextColor(textColor)
        mView.lblWeekDayOfWeek.text = date.dayOfWeek.toLocalizedString(mView.context)

        val viewedDate: LocalDate = mainActivity.viewedDate

        if (viewedDate.dayOfYear == date.dayOfYear && viewedDate.year == date.year) {
            mView.layoutDayWeek.setBackgroundResource(R.drawable.selector_bg_outline_rounded_rectangle)
        }
        mView.layoutDayWeek.setOnClickListener {
            mainActivity.viewedDate = date
            mainActivity.setFragment(DayFragment)
        }
        return mView
    }

    companion object : WeekDayCreator {
        @JvmStatic
        override fun createInstance(): WeekDayFragment = WeekDayFragment()

        @JvmStatic
        override fun createInstance(date: LocalDate): WeekDayFragment {
            val result = WeekDayFragment()
            result.date = date
            return result
        }
    }
}

interface WeekDayCreator: InstanceCreator<WeekDayFragment> {
    fun createInstance(localDate: LocalDate): WeekDayFragment
}