@file:Suppress("MemberVisibilityCanBePrivate")

package com.sztorm.notecalendar

import java.time.DayOfWeek

class WeekDayTextColors private constructor(val colors: IntArray)  {
    val monday: Int
        get() = colors[0]
    val tuesday: Int
        get() = colors[1]
    val wednesday: Int
        get() = colors[2]
    val thursday: Int
        get() = colors[3]
    val friday: Int
        get() = colors[4]
    val saturday: Int
        get() = colors[5]
    val sunday: Int
        get() = colors[6]

    constructor(monday: Int, tuesday: Int, wednesday: Int, thursday: Int, friday: Int,
                saturday: Int, sunday: Int) : this(IntArray(7)) {
        colors[0] = monday
        colors[1] = tuesday
        colors[2] = wednesday
        colors[3] = thursday
        colors[4] = friday
        colors[5] = saturday
        colors[6] = sunday
    }

    fun getTextColorOf(dayOfWeek: DayOfWeek): Int = colors[dayOfWeek.value - 1]

    fun equals(other: WeekDayTextColors): Boolean = colors contentEquals other.colors

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        return if (other is WeekDayTextColors) equals(other) else false
    }

    override fun hashCode(): Int {
        var hash = 9

        for (color in colors) {
            hash = hash * 13 + color
        }
        return hash
    }

    companion object {
        /**
         * Creates new [WeekDayTextColors] instance out of 7 color values from monday to sunday.
         *
         * Throws [IllegalArgumentException] if [colors] array argument size is not 7.
         */
        fun of(colors: IntArray): WeekDayTextColors {
            if (colors.size != 7) {
                throw IllegalArgumentException("Colors array size must be 7.")
            }
            return WeekDayTextColors(colors)
        }
    }
}