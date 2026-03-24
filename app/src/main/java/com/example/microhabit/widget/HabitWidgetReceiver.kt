package com.example.microhabit.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

abstract class BaseHabitWidgetReceiver(
    private val widget: GlanceAppWidget
) : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = widget
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        WidgetDebugLog.d(
            "receiver.onUpdate receiver=${this::class.java.simpleName} ids=${appWidgetIds.joinToString(",")}"
        )
        scope.launch {
            runCatching {
                val appContext = context.applicationContext
                val glanceManager = GlanceAppWidgetManager(appContext)
                val glanceIds: List<GlanceId> = when (widget) {
                    is SmallHabitWidget -> glanceManager.getGlanceIds(SmallHabitWidget::class.java)
                    is MediumHabitWidget -> glanceManager.getGlanceIds(MediumHabitWidget::class.java)
                    is LargeHabitWidget -> glanceManager.getGlanceIds(LargeHabitWidget::class.java)
                    else -> emptyList()
                }
                WidgetDebugLog.d(
                    "receiver.onUpdate mapped receiver=${this@BaseHabitWidgetReceiver::class.java.simpleName} " +
                        "glanceIds=${glanceIds.size}"
                )
                var updatedCount = 0
                glanceIds.forEach { glanceId ->
                    val resolvedAppWidgetId = glanceManager.getAppWidgetId(glanceId)
                    if (appWidgetIds.contains(resolvedAppWidgetId)) {
                        widget.update(appContext, glanceId)
                        updatedCount += 1
                        WidgetDebugLog.d(
                            "receiver.onUpdate updated receiver=${this@BaseHabitWidgetReceiver::class.java.simpleName} " +
                                "appWidgetId=$resolvedAppWidgetId glanceId=$glanceId"
                        )
                    }
                }
                if (updatedCount == 0 && glanceIds.isNotEmpty()) {
                    WidgetDebugLog.d(
                        "receiver.onUpdate no direct match, fallback first glanceId=${glanceIds.first()}"
                    )
                    widget.update(appContext, glanceIds.first())
                }
            }.onSuccess {
                WidgetDebugLog.d("receiver.onUpdate done receiver=${this@BaseHabitWidgetReceiver::class.java.simpleName}")
            }.onFailure {
                WidgetDebugLog.e(
                    "receiver.onUpdate failed receiver=${this@BaseHabitWidgetReceiver::class.java.simpleName}",
                    it
                )
            }
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        WidgetDebugLog.d("onDeleted ids=${appWidgetIds.joinToString(",")}")
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
        WidgetDebugLog.d("HabitWidgetReceiver.refreshAll")
        WidgetUpdateTrigger.triggerUpdate(context)
        HabitWidgetUpdateScheduler.scheduleWidgetUpdates(context)
    }
}
