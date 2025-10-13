package com.sztorm.notecalendar

import android.graphics.drawable.*
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.sztorm.timepicker.TwoStepTimePicker

class ThemePainter(val values: ThemeColors) {
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