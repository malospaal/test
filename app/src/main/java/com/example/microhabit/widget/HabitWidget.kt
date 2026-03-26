package com.example.microhabit.widget

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.LocalState
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.color.ColorProvider as ColorProviderFn
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.microhabit.MainActivity
import com.example.microhabit.R
import com.example.microhabit.data.HabitRepository
import com.example.microhabit.data.TrackingType
import java.time.LocalDate
import java.time.format.TextStyle as DateTextStyle
import java.util.Locale

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
            .background(ImageProvider(R.drawable.widget_bg))
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
                    color = WidgetTextPrimary
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
            .background(ImageProvider(R.drawable.widget_bg))
            .padding(start = 14.dp, end = 14.dp, top = 14.dp, bottom = 8.dp)
    ) {
        SmallHeader(data)

        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .defaultWeight(),
            contentAlignment = Alignment.Center
        ) {
            SmallWeekDots(data)
        }
        Spacer(GlanceModifier.height(4.dp))
        SmallMarkButton(data = data)
    }
}

@Composable
private fun MediumWidgetContent(data: WidgetHabitData) {
    val horizontalPadding = 16.dp
    val dayGap = 4.dp
    val daySide = weekCellSide(horizontalPadding = horizontalPadding, gap = dayGap, min = 10.dp, max = 60.dp)
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.widget_bg))
            .padding(horizontal = horizontalPadding, vertical = 10.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(bottom = 56.dp)
        ) {
            MediumHeader(data)
            Spacer(GlanceModifier.height(8.dp))
            WeekRow(data = data, side = daySide, radius = 8.dp, gap = dayGap, fontSize = 10.sp)
            Spacer(GlanceModifier.height(8.dp))
            ProgressRow(data)
        }
        MarkButton(data = data, fontSize = 13.sp, verticalPadding = 10.dp, bottomInset = 9.dp)
    }
}

@Composable
private fun LargeWidgetContent(data: WidgetHabitData) {
    val horizontalPadding = 16.dp
    val dayGap = 4.dp
    val daySide = weekCellSide(horizontalPadding = horizontalPadding, gap = dayGap, min = 10.dp, max = 60.dp)
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.widget_bg))
            .padding(horizontal = horizontalPadding, vertical = 10.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(bottom = 58.dp)
        ) {
            LargeHeader(data)
            Spacer(GlanceModifier.height(8.dp))
            LargeStatsRow(data)
            Spacer(GlanceModifier.height(8.dp))
            WeekRow(data = data, side = daySide, radius = 9.dp, gap = dayGap, fontSize = 10.sp)
            Spacer(GlanceModifier.height(8.dp))
            ProgressRow(data)

            val insight = buildInsight(data)
            if (insight != null) {
                Spacer(GlanceModifier.height(8.dp))
                InsightRow(insight)
            }
        }
        MarkButton(data = data, fontSize = 13.sp, verticalPadding = 10.dp, bottomInset = 9.dp)
    }
}

@Composable
private fun SmallHeader(data: WidgetHabitData) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = data.emoji, style = TextStyle(fontSize = 18.sp))
            Spacer(GlanceModifier.width(6.dp))
            Text(
                text = data.title,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = WidgetTextPrimary
                ),
                maxLines = 1
            )
        }
        Spacer(GlanceModifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "🔥", style = TextStyle(fontSize = 10.sp))
            Spacer(GlanceModifier.width(3.dp))
            Text(
                text = data.currentStreak.toString(),
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = WidgetStreak
                )
            )
            Spacer(GlanceModifier.width(3.dp))
            Text(
                text = "серия",
                style = TextStyle(
                    fontSize = 13.sp,
                    color = WidgetTextMuted
                )
            )
        }
    }
}

@Composable
private fun SmallWeekDots(
    data: WidgetHabitData
) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(7) { index ->
            val status = data.last7Days.getOrElse(index) { DayStatus.FUTURE }
            Box(
                modifier = GlanceModifier
                    .defaultWeight()
                    .height(28.dp)
                    .padding(
                        start = if (index == 0) 0.dp else 1.dp,
                        end = if (index == 6) 0.dp else 1.dp
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .cornerRadius(4.dp)
                        .background(ImageProvider(smallDotDrawable(status)))
                ) {}
            }
        }
    }
}

