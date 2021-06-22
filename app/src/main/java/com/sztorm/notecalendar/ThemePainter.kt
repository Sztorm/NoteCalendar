package com.sztorm.notecalendar

import android.annotation.SuppressLint
import android.graphics.drawable.DrawableContainer.DrawableContainerState
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.CalendarView
import android.widget.Switch
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.sztorm.notecalendar.helpers.ContextHelper.Companion.getDrawableCompat
import com.sztorm.notecalendar.helpers.ContextHelper.Companion.getPixelsFromDip
import com.sztorm.notecalendar.helpers.DrawableHelper.Companion.wrapCompat

class ThemePainter(val values: ThemeValues) {
    fun paintCalendar(calendarView: CalendarView) {

    }

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

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    fun paintSwitch( switch: Switch) {
        switch.thumbTintList = values.switchThumbColorStateList
        switch.trackTintList = values.switchTrackColorStateList
    }
}