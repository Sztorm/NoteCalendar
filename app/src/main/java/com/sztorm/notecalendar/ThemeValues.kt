package com.sztorm.notecalendar

import android.content.res.ColorStateList
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import com.sztorm.notecalendar.helpers.ColorStateListHelper

class ThemeValues(
    @ColorInt val primaryColor: Int,
    @ColorInt val secondaryColor: Int,
    @ColorInt val inactiveItemColor: Int,
    @ColorInt val inactiveItemColorVariant: Int,
    @ColorInt val noteColor: Int,
    @ColorInt val noteColorVariant: Int,
    @ColorInt val textColor: Int,
    @ColorInt val buttonTextColor: Int,
    @ColorInt val noteTextColor: Int,
    @ColorInt val backgroundColor: Int) {
    val textHighlightColor: Int = ColorUtils.setAlphaComponent(secondaryColor, 255/3)

    val buttonRippleColorStateList: ColorStateList = ColorStateListHelper
        .createRippleColorStateList(
            color = ColorUtils.setAlphaComponent(primaryColor, 255/10))

    val alertButtonRippleColorStateList: ColorStateList = ColorStateListHelper
        .createRippleColorStateList(
            color = ColorUtils.setAlphaComponent(primaryColor, 255/7))

    val buttonIconColorStateList: ColorStateList = ColorStateListHelper
        .createButtonColorStateList(enabledColor = buttonTextColor)

    val buttonBackgroundColorStateList: ColorStateList = ColorStateListHelper
        .createButtonColorStateList(
            enabledColor = primaryColor,
            disabledColor = inactiveItemColor)

    val navigationButtonStrokeColorStateList: ColorStateList = ColorStateListHelper
        .createToggleColorStateList(
            checkedColor = primaryColor,
            uncheckedColor = inactiveItemColorVariant)

    val navigationButtonIconColorStateList: ColorStateList = ColorStateListHelper
        .createToggleColorStateList(
            checkedColor = primaryColor,
            uncheckedColor = inactiveItemColor)

    val navigationButtonBackgroundColorStateList: ColorStateList = ColorStateListHelper
        .createToggleColorStateList(
            checkedColor = ColorUtils.setAlphaComponent(primaryColor, 255/10))

    val outlinedButtonStrokeColorStateList: ColorStateList = ColorStateListHelper
        .createButtonColorStateList(
            enabledColor = primaryColor,
            disabledColor = inactiveItemColor)

    val outlinedButtonIconColorStateList: ColorStateList = ColorStateListHelper
        .createButtonColorStateList(
            enabledColor = primaryColor,
            disabledColor = inactiveItemColor)

    val switchThumbColorStateList: ColorStateList = ColorStateListHelper
        .createToggleColorStateList(
            checkedColor = secondaryColor,
            uncheckedColor = inactiveItemColor)

    val switchTrackColorStateList: ColorStateList = ColorStateListHelper
        .createToggleColorStateList(
            checkedColor = secondaryColor,
            uncheckedColor = inactiveItemColor)
}