@Composable
private fun SmallMarkButton(data: WidgetHabitData) {
    val action = actionRunCallback<MarkDoneAction>(
        actionParametersOf(HabitIdParamKey to data.habitId)
    )
    val background = if (data.isCompletedToday) {
        ImageProvider(R.drawable.widget_small_cta_done)
    } else {
        ImageProvider(R.drawable.widget_small_cta_pending)
    }
    val text = if (data.isCompletedToday) "Выполнено ✓" else "Отметить"
    val textColor = if (data.isCompletedToday) WidgetDayDoneText else WidgetDayTodayPendingBorder

    Box(
        modifier = GlanceModifier
            .fillMaxWidth()
            .height(36.dp)
            .background(background)
            .clickable(action),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        )
    }
}

private fun smallDotDrawable(status: DayStatus): Int {
    return when (status) {
        DayStatus.DONE -> R.drawable.widget_small_dot_done
        DayStatus.TODAY_DONE -> R.drawable.widget_small_dot_today_done
        DayStatus.TODAY_PENDING -> R.drawable.widget_small_dot_today_pending
        DayStatus.MISSED -> R.drawable.widget_small_dot_missed
        DayStatus.FUTURE, DayStatus.NOT_SCHEDULED -> R.drawable.widget_small_dot_future
    }
}

@Composable
private fun MediumHeader(data: WidgetHabitData) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = GlanceModifier.width(170.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = data.emoji, style = TextStyle(fontSize = 20.sp))
                Spacer(GlanceModifier.width(6.dp))
                Text(
                    text = data.title,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = WidgetTextPrimary
                    ),
                    maxLines = 1
                )
            }
            Spacer(GlanceModifier.height(2.dp))
            Text(
                text = buildDateSubtitle(),
                style = TextStyle(
                    fontSize = 11.sp,
                    color = WidgetTextMuted
                ),
                maxLines = 1
            )
        }
        Spacer(GlanceModifier.width(8.dp))
        Row(
            modifier = GlanceModifier
                .background(WidgetSurfaceSecondary)
                .cornerRadius(8.dp)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "🔥", style = TextStyle(fontSize = 12.sp))
            Spacer(GlanceModifier.width(4.dp))
            Text(
                text = data.currentStreak.toString(),
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = WidgetStreak
                )
            )
            Spacer(GlanceModifier.width(3.dp))
            Text(
                text = "дн",
                style = TextStyle(
                    fontSize = 10.sp,
                    color = WidgetTextMuted
                )
            )
        }
    }
}

@Composable
private fun LargeHeader(data: WidgetHabitData) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = GlanceModifier.width(194.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = data.emoji, style = TextStyle(fontSize = 22.sp))
                Spacer(GlanceModifier.width(6.dp))
                Text(
                    text = data.title,
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = WidgetTextPrimary
                    ),
                    maxLines = 1
                )
            }
            Spacer(GlanceModifier.height(2.dp))
            Text(
                text = if (data.isCompletedToday) "Сегодня: выполнено" else "Сегодня: в процессе",
                style = TextStyle(
                    fontSize = 11.sp,
                    color = WidgetTextMuted
                ),
                maxLines = 1
            )
        }
        Spacer(GlanceModifier.width(8.dp))
        Box(
            modifier = GlanceModifier.size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .cornerRadius(20.dp)
                    .background(WidgetProgressTrack),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = GlanceModifier.fillMaxSize(),
                    color = WidgetDayDoneFill
                )
            }
            Text(
                text = "${data.weekCompletionPct.coerceIn(0, 100)}%",
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = WidgetAccent
                )
            )
        }
    }
}

