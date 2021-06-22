package com.sztorm.notecalendar.helpers

import android.content.res.ColorStateList

class ColorStateListHelper {
    companion object {
        val EMPTY = ColorStateList(
            Array(size = 0) { IntArray(size = 0) }, IntArray(size = 0))

        fun createToggleColorStateList(checkedColor: Int, uncheckedColor: Int): ColorStateList {
            val states: Array<IntArray> = arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked))
            val colors = intArrayOf(
                checkedColor,
                uncheckedColor)

            return ColorStateList(states, colors)
        }

        fun createToggleColorStateList(checkedColor: Int): ColorStateList {
            val states: Array<IntArray> = arrayOf(intArrayOf(android.R.attr.state_checked))
            val colors = intArrayOf(checkedColor)

            return ColorStateList(states, colors)
        }

        fun createButtonColorStateList(enabledColor: Int, disabledColor: Int): ColorStateList {
            val states: Array<IntArray> = arrayOf(
                intArrayOf(android.R.attr.state_enabled),
                intArrayOf(-android.R.attr.state_enabled))
            val colors = intArrayOf(enabledColor, disabledColor)

            return ColorStateList(states, colors)
        }

        fun createButtonColorStateList(enabledColor: Int): ColorStateList {
            val states: Array<IntArray> = arrayOf(intArrayOf(android.R.attr.state_enabled))
            val colors = intArrayOf(enabledColor)

            return ColorStateList(states, colors)
        }

        fun createRippleColorStateList(color: Int): ColorStateList {
            val states: Array<IntArray> = arrayOf(
                intArrayOf(android.R.attr.state_pressed),
                intArrayOf(-android.R.attr.state_pressed))
            val colors = intArrayOf(color, color)

            return ColorStateList(states, colors)
        }
    }
}