package com.sztorm.notecalendar

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NoteNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: Notification = intent.getParcelableExtra(NOTIFICATION_EXTRA)!!
        manager.notify(NoteNotificationManager.NOTIFICATION_ID, notification)
    }

    companion object {
        const val NOTIFICATION_EXTRA: String = "note-notification-extra"
    }
}