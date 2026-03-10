package com.example.microhabit.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.microhabit.data.HabitRepository

class HabitReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != HabitReminderScheduler.ACTION_REMINDER) return
        val taskId = intent.getStringExtra(HabitReminderScheduler.EXTRA_TASK_ID) ?: return

        val appContext = context.applicationContext
        val repository = HabitRepository(appContext)
        val scheduler = HabitReminderScheduler(appContext, repository)
        scheduler.onReminderTriggered(taskId)
    }
}
