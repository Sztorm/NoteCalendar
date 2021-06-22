package com.sztorm.notecalendar.helpers

import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.DrawableCompat

class DrawableHelper {
    companion object {
        fun Drawable.wrapCompat(): Drawable
            = DrawableCompat.wrap(this)
    }
}