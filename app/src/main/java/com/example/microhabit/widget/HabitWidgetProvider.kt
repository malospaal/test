package com.example.microhabit.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.microhabit.MainActivity
import com.example.microhabit.R
import com.example.microhabit.data.HabitRepository

class HabitWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun refreshAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val component = ComponentName(context, HabitWidgetProvider::class.java)
            val ids = manager.getAppWidgetIds(component)
            ids.forEach { id -> updateWidget(context, manager, id) }
        }

        private fun updateWidget(context: Context, manager: AppWidgetManager, appWidgetId: Int) {
            val repo = HabitRepository(context)
            val (taskName, streak, progress) = repo.selectedTaskWidgetSummary()

            val openAppIntent = PendingIntent.getActivity(
                context,
                1,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val views = RemoteViews(context.packageName, R.layout.widget_habit).apply {
                setTextViewText(R.id.widgetHabit, taskName)
                setTextViewText(R.id.widgetStreak, "Streak: $streak")
                setProgressBar(R.id.widgetProgressBar, 100, progress, false)
                setTextViewText(R.id.widgetProgressText, "Progress: $progress%")
                setOnClickPendingIntent(R.id.widgetHabit, openAppIntent)
                setOnClickPendingIntent(R.id.widgetStreak, openAppIntent)
                setOnClickPendingIntent(R.id.widgetProgressText, openAppIntent)
            }
            manager.updateAppWidget(appWidgetId, views)
        }
    }
}
