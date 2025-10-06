package com.sztorm.notecalendar.timepickerpreference

import android.content.Context
import android.content.SharedPreferences
import android.text.format.DateFormat.is24HourFormat
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.core.content.edit
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceViewHolder
import com.google.android.material.timepicker.TimeFormat
import com.sztorm.notecalendar.MainActivity
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.timepickerpreference.TimePickerPreference.Time.Companion.asTime
import com.sztorm.timepicker.PickedTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

open class TimePickerPreference : Preference, View.OnClickListener {
    class Time {
        private val value: Int

        val hour: Int
            get() = value and HOUR_BITS
        val minute: Int
            get() = (value and MINUTE_BITS) shr HOUR_BITS_SIZE

        private constructor(value: Int) {
            this.value = value
        }

        constructor(hour: Int, minute: Int) {
            this.value = asInt(hour, minute)
        }

        fun asInt(): Int = value

        fun toLocalTime(): LocalTime = LocalTime.of(hour, minute)

        override fun toString(): String =
            "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"

        companion object {
            // 5 bits = [0, 31] range of values, while [0, 23] is needed
            private const val HOUR_BITS: Int = 0b00000000_00000000_00000000_00011111
            private const val HOUR_BITS_SIZE: Int = 5
            // 6 bits = [0, 63] range of values, while [0, 59] is needed
            private const val MINUTE_BITS: Int = 0b00000000_00000000_00000111_11100000
            private const val MINUTE_BITS_SIZE: Int = 6

            fun asInt(hour: Int, minute: Int): Int = hour or (minute shl HOUR_BITS_SIZE)

            fun Int.asTime() = Time(this)
        }
    }

    companion object {
        val DEFAULT_TIME_RAW: Int = Time.asInt(hour = 8, minute = 0)
        val DEFAULT_TIME: Time = DEFAULT_TIME_RAW.asTime()
    }

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    private lateinit var sharedPrefs: SharedPreferences

    init {
        widgetLayoutResource = R.layout.preference_timepicker
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) :
        super(context, attributeSet, defStyle)

    protected open fun createTimePickerDialog(): TimePickerDialog {
        val time: Time = sharedPrefs.getInt(key, DEFAULT_TIME_RAW).asTime()
        val timeFormat = if (is24HourFormat(context)) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        return TimePickerDialog.Builder()
            .setTimeFormat(timeFormat)
            .setHour(time.hour)
            .setMinute(time.minute)
            .setTitleText(title)
            .build()
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val time: Time = sharedPrefs.getInt(key, DEFAULT_TIME_RAW).asTime()
        val settingView: View = holder.itemView
        val timeValueLabel = holder.findViewById(R.id.timeValue) as TextView

        timeValueLabel.text = formatter.format(LocalTime.of(time.hour, time.minute))
        settingView.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val mainActivity = context as MainActivity
        val dialog = createTimePickerDialog()

        dialog.addOnPositiveButtonClickListener {
            val time: PickedTime = dialog.picker.time
            val preferenceValue = Time(time.hour, time.minute)

            sharedPrefs.edit {
                putInt(key, preferenceValue.asInt())
            }
            onPreferenceChangeListener?.onPreferenceChange(this, preferenceValue)

            val timeValue = v.findViewById<TextView>(R.id.timeValue)
            timeValue.text = formatter.format(preferenceValue.toLocalTime())
        }
        dialog.show(mainActivity.supportFragmentManager, null)
    }
}