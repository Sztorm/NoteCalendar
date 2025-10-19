package com.sztorm.notecalendar

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import com.sztorm.notecalendar.NoteCalendarApplication.Companion.BUNDLE_KEY_MAIN_FRAGMENT_TYPE
import com.sztorm.notecalendar.repositories.NoteRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class AppNotificationManager(val mainActivity: MainActivity) {
    private fun getOrCreateNotificationChannel(name: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID_NAME, name, importance)
            channel.enableLights(true)
            channel.description = description
            channel.lightColor = Color.BLUE
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

            val notificationManager: NotificationManager = mainActivity
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(note: NoteData): Notification {
        getOrCreateNotificationChannel(
            mainActivity.getString(R.string.Notification_Note_ChannelName),
            mainActivity.getString(R.string.Notification_Note_ChannelDescription)
        )
        val intent = Intent(mainActivity, MainActivity::class.java)
            .addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
            .putExtra(BUNDLE_KEY_MAIN_FRAGMENT_TYPE, MainFragmentType.DAY.ordinal)
        val activity: PendingIntent = PendingIntent.getActivity(
            mainActivity, NOTIFICATION_ID, intent, getIntentCancelCurrentFlags()
        )
        val builder = NotificationCompat.Builder(mainActivity, NOTIFICATION_CHANNEL_ID_NAME)
            .setContentTitle(mainActivity.getString(R.string.Notification_Note_Title))
            .setContentText(note.text)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.icon_note)
            .setContentIntent(activity)

        return builder.build()
    }

    private fun scheduleNotification(notificationData: NoteNotificationData) {
        val note: NoteData = notificationData.note
        val dateTime: LocalDateTime = notificationData.dateTime
        val notification: Notification = createNotification(note)
        val notificationIntent = Intent(mainActivity, NoteNotificationReceiver::class.java)
            .putExtra(NoteNotificationReceiver.NOTIFICATION_EXTRA, notification)
        val pendingIntent = PendingIntent.getBroadcast(
            mainActivity, NOTIFICATION_ID, notificationIntent, getIntentCancelCurrentFlags()
        )
        val calendar = Calendar.getInstance()
        calendar[Calendar.YEAR] = dateTime.year
        calendar[Calendar.DAY_OF_YEAR] = dateTime.dayOfYear
        calendar[Calendar.HOUR_OF_DAY] = dateTime.hour
        calendar[Calendar.MINUTE] = dateTime.minute
        calendar[Calendar.SECOND] = 0
        val alarmManager = mainActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExactAndAllowWhileIdleCompat(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent
        )
    }

    suspend fun tryScheduleNotification(
        args: ScheduleNoteNotificationArguments,
        noteRepository: NoteRepository
    ): Boolean {
        val settings = mainActivity.settings
        val enabledNotifications: Boolean =
            args.turnOnNotifications ?: settings.getTurnOnNotifications()

        if (!enabledNotifications) {
            Timber.i("${LogTags.NOTIFICATIONS} Scheduling failed beacuse notifications are disabled.")
            return false
        }
        val permissionManager = mainActivity.permissionManager

        if (!permissionManager.isGranted(AppPermissionCode.NOTIFICATIONS)) {
            if (args.grantPermissions) {
                mainActivity.permissionManager.requestUngrantedPermissions(
                    AppPermissionCode.NOTIFICATIONS
                ) { isSuccess ->
                    mainActivity.lifecycleScope.launch {
                        if (isSuccess) {
                            tryScheduleNotification(
                                args.copy(grantPermissions = false), noteRepository
                            )
                        } else {
                            mainActivity.settings.setTurnOnNotifications(false)
                            Timber.i("${LogTags.NOTIFICATIONS} Scheduling failed beacuse notifications permissions are denied (request permission callback).")
                        }
                    }
                }
                return true
            } else {
                mainActivity.settings.setTurnOnNotifications(false)
                Timber.i("${LogTags.NOTIFICATIONS} Scheduling failed beacuse notifications permissions are denied.")
                return false
            }
        }
        val notificationTime =
            args.notificationTime ?: settings.getNotificationTime()
        val currentDateTime = LocalDateTime.now()
        val notificationDateTime =
            if ((notificationTime <= currentDateTime.toLocalTime())) {
                LocalDateTime.of(
                    currentDateTime.toLocalDate().plusDays(1), notificationTime
                )
            } else {
                LocalDateTime.of(currentDateTime.toLocalDate(), notificationTime)
            }
        val note: NoteData? =
            args.note ?: noteRepository.getBy(notificationDateTime.toLocalDate())

        if (note === null || note.date != notificationDateTime.toLocalDate().toString()) {
            Timber.i("${LogTags.NOTIFICATIONS} Scheduling failed beacuse note is invalid.")
            return false
        }
        val notificationData = NoteNotificationData(note, notificationDateTime)

        scheduleNotification(notificationData)
        return true
    }

    fun cancelScheduledNotification() {
        val notificationIntent = Intent(mainActivity, NoteNotificationReceiver::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
            mainActivity,
            NOTIFICATION_ID,
            notificationIntent,
            getIntentUpdateCurrentFlags()
        )
        val alarmManager = mainActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    suspend fun tryCancelScheduledNotification(noteDate: LocalDate): Boolean {
        val notificationTime =
            mainActivity.settings.getNotificationTime()
        val currentDateTime = LocalDateTime.now()
        var notificationDateTime = LocalDateTime.of(
            currentDateTime.toLocalDate(), notificationTime
        )
        if (notificationTime <= currentDateTime.toLocalTime()) {
            notificationDateTime = notificationDateTime.plusDays(1)
        }
        if (notificationDateTime.toLocalDate() != noteDate) {
            return false
        }
        cancelScheduledNotification()

        return true
    }

    companion object {
        const val NOTIFICATION_ID: Int = 1

        @Suppress("MemberVisibilityCanBePrivate")
        const val NOTIFICATION_CHANNEL_ID_NAME = "note-notification-channel"

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