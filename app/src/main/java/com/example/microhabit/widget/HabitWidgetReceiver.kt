package com.example.microhabit.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver

abstract class BaseHabitWidgetReceiver(
    private val widget: GlanceAppWidget
) : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = widget

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        appWidgetIds.forEach { appWidgetId ->
            WidgetBindingStore.clearHabitId(context, appWidgetId)
        }
    }
}

class HabitWidgetSmallReceiver : BaseHabitWidgetReceiver(SmallHabitWidget())
class HabitWidgetMediumReceiver : BaseHabitWidgetReceiver(MediumHabitWidget())
class HabitWidgetLargeReceiver : BaseHabitWidgetReceiver(LargeHabitWidget())

object HabitWidgetReceiver {
    suspend fun allGlanceIds(context: Context): List<GlanceId> {
        val manager = GlanceAppWidgetManager(context)
        val ids = mutableListOf<GlanceId>()
        ids += manager.getGlanceIds(SmallHabitWidget::class.java)
        ids += manager.getGlanceIds(MediumHabitWidget::class.java)
        ids += manager.getGlanceIds(LargeHabitWidget::class.java)
        return ids
    }

    fun refreshAll(context: Context) {
        GlanceWidgetUpdateDispatcher.enqueue(context)
    }
}
