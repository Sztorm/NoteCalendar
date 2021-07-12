package com.sztorm.notecalendar

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sztorm.notecalendar.helpers.DateHelper.Companion.seventhDayOfWeek
import com.sztorm.notecalendar.helpers.DateHelper.Companion.toLocalizedString
import kotlinx.android.synthetic.main.fragment_week.view.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.*
import kotlin.collections.ArrayDeque

class WeekFragment : Fragment() {
    private lateinit var mView: View
    private lateinit var adapter: DayListAdapter
    private lateinit var mainActivity: MainActivity
    private lateinit var textColors: WeekDayTextColors
    private val dayItems: ArrayDeque<DayItem> = ArrayDeque(initialCapacity = CACHED_DAY_ITEMS_COUNT)

    inner class WeekDayOnScrollListener: RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val llm = recyclerView.layoutManager as LinearLayoutManager
            val directionDown = 1
            val directionUp: Int = -1

            if (!recyclerView.canScrollVertically(directionDown)) {
                val firstVisiblePos: Int = llm.findFirstVisibleItemPosition()

                loadNextDayItems(mView, LOADED_DAY_ITEMS_COUNT)
                adapter.notifyDataSetChanged()
                recyclerView.scrollToPosition(firstVisiblePos - LOADED_DAY_ITEMS_COUNT)
            }
            else if (!recyclerView.canScrollVertically(directionUp)) {
                val lastVisiblePos: Int = llm.findLastVisibleItemPosition()

                loadPrevDayItems(mView, LOADED_DAY_ITEMS_COUNT)
                adapter.notifyDataSetChanged()
                recyclerView.scrollToPosition(lastVisiblePos + LOADED_DAY_ITEMS_COUNT)
            }
            super.onScrolled(recyclerView, dx, dy)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }

    private fun initDayItems(view: View, count: Int) {
        var date: LocalDate = mainActivity.viewedDate.minusDays(count / 2L)

        for (i: Int in 0..count) {
            val dayItem = DayItem(
                date,
                date.dayOfMonth.toString(),
                date.dayOfWeek.toLocalizedString(view.context))

            dayItems.addLast(dayItem)
            date = date.plusDays(1)
        }
    }

    private fun loadNextDayItems(view: View, count: Int) {
        var nextDate: LocalDate = dayItems.last().date

        for (i: Int in 0..count) {
            nextDate = nextDate.plusDays(1)

            val dayItem = DayItem(
                    nextDate,
                    nextDate.dayOfMonth.toString(),
                    nextDate.dayOfWeek.toLocalizedString(view.context))

            dayItems.removeFirst()
            dayItems.addLast(dayItem)
        }
    }

    private fun loadPrevDayItems(view: View, count: Int) {
        var prevDate: LocalDate = dayItems.first().date

        for (i: Int in 0..count) {
            prevDate = prevDate.minusDays(1)

            val dayItem = DayItem(
                    prevDate,
                    prevDate.dayOfMonth.toString(),
                    prevDate.dayOfWeek.toLocalizedString(view.context))

            dayItems.removeLast()
            dayItems.addFirst(dayItem)
        }
    }

    private fun setTheme() {
        val themePainter: ThemePainter = mainActivity.themePainter
        val themeValues: ThemeValues = themePainter.values
        val firstDayOfWeek: DayOfWeek = mainActivity.settingsReader.firstDayOfWeek
        val seventhDayOfWeek: DayOfWeek = firstDayOfWeek - 1
        val sixthDayOfWeek: DayOfWeek = firstDayOfWeek - 2
        val colors = IntArray(size = 7)

        for (i in 0..6) {
            colors[i] = when(i + 1) {
                seventhDayOfWeek.value -> themeValues.primaryColor
                sixthDayOfWeek.value -> themeValues.secondaryColor
                else -> themeValues.textColor
            }
        }
        textColors = WeekDayTextColors.of(colors)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        mView = inflater.inflate(R.layout.fragment_week, container, false)
        setTheme()
        initDayItems(mView, CACHED_DAY_ITEMS_COUNT)
        adapter = DayListAdapter(dayItems, mainActivity, textColors)
        adapter.onItemClick = {
            mainActivity.setMainFragment(DayFragment, date = it.date)
        }
        val recyclerView: RecyclerView = mView.weekDayFragmentContainer
        recyclerView.layoutManager = LinearLayoutManager(mView.context)
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(WeekDayOnScrollListener())
        recyclerView.scrollToPosition(CACHED_DAY_ITEMS_COUNT / 2)

        return mView
    }

    companion object : MainFragmentCreator<WeekFragment> {
        private const val CACHED_DAY_ITEMS_COUNT: Int = 45
        private const val LOADED_DAY_ITEMS_COUNT: Int = 30

        @JvmStatic
        override fun createInstance(): WeekFragment = WeekFragment()

        @JvmStatic
        override val fragmentType: MainActivity.MainFragmentType
                = MainActivity.MainFragmentType.WEEK_FRAGMENT
    }
}