package com.example.microhabit.widget

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalState
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.example.microhabit.MainActivity
import com.example.microhabit.data.HabitRepository
import com.example.microhabit.data.TrackingType
import java.time.LocalDate

internal enum class WidgetLayoutSize {
    SMALL,
    MEDIUM,
    LARGE
}

internal val HabitIdParamKey = ActionParameters.Key<String>("habitId")
internal val WidgetRefreshNonceKey = longPreferencesKey("widget_refresh_nonce")

internal open class HabitWidget(
    private val layoutSize: WidgetLayoutSize
) : GlanceAppWidget() {
    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appWidgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)

        provideContent {
            val statePrefs = (LocalState.current as? Preferences)
            val nonce = statePrefs?.get(WidgetRefreshNonceKey) ?: 0L
            val dataProvider = WidgetDataProvider(context)
            val habitId = WidgetBindingStore.getHabitId(context, appWidgetId)
            val data = dataProvider.getWidgetData(habitId)
            WidgetDebugLog.d(
                "provideContent size=$layoutSize appWidgetId=$appWidgetId boundHabitId=$habitId " +
                    "resolvedHabitId=${data.habitId} isCompletedToday=${data.isCompletedToday} nonce=$nonce"
            )
            when {
                !data.isProUser -> ProLockedContent()
                layoutSize == WidgetLayoutSize.SMALL -> SmallWidgetContent(data)
                layoutSize == WidgetLayoutSize.MEDIUM -> MediumWidgetContent(data)
                else -> LargeWidgetContent(data)
            }
        }
    }
}

internal class SmallHabitWidget : HabitWidget(WidgetLayoutSize.SMALL)
internal class MediumHabitWidget : HabitWidget(WidgetLayoutSize.MEDIUM)
internal class LargeHabitWidget : HabitWidget(WidgetLayoutSize.LARGE)

class MarkDoneAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        runCatching {
            val appWidgetId = GlanceAppWidgetManager(context).getAppWidgetId(glanceId)
            val paramHabitId = parameters[HabitIdParamKey]
            val boundHabitId = WidgetBindingStore.getHabitId(context, appWidgetId)
            val defaultHabitId = WidgetDataProvider(context).defaultHabitId()
            val habitId = paramHabitId ?: boundHabitId ?: defaultHabitId ?: return

            WidgetDebugLog.d(
                "onAction start appWidgetId=$appWidgetId paramHabitId=$paramHabitId " +
                    "boundHabitId=$boundHabitId defaultHabitId=$defaultHabitId resolvedHabitId=$habitId"
            )

            val repository = HabitRepository(context)
            val task = repository.getTasks().firstOrNull { it.id == habitId }
            if (task == null) {
                WidgetDebugLog.e("onAction abort: task not found for habitId=$habitId")
                return
            }

            val today = LocalDate.now()
            val isScheduledToday = repository.isScheduledOn(task, today)
            val isDoneToday = if (isScheduledToday) {
                repository.isCompletedOn(task, today)
            } else {
                repository.getDayValue(task, today) > 0
            }
            val targetValue = when (task.trackingType) {
                TrackingType.YES_NO -> 1
                TrackingType.COUNT, TrackingType.DURATION -> repository.dailyTarget(task)
            }
            val newValue = if (isDoneToday) 0 else targetValue
            WidgetDebugLog.d(
                "onAction beforeWrite habitId=${task.id} date=$today scheduled=$isScheduledToday " +
                    "isDoneToday=$isDoneToday targetValue=$targetValue writeValue=$newValue"
            )
            repository.setDayValue(task, today, newValue, refreshWidgets = false)

