package com.sztorm.notecalendar.calendarview

import android.view.View
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.sztorm.notecalendar.MainActivity
import com.sztorm.notecalendar.NoteData
import com.sztorm.notecalendar.ThemePainter
import java.time.LocalDate
import java.time.Month

class ThemedDayBinder(val mainActivity: MainActivity): DayBinder<DayViewContainer> {
    private var cachedMonthNotesList: List<NoteData> = mainActivity.noteRepository.getByMonth(
        mainActivity.viewedDate.month)
    private var cachedNotesMonth: Month = mainActivity.viewedDate.month
    private val today = LocalDate.now()

    override fun create(view: View) = DayViewContainer(view, mainActivity)

    override fun bind(container: DayViewContainer, day: CalendarDay) {
        val themePainter: ThemePainter = mainActivity.themePainter
        val viewedDate: LocalDate = mainActivity.viewedDate
        val textView = container.textView
        container.day = day
        textView.text = day.date.dayOfMonth.toString()

        if (day.date.month != cachedNotesMonth) {
            cachedNotesMonth = day.date.month
            cachedMonthNotesList = mainActivity.noteRepository.getByMonth(cachedNotesMonth)
        }

        themePainter.paintCalendarDayView(
            textView,
            isInMonth = day.owner == DayOwner.THIS_MONTH,
            isSelected = viewedDate == day.date,
            isToday = today == day.date,
            hasNote = cachedMonthNotesList.any({ n -> n.date == day.date.toString() }))
    }
}