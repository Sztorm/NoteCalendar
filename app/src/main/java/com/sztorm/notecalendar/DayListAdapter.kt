package com.sztorm.notecalendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sztorm.notecalendar.databinding.FragmentWeekDayBinding
import java.time.LocalDate

class DayListAdapter(
        private val dayItems: List<DayItem>,
        private val mainActivity: MainActivity,
        private val textColors: WeekDayTextColors) :
        RecyclerView.Adapter<DayListAdapter.ViewHolder>() {
    var onItemClick: ((DayItem) -> Unit)? = null

    inner class ViewHolder(binding: FragmentWeekDayBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        val dayOfMonthLabel: TextView = binding.lblWeekDayOfMonth
        val dayOfWeekLabel: TextView = binding.lblWeekDayOfWeek
        val layout: LinearLayout = binding.layoutDayWeek
        var dataPosition: Int = -1

        override fun onClick(view: View) {
            onItemClick?.invoke(dayItems[dataPosition])
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding = FragmentWeekDayBinding.inflate(inflater, viewGroup, false)

        return ViewHolder(binding)
    }

    private fun areRepresentingTheSameDay(dateA: LocalDate, dateB: LocalDate): Boolean
        = dateA.year == dateB.year && dateA.dayOfYear == dateB.dayOfYear

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val dayItem: DayItem = dayItems[position]
        val dayOfMonthTextColor: Int = textColors.getTextColorOf(dayItem.date.dayOfWeek)
        val dayOfWeekTextColor: Int = mainActivity.themePainter.values.textColor
        val viewedDate: LocalDate = mainActivity.viewedDate

        viewHolder.dataPosition = position
        viewHolder.dayOfMonthLabel.text = dayItem.dayOfMonth
        viewHolder.dayOfMonthLabel.setTextColor(dayOfMonthTextColor)
        viewHolder.dayOfWeekLabel.text = dayItem.dayOfWeek
        viewHolder.dayOfWeekLabel.setTextColor(dayOfWeekTextColor)
        viewHolder.layout.setOnClickListener(viewHolder)

        if (areRepresentingTheSameDay(viewedDate, dayItem.date)) {
            mainActivity.themePainter.paintSelectedWeekDayItem(viewHolder.layout)
        }
        else {
            mainActivity.themePainter.paintWeekDayItem(viewHolder.layout)
        }
    }

    override fun getItemCount() = dayItems.size
}
