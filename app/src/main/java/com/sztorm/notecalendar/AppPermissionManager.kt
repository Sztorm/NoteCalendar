@file:Suppress("MemberVisibilityCanBePrivate", "SameParameterValue")

package com.sztorm.notecalendar

import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlin.collections.removeLast as removeLastKt

class AppPermissionManager(val mainActivity: MainActivity) {
    private val requestPermissionCallbacksByCode =
        Array<ArrayList<(isSuccess: Boolean) -> Unit>>(size = 1) { ArrayList() }

    private fun requestPermissions(
        permissions: Array<out String>, permissionCode: AppPermissionCode
    ) = ActivityCompat.requestPermissions(mainActivity, permissions, permissionCode.value)

    private fun requestSettingsActivity(actionRequest: String) {
        val appUri = Uri.fromParts("package", mainActivity.packageName, null)
        val requestExactAlarmPermissionIntent = Intent().apply {
            action = actionRequest
            data = appUri
        }
        mainActivity.startActivity(requestExactAlarmPermissionIntent)
    }

    fun isGranted(permission: String) = ContextCompat
        .checkSelfPermission(mainActivity, permission) == PackageManager.PERMISSION_GRANTED

    fun isGranted(permissionCode: AppPermissionCode): Boolean {
        val permissions: Array<out String> = permissionCode.getPermissionArray()

        return when (permissionCode) {
            AppPermissionCode.NOTIFICATIONS -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val alarmManager =
                        mainActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                    alarmManager.canScheduleExactAlarms() && permissions.all { isGranted(it) }
                } else {
                    permissions.all { isGranted(it) }
                }
            }

            else -> permissions.all { isGranted(it) }
        }
    }

    fun requestUngrantedPermissions(
        permissionCode: AppPermissionCode, onResult: ((isSuccess: Boolean) -> Unit)? = null
    ) {
        val permissions: Array<out String> = permissionCode
            .getPermissionArray()
            .filter { !isGranted(it) }
            .toTypedArray()

        when (permissionCode) {
            AppPermissionCode.NOTIFICATIONS -> {
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                        if (onResult == null) {
                            val alarmManager = mainActivity
                                .getSystemService(Context.ALARM_SERVICE) as AlarmManager

                            if (!alarmManager.canScheduleExactAlarms()) {
                                requestSettingsActivity(
                                    Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                                )
                            }
                            if (permissions.isNotEmpty()) {
                                requestPermissions(permissions, permissionCode)
                            }
                        } else {
                            val alarmManager = mainActivity
                                .getSystemService(Context.ALARM_SERVICE) as AlarmManager

                            if (!alarmManager.canScheduleExactAlarms()) {
                                if (permissions.isEmpty()) {
                                    val requestSettingCallback = RequestSettingCallback(onResult)
                                    mainActivity.registerActivityLifecycleCallbacks(
                                        requestSettingCallback
                                    )
                                }
                                requestSettingsActivity(
                                    Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                                )
                            }
                            if (permissions.isNotEmpty()) {
                                requestPermissionCallbacksByCode[permissionCode.value]
                                    .add(onResult)
                                requestPermissions(permissions, permissionCode)
                            }
                        }
                    }

                    else -> {
                        if (onResult != null) {
                            requestPermissionCallbacksByCode[permissionCode.value].add(onResult)
                        }
                        if (permissions.isNotEmpty()) {
                            requestPermissions(permissions, permissionCode)
                        }
                    }
                }
            }

            else -> {
                if (permissions.isEmpty()) {
                    return
                } else {
                    if (onResult != null) {
                        requestPermissionCallbacksByCode[permissionCode.value].add(onResult)
                    }
                    requestPermissions(permissions, permissionCode)
                }
            }
        }
    }

    @Suppress("unused")
    fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        val callbacks = requestPermissionCallbacksByCode[requestCode]
        val isSuccess = grantResults.all { r -> r == PackageManager.PERMISSION_GRANTED }

        while (callbacks.isNotEmpty()) {
            callbacks.last().invoke(isSuccess)
            callbacks.removeLastKt()
        }
    }
}

private class RequestSettingCallback(
    val onResult: (isSuccess: Boolean) -> Unit
) : DefaultActivityLifecycleCallbacks {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onActivityResumed(activity: Activity) {
        activity.unregisterActivityLifecycleCallbacks(this)
        val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        onResult(alarmManager.canScheduleExactAlarms())
    }
}