            val postValue = repository.getDayValue(task, today)
            val postDoneScheduled = repository.isCompletedOn(task, today)
            val postDoneWidget = if (repository.isScheduledOn(task, today)) {
                postDoneScheduled
            } else {
                postValue > 0
            }
            WidgetDebugLog.d(
                "onAction afterWrite habitId=${task.id} date=$today value=$postValue " +
                    "isCompletedOn=$postDoneScheduled widgetDoneState=$postDoneWidget"
            )

            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[WidgetRefreshNonceKey] = System.currentTimeMillis()
            }
            WidgetDebugLog.d("onAction nonce updated glanceId=$glanceId")
            WidgetUpdateTrigger.updateGlanceId(context, glanceId)
            WidgetDebugLog.d("onAction updateGlanceId done")
            WidgetUpdateTrigger.updateAllWidgetInstances(context)
            WidgetDebugLog.d("onAction updateAllWidgetInstances done")
            WidgetUpdateTrigger.triggerUpdateViaBroadcast(context)
            WidgetDebugLog.d("onAction triggerUpdateViaBroadcast done")
            HabitWidgetUpdateScheduler.triggerNow(context)
            WidgetDebugLog.d("onAction triggerNow done")
            HabitWidgetUpdateScheduler.scheduleWidgetUpdates(context)
            WidgetDebugLog.d("onAction scheduleWidgetUpdates done")
        }.onFailure { error ->
            WidgetDebugLog.e("onAction failed", error)
        }
    }
}

@Composable
private fun ProLockedContent() {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(day = Color(0xFFF2F7F6), night = Color(0xCC141D1A)))
            .padding(12.dp)
            .clickable(actionStartActivity<MainActivity>()),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🔒", style = TextStyle(fontSize = 16.sp))
            Spacer(GlanceModifier.height(6.dp))
            Text(
                text = "Widgets are PRO",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorProvider(day = Color(0xFF1F2926), night = Color(0xFFE7EFEB))
                )
            )
        }
    }
}

@Composable
private fun SmallWidgetContent(data: WidgetHabitData) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(day = Color(0xFFF2F7F6), night = Color(0xCC141D1A)))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(data.emoji, style = TextStyle(fontSize = 20.sp))
        Text(
            text = data.title,
            style = TextStyle(
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = ColorProvider(day = Color(0xFF1F2926), night = Color(0xFFE7EFEB))
            ),
            maxLines = 1
        )
        Spacer(GlanceModifier.height(6.dp))
        Text(
            text = "🔥 ${data.currentStreak}",
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = ColorProvider(day = Color(0xFF1F6F64), night = Color(0xFF6EC8B7))
            )
        )
        Spacer(GlanceModifier.height(8.dp))
        MarkButton(data, compact = true)
    }
}

@Composable
private fun MediumWidgetContent(data: WidgetHabitData) {
    Row(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(day = Color(0xFFF2F7F6), night = Color(0xCC141D1A)))
            .padding(12.dp)
    ) {
        Column(modifier = GlanceModifier.width(96.dp)) {
            Row {
                Text(data.emoji, style = TextStyle(fontSize = 16.sp))
                Spacer(GlanceModifier.width(6.dp))
                Text(
                    text = data.title,
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorProvider(day = Color(0xFF1F2926), night = Color(0xFFE7EFEB))
                    ),
                    maxLines = 1
                )
            }
            Spacer(GlanceModifier.height(8.dp))
            Text(
                text = "🔥 ${data.currentStreak}",
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorProvider(day = Color(0xFF1F6F64), night = Color(0xFF6EC8B7))
                )
            )
        }
        Spacer(GlanceModifier.width(10.dp))
        Column(modifier = GlanceModifier.fillMaxWidth()) {
            Text(
                text = "This week",
                style = TextStyle(
                    fontSize = 10.sp,
                    color = ColorProvider(day = Color(0xFF5A6B64), night = Color(0xFF8D9B95))
                )
            )
            Spacer(GlanceModifier.height(6.dp))
            Row {
                data.last7Days.forEachIndexed { index, status ->
                    DayChip(
                        label = data.dayLabels.getOrElse(index) { "-" },
                        status = status,
                        compact = true
                    )
                    if (index < 6) Spacer(GlanceModifier.width(3.dp))
                }
            }
            Spacer(GlanceModifier.height(8.dp))
            MarkButton(data, compact = false)
        }
    }
}

