package com.sztorm.notecalendar

import android.Manifest
import android.os.Build

@JvmInline
value class AppPermissionCode(val value: Int) {
    fun getPermissionArray(): Array<out String> = when (this) {
        NOTIFICATIONS -> {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS
                )

                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.SCHEDULE_EXACT_ALARM
                )

                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> arrayOf(
                    Manifest.permission.SCHEDULE_EXACT_ALARM
                )

                else -> emptyArray()
            }
        }

        else -> emptyArray()
    }

    companion object {
        val NOTIFICATIONS
            get() = AppPermissionCode(0)
    }
}