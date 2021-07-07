package com.sztorm.notecalendar

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.DrawableContainer.DrawableContainerState
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Switch
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import com.google.android.material.button.MaterialButton
import com.sztorm.notecalendar.helpers.ContextHelper.Companion.getDrawableCompat
import com.sztorm.notecalendar.helpers.ContextHelper.Companion.getPixelsFromDip
import com.sztorm.notecalendar.helpers.DrawableHelper.Companion.wrapCompat
import kotlinx.android.synthetic.main.calendar_week_day_bar.view.*
import picker.ugurtekbas.com.Picker.Picker

class ThemePainter(val values: ThemeValues) {
    fun paintWeekDayItem(view: View) {
        val selector = view.context
            .getDrawableCompat(R.drawable.selector_week_day)!!
            .wrapCompat()
        val selectorStates = (selector.constantState as DrawableContainerState).children
        val selectorPressed = selectorStates[0] as GradientDrawable
        val width = view.context.getPixelsFromDip(3f).toInt()

        selectorPressed.setStroke(width, values.secondaryColor)
        view.background = selector
    }

    fun paintSelectedWeekDayItem(view: View) {
        val selector = view.context
            .getDrawableCompat(R.drawable.selector_week_day_selected)!!
            .wrapCompat()
        val selectorStates = (selector.constantState as DrawableContainerState).children
        val selectorPressed = selectorStates[0] as GradientDrawable
        val selectorUnpressed = selectorStates[1] as GradientDrawable
        val width = view.context.getPixelsFromDip(3f).toInt()

        selectorPressed.setStroke(width, values.secondaryColor)
        selectorUnpressed.setStroke(width, values.primaryColor)

        view.background = selector
    }

    fun paintWindowStatusBar(window: Window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = values.primaryColor
    }

    fun paintNote(view: View) {
        val note: LayerDrawable = view.context
            .getDrawableCompat(R.drawable.bg_note)!!
            .wrapCompat() as LayerDrawable

        note.findDrawableByLayerId(R.id.layerNotePrimary)
            .setTint(values.noteColor)

        note.findDrawableByLayerId(R.id.layerNoteSecondary)
            .setTint(values.noteColorVariant)

        view.background = note
    }

    fun paintButton(button: MaterialButton) {
        button.setTextColor(values.buttonTextColor)
        button.iconTint = values.buttonIconColorStateList
        button.backgroundTintList = values.buttonBackgroundColorStateList
        button.rippleColor = values.buttonRippleColorStateList
    }

    fun paintNavigationButton(button: MaterialButton) {
        button.strokeColor = values.navigationButtonStrokeColorStateList
        button.iconTint = values.navigationButtonIconColorStateList
        button.backgroundTintList = values.navigationButtonBackgroundColorStateList
        button.rippleColor = values.buttonRippleColorStateList
    }

    fun paintOutlinedButton(button: MaterialButton) {
        button.strokeColor = values.outlinedButtonStrokeColorStateList
        button.iconTint = values.outlinedButtonIconColorStateList
        button.rippleColor = values.buttonRippleColorStateList
    }

    fun paintDialogButton(button: MaterialButton) {
        button.setTextColor(values.primaryColor)
        button.rippleColor = values.alertButtonRippleColorStateList
    }

    fun paintTextView(textView: TextView) {
        textView.setTextColor(values.noteTextColor)
        textView.background.setTint(values.secondaryColor)
        textView.highlightColor = values.textHighlightColor

        textView.context
            .getDrawableCompat(R.drawable.cursor_edit_text)!!
            .wrapCompat()
            .setTint(values.secondaryColor)

        textView.context
            .getDrawableCompat(R.drawable.text_select_handle_middle)!!
            .wrapCompat()
            .setTint(values.secondaryColor)

        textView.context
            .getDrawableCompat(R.drawable.text_select_handle_left)!!
            .wrapCompat()
            .setTint(values.secondaryColor)

        textView.context
            .getDrawableCompat(R.drawable.text_select_handle_right)!!
            .wrapCompat()
            .setTint(values.secondaryColor)
    }

    fun paintCalendarDayView(
        textView: TextView,
        isInMonth: Boolean,
        isSelected: Boolean,
        isToday: Boolean,
        hasNote: Boolean) {
        val background: GradientDrawable = (textView.background.mutate() as GradientDrawable)
        var strokeColor: Int = Color.TRANSPARENT
        var strokeWidth = 0
        var textColor: Int = values.textColor
        var backgroundColor: ColorStateList = values.dayViewButtonColorStateList

        if (!isInMonth) {
            textColor = values.inactiveTextColor

            if (hasNote) {
                strokeWidth = textView.context.getPixelsFromDip(4f).toInt()
                strokeColor = ColorUtils.setAlphaComponent(values.noteColor, 255/3)
            }
            if (isToday) {
                textColor = ColorUtils.setAlphaComponent(values.secondaryColor, 255/3)
            }
            background.color = backgroundColor
            background.setStroke(strokeWidth, strokeColor)
            textView.setTextColor(textColor)

            return
        }
        if (hasNote) {
            strokeWidth = textView.context.getPixelsFromDip(4f).toInt()
            strokeColor = values.noteColor
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

    fun paintCaledarDayOfWeekBar(bar: View) {
        bar.setBackgroundColor(values.secondaryColor)
        bar.lblFirstDay.setTextColor(values.buttonTextColor)
        bar.lblSecondDay.setTextColor(values.buttonTextColor)
        bar.lblThirdDay.setTextColor(values.buttonTextColor)
        bar.lblFourthDay.setTextColor(values.buttonTextColor)
        bar.lblFifthDay.setTextColor(values.buttonTextColor)
        bar.lblSixthDay.setTextColor(values.buttonTextColor)
        bar.lblSeventhDay.setTextColor(values.buttonTextColor)
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    fun paintSwitch(switch: Switch) {
        switch.thumbTintList = values.switchThumbColorStateList
        switch.trackTintList = values.switchTrackColorStateList
    }

    fun paintTimePicker(picker: Picker) {
        picker.setCanvasColor(values.backgroundColor)
        picker.setClockColor(ColorUtils.setAlphaComponent(values.secondaryColor, 64))
        picker.setTextColor(values.textColor)
        picker.setDialColor(values.secondaryColor)
    }
}