@Composable
private fun LargeWidgetContent(data: WidgetHabitData) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(day = Color(0xFFF2F7F6), night = Color(0xCC141D1A)))
            .padding(14.dp)
    ) {
        Row(modifier = GlanceModifier.fillMaxWidth()) {
            Text(data.emoji, style = TextStyle(fontSize = 18.sp))
            Spacer(GlanceModifier.width(8.dp))
            Text(
                text = data.title,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorProvider(day = Color(0xFF1F2926), night = Color(0xFFE7EFEB))
                ),
                maxLines = 1
            )
        }
        Spacer(GlanceModifier.height(10.dp))
        Row {
            data.last7Days.forEachIndexed { index, status ->
                DayChip(
                    label = data.dayLabels.getOrElse(index) { "-" },
                    status = status,
                    compact = false
                )
                if (index < 6) Spacer(GlanceModifier.width(4.dp))
            }
        }
        Spacer(GlanceModifier.height(12.dp))
        ProgressBlocks(data.weekCompletionPct)
        Spacer(GlanceModifier.height(10.dp))
        MarkButton(data, compact = false, large = true)
    }
}

@Composable
private fun ProgressBlocks(weekCompletionPct: Int) {
    val filled = (weekCompletionPct.coerceIn(0, 100) / 10).coerceIn(0, 10)
    Column {
        Row {
            repeat(10) { index ->
                Box(
                    modifier = GlanceModifier
                        .width(10.dp)
                        .height(6.dp)
                        .background(
                            if (index < filled) {
                                ColorProvider(day = Color(0xFF2E8C63), night = Color(0xFF6EC8B7))
                            } else {
                                ColorProvider(day = Color(0xFFD8E1DD), night = Color(0xFF1F2926))
                            }
                        )
                ) {}
                if (index < 9) Spacer(GlanceModifier.width(2.dp))
            }
        }
        Spacer(GlanceModifier.height(4.dp))
        Text(
            text = "$weekCompletionPct% this week",
            style = TextStyle(
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = ColorProvider(day = Color(0xFF1F6F64), night = Color(0xFF6EC8B7))
            )
        )
    }
}

@Composable
private fun DayChip(
    label: String,
    status: DayStatus,
    compact: Boolean
) {
    val doneBg = ColorProvider(day = Color(0xFF2E8C63), night = Color(0xFF2E8C63))
    val pendingBg = ColorProvider(day = Color(0xFFE1E9E5), night = Color(0xFF1F2926))
    val idleBg = ColorProvider(day = Color(0xFFD8E1DD), night = Color(0xFF1F2926))
    val doneText = ColorProvider(day = Color.White, night = Color.White)
    val pendingText = ColorProvider(day = Color(0xFF1F6F64), night = Color(0xFF6EC8B7))
    val idleText = ColorProvider(day = Color(0xFF6B7973), night = Color(0xFF8D9B95))
    val side = if (compact) 20.dp else 26.dp
    val textSize = if (compact) 8.sp else 10.sp
    val (bg, textColor) = when (status) {
        DayStatus.DONE, DayStatus.TODAY_DONE -> doneBg to doneText
        DayStatus.TODAY_PENDING -> pendingBg to pendingText
        DayStatus.MISSED, DayStatus.NOT_SCHEDULED, DayStatus.FUTURE -> idleBg to idleText
    }
    Box(
        modifier = GlanceModifier
            .size(side)
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = textSize,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        )
    }
}

@Composable
private fun MarkButton(
    data: WidgetHabitData,
    compact: Boolean,
    large: Boolean = false
) {
    val doneBg = ColorProvider(day = Color(0xFFDAEEE7), night = Color(0x3362B88E))
    val actionBg = ColorProvider(day = Color(0xFF1F6F64), night = Color(0xFF1F6F64))
    val doneText = ColorProvider(day = Color(0xFF2E8C63), night = Color(0xFF62B88E))
    val actionText = ColorProvider(day = Color.White, night = Color.White)
    val textSize = when {
        large -> 14.sp
        compact -> 10.sp
        else -> 12.sp
    }
    val action = actionRunCallback<MarkDoneAction>(
        actionParametersOf(HabitIdParamKey to data.habitId)
    )
    Box(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(if (data.isCompletedToday) doneBg else actionBg)
            .padding(vertical = if (large) 12.dp else 8.dp)
            .clickable(action),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (data.isCompletedToday) "Completed ✓" else "Mark done",
            style = TextStyle(
                fontSize = textSize,
                fontWeight = FontWeight.Bold,
                color = if (data.isCompletedToday) doneText else actionText
            )
        )
    }
}
