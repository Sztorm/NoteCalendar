package com.sztorm.notecalendar.platform.permissions

import android.Manifest
import android.os.Build

enum class AppPermission {
    ScheduleExactAlarm,
    PostNotifications;

    val isAlwaysGranted
        get() = when (this) {
            ScheduleExactAlarm ->
                Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

            PostNotifications -> Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        }

    val permissionString
        get() = when (this) {
            ScheduleExactAlarm ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    Manifest.permission.SCHEDULE_EXACT_ALARM
                else "android.permission.SCHEDULE_EXACT_ALARM"

            PostNotifications ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    Manifest.permission.POST_NOTIFICATIONS
                else "android.permission.POST_NOTIFICATIONS"
        }
}