package com.example.microhabit.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object WidgetUpdateTrigger {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    suspend fun updateAllWidgetInstances(context: Context) {
        val appContext = context.applicationContext
        val manager = GlanceAppWidgetManager(appContext)
        val smallIds = manager.getGlanceIds(SmallHabitWidget::class.java)
        val mediumIds = manager.getGlanceIds(MediumHabitWidget::class.java)
        val largeIds = manager.getGlanceIds(LargeHabitWidget::class.java)
        WidgetDebugLog.d(
            "updateAllWidgetInstances small=${smallIds.size} medium=${mediumIds.size} large=${largeIds.size}"
        )

        smallIds.forEach { glanceId ->
            updateAppWidgetState(appContext, glanceId) { prefs ->
                prefs[WidgetRefreshNonceKey] = System.currentTimeMillis()
            }
            SmallHabitWidget().update(appContext, glanceId)
            WidgetDebugLog.d("updateAllWidgetInstances updated small glanceId=$glanceId")
        }
        mediumIds.forEach { glanceId ->
            updateAppWidgetState(appContext, glanceId) { prefs ->
                prefs[WidgetRefreshNonceKey] = System.currentTimeMillis()
            }
            MediumHabitWidget().update(appContext, glanceId)
            WidgetDebugLog.d("updateAllWidgetInstances updated medium glanceId=$glanceId")
        }
        largeIds.forEach { glanceId ->
            updateAppWidgetState(appContext, glanceId) { prefs ->
                prefs[WidgetRefreshNonceKey] = System.currentTimeMillis()
            }
            LargeHabitWidget().update(appContext, glanceId)
            WidgetDebugLog.d("updateAllWidgetInstances updated large glanceId=$glanceId")
        }
    }

    fun triggerUpdate(context: Context) {
        val appContext = context.applicationContext
        WidgetDebugLog.d("triggerUpdate enqueue")
        scope.launch {
            runCatching { updateAllWidgetInstances(appContext) }
                .onFailure {
                    WidgetDebugLog.e("triggerUpdate updateAllWidgetInstances failed, fallback to updateAll", it)
                    SmallHabitWidget().updateAll(appContext)
                    MediumHabitWidget().updateAll(appContext)
                    LargeHabitWidget().updateAll(appContext)
                    WidgetDebugLog.d("triggerUpdate fallback updateAll done")
                }
            triggerUpdateViaBroadcast(appContext)
        }
    }

    fun triggerUpdateViaBroadcast(context: Context) {
        WidgetDebugLog.d("triggerUpdateViaBroadcast start")
        sendUpdateBroadcast(context, HabitWidgetSmallReceiver::class.java)
        sendUpdateBroadcast(context, HabitWidgetMediumReceiver::class.java)
        sendUpdateBroadcast(context, HabitWidgetLargeReceiver::class.java)
    }

    suspend fun updateGlanceId(context: Context, glanceId: GlanceId) {
        val appContext = context.applicationContext
        val manager = GlanceAppWidgetManager(appContext)
        WidgetDebugLog.d("updateGlanceId target=$glanceId")
        updateAppWidgetState(appContext, glanceId) { prefs ->
            prefs[WidgetRefreshNonceKey] = System.currentTimeMillis()
        }
        when {
            manager.getGlanceIds(SmallHabitWidget::class.java).any { it == glanceId } -> {
                SmallHabitWidget().update(appContext, glanceId)
                WidgetDebugLog.d("updateGlanceId updated small target=$glanceId")
            }
            manager.getGlanceIds(MediumHabitWidget::class.java).any { it == glanceId } -> {
                MediumHabitWidget().update(appContext, glanceId)
                WidgetDebugLog.d("updateGlanceId updated medium target=$glanceId")
            }
            manager.getGlanceIds(LargeHabitWidget::class.java).any { it == glanceId } -> {
                LargeHabitWidget().update(appContext, glanceId)
                WidgetDebugLog.d("updateGlanceId updated large target=$glanceId")
            }
            else -> {
                WidgetDebugLog.d("updateGlanceId target not found in current ids, fallback updateAllWidgetInstances")
                updateAllWidgetInstances(appContext)
            }
        }
    }

    private fun sendUpdateBroadcast(
        context: Context,
        receiverClass: Class<*>
    ) {
        val manager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, receiverClass)
        val ids = manager.getAppWidgetIds(componentName)
        if (ids.isEmpty()) {
            WidgetDebugLog.d("sendUpdateBroadcast receiver=${receiverClass.simpleName} no widget ids")
            return
        }
        WidgetDebugLog.d(
            "sendUpdateBroadcast receiver=${receiverClass.simpleName} ids=${ids.joinToString(",")}"
        )

        val intent = Intent(context, receiverClass).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        }
        context.sendBroadcast(intent)
        WidgetDebugLog.d("sendUpdateBroadcast dispatched receiver=${receiverClass.simpleName}")
    }
}
