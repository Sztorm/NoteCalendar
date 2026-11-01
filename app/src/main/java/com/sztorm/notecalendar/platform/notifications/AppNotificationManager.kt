package com.sztorm.notecalendar.platform.notifications

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.sztorm.notecalendar.MainActivity
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.core.common.setExactAndAllowWhileIdleCompat
import com.sztorm.notecalendar.core.common.stableHash
import com.sztorm.notecalendar.core.logging.AppLogger
import com.sztorm.notecalendar.core.logging.LogTags
import com.sztorm.notecalendar.domain.models.ReminderNote
import com.sztorm.notecalendar.platform.permissions.AppPermission
import com.sztorm.notecalendar.platform.permissions.AppPermissionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar

class AppNotificationManager(val context: Context, val logger: AppLogger) {
    private val appContext = context.applicationContext

    private fun createNotificationChannel(name: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID_NAME, name, importance)
            channel.enableLights(true)
            channel.description = description
            channel.lightColor = Color.BLUE
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

            val notificationManager: NotificationManager = appContext
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(note: ReminderNote): Notification {
        createNotificationChannel(
            appContext.getString(R.string.Notification_Note_ChannelName),
            appContext.getString(R.string.Notification_Note_ChannelDescription)
        )
        val intent = Intent(appContext, MainActivity::class.java)
            .putExtra(NotificationIntentKeys.EXTRA_NOTIFICATION_LAUNCH_DAY_SCREEN, true)
            .putExtra(NotificationIntentKeys.EXTRA_NOTE_DATE, note.date.toString())
        val activity = PendingIntent.getActivity(
            appContext,
            note.date.stableHash(),
            intent,
            getIntentUpdateCurrentFlags()
        )
        val builder = NotificationCompat
            .Builder(appContext, NOTIFICATION_CHANNEL_ID_NAME)
            .setContentTitle(
                appContext
                    .getString(R.string.Notification_NoteFrom_Day)
                    .format(note.date.toString())
            )
            .setContentText(note.text)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.icon_outline_rounded_note)
            .setContentIntent(activity)

        return builder.build()
    }

    private fun scheduleNotification(note: ReminderNote) {
        val dateTime = note.reminderDateTime
        val id = note.date.stableHash()
        val notification = createNotification(note)
        val notificationIntent =
            Intent(appContext, NoteNotificationReceiver::class.java)
                .putExtra(NotificationIntentKeys.EXTRA_NOTIFICATION, notification)
                .putExtra(NotificationIntentKeys.EXTRA_NOTE_DATE_ID, id)
        val pendingIntent = PendingIntent.getBroadcast(
            appContext,
            id,
            notificationIntent,
            getIntentUpdateCurrentFlags()
        )
        val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExactAndAllowWhileIdleCompat(
            AlarmManager.RTC_WAKEUP,
            dateTime.toInstant().toEpochMilli(),
            pendingIntent
        )
    }

    fun tryScheduleNotification(
        note: ReminderNote,
        permissionManager: AppPermissionManager,
        coroutineScope: CoroutineScope,
        onCompletion: (Boolean) -> Unit
    ) {
        val scheduleCallback = { _: Boolean ->
            coroutineScope.launch {
                if (!permissionManager.isGranted(AppPermission.ScheduleExactAlarm) ||
                    !permissionManager.isGranted(AppPermission.PostNotifications)
                ) {
                    logger.info("${LogTags.NOTIFICATIONS} Scheduling failed: notifications permissions are denied")
                    onCompletion(false)
                } else {
                    scheduleNotification(note)
                    logger.info("${LogTags.NOTIFICATIONS} Scheduled notification at ${note.reminderDateTime}")
                    onCompletion(true)
                }
            }
            Unit
        }
        if (permissionManager.isGranted(AppPermission.ScheduleExactAlarm) &&
            permissionManager.isGranted(AppPermission.PostNotifications)
        ) {
            scheduleCallback(true)
        } else {
            permissionManager.requestPermission(
                AppPermission.ScheduleExactAlarm, scheduleCallback
            )
            permissionManager.requestPermission(
                AppPermission.PostNotifications, scheduleCallback
            )
        }
    }

    fun cancelScheduledNotification(noteDate: LocalDate) {
        val notificationIntent = Intent(
            appContext, NoteNotificationReceiver::class.java
        )
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
            appContext,
            noteDate.stableHash(),
            notificationIntent,
            getIntentCancelCurrentFlags()
        )
        val alarmManager = appContext
            .getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)

        logger.info("${LogTags.NOTIFICATIONS} Cancelled notification at $noteDate")
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID_NAME = "note-notification-channel"

        private fun getIntentUpdateCurrentFlags(): Int {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            }
            return PendingIntent.FLAG_UPDATE_CURRENT
        }

        private fun getIntentCancelCurrentFlags(): Int {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            }
            return PendingIntent.FLAG_CANCEL_CURRENT
        }
    }
}