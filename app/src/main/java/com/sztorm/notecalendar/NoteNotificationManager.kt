package com.sztorm.notecalendar

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import java.time.LocalDateTime
import java.util.*
import com.sztorm.notecalendar.helpers.AlarmManagerHelper.Companion.setExactAndAllowWhileIdleCompat

class NoteNotificationManager {
    companion object {
        const val NOTIFICATION_ID: Int = 1
        const val NOTIFICATION_CHANNEL_ID_NAME = "note-notification-channel"

        private fun getOrCreateNotificationChannel(
            context: Context, name: String, description: String): String {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_LOW
                val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID_NAME, name, importance)
                channel.enableLights(true)
                channel.description = description
                channel.lightColor = Color.BLUE
                channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

                val notificationManager: NotificationManager? = context
                    .getSystemService(NotificationManager::class.java)
                notificationManager?.createNotificationChannel(channel)
            }
            return NOTIFICATION_CHANNEL_ID_NAME
        }

        private fun getPendingIntentUpdateFlags(): Int {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            }
            return PendingIntent.FLAG_UPDATE_CURRENT
        }

        private fun buildNotification(context: Context, note: NoteData): Notification {
            val channel = getOrCreateNotificationChannel(
                context,
                context.getString(R.string.Notification_Note_ChannelName),
                context.getString(R.string.Notification_Note_ChannelDescription))

            val intent = Intent(context, MainActivity::class.java)
            val activity: PendingIntent = PendingIntent.getActivity(
                context, NOTIFICATION_ID, intent, getPendingIntentUpdateFlags())
            val builder = NotificationCompat.Builder(context, channel)
                .setContentTitle(context.getString(R.string.Notification_Note_Title))
                .setContentText(note.text)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.icon_note)
                .setContentIntent(activity)

            return builder.build()
        }

        fun scheduleNotification(context: Context, notificationData: NoteNotificationData) {
            val note: NoteData = notificationData.note
            val dateTime: LocalDateTime = notificationData.dateTime
            val notification: Notification = buildNotification(context, note)
            val notificationIntent = Intent(context, NoteNotificationReceiver::class.java)
                .putExtra(NoteNotificationReceiver.NOTIFICATION_EXTRA, notification)

            val pendingIntent = PendingIntent.getBroadcast(
                context, NOTIFICATION_ID, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT or getPendingIntentUpdateFlags())

            val calendar = Calendar.getInstance()
            calendar[Calendar.YEAR] = dateTime.year
            calendar[Calendar.DAY_OF_YEAR] = dateTime.dayOfYear
            calendar[Calendar.HOUR_OF_DAY] = dateTime.hour
            calendar[Calendar.MINUTE] = dateTime.minute
            calendar[Calendar.SECOND] = 0

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            alarmManager.setExactAndAllowWhileIdleCompat(
                AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }

        fun cancelScheduledNotification(context: Context) {
            val notificationIntent = Intent(context, NoteNotificationReceiver::class.java)
            val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
                context,
                NOTIFICATION_ID,
                notificationIntent,
                getPendingIntentUpdateFlags())
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
        }
    }
}