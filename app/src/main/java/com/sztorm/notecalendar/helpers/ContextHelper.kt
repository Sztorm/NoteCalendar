package com.sztorm.notecalendar.helpers

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

class ContextHelper {
    companion object {
        @ColorInt
        fun Context.getColorFromAttr(@AttrRes attrColor: Int): Int {
            val typedValue = TypedValue()
            this.theme.resolveAttribute(attrColor, typedValue, true)
            return typedValue.data
        }

        @ColorInt
        fun Context.getColorCompat(@ColorRes id: Int): Int = ContextCompat.getColor(this, id)

        fun Context.getPixelsFromDip(dp: Float): Float = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)

        fun Context.getDrawableCompat(@DrawableRes id: Int): Drawable?
            = ContextCompat.getDrawable(this, id)

        val Context.isDarkThemeEnabled: Boolean
            get() = (this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                 Configuration.UI_MODE_NIGHT_YES
    }
}