package com.sztorm.notecalendar.helpers

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
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

        fun Context.getPixelsFromDip(dp: Float): Float = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)

        fun Context.getDrawableCompat(@DrawableRes id: Int): Drawable?
            = ContextCompat.getDrawable(this, id)
    }
}