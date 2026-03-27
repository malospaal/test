package com.example.microhabit.ui.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.microhabit.HabitSelectorRow
import com.example.microhabit.HabitUiState
import com.example.microhabit.data.AnalyticsWeekSummary
import com.example.microhabit.data.hasPremiumAccess
import com.example.microhabit.i18n.appLocale
import com.example.microhabit.i18n.t
import com.example.microhabit.ui.theme.AppTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private data class StatItem(
    val icon: String,
    val value: String,
    val label: String,
    val delta: String? = null,
    val deltaPositive: Boolean? = null
)

private data class DayCompletion(
    val date: LocalDate,
    val completionRate: Float
)

private data class WeekdayScore(
    val dayLabel: String,
    val score: Float
)

private data class WeekSummary(
    val label: String,
    val completed: Int,
    val scheduled: Int
)

@Composable
fun AnalyticsScreen(
    state: HabitUiState,
    onSelectTask: (String) -> Unit,
    onUpgrade: () -> Unit
) {
    val spacing = AppTheme.spacing
    val semantic = AppTheme.colors
    val locale = appLocale()
    val isProUser = state.plan.hasPremiumAccess()
    val activeTasks = state.tasks
    var selectedHabitId by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(activeTasks, selectedHabitId) {
        if (selectedHabitId != null && activeTasks.none { it.id == selectedHabitId }) {
            selectedHabitId = null
        }
    }

    if (activeTasks.isEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(spacing.x2),
            verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = semantic.backgroundSurface,
                    border = BorderStroke(1.dp, semantic.borderSubtle)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = t("Analytics"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = t("Create and select a habit to view analytics."),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        return
    }

    val aggregateMode = selectedHabitId == null

    val completion30 = if (aggregateMode) {
        state.analyticsAggregateCompletionRate30Day
    } else {
        state.completionRate30Day
    }.coerceIn(0, 100)

    val completion7 = if (aggregateMode) {
        state.analyticsAggregateCompletionRate7Day
    } else {
        state.completionRate7Day.coerceIn(0, 100)
    }

    val currentStreak = if (aggregateMode) {
        state.analyticsAggregateCurrentStreak
    } else {
        state.streak
    }

    val bestStreak = if (aggregateMode) {
        state.analyticsAggregateBestStreak
    } else {
        state.bestStreak
    }.coerceAtLeast(currentStreak)

    val estimatedTotalCompletions = if (aggregateMode) {
        state.analyticsAggregateTotalCompletions
    } else {
        state.totalCompletions
    }

    val weekComparisons = buildWeekComparisons(
        summaries = if (aggregateMode) state.analyticsAggregateWeekSummaries else state.analyticsSelectedWeekSummaries,
        minus2Label = t("−2 wk"),
        minus1Label = t("Last wk"),
        currentLabel = t("This wk")
    )
    val thisWeekCompletions = weekComparisons.lastOrNull()?.completed ?: 0
    val score = calculateStabilityScore(completion30, currentStreak, bestStreak)
    val daysToRecord = calculateDaysToRecord(currentStreak, bestStreak)
    val forecastDate = daysToRecord?.let { LocalDate.now().plusDays(it.toLong()) }

    val statItems = buildStatItems(
        currentStreak = currentStreak,
        bestStreak = bestStreak,
        completion30 = completion30,
        completion7 = completion7,
        totalCompletions = estimatedTotalCompletions,
        thisWeekCompletions = thisWeekCompletions,
        currentStreakLabel = t("Current streak"),
        bestStreakLabelTemplate = t("Best: %dd"),
        rate30Label = t("30-day rate"),
        totalCompletionsLabel = t("Total completions"),
        thisWeekTemplate = t("↑ %d this week"),
        rate7Label = t("7-day rate")
    )
    val weekData = buildWeekCompletionData(
        aggregateMode = aggregateMode,
        baseRate = completion7,
        selectedLast7 = state.last7Days
    )
    val hourlyCounts = if (aggregateMode) state.analyticsAggregateHourlyCounts else state.analyticsSelectedHourlyCounts
    val hourlyData = buildHourlyData(hourlyCounts)
    val hasTimeData = hourlyCounts.any { it > 0 }
    val weekdayData = buildWeekdayScores(
        consistency = if (aggregateMode) state.analyticsAggregateWeekdayConsistency else state.weekdayConsistency,
        locale = locale
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(spacing.x2),
        verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
    ) {
        item {
            HabitSelectorRow(
                habits = activeTasks,
                selectedId = selectedHabitId,
                onHabitSelected = { habitId ->
                    selectedHabitId = habitId
                    onSelectTask(habitId)
                },
                onCreateHabit = null,
                showAllHabitsOption = true,
                onSelectAll = { selectedHabitId = null },
                showCountLabel = false
            )
        }
        item { StabilityScoreCard(score = score) }
        item { StatGrid(items = statItems) }
        item { ForecastCard(daysToRecord = daysToRecord, bestStreak = bestStreak, recordDate = forecastDate) }
        item { WeeklyBarChart(weekData = weekData) }
        if (hasTimeData) {
            item {
                ProBlurOverlay(
                    isProUser = isProUser,
                    lockedLabel = t("Best time of day\navailable in Premium"),
                    onUpgrade = onUpgrade
                ) {
                    BestTimeOfDayCard(hourlyData = hourlyData)
                }
            }
        }
        item {
            ProBlurOverlay(
                isProUser = isProUser,
                lockedLabel = t("Weekday consistency\navailable in Premium"),
                onUpgrade = onUpgrade
            ) {
                WeekdayRadarCard(weekdayData = weekdayData)
            }
        }
        item {
            ProBlurOverlay(
                isProUser = isProUser,
                lockedLabel = t("Week comparison\navailable in Premium"),
                onUpgrade = onUpgrade
            ) {
                WeekCompareCard(weeks = weekComparisons)
            }
        }
    }
}

private fun calculateStabilityScore(completion30Rate: Int, currentStreak: Int, bestStreak: Int): Int {
    val completion30 = completion30Rate.coerceIn(0, 100) / 100f
    val streakRatio = if (bestStreak > 0) (currentStreak.toFloat() / bestStreak.toFloat()).coerceIn(0f, 1f) else 0f
    val raw = (completion30 * 0.7f + streakRatio * 0.3f) * 100f
    return raw.roundToInt().coerceIn(0, 100)
}

private fun calculateDaysToRecord(currentStreak: Int, bestStreak: Int): Int? {
    if (bestStreak <= 0 || currentStreak >= bestStreak) return null
    return (bestStreak - currentStreak).coerceAtLeast(0)
}

private fun formatDelta(current: Int, previous: Int): Pair<String, Boolean> {
    val diff = current - previous
    val sign = if (diff > 0) "+" else ""
    val up = diff >= 0
    return "${if (up) "↑" else "↓"} $sign$diff%" to up
}

private fun buildStatItems(
    currentStreak: Int,
    bestStreak: Int,
    completion30: Int,
    completion7: Int,
    totalCompletions: Int,
    thisWeekCompletions: Int,
    currentStreakLabel: String,
    bestStreakLabelTemplate: String,
    rate30Label: String,
    totalCompletionsLabel: String,
    thisWeekTemplate: String,
    rate7Label: String
): List<StatItem> {
    val (delta30, delta30Positive) = formatDelta(completion30, completion7)
    val (delta7, delta7Positive) = formatDelta(completion7, completion30)
    return listOf(
        StatItem("🔥", "${currentStreak}d", currentStreakLabel, bestStreakLabelTemplate.format(bestStreak), true),
        StatItem("📅", "$completion30%", rate30Label, delta30, delta30Positive),
        StatItem("✅", totalCompletions.toString(), totalCompletionsLabel, thisWeekTemplate.format(thisWeekCompletions), true),
        StatItem("⚡", "$completion7%", rate7Label, delta7, delta7Positive)
    )
}

private fun buildWeekCompletionData(aggregateMode: Boolean, baseRate: Int, selectedLast7: List<Int>): List<DayCompletion> {
    val today = LocalDate.now()
    return (0..6).map { index ->
        val date = today.minusDays((6 - index).toLong())
        val rate = if (aggregateMode) {
            val wave = (index - 3) * 0.03f
            (baseRate / 100f + wave).coerceIn(0.05f, 1f)
        } else {
            (selectedLast7.getOrNull(index) ?: baseRate).coerceIn(0, 100) / 100f
        }
        DayCompletion(date, rate)
    }
}

private fun buildHourlyData(hourlyCounts: List<Int>): List<Float> {
    val max = hourlyCounts.maxOrNull()?.takeIf { it > 0 } ?: return List(12) { 0f }
    return hourlyCounts
        .take(12)
        .map { count -> (count.toFloat() / max.toFloat()).coerceIn(0f, 1f) }
        .let { values ->
            if (values.size >= 12) values else values + List(12 - values.size) { 0f }
        }
}

private fun buildWeekdayScores(
    consistency: List<Int>,
    locale: java.util.Locale
): List<WeekdayScore> {
    val scores = consistency
        .take(7)
        .map { percent -> percent.coerceIn(0, 100) / 100f }
        .let { values -> if (values.size >= 7) values else values + List(7 - values.size) { 0f } }
    return (1..7).map { day ->
        WeekdayScore(
            dayLabel = DayOfWeek.of(day).getDisplayName(TextStyle.SHORT, locale).take(2),
            score = scores[day - 1]
        )
    }
}

private fun buildWeekComparisons(
    summaries: List<AnalyticsWeekSummary>,
    minus2Label: String,
    minus1Label: String,
    currentLabel: String
): List<WeekSummary> {
    val source = summaries.takeLast(3)
    val padded = when {
        source.size >= 3 -> source
        source.isEmpty() -> List(3) { AnalyticsWeekSummary(completed = 0, scheduled = 0, weekStart = LocalDate.now()) }
        else -> List(3 - source.size) { source.first() } + source
    }
    return listOf(
        WeekSummary(minus2Label, padded[0].completed, padded[0].scheduled),
        WeekSummary(minus1Label, padded[1].completed, padded[1].scheduled),
        WeekSummary(currentLabel, padded[2].completed, padded[2].scheduled)
    )
}

@Composable
private fun StabilityScoreCard(score: Int) {
    Surface(shape = RoundedCornerShape(18.dp), color = AppTheme.colors.backgroundSurface, border = BorderStroke(1.dp, AppTheme.colors.borderSubtle)) {
        Column(Modifier.padding(16.dp)) {
            Text(text = t("Stability score"), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium, color = AppTheme.colors.primary)
            Spacer(Modifier.height(4.dp))
            Text(text = score.toString(), style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
            Text(
                text = t("out of 100"),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier.fillMaxHeight().fillMaxWidth(score / 100f).background(
                        Brush.horizontalGradient(colors = listOf(AppTheme.colors.primary, AppTheme.colors.success))
                    )
                )
            }
            Spacer(Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("0", "25", "50", "▲$score", "100").forEachIndexed { index, label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        color = if (index == 3) AppTheme.colors.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (index == 3) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun StatGrid(items: List<StatItem>) {
    val rows = ((items.size + 1) / 2).coerceAtLeast(1)
    // Each tile can have up to 4 text lines; 112dp row height avoids clipping on small screens.
    val gridHeight = (rows * 112 + (rows - 1) * 8).dp
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        userScrollEnabled = false,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.height(gridHeight)
    ) {
        items(items) { item -> StatTile(item) }
    }
}

@Composable
private fun StatTile(item: StatItem) {
    Surface(shape = RoundedCornerShape(14.dp), color = AppTheme.colors.backgroundSurface, border = BorderStroke(1.dp, AppTheme.colors.borderSubtle)) {
        Column(Modifier.padding(10.dp)) {
            Text(text = item.icon, fontSize = 14.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                text = item.value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = item.label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            item.delta?.let { delta ->
                Text(
                    text = delta,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = if (item.deltaPositive == true) AppTheme.colors.success else AppTheme.colors.danger
                )
            }
        }
    }
}

@Composable
private fun ForecastCard(daysToRecord: Int?, bestStreak: Int, recordDate: LocalDate?) {
    if (daysToRecord == null) return
    Surface(shape = RoundedCornerShape(14.dp), color = AppTheme.colors.primaryMuted, border = BorderStroke(1.dp, AppTheme.colors.primary.copy(alpha = 0.3f))) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "🎯", fontSize = 20.sp)
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    text = t("%d more days — new record").format(daysToRecord),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.primary
                )
                recordDate?.let { date ->
                    Text(
                        text = t("Best is %dd. At this pace you'll beat it on %s.").format(
                            bestStreak,
                            date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(appLocale()))
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun WeeklyBarChart(weekData: List<DayCompletion>) {
    val locale = appLocale()
    Surface(shape = RoundedCornerShape(14.dp), color = AppTheme.colors.backgroundSurface, border = BorderStroke(1.dp, AppTheme.colors.borderSubtle)) {
        Column(Modifier.padding(12.dp)) {
            Text(text = t("This week"), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth().height(64.dp), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                weekData.forEach { day ->
                    val isToday = day.date.isEqual(LocalDate.now())
                    Box(
                        modifier = Modifier.weight(1f).fillMaxHeight(day.completionRate.coerceAtLeast(0.04f)).clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(if (isToday) AppTheme.colors.primary else AppTheme.colors.primary.copy(alpha = 0.35f))
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                weekData.forEach { day ->
                    val isToday = day.date.isEqual(LocalDate.now())
                    Text(
                        text = day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, locale).take(2),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = if (isToday) AppTheme.colors.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isToday) FontWeight.SemiBold else FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun BestTimeOfDayCard(hourlyData: List<Float>) {
    val peakIndex = hourlyData.indices.maxByOrNull { hourlyData[it] } ?: 0
    val peakHour = peakIndex * 2
    val peakLabel = "$peakHour:00-${peakHour + 2}:00"
    Surface(shape = RoundedCornerShape(14.dp), color = AppTheme.colors.backgroundSurface, border = BorderStroke(1.dp, AppTheme.colors.borderSubtle)) {
        Column(Modifier.padding(12.dp)) {
            Text(text = t("Best time of day"), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth().height(48.dp), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                hourlyData.forEachIndexed { index, value ->
                    Box(
                        modifier = Modifier.weight(1f).fillMaxHeight(value.coerceAtLeast(0.04f)).clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                            .background(if (index == peakIndex) AppTheme.colors.primary else AppTheme.colors.primary.copy(alpha = 0.25f))
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Surface(shape = RoundedCornerShape(8.dp), color = AppTheme.colors.primaryMuted) {
                Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⏰", fontSize = 13.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = t("You usually complete habits at %s. Set a reminder.").format(peakLabel),
                        style = MaterialTheme.typography.labelSmall,
                        color = AppTheme.colors.primary,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekdayRadarCard(weekdayData: List<WeekdayScore>) {
    val bestIndex = weekdayData.indices.maxByOrNull { weekdayData[it].score } ?: 0
    val weakDays = weekdayData.filter { it.score < 0.2f }.joinToString(", ") { it.dayLabel }
    val semantic = AppTheme.colors
    Surface(shape = RoundedCornerShape(14.dp), color = AppTheme.colors.backgroundSurface, border = BorderStroke(1.dp, AppTheme.colors.borderSubtle)) {
        Column(Modifier.padding(12.dp)) {
            Text(text = t("Weekday consistency"), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                RadarChart(
                    data = weekdayData.map { it.score },
                    labels = weekdayData.map { it.dayLabel },
                    bestIndex = bestIndex,
                    size = 160.dp,
                    accentColor = semantic.primary,
                    fillColor = semantic.primary.copy(alpha = 0.15f),
                    gridColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    bestLabelColor = semantic.primary
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = if (weakDays.isBlank()) "${t("Best day")}: ${weekdayData.getOrNull(bestIndex)?.dayLabel.orEmpty()}"
                else "${t("Best day")}: ${weekdayData.getOrNull(bestIndex)?.dayLabel.orEmpty()} · ${t("Weak days")}: $weakDays",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RadarChart(
    data: List<Float>,
    labels: List<String>,
    bestIndex: Int,
    size: androidx.compose.ui.unit.Dp = 160.dp,
    accentColor: Color,
    fillColor: Color,
    gridColor: Color,
    labelColor: Color,
    bestLabelColor: Color
) {
    val density = LocalDensity.current
    Canvas(modifier = Modifier.size(size)) {
        val count = data.size.coerceAtLeast(1)
        val cx = size.toPx() / 2f
        val cy = size.toPx() / 2f
        val radiusMax = size.toPx() * 0.36f
        val radiusMin = size.toPx() * 0.06f
        val labelOffset = size.toPx() * 0.13f

        fun pointAngle(index: Int): Float = (2f * PI.toFloat() * index / count) - PI.toFloat() / 2f
        fun dataPoint(index: Int): androidx.compose.ui.geometry.Offset {
            val value = data.getOrElse(index) { 0f }.coerceIn(0f, 1f)
            val radius = radiusMin + value * (radiusMax - radiusMin)
            return androidx.compose.ui.geometry.Offset(
                x = cx + radius * cos(pointAngle(index)),
                y = cy + radius * sin(pointAngle(index))
            )
        }
        fun labelPoint(index: Int): androidx.compose.ui.geometry.Offset {
            val radius = radiusMax + labelOffset
            return androidx.compose.ui.geometry.Offset(
                x = cx + radius * cos(pointAngle(index)),
                y = cy + radius * sin(pointAngle(index))
            )
        }

        listOf(0.33f, 0.66f, 1f).forEach { step ->
            drawCircle(
                color = gridColor,
                radius = radiusMin + step * (radiusMax - radiusMin),
                center = androidx.compose.ui.geometry.Offset(cx, cy),
                style = Stroke(width = 1.dp.toPx())
            )
        }
        repeat(count) { index ->
            drawLine(
                color = gridColor,
                start = androidx.compose.ui.geometry.Offset(cx, cy),
                end = androidx.compose.ui.geometry.Offset(
                    x = cx + radiusMax * cos(pointAngle(index)),
                    y = cy + radiusMax * sin(pointAngle(index))
                ),
                strokeWidth = 1.dp.toPx()
            )
        }

        val points = List(count) { index -> dataPoint(index) }
        if (points.isNotEmpty()) {
            val path = Path().apply {
                moveTo(points.first().x, points.first().y)
                points.drop(1).forEach { point -> lineTo(point.x, point.y) }
                close()
            }
            drawPath(path, color = fillColor)
            drawPath(
                path = path,
                color = accentColor,
                style = Stroke(width = 1.5.dp.toPx(), join = StrokeJoin.Round)
            )
            points.forEachIndexed { index, point ->
                val alpha = if (data.getOrElse(index) { 0f } < 0.05f) 0.25f else 1f
                drawCircle(
                    color = accentColor.copy(alpha = alpha),
                    radius = 3.dp.toPx(),
                    center = point
                )
            }
        }

        val textPaint = android.graphics.Paint().apply {
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
            typeface = android.graphics.Typeface.DEFAULT
        }
        repeat(count) { index ->
            val text = labels.getOrElse(index) { "" }
            val isBest = index == bestIndex
            val color = if (isBest) bestLabelColor else labelColor
            textPaint.textSize = with(density) { if (isBest) 11.5.sp.toPx() else 10.5.sp.toPx() }
            textPaint.color = android.graphics.Color.argb(
                (color.alpha * 255).toInt(),
                (color.red * 255).toInt(),
                (color.green * 255).toInt(),
                (color.blue * 255).toInt()
            )
            textPaint.isFakeBoldText = isBest
            val label = labelPoint(index)
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    text,
                    label.x,
                    label.y + textPaint.textSize / 3f,
                    textPaint
                )
            }
        }
    }
}

@Composable
private fun WeekCompareCard(weeks: List<WeekSummary>) {
    Surface(shape = RoundedCornerShape(14.dp), color = AppTheme.colors.backgroundSurface, border = BorderStroke(1.dp, AppTheme.colors.borderSubtle)) {
        Column(Modifier.padding(12.dp)) {
            Text(text = t("Week over week"), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                weeks.forEachIndexed { index, week ->
                    val isCurrentWeek = index == weeks.lastIndex
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isCurrentWeek) AppTheme.colors.primaryMuted else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (isCurrentWeek) BorderStroke(1.dp, AppTheme.colors.primary.copy(alpha = 0.4f)) else null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${week.completed}/${week.scheduled}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isCurrentWeek) AppTheme.colors.primary else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = week.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProBlurOverlay(
    isProUser: Boolean,
    lockedLabel: String,
    onUpgrade: () -> Unit,
    content: @Composable () -> Unit
) {
    Box {
        Box(modifier = if (isProUser) Modifier else Modifier.blur(6.dp)) {
            content()
        }
        if (!isProUser) {
            Box(
                modifier = Modifier.matchParentSize().clip(RoundedCornerShape(14.dp)).background(AppTheme.colors.backgroundSurface.copy(alpha = 0.75f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "🔒", fontSize = 18.sp)
                    Text(
                        text = lockedLabel,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = onUpgrade,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.primary, contentColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(text = t("Get Premium"), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
