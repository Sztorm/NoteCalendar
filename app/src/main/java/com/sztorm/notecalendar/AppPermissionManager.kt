package com.sztorm.notecalendar

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class AppPermissionManager {
    companion object {
        private const val POST_NOTIFICATIONS_FLAG = 0b00000001
        private const val SCHEDULE_EXACT_ALARM_FLAG = 0b00000010

        fun requestScheduleNotificationPermissionsIfNeeded(context: Activity) {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    val canPostNotifications = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                    val canScheduleExactAlarm = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.SCHEDULE_EXACT_ALARM
                    ) == PackageManager.PERMISSION_GRANTED
                    when {
                        !canPostNotifications && !canScheduleExactAlarm -> {
                            ActivityCompat.requestPermissions(
                                context, arrayOf(
                                    Manifest.permission.POST_NOTIFICATIONS,
                                    Manifest.permission.SCHEDULE_EXACT_ALARM
                                ), POST_NOTIFICATIONS_FLAG or SCHEDULE_EXACT_ALARM_FLAG
                            )
                        }
                        !canPostNotifications -> {
                            ActivityCompat.requestPermissions(
                                context,
                                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                                POST_NOTIFICATIONS_FLAG
                            )
                        }
                        !canScheduleExactAlarm -> {
                            ActivityCompat.requestPermissions(
                                context,
                                arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM),
                                SCHEDULE_EXACT_ALARM_FLAG
                            )
                        }
                    }
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    if (ContextCompat.checkSelfPermission(
                            context, Manifest.permission.SCHEDULE_EXACT_ALARM
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            context,
                            arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM),
                            SCHEDULE_EXACT_ALARM_FLAG
                        )
                    }
                }
            }
        }

        fun areScheduleNotificationPermissionsGrantedOnRequest(
            requestCode: Int, grantResults: IntArray
        ): Boolean = when {
            grantResults.isEmpty() -> true
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> when (requestCode) {
                POST_NOTIFICATIONS_FLAG or SCHEDULE_EXACT_ALARM_FLAG,
                POST_NOTIFICATIONS_FLAG,
                SCHEDULE_EXACT_ALARM_FLAG -> grantResults
                    .all { r -> r == PackageManager.PERMISSION_GRANTED }
                else -> true
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                requestCode == SCHEDULE_EXACT_ALARM_FLAG &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED
            }
            else -> true
        }
    }
}