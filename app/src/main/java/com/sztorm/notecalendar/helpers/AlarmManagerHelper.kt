package com.sztorm.notecalendar.helpers

import android.app.AlarmManager
import android.app.PendingIntent
import android.os.Build

class AlarmManagerHelper private constructor() {
    companion object {
        /**
         * API level 31 (S) and greater: Checks if [AlarmManager.canScheduleExactAlarms] then calls
         * [AlarmManager.setExactAndAllowWhileIdle].
         *
         * API level 23 (M) and greater: Calls [AlarmManager.setExactAndAllowWhileIdle].
         *
         * Lower API levels: Calls [AlarmManager.setExact].
         **/
        fun AlarmManager.setExactAndAllowWhileIdleCompat(
            type: Int, triggerAtMillis: Long, operation: PendingIntent
        ) {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
                if (canScheduleExactAlarms()) {
                    setExactAndAllowWhileIdle(type, triggerAtMillis, operation)
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
                    setExactAndAllowWhileIdle(type, triggerAtMillis, operation)
                else -> setExact(type, triggerAtMillis, operation)
            }
        }
    }
}