package com.sztorm.notecalendar

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.*
import android.graphics.drawable.DrawableContainer.DrawableContainerState
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.graphics.ColorUtils
import com.google.android.material.button.MaterialButton
import com.sztorm.notecalendar.databinding.CalendarWeekDayBarBinding
import com.sztorm.notecalendar.helpers.ContextHelper.Companion.getDrawableCompat
import com.sztorm.notecalendar.helpers.ContextHelper.Companion.getPixelsFromDip
import com.sztorm.notecalendar.helpers.ContextHelper.Companion.isDarkThemeEnabled
import com.sztorm.timepicker.TwoStepTimePicker

class ThemePainter(val values: ThemeValues) {
    // Changing text cursor, select handles programmatically is allowed from API >= 29, so caching
    // modified shared drawables may help to maintain drawables with changed color in lower API
    // levels.
    private lateinit var textCursor: Drawable
    private lateinit var textSelectHandleMiddle: Drawable
    private lateinit var textSelectHandleLeft: Drawable
    private lateinit var textSelectHandleRight: Drawable

    fun paintWeekDayItem(view: View) {
        val selector = view.context.getDrawableCompat(R.drawable.selector_week_day)!!
        val selectorStates = (selector.constantState as DrawableContainerState).children
        val selectorPressed = selectorStates[0] as GradientDrawable
        val width = view.context.getPixelsFromDip(3f).toInt()

        selectorPressed.setStroke(width, values.secondaryColor)
        view.background = selector
    }

    fun paintSelectedWeekDayItem(view: View) {
        val selector = view.context.getDrawableCompat(R.drawable.selector_week_day_selected)!!
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

    fun paintNote(noteHolder: View) {
        val note = noteHolder.context.getDrawableCompat(R.drawable.bg_note)!! as LayerDrawable

        note.getDrawable(0).setTint(values.noteColor)
        note.getDrawable(1).setTint(values.noteColorVariant)

        noteHolder.background = note
    }

    fun paintBackArrowIcon(iconView: ImageView) {
        val icon: LayerDrawable = iconView.drawable as LayerDrawable

        icon.getDrawable(0).setTintList(values.backArrowIconRippleColorStateList)
        icon.getDrawable(1).setTint(values.textColor)
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

    fun paintEditText(editText: EditText) {
        textCursor = editText.context
            .getDrawableCompat(R.drawable.cursor_edit_text)!!
        textSelectHandleMiddle = editText.context
            .getDrawableCompat(R.drawable.text_select_handle_middle)!!
        textSelectHandleLeft = editText.context
            .getDrawableCompat(R.drawable.text_select_handle_left)!!
        textSelectHandleRight = editText.context
            .getDrawableCompat(R.drawable.text_select_handle_right)!!

        textCursor.setTint(values.secondaryColor)
        textSelectHandleMiddle.setTint(values.secondaryColor)
        textSelectHandleLeft.setTint(values.secondaryColor)
        textSelectHandleRight.setTint(values.secondaryColor)
        editText.setTextColor(values.noteTextColor)
        editText.backgroundTintList = ColorStateList.valueOf(values.secondaryColor)
        editText.highlightColor = values.textHighlightColor
        editText.invalidate()
    }

    fun paintCalendarDayView(
        textView: TextView,
        isInMonth: Boolean,
        isSelected: Boolean,
        isToday: Boolean,
        hasNote: Boolean) {
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
                    if (isDarkThemeEnabled) values.noteColorVariant else values.noteColor, 255/3)
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

    fun paintCaledarDayOfWeekBar(weekDayBinding: CalendarWeekDayBarBinding) {
        weekDayBinding.root.setBackgroundColor(values.secondaryColor)
        weekDayBinding.lblFirstDay.setTextColor(values.buttonTextColor)
        weekDayBinding.lblSecondDay.setTextColor(values.buttonTextColor)
        weekDayBinding.lblThirdDay.setTextColor(values.buttonTextColor)
        weekDayBinding.lblFourthDay.setTextColor(values.buttonTextColor)
        weekDayBinding.lblFifthDay.setTextColor(values.buttonTextColor)
        weekDayBinding.lblSixthDay.setTextColor(values.buttonTextColor)
        weekDayBinding.lblSeventhDay.setTextColor(values.buttonTextColor)
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