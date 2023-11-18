@file:Suppress("MemberVisibilityCanBePrivate", "SameParameterValue")

package com.sztorm.notecalendar

import android.app.Activity
import android.app.AlarmManager
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity

class AppPermissionManager(val mainActivity: MainActivity) {
    private val requestPermissionCallbacksByCode =
        Array<ArrayList<(isSuccess: Boolean) -> Unit>>(REQUEST_CODE_COUNT) { ArrayList() }

    private fun requestPermissions(
        permissions: Array<out String>, permissionCode: AppPermissionCode
    ) = ActivityCompat.requestPermissions(mainActivity, permissions, permissionCode.value)

    private fun requestSettingsActivity(actionRequest: String) {
        val appUri =
            Uri.fromParts("package", mainActivity.packageName, null)
        val requestExactAlarmPermissionIntent = Intent().apply {
            action = actionRequest
            data = appUri
        }
        startActivity(mainActivity, requestExactAlarmPermissionIntent, null)
    }

    fun isGranted(permission: String) = ContextCompat
        .checkSelfPermission(mainActivity, permission) == PackageManager.PERMISSION_GRANTED

    fun isGranted(permissionCode: AppPermissionCode): Boolean {
        val permissions: Array<out String> = permissionCode.getPermissionArray()

        return when(permissionCode) {
            AppPermissionCode.NOTIFICATIONS -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val alarmManager =
                        mainActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                    alarmManager.canScheduleExactAlarms() && permissions.all { isGranted(it) }
                }
                else {
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
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
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
                }
                else {
                    if (onResult != null) {
                        requestPermissionCallbacksByCode[permissionCode.value].add(onResult)
                    }
                    requestPermissions(permissions, permissionCode)
                }
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        val callbacks = requestPermissionCallbacksByCode[requestCode]
        val isSuccess = grantResults.all { r -> r == PackageManager.PERMISSION_GRANTED }

        while (callbacks.isNotEmpty()) {
            callbacks.last().invoke(isSuccess)
            callbacks.removeLast()
        }
    }

    companion object {
        private const val REQUEST_CODE_COUNT = 1
    }
}

private class RequestSettingCallback(
    val onResult: (isSuccess: Boolean) -> Unit
) : ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) { }

    override fun onActivityStarted(activity: Activity) { }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onActivityResumed(activity: Activity) {
        activity.unregisterActivityLifecycleCallbacks(this)
        val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (!alarmManager.canScheduleExactAlarms()) {
            onResult(alarmManager.canScheduleExactAlarms())
        }
    }

    override fun onActivityPaused(activity: Activity) { }

    override fun onActivityStopped(activity: Activity) { }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) { }

    override fun onActivityDestroyed(activity: Activity) { }
}