@Composable
private fun LargeStatsRow(data: WidgetHabitData) {
    val statWidth = ((LocalSize.current.width - 44.dp) / 3f).coerceAtLeast(72.dp)
    Row(modifier = GlanceModifier.fillMaxWidth()) {
        StatBlock(
            modifier = GlanceModifier.width(statWidth),
            label = "СЕРИЯ"
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🔥", style = TextStyle(fontSize = 12.sp))
                Spacer(GlanceModifier.width(4.dp))
                Text(
                    text = data.currentStreak.toString(),
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = WidgetStreak
                    )
                )
            }
        }
        Spacer(GlanceModifier.width(6.dp))
        StatBlock(
            modifier = GlanceModifier.width(statWidth),
            label = "РЕКОРД"
        ) {
            Text(
                text = data.currentStreak.toString(),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = WidgetTextPrimary
                )
            )
        }
        Spacer(GlanceModifier.width(6.dp))
        StatBlock(
            modifier = GlanceModifier.width(statWidth),
            label = "7 ДНЕЙ"
        ) {
            Text(
                text = "${data.weekCompletionPct.coerceIn(0, 100)}%",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = WidgetAccent
                )
            )
        }
    }
}

@Composable
private fun StatBlock(
    modifier: GlanceModifier,
    label: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .background(WidgetSurfaceSecondary)
            .cornerRadius(10.dp)
            .padding(horizontal = 6.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 11.sp,
                color = WidgetTextMuted
            ),
            maxLines = 1
        )
        Spacer(GlanceModifier.height(4.dp))
        content()
    }
}

@Composable
private fun WeekRow(
    data: WidgetHabitData,
    side: Dp,
    radius: Dp,
    gap: Dp,
    fontSize: TextUnit,
    showLabels: Boolean = true
) {
    Row(modifier = GlanceModifier.fillMaxWidth()) {
        repeat(7) { index ->
            val status = data.last7Days.getOrElse(index) { DayStatus.FUTURE }
            DayChip(
                label = data.dayLabels.getOrElse(index) { "-" },
                status = status,
                side = side,
                radius = radius,
                fontSize = fontSize,
                showLabel = showLabels
            )
            if (index < 6) Spacer(GlanceModifier.width(gap))
        }
    }
}

@Composable
private fun DayChip(
    label: String,
    status: DayStatus,
    side: Dp,
    radius: Dp,
    fontSize: TextUnit,
    showLabel: Boolean = true
) {
    val fillColor = when (status) {
        DayStatus.DONE -> WidgetDayDoneFill
        DayStatus.TODAY_DONE -> WidgetDayTodayDoneFill
        DayStatus.TODAY_PENDING -> WidgetDayTodayPendingFill
        DayStatus.FUTURE, DayStatus.MISSED, DayStatus.NOT_SCHEDULED -> WidgetDayFutureFill
    }
    val textColor = when (status) {
        DayStatus.DONE -> WidgetDayDoneText
        DayStatus.TODAY_DONE -> WidgetDayTodayDoneText
        DayStatus.TODAY_PENDING -> WidgetDayTodayPendingText
        DayStatus.FUTURE, DayStatus.MISSED, DayStatus.NOT_SCHEDULED -> WidgetDayFutureText
    }
    val borderColor = when (status) {
        DayStatus.TODAY_PENDING -> WidgetDayTodayPendingBorder
        DayStatus.FUTURE, DayStatus.MISSED, DayStatus.NOT_SCHEDULED -> WidgetDayFutureBorder
        else -> null
    }
    val borderWidth = when (status) {
        DayStatus.TODAY_PENDING -> 1.5.dp
        DayStatus.FUTURE, DayStatus.MISSED, DayStatus.NOT_SCHEDULED -> 1.dp
        else -> 0.dp
    }
    val outerRingColor = if (status == DayStatus.TODAY_DONE) WidgetDayTodayDoneOuterRing else null
    val outerRingWidth = if (status == DayStatus.TODAY_DONE) 1.dp else 0.dp

    Box(contentAlignment = Alignment.Center) {
        DayDotShell(
            side = side,
            radius = radius,
            fill = fillColor,
            textColor = textColor,
            label = label,
            fontSize = fontSize,
            showLabel = showLabel,
            borderColor = borderColor,
            borderWidth = borderWidth,
            outerRingColor = outerRingColor,
            outerRingWidth = outerRingWidth
        )
    }
}

