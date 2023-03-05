package com.sztorm.notecalendar.helpers

import android.content.Intent
import android.os.Build
import android.os.Parcelable

class IntentHelper {
    companion object {
        @Suppress("DEPRECATION")
        fun <T : Parcelable> Intent.getParcelableExtraCompat(
            name: String, clazz: Class<T>
        ): T? {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                return getParcelableExtra(name, clazz)
            }
            return getParcelableExtra(name)
        }
    }
}