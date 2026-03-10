package com.example.microhabit.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.microhabit.data.HabitRepository

class HabitReminderRescheduleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        if (action !in SUPPORTED_ACTIONS) return

        val appContext = context.applicationContext
        val repository = HabitRepository(appContext)
        val scheduler = HabitReminderScheduler(appContext, repository)
        scheduler.ensureNotificationChannel()
        scheduler.syncAllReminders()
    }

    private companion object {
        val SUPPORTED_ACTIONS = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED
        )
    }
}
