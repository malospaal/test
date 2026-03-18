package com.example.microhabit.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class HabitWidgetWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        SmallHabitWidget().updateAll(applicationContext)
        MediumHabitWidget().updateAll(applicationContext)
        LargeHabitWidget().updateAll(applicationContext)
        return Result.success()
    }
}

object HabitWidgetUpdateScheduler {
    private const val PERIODIC_WORK_NAME = "habit_widget_periodic_update"
    private const val IMMEDIATE_WORK_NAME = "habit_widget_immediate_update"

    fun scheduleWidgetUpdates(context: Context) {
        val request = PeriodicWorkRequestBuilder<HabitWidgetWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun triggerNow(context: Context) {
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
        HabitWidgetUpdateScheduler.scheduleWidgetUpdates(context)
    }
}