@Composable
private fun DayDotShell(
    side: Dp,
    radius: Dp,
    fill: ColorProvider,
    textColor: ColorProvider,
    label: String,
    fontSize: TextUnit,
    showLabel: Boolean,
    borderColor: ColorProvider?,
    borderWidth: Dp,
    outerRingColor: ColorProvider?,
    outerRingWidth: Dp
) {
    val shellModifier = GlanceModifier
        .size(side)
        .cornerRadius(radius)
    val innerRadius = (radius - borderWidth - outerRingWidth).coerceAtLeast(1.dp)

    if (outerRingColor != null && outerRingWidth > 0.dp) {
        Box(
            modifier = shellModifier
                .background(outerRingColor)
                .padding(outerRingWidth),
            contentAlignment = Alignment.Center
        ) {
            DayDotInner(
                modifier = GlanceModifier.fillMaxSize(),
                radius = innerRadius,
                fill = fill,
                textColor = textColor,
                label = label,
                fontSize = fontSize,
                showLabel = showLabel,
                borderColor = borderColor,
                borderWidth = borderWidth
            )
        }
    } else {
        DayDotInner(
            modifier = shellModifier,
            radius = innerRadius,
            fill = fill,
            textColor = textColor,
            label = label,
            fontSize = fontSize,
            showLabel = showLabel,
            borderColor = borderColor,
            borderWidth = borderWidth
        )
    }
}

@Composable
private fun DayDotInner(
    modifier: GlanceModifier,
    radius: Dp,
    fill: ColorProvider,
    textColor: ColorProvider,
    label: String,
    fontSize: TextUnit,
    showLabel: Boolean,
    borderColor: ColorProvider?,
    borderWidth: Dp
) {
    if (borderColor != null && borderWidth > 0.dp) {
        Box(
            modifier = modifier
                .cornerRadius(radius)
                .background(borderColor)
                .padding(borderWidth),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .cornerRadius(radius)
                    .background(fill),
                contentAlignment = Alignment.Center
            ) {
                if (showLabel) {
                    Text(
                        text = label,
                        style = TextStyle(
                            fontSize = fontSize,
                            fontWeight = FontWeight.Medium,
                            color = textColor
                        )
                    )
                }
            }
        }
    } else {
        Box(
            modifier = modifier
                .cornerRadius(radius)
                .background(fill),
            contentAlignment = Alignment.Center
        ) {
            if (showLabel) {
                Text(
                    text = label,
                    style = TextStyle(
                        fontSize = fontSize,
                        fontWeight = FontWeight.Medium,
                        color = textColor
                    )
                )
            }
        }
    }
}

@Composable
private fun ProgressRow(data: WidgetHabitData) {
    val completedDays = data.last7Days.count { it == DayStatus.DONE || it == DayStatus.TODAY_DONE }
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LinearProgressIndicator(
            data.weekCompletionPct.coerceIn(0, 100) / 100f,
            GlanceModifier
                .fillMaxWidth()
                .height(4.dp)
                .cornerRadius(4.dp),
            WidgetDayDoneFill,
            WidgetProgressTrack
        )
    }
    Spacer(GlanceModifier.height(4.dp))
    Box(modifier = GlanceModifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
        Text(
            text = "$completedDays / 7 дн",
            style = TextStyle(
                fontSize = 10.sp,
                color = WidgetTextMuted
            ),
            maxLines = 1
        )
    }
}

private data class WidgetInsight(
    val primary: String,
    val secondary: String
)

private fun buildInsight(data: WidgetHabitData): WidgetInsight? {
    if (data.currentStreak <= 0) return null
    return WidgetInsight(
        primary = "Текущий рекорд: ${data.currentStreak} дней",
        secondary = if (data.isCompletedToday) {
            "Сегодня отмечено. Продолжай серию."
        } else {
            "Отметь сегодня и сохрани серию."
        }
    )
}

