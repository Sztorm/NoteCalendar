package com.sztorm.notecalendar

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sztorm.notecalendar.helpers.IntentHelper.Companion.getParcelableExtraCompat

class NoteNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: Notification = intent.getParcelableExtraCompat(
            NOTIFICATION_EXTRA, Notification::class.java)!!
        manager.notify(AppNotificationManager.NOTIFICATION_ID, notification)
    }

    companion object {
        const val NOTIFICATION_EXTRA: String = "note-notification-extra"
    }
}