package com.example.microhabit.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.microhabit.MainActivity
import com.example.microhabit.data.HabitRepository
import com.example.microhabit.data.HabitTask
import com.example.microhabit.data.TaskFrequency
import com.example.microhabit.i18n.translate
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class HabitReminderScheduler(
    private val context: Context,
    private val repository: HabitRepository
) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    fun ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val language = repository.getLanguage()
        val channel = NotificationChannel(
            CHANNEL_ID,
            translate(language, "Habit reminders"),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = translate(language, "Daily habit reminders")
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun syncAllReminders() {
        val tasks = repository.getTasks()
        tasks.forEach { task ->
            if (!repository.isHabitActive(task) || !task.reminderEnabled) {
                cancelReminder(task.id)
            } else {
                scheduleReminder(task)
            }
        }
    }

    fun syncReminderForTask(taskId: String) {
        val task = repository.getTasks().firstOrNull { it.id == taskId }
        if (task == null || !repository.isHabitActive(task) || !task.reminderEnabled) {
            cancelReminder(taskId)
            return
        }
        scheduleReminder(task)
    }

    fun cancelReminder(taskId: String) {
        val pendingIntent = reminderPendingIntent(taskId)
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
        notificationManager.cancel(notificationId(taskId))
    }

    fun onReminderTriggered(taskId: String) {
        val task = repository.getTasks().firstOrNull { it.id == taskId }
        if (task == null || !repository.isHabitActive(task) || !task.reminderEnabled) {
            cancelReminder(taskId)
            return
        }

        showNotification(task)
        scheduleReminder(task)
    }

    private fun scheduleReminder(task: HabitTask) {
        if (task.frequency == TaskFrequency.SELECTED_DAYS && task.customDays.isEmpty()) {
            cancelReminder(task.id)
            return
        }
        val now = LocalDateTime.now(ZoneId.systemDefault())
        task.endDate?.let { endDate ->
            val lastReminderAt = endDate.atTime(task.reminderHour, task.reminderMinute)
            if (!lastReminderAt.isAfter(now)) {
                cancelReminder(task.id)
                return
            }
        }
        ensureNotificationChannel()
        val triggerAtMillis = nextTriggerAtMillis(task)
        val pendingIntent = reminderPendingIntent(task.id)

        alarmManager.cancel(pendingIntent)
        try {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms() ->
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                else ->
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        } catch (_: SecurityException) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    private fun nextTriggerAtMillis(task: HabitTask): Long {
        val zone = ZoneId.systemDefault()
        val now = LocalDateTime.now(zone)
        var targetDate = maxOf(now.toLocalDate(), task.startDate)

        var candidate = targetDate.atTime(task.reminderHour, task.reminderMinute)
        var attempts = 0
        while ((!candidate.isAfter(now) || !shouldRemindOn(task, targetDate)) && attempts < 3660) {
            targetDate = targetDate.plusDays(1)
            candidate = targetDate.atTime(task.reminderHour, task.reminderMinute)
            attempts += 1
        }

        return candidate.atZone(zone).toInstant().toEpochMilli()
    }

    private fun shouldRemindOn(task: HabitTask, date: LocalDate): Boolean {
        if (date.isBefore(task.startDate)) return false
        val endDate = task.endDate
        if (endDate != null && date.isAfter(endDate)) return false
        return when (task.frequency) {
            TaskFrequency.DAILY -> true
            TaskFrequency.TIMES_PER_WEEK -> true
            TaskFrequency.SELECTED_DAYS -> task.customDays.isNotEmpty() && date.dayOfWeek.value in task.customDays
        }
    }

    private fun showNotification(task: HabitTask) {
        if (!canDeliverNotifications(context)) return

        val language = repository.getLanguage()
        val contentIntent = PendingIntent.getActivity(
            context,
            notificationId(task.id),
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_OPEN_HABIT_ID, task.id)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("${task.emoji.ifBlank { "" }} ${task.title}".trim())
            .setContentText(translate(language, "Time to complete your habit!"))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contentIntent)
            .build()

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(context).notify(notificationId(task.id), notification)
    }

    private fun reminderPendingIntent(taskId: String): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            reminderRequestCode(taskId),
            Intent(context, HabitReminderReceiver::class.java).apply {
                action = ACTION_REMINDER
                putExtra(EXTRA_TASK_ID, taskId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun reminderRequestCode(taskId: String): Int = taskId.hashCode()

    private fun notificationId(taskId: String): Int = taskId.hashCode()

    companion object {
        const val ACTION_REMINDER = "com.example.microhabit.ACTION_REMINDER"
        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_OPEN_HABIT_ID = "extra_open_habit_id"
        private const val CHANNEL_ID = "habit_reminders"

        fun hasNotificationPermission(context: Context): Boolean {
            return hasRuntimeNotificationPermission(context)
        }

        fun hasRuntimeNotificationPermission(context: Context): Boolean {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
            return context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        }

        fun canDeliverNotifications(context: Context): Boolean {
            val appNotificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
            return hasRuntimeNotificationPermission(context) && appNotificationsEnabled
        }
    }
}
