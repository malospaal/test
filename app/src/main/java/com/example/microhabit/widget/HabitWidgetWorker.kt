package com.example.microhabit.widget

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.Calendar
import java.util.concurrent.TimeUnit

class HabitWidgetWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        WidgetDebugLog.d("HabitWidgetWorker.doWork")
        WidgetUpdateTrigger.triggerUpdate(applicationContext)
        return Result.success()
    }
}

class MidnightWidgetWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        WidgetDebugLog.d("MidnightWidgetWorker.doWork")
        WidgetUpdateTrigger.triggerUpdate(applicationContext)
        HabitWidgetUpdateScheduler.scheduleMidnightUpdate(applicationContext)
        return Result.success()
    }
}

object HabitWidgetUpdateScheduler {
    private const val MIDNIGHT_WORK_NAME = "habit_widget_midnight_update"
    private const val IMMEDIATE_WORK_NAME = "habit_widget_immediate_update"
    private const val LEGACY_PERIODIC_WORK_NAME = "habit_widget_periodic_update"

    fun scheduleWidgetUpdates(context: Context) {
        WidgetDebugLog.d("scheduleWidgetUpdates")
        scheduleMidnightUpdate(context)
    }

    fun scheduleMidnightUpdate(context: Context) {
        WidgetDebugLog.d("scheduleMidnightUpdate start")
        WorkManager.getInstance(context).cancelUniqueWork(LEGACY_PERIODIC_WORK_NAME)
        val now = Calendar.getInstance()
        val midnight = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 30)
            set(Calendar.MILLISECOND, 0)
        }
        val delayMs = (midnight.timeInMillis - now.timeInMillis).coerceAtLeast(1L)
        val request = OneTimeWorkRequestBuilder<MidnightWidgetWorker>()
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            MIDNIGHT_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
        WidgetDebugLog.d("scheduleMidnightUpdate enqueued delayMs=$delayMs")
    }

    fun triggerNow(context: Context) {
        WidgetDebugLog.d("triggerNow enqueue")
        val request = OneTimeWorkRequestBuilder<HabitWidgetWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            IMMEDIATE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}

object GlanceWidgetUpdateDispatcher {
    fun enqueue(context: Context) {
        HabitWidgetUpdateScheduler.triggerNow(context)
        HabitWidgetUpdateScheduler.scheduleMidnightUpdate(context)
    }
}
