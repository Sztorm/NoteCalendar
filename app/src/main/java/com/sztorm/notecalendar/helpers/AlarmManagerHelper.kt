package com.sztorm.notecalendar.helpers

import android.app.AlarmManager
import android.app.PendingIntent
import android.os.Build

class AlarmManagerHelper private constructor() {
    companion object {
        /**
         * Calls [AlarmManager.setExact] for API level < 23 (M) and
         * [AlarmManager.setExactAndAllowWhileIdle] for the rest.
         **/
        fun AlarmManager.setExactAndAllowWhileIdleCompat(
            type: Int, triggerAtMillis: Long, operation: PendingIntent)
                = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            setExact(type, triggerAtMillis,operation)
        }
        else {
            setExactAndAllowWhileIdle(type, triggerAtMillis, operation)
        }
    }
}