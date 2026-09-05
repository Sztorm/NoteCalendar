package com.sztorm.notecalendar.platform.notifications

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sztorm.notecalendar.core.common.getParcelableExtraCompat

class NoteNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val manager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = intent.getParcelableExtraCompat(
            NotificationIntentKeys.EXTRA_NOTIFICATION, Notification::class.java
        )
        val noteDateId = intent
            .getIntExtra(NotificationIntentKeys.EXTRA_NOTE_DATE_ID, 0)

        if (notification != null) {
            manager.notify(noteDateId, notification)
        }
    }
}