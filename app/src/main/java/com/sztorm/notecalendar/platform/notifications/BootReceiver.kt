package com.sztorm.notecalendar.platform.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sztorm.notecalendar.platform.logging.TimberLogger

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            AppNotificationManager(context, TimberLogger).rescheduleAll()
        }
    }
}