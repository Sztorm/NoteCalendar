package com.sztorm.notecalendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_week_day.view.*
import java.time.LocalDate

class DayListAdapter(
        private val dayItems: List<DayItem>,
        private val mainActivity: MainActivity,
        private val textColors: WeekDayTextColors) :
        RecyclerView.Adapter<DayListAdapter.ViewHolder>() {
    var onItemClick: ((DayItem) -> Unit)? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val dayOfMonthLabel: TextView = view.lblWeekDayOfMonth
        val dayOfWeekLabel: TextView = view.lblWeekDayOfWeek
        val layout: LinearLayout = view.layoutDayWeek
        var dataPosition: Int = -1
        var layoutBackgroundResourceId: Int = R.drawable.selector_week_day

        override fun onClick(view: View) {
            onItemClick?.invoke(dayItems[dataPosition])
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.fragment_week_day, viewGroup, false)

        return ViewHolder(view)
    }

    private fun areRepresentingTheSameDay(dateA: LocalDate, dateB: LocalDate): Boolean
        = dateA.year == dateB.year && dateA.dayOfYear == dateB.dayOfYear

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val dayItem: DayItem = dayItems[position]
        val textColor: Int = textColors.getTextColorOf(dayItem.date.dayOfWeek)
        val viewedDate: LocalDate = mainActivity.viewedDate

        viewHolder.dataPosition = position
        viewHolder.dayOfMonthLabel.text = dayItem.dayOfMonth
        viewHolder.dayOfMonthLabel.setTextColor(textColor)
        viewHolder.dayOfWeekLabel.text = dayItem.dayOfWeek
        viewHolder.dayOfWeekLabel.setTextColor(textColor)
        viewHolder.layout.setOnClickListener(viewHolder)

        if (areRepresentingTheSameDay(viewedDate, dayItem.date)) {
            viewHolder.layout.setBackgroundResource(R.drawable.selector_week_day_selected)
            viewHolder.layoutBackgroundResourceId = R.drawable.selector_week_day_selected
        }
        else if (viewHolder.layoutBackgroundResourceId == R.drawable.selector_week_day_selected){
            viewHolder.layout.setBackgroundResource(R.drawable.selector_week_day)
        }
    }

    override fun getItemCount() = dayItems.size
}
