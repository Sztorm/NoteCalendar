package com.sztorm.notecalendar

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.*
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.sztorm.notecalendar.helpers.ContextHelper.Companion.getPixelsFromDip
import com.sztorm.notecalendar.helpers.ContextHelper.Companion.isDarkThemeEnabled
import com.sztorm.timepicker.TwoStepTimePicker

class ThemePainter(val values: ThemeValues) {
    fun paintStatusBarAndSetSystemInsets(
        window: Window, navigation: MaterialButtonToggleGroup, fragmentContainer: LinearLayout
    ) {
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, insets ->
            val systemInsets = insets.getInsets(
                WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars()
            )
            view.setBackgroundColor(values.primaryColor)
            view.setPadding(0, systemInsets.top, 0, 0)

            navigation.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                setMargins(systemInsets.left, 0, systemInsets.right, systemInsets.bottom)
            }
            fragmentContainer.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                setMargins(systemInsets.left, 0, systemInsets.right, systemInsets.bottom)
            }
            insets
        }
    }

    fun paintBackArrowIcon(iconView: ImageView) {
        val icon: LayerDrawable = iconView.drawable as LayerDrawable

        icon.getDrawable(0).setTintList(values.backArrowIconRippleColorStateList)
        icon.getDrawable(1).setTint(values.textColor)
    }

    fun paintNavigationButton(button: MaterialButton) {
        button.strokeColor = values.navigationButtonStrokeColorStateList
        button.iconTint = values.navigationButtonIconColorStateList
        button.backgroundTintList = values.navigationButtonBackgroundColorStateList
        button.rippleColor = values.buttonRippleColorStateList
    }

    fun paintDialogButton(button: MaterialButton) {
        button.setTextColor(values.primaryColor)
        button.rippleColor = values.alertButtonRippleColorStateList
    }

    fun paintCalendarDayView(
        textView: TextView,
        isInMonth: Boolean,
        isSelected: Boolean,
        isToday: Boolean,
        hasNote: Boolean
    ) {
        val background: GradientDrawable = (textView.background.mutate() as GradientDrawable)
        val isDarkThemeEnabled: Boolean = textView.context.isDarkThemeEnabled
        var strokeColor: Int = Color.TRANSPARENT
        var strokeWidth = 0
        var textColor: Int = values.textColor
        var backgroundColor: ColorStateList = values.dayViewButtonColorStateList

        if (!isInMonth) {
            textColor = values.inactiveTextColor

            if (hasNote) {
                strokeWidth = textView.context.getPixelsFromDip(4f).toInt()
                strokeColor = ColorUtils.setAlphaComponent(
                    if (isDarkThemeEnabled) values.noteColorVariant else values.noteColor, 255 / 3
                )
            }
            if (isToday) {
                textColor = ColorUtils.setAlphaComponent(values.secondaryColor, 255 / 3)
            }
            background.color = backgroundColor
            background.setStroke(strokeWidth, strokeColor)
            textView.setTextColor(textColor)

            return
        }
        if (hasNote) {
            strokeWidth = textView.context.getPixelsFromDip(4f).toInt()
            strokeColor = if (isDarkThemeEnabled) values.noteColorVariant else values.noteColor
        }
        if (isToday) {
            textColor = values.secondaryColor
        }
        if (isSelected) {
            textColor = values.buttonTextColor
            backgroundColor = values.dayViewSelectedButtonColorStateList
        }
        background.color = backgroundColor
        background.setStroke(strokeWidth, strokeColor)
        textView.setTextColor(textColor)
    }

    fun paintBackground(view: View) {
        view.setBackgroundColor(values.backgroundColor)
    }

    fun paintSwitch(switch: SwitchCompat) {
        switch.thumbTintList = values.switchThumbColorStateList
        switch.trackTintList = values.switchTrackColorStateList
    }

    fun paintRadio(radio: RadioButton) {
        radio.buttonTintList = values.radioButtonTintList
    }

    fun paintTimePicker(picker: TwoStepTimePicker) {
        picker.canvasColor = values.backgroundColor
        picker.clockFaceColor = values.backgroundColor
        picker.trackColor = values.textColor
        picker.pointerColor = values.secondaryColor
        picker.textColor = values.textColor
        picker.pickedTextColor = values.secondaryColor
    }
}