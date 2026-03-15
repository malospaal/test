package com.example.microhabit.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.microhabit.data.HabitRepository
import com.example.microhabit.data.HabitTask
import com.example.microhabit.data.TaskFrequency
import com.example.microhabit.data.TrackingType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.time.LocalDate
import java.time.ZoneId

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class HabitReminderSchedulerTest {

    private lateinit var context: Context
    private lateinit var repository: HabitRepository
    private lateinit var scheduler: HabitReminderScheduler
    private lateinit var alarmManager: AlarmManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences("habit_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        repository = HabitRepository(context)
        scheduler = HabitReminderScheduler(context, repository)
        alarmManager = context.getSystemService(AlarmManager::class.java)
        clearScheduledAlarms()
    }

    @Test
    fun syncAllReminders_schedulesOnlyActiveAndEnabledTasks() {
        repository.setNotificationsEnabled(true)
        val activeEnabled = createTask("Active enabled", reminderEnabled = true, endDate = null)
        createTask("Active disabled", reminderEnabled = false, endDate = null)
        createTask("Completed enabled", reminderEnabled = true, endDate = LocalDate.now().minusDays(1))

        scheduler.syncAllReminders()

        val scheduledIds = scheduledTaskIds()
        assertTrue(scheduledIds.contains(activeEnabled.id))
        assertEquals(1, scheduledIds.size)
    }

    @Test
    fun syncAllReminders_whenNotificationsDisabled_cancelsEverything() {
        repository.setNotificationsEnabled(true)
        createTask("Active enabled", reminderEnabled = true, endDate = null)
        scheduler.syncAllReminders()
        assertFalse(scheduledTaskIds().isEmpty())

        repository.setNotificationsEnabled(false)
        scheduler.syncAllReminders()
        assertTrue(scheduledTaskIds().isEmpty())
    }

    @Test
    fun syncReminderForTask_endDateWithPastReminderTime_isCancelled() {
        repository.setNotificationsEnabled(true)
        val todayTask = createTask(
            title = "Ends today at midnight",
            reminderEnabled = true,
            reminderHour = 0,
            reminderMinute = 0,
            endDate = LocalDate.now()
        )

        scheduler.syncReminderForTask(todayTask.id)
        assertTrue(scheduledTaskIds().isEmpty())
    }

    @Test
    fun syncReminderForTask_archivedTask_isCancelled() {
        repository.setNotificationsEnabled(true)
        val task = createTask("Archive me", reminderEnabled = true, endDate = null)
        scheduler.syncReminderForTask(task.id)
        assertTrue(scheduledTaskIds().contains(task.id))

        repository.archiveTask(task.id, true)
        scheduler.syncReminderForTask(task.id)
        assertFalse(scheduledTaskIds().contains(task.id))
    }

    @Test
    fun syncReminderForTask_reminderDisabledAfterScheduling_cancelsReminder() {
        repository.setNotificationsEnabled(true)
        val task = createTask("Toggle reminder", reminderEnabled = true, endDate = null)
        scheduler.syncReminderForTask(task.id)
        assertTrue(scheduledTaskIds().contains(task.id))

        repository.updateTask(
            taskId = task.id,
            title = task.title,
            emoji = task.emoji,
            colorHex = task.colorHex,
            trackingType = task.trackingType,
            dailyTarget = task.dailyTarget,
            unitLabel = task.unitLabel,
            frequency = task.frequency,
            customDays = task.customDays,
            timesPerWeek = task.timesPerWeek,
            reminderHour = task.reminderHour,
            reminderMinute = task.reminderMinute,
            reminderEnabled = false,
            startDate = task.startDate,
            endDate = task.endDate
        )
        scheduler.syncReminderForTask(task.id)
        assertFalse(scheduledTaskIds().contains(task.id))
    }

    @Test
    fun syncReminderForTask_startDateInFuture_schedulesNotEarlierThanStartDate() {
        repository.setNotificationsEnabled(true)
        val futureStart = LocalDate.now().plusDays(2)
        val task = createTask(
            title = "Future start",
            reminderEnabled = true,
            reminderHour = 9,
            reminderMinute = 15,
            startDate = futureStart,
            endDate = null
        )

        scheduler.syncReminderForTask(task.id)
        val alarm = scheduledAlarmForTaskId(task.id)
        assertTrue(alarm != null)

        val earliestAllowed = futureStart.atTime(9, 15).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        assertTrue((alarm?.triggerAtTime ?: 0L) >= earliestAllowed)
    }

    @Test
    fun syncReminderForTask_selectedDaysWithoutCustomDays_doesNotSchedule() {
        repository.setNotificationsEnabled(true)
        val task = createTask(
            title = "No selected days",
            reminderEnabled = true,
            frequency = TaskFrequency.SELECTED_DAYS,
            customDays = emptySet(),
            endDate = null
        )

        scheduler.syncReminderForTask(task.id)
        assertFalse(scheduledTaskIds().contains(task.id))
    }

    private fun clearScheduledAlarms() {
        val alarms = shadowOf(alarmManager).scheduledAlarms.toList()
        alarms.forEach { alarm ->
            val operation = alarm.operation ?: return@forEach
            alarmManager.cancel(operation)
            operation.cancel()
        }
    }

    private fun scheduledTaskIds(): Set<String> {
        return shadowOf(alarmManager).scheduledAlarms.mapNotNull { alarm ->
            val operation = alarm.operation ?: return@mapNotNull null
            pendingIntentTaskId(operation)
        }.toSet()
    }

    private fun scheduledAlarmForTaskId(taskId: String) = shadowOf(alarmManager).scheduledAlarms.firstOrNull { alarm ->
        val operation = alarm.operation ?: return@firstOrNull false
        pendingIntentTaskId(operation) == taskId
    }

    private fun pendingIntentTaskId(operation: PendingIntent): String? {
        val intent = shadowOf(operation).savedIntent
        return intent?.getStringExtra(HabitReminderScheduler.EXTRA_TASK_ID)
    }

    private fun createTask(
        title: String,
        reminderEnabled: Boolean,
        frequency: TaskFrequency = TaskFrequency.DAILY,
        customDays: Set<Int> = emptySet(),
        reminderHour: Int = 8,
        reminderMinute: Int = 0,
        startDate: LocalDate = LocalDate.now().minusDays(10),
        endDate: LocalDate?
    ): HabitTask {
        return repository.createTask(
            title = title,
            emoji = "🔔",
            colorHex = "#1F6F64",
            trackingType = TrackingType.YES_NO,
            dailyTarget = 1,
            unitLabel = "",
            frequency = frequency,
            customDays = customDays,
            timesPerWeek = 3,
            reminderHour = reminderHour,
            reminderMinute = reminderMinute,
            reminderEnabled = reminderEnabled,
            startDate = startDate,
            endDate = endDate
        )
    }
}