@Composable
private fun InsightRow(insight: WidgetInsight) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(WidgetSurfaceSecondary)
            .cornerRadius(10.dp)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "⚡",
            style = TextStyle(fontSize = 14.sp)
        )
        Spacer(GlanceModifier.width(8.dp))
        Column(modifier = GlanceModifier.fillMaxWidth()) {
            Text(
                text = insight.primary,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = WidgetTextPrimary
                ),
                maxLines = 1
            )
            Spacer(GlanceModifier.height(2.dp))
            Text(
                text = insight.secondary,
                style = TextStyle(
                    fontSize = 10.sp,
                    color = WidgetTextMuted
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun MarkButton(
    data: WidgetHabitData,
    fontSize: TextUnit,
    verticalPadding: Dp,
    bottomInset: Dp = 0.dp,
    horizontalInset: Dp = 0.dp
) {
    val action = actionRunCallback<MarkDoneAction>(
        actionParametersOf(HabitIdParamKey to data.habitId)
    )

    if (data.isCompletedToday) {
        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(horizontal = horizontalInset)
                .padding(bottom = bottomInset)
                .cornerRadius(12.dp)
                .background(WidgetDayDoneFill)
                .padding(vertical = verticalPadding)
                .clickable(action),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Выполнено",
                    style = TextStyle(
                        fontSize = fontSize,
                        fontWeight = FontWeight.Medium,
                        color = WidgetDayDoneText
                    )
                )
                Spacer(GlanceModifier.width(6.dp))
                Text(
                    text = "✓",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = WidgetDoneCheckForeground
                    )
                )
            }
        }
    } else {
        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(horizontal = horizontalInset)
                .padding(bottom = bottomInset)
                .clickable(action),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .background(ImageProvider(R.drawable.widget_btn_pending_outline))
                    .padding(vertical = verticalPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Отметить ✓",
                    style = TextStyle(
                        fontSize = fontSize,
                        fontWeight = FontWeight.Medium,
                        color = WidgetDayTodayPendingBorder
                    )
                )
            }
        }
    }
}

@Composable
private fun weekCellSide(
    horizontalPadding: Dp,
    gap: Dp,
    min: Dp,
    max: Dp
): Dp {
    val width = LocalSize.current.width
    val raw = (width - horizontalPadding - horizontalPadding - (gap * 6)) / 7f
    return raw.coerceAtLeast(min).coerceAtMost(max)
}

private fun buildDateSubtitle(): String {
    val now = LocalDate.now()
    val locale = Locale.getDefault()
    val dow = now.dayOfWeek.getDisplayName(DateTextStyle.SHORT, locale)
    val month = now.month.getDisplayName(DateTextStyle.SHORT, locale)
    return "$dow, ${now.dayOfMonth} $month"
}

private fun singleTone(color: Color): ColorProvider = ColorProviderFn(day = color, night = color)

private val WidgetBackground = singleTone(Color(0xFF0F2318))
private val WidgetSurfaceSecondary = singleTone(Color(0xFF0A1F13))
private val WidgetDayDoneFill = singleTone(Color(0xFF1D9E75))
private val WidgetDayDoneText = singleTone(Color(0xFFE1F5EE))
private val WidgetDayTodayDoneFill = singleTone(Color(0xFF2DCF96))
private val WidgetDayTodayDoneText = singleTone(Color(0xFF04342C))
private val WidgetDayTodayDoneOuterRing = singleTone(Color(0xFF0F2318))
private val WidgetDayTodayPendingFill = singleTone(Color(0xFF152E1F))
private val WidgetDayTodayPendingText = singleTone(Color(0xFF2DCF96))
private val WidgetDayTodayPendingBorder = singleTone(Color(0xFF2DCF96))
private val WidgetDayFutureFill = singleTone(Color(0xFF0F2318))
private val WidgetDayFutureText = singleTone(Color(0xFF2A5A3A))
private val WidgetDayFutureBorder = singleTone(Color(0xFF2A5A3A))
private val WidgetTextMuted = singleTone(Color(0xFF6AAA85))
private val WidgetTextPrimary = singleTone(Color(0xFFE8F5EF))
private val WidgetStreak = singleTone(Color(0xFFF59E42))
private val WidgetAccent = singleTone(Color(0xFF2DCF96))
private val WidgetProgressTrack = singleTone(Color(0xFF152E1F))
private val WidgetDoneCheckForeground = singleTone(Color(0xFFFFFFFF))
private val WidgetSmallInnerCard = singleTone(Color(0xFF063220))
