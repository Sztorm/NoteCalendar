package com.sztorm.notecalendar

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.compose.ui.graphics.Color as CColor
import androidx.annotation.ColorInt
import androidx.compose.material3.lightColorScheme
import androidx.core.graphics.ColorUtils
import com.sztorm.notecalendar.helpers.ColorStateListHelper
import java.time.DayOfWeek

class ThemeColors(
    @ColorInt val primaryColor: Int,
    @ColorInt val secondaryColor: Int,
    @ColorInt val inactiveItemColor: Int,
    @ColorInt val inactiveItemColorVariant: Int,
    @ColorInt val noteColor: Int,
    @ColorInt val noteColorVariant: Int,
    @ColorInt val textColor: Int,
    @ColorInt val buttonTextColor: Int,
    @ColorInt val noteTextColor: Int,
    @ColorInt val backgroundColor: Int
) {
    val colorScheme = lightColorScheme().copy(
        primary = CColor(primaryColor),
        secondary = CColor(secondaryColor),
        background = CColor(backgroundColor),
        surface = CColor(backgroundColor),
        surfaceVariant = CColor(backgroundColor),
        surfaceTint = CColor(backgroundColor),
        surfaceContainer = CColor(backgroundColor)
    )
    val selectBackgroundColor: Int =
        if (ColorUtils.calculateLuminance(backgroundColor) < 0.5) 0x40ffffff
        else 0x40000000
    val backgroundColorVariant: Int = ColorUtils.blendARGB(
        backgroundColor, 0xffffffff.toInt(), 0.06f) // TODO: make it modifiable from settigns
    val textHighlightColor: Int = ColorUtils.setAlphaComponent(secondaryColor, 255 / 3)
    val inactiveTextColor: Int = ColorUtils.setAlphaComponent(textColor, 255 / 3)
    val summaryTextColor: Int = ColorUtils.setAlphaComponent(textColor, 210)
    val buttonRippleColorStateList: ColorStateList = ColorStateListHelper
        .createRippleColorStateList(
            color = ColorUtils.setAlphaComponent(primaryColor, 255 / 10)
        )

    val alertButtonRippleColorStateList: ColorStateList = ColorStateListHelper
        .createRippleColorStateList(
            color = ColorUtils.setAlphaComponent(primaryColor, 255 / 7)
        )

    val buttonIconColorStateList: ColorStateList = ColorStateListHelper
        .createButtonColorStateList(enabledColor = buttonTextColor)

    val buttonBackgroundColorStateList: ColorStateList = ColorStateListHelper
        .createButtonColorStateList(
            enabledColor = primaryColor,
            disabledColor = inactiveItemColor
        )

    val navigationButtonStrokeColorStateList: ColorStateList = ColorStateListHelper
        .createToggleColorStateList(
            checkedColor = primaryColor,
            uncheckedColor = inactiveItemColorVariant
        )

    val navigationButtonIconColorStateList: ColorStateList = ColorStateListHelper
        .createToggleColorStateList(
            checkedColor = primaryColor,
            uncheckedColor = inactiveItemColor
        )

    val navigationButtonBackgroundColorStateList: ColorStateList = ColorStateListHelper
        .createToggleColorStateList(
            checkedColor = ColorUtils.setAlphaComponent(primaryColor, 255 / 10)
        )

    val outlinedButtonStrokeColorStateList: ColorStateList = ColorStateListHelper
        .createButtonColorStateList(
            enabledColor = primaryColor,
            disabledColor = inactiveItemColor
        )

    val outlinedButtonIconColorStateList: ColorStateList = ColorStateListHelper
        .createButtonColorStateList(
            enabledColor = primaryColor,
            disabledColor = inactiveItemColor
        )

    val switchThumbColorStateList: ColorStateList = ColorStateListHelper
        .createToggleColorStateList(
            checkedColor = secondaryColor,
            uncheckedColor = inactiveItemColor
        )

    val switchTrackColorStateList: ColorStateList = ColorStateListHelper
        .createToggleColorStateList(
            checkedColor = ColorUtils.setAlphaComponent(secondaryColor, 128),
            uncheckedColor = ColorUtils.setAlphaComponent(inactiveItemColor, 128)
        )

    val dayViewButtonColorStateList: ColorStateList = ColorStateListHelper
        .createClickableButtonColorStateList(
            pressedColor = inactiveItemColor,
            unpressedColor = Color.TRANSPARENT
        )

    val radioButtonTintList: ColorStateList = ColorStateListHelper
        .createToggleColorStateList(
            checkedColor = secondaryColor,
            uncheckedColor = inactiveItemColor
        )

    val dayViewSelectedButtonColorStateList: ColorStateList = ColorStateListHelper
        .createClickableButtonColorStateList(
            pressedColor = inactiveItemColor,
            unpressedColor = secondaryColor
        )

    val backArrowIconRippleColorStateList: ColorStateList = ColorStateListHelper
        .createClickableButtonColorStateList(
            pressedColor = ColorUtils.setAlphaComponent(textColor, 255 / 7),
            unpressedColor = Color.TRANSPARENT
        )

    fun getTextColorOf(dayOfWeek: DayOfWeek, firstDayOfWeek: DayOfWeek): Int {
        val sixthDayOfWeek: DayOfWeek = firstDayOfWeek - 2
        val seventhDayOfWeek: DayOfWeek = firstDayOfWeek - 1

        return when (dayOfWeek) {
            seventhDayOfWeek -> primaryColor
            sixthDayOfWeek -> secondaryColor
            else -> textColor
        }
    }
}