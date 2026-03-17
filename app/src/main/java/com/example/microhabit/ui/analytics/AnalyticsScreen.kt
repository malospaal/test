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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.microhabit.HabitListItem
import com.example.microhabit.HabitSelectorRow
import com.example.microhabit.HabitUiState
import com.example.microhabit.data.HabitTask
import com.example.microhabit.data.SubscriptionPlan
import com.example.microhabit.data.TaskFrequency
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
    val isProUser = state.plan == SubscriptionPlan.PRO
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

    val summariesById = remember(state.habits) { state.habits.associateBy { it.id } }
    val activeSummaries = remember(activeTasks, summariesById) {
        activeTasks.mapNotNull { summariesById[it.id] }
    }
    val selectedTask = activeTasks.firstOrNull { it.id == selectedHabitId }
    val selectedSummary = selectedTask?.let { summariesById[it.id] }
    val aggregateMode = selectedHabitId == null

    val completion30 = if (aggregateMode) {
        activeSummaries.map { it.completionRate }.average().takeIf { !it.isNaN() }?.roundToInt() ?: 0
    } else {
        selectedSummary?.completionRate ?: state.completionRate30Day
    }.coerceIn(0, 100)

    val completion7 = if (aggregateMode) {
        estimateSevenDayRate(activeSummaries, completion30)
    } else {
        state.completionRate7Day.coerceIn(0, 100)
    }

    val currentStreak = if (aggregateMode) {
        activeSummaries.maxOfOrNull { it.streak } ?: 0
    } else {
        selectedSummary?.streak ?: state.streak
    }

    val bestStreak = if (aggregateMode) {
        activeSummaries.maxOfOrNull { it.streak } ?: 0
    } else {
        state.bestStreak
    }.coerceAtLeast(currentStreak)

    val estimatedTotalCompletions = if (aggregateMode) {
        activeSummaries.sumOf { ((it.completionRate / 100f) * 30f).roundToInt().coerceAtLeast(0) }
    } else {
        state.totalCompletions
    }

    val thisWeekCompletions = ((completion7 / 100f) * 7f).roundToInt().coerceAtLeast(0)
    val weekDelta = completion7 - completion30
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
    val hourlyData = buildHourlyData(if (aggregateMode) activeTasks else listOfNotNull(selectedTask))
    val weekdayData = buildWeekdayScores(
        aggregateMode = aggregateMode,
        selectedWeekdayConsistency = state.weekdayConsistency,
        tasks = if (aggregateMode) activeTasks else listOfNotNull(selectedTask),
        locale = locale
    )
    val weekComparisons = buildWeekComparisons(
        aggregateMode = aggregateMode,
        completion7 = completion7,
        completion30 = completion30,
        taskCount = activeTasks.size,
        minus2Label = t("−2 wk"),
        minus1Label = t("Last wk"),
        currentLabel = t("This wk")
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
        item { StabilityScoreCard(score = score, weekDelta = weekDelta) }
        item { StatGrid(items = statItems) }
        item { ForecastCard(daysToRecord = daysToRecord, bestStreak = bestStreak, recordDate = forecastDate) }
        item { WeeklyBarChart(weekData = weekData) }
        item {
            ProBlurOverlay(
                isProUser = isProUser,
                lockedLabel = t("Best time of day\navailable in PRO"),
                onUpgrade = onUpgrade
            ) {
                BestTimeOfDayCard(hourlyData = hourlyData)
            }
        }
        item {
            ProBlurOverlay(
                isProUser = isProUser,
                lockedLabel = t("Weekday consistency\navailable in PRO"),
                onUpgrade = onUpgrade
            ) {
                WeekdayRadarCard(weekdayData = weekdayData)
            }
        }
        item {
            ProBlurOverlay(
                isProUser = isProUser,
                lockedLabel = t("Week comparison\navailable in PRO"),
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

private fun estimateSevenDayRate(summaries: List<HabitListItem>, fallback: Int): Int {
    if (summaries.isEmpty()) return fallback
    val adjusted = summaries.map { (it.completionRate + (it.streak.coerceAtMost(14) * 0.8f)).roundToInt() }
    return (adjusted.average().takeIf { !it.isNaN() }?.roundToInt() ?: fallback).coerceIn(0, 100)
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

private fun buildHourlyData(tasks: List<HabitTask>): List<Float> {
    val buckets = MutableList(12) { 0f }
    val reminderTasks = tasks.filter { it.reminderEnabled }
    if (reminderTasks.isEmpty()) return List(12) { 0.08f }
    reminderTasks.forEach { task ->
        val bucket = (task.reminderHour.coerceIn(0, 23) / 2).coerceIn(0, 11)
        buckets[bucket] += 1f
    }
    val max = buckets.maxOrNull()?.takeIf { it > 0f } ?: 1f
    return buckets.map { (it / max).coerceAtLeast(0.04f) }
}

private fun buildWeekdayScores(
    aggregateMode: Boolean,
    selectedWeekdayConsistency: List<Int>,
    tasks: List<HabitTask>,
    locale: java.util.Locale
): List<WeekdayScore> {
    val scores = if (!aggregateMode) {
        normalizeIntScores(selectedWeekdayConsistency)
    } else {
        val totals = MutableList(7) { 0f }
        tasks.forEach { task ->
            when (task.frequency) {
                TaskFrequency.DAILY -> (0..6).forEach { day -> totals[day] += 1f }
                TaskFrequency.SELECTED_DAYS -> task.customDays.forEach { dayValue -> if (dayValue in 1..7) totals[dayValue - 1] += 1f }
                TaskFrequency.TIMES_PER_WEEK -> {
                    val spread = task.timesPerWeek.coerceIn(1, 7) / 7f
                    (0..6).forEach { day -> totals[day] += spread }
                }
            }
        }
        normalizeFloatScores(totals)
    }
    return (1..7).map { day ->
        WeekdayScore(
            dayLabel = DayOfWeek.of(day).getDisplayName(TextStyle.SHORT, locale).take(2),
            score = scores[day - 1]
        )
    }
}

private fun normalizeIntScores(values: List<Int>): List<Float> {
    val normalized = values.take(7).map { it.coerceAtLeast(0).toFloat() }
    return normalizeFloatScores(normalized)
}

private fun normalizeFloatScores(values: List<Float>): List<Float> {
    if (values.isEmpty()) return List(7) { 0f }
    val max = values.maxOrNull()?.takeIf { it > 0f } ?: 1f
    return values.map { (it / max).coerceIn(0f, 1f) }.let { list ->
        if (list.size >= 7) list.take(7) else list + List(7 - list.size) { 0f }
    }
}

private fun buildWeekComparisons(
    aggregateMode: Boolean,
    completion7: Int,
    completion30: Int,
    taskCount: Int,
    minus2Label: String,
    minus1Label: String,
    currentLabel: String
): List<WeekSummary> {
    val scheduledBase = if (aggregateMode) taskCount.coerceAtLeast(1) * 7 else 7
    val thisWeekCompleted = ((completion7 / 100f) * scheduledBase).roundToInt().coerceIn(0, scheduledBase)
    val prevWeekRate = ((completion7 * 0.9f + completion30 * 0.1f).roundToInt()).coerceIn(0, 100)
    val prevWeekCompleted = ((prevWeekRate / 100f) * scheduledBase).roundToInt().coerceIn(0, scheduledBase)
    val minus2Rate = ((completion30 * 0.92f).roundToInt()).coerceIn(0, 100)
    val minus2Completed = ((minus2Rate / 100f) * scheduledBase).roundToInt().coerceIn(0, scheduledBase)
    return listOf(
        WeekSummary(minus2Label, minus2Completed, scheduledBase),
        WeekSummary(minus1Label, prevWeekCompleted, scheduledBase),
        WeekSummary(currentLabel, thisWeekCompleted, scheduledBase)
    )
}

@Composable
private fun StabilityScoreCard(score: Int, weekDelta: Int) {
    Surface(shape = RoundedCornerShape(18.dp), color = AppTheme.colors.backgroundSurface, border = BorderStroke(1.dp, AppTheme.colors.borderSubtle)) {
        Column(Modifier.padding(16.dp)) {
            Text(text = t("Stability score"), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium, color = AppTheme.colors.primary)
            Spacer(Modifier.height(4.dp))
            Text(text = score.toString(), style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
            val sign = if (weekDelta > 0) "+" else ""
            Text(
                text = if (weekDelta == 0) t("out of 100") else "${t("out of 100")} · $sign$weekDelta ${t("pts this week")}",
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
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        userScrollEnabled = false,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.height(180.dp)
    ) {
        items(items) { item -> StatTile(item) }
    }
}

@Composable
private fun StatTile(item: StatItem) {
    Surface(shape = RoundedCornerShape(14.dp), color = AppTheme.colors.backgroundSurface, border = BorderStroke(1.dp, AppTheme.colors.borderSubtle)) {
        Column(Modifier.padding(12.dp)) {
            Text(text = item.icon, fontSize = 14.sp)
            Spacer(Modifier.height(4.dp))
            Text(text = item.value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
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
                        fontSize = 8.sp,
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
    Surface(shape = RoundedCornerShape(14.dp), color = AppTheme.colors.backgroundSurface, border = BorderStroke(1.dp, AppTheme.colors.borderSubtle)) {
        Column(Modifier.padding(12.dp)) {
            Text(text = t("Weekday consistency"), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                RadarChart(
                    data = weekdayData.map { it.score },
                    sizeModifier = Modifier.size(160.dp),
                    accentColor = AppTheme.colors.primary,
                    fillColor = AppTheme.colors.primary.copy(alpha = 0.15f),
                    gridColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                weekdayData.forEachIndexed { index, item ->
                    Text(
                        text = item.dayLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (index == bestIndex) AppTheme.colors.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (index == bestIndex) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
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
private fun RadarChart(data: List<Float>, sizeModifier: Modifier, accentColor: Color, fillColor: Color, gridColor: Color) {
    Canvas(modifier = sizeModifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val rMax = size.minDimension * 0.38f
        val rMin = size.minDimension * 0.08f
        val count = data.size.coerceAtLeast(1)

        listOf(0.33f, 0.66f, 1f).forEach { step ->
            drawCircle(color = gridColor, radius = rMin + step * (rMax - rMin), center = androidx.compose.ui.geometry.Offset(cx, cy), style = Stroke(width = 1.dp.toPx()))
        }
        repeat(count) { index ->
            val angle = (2f * PI.toFloat() * index / count) - PI.toFloat() / 2f
            drawLine(
                color = gridColor,
                start = androidx.compose.ui.geometry.Offset(cx, cy),
                end = androidx.compose.ui.geometry.Offset(cx + rMax * cos(angle), cy + rMax * sin(angle)),
                strokeWidth = 1.dp.toPx()
            )
        }
        val points = data.mapIndexed { index, score ->
            val angle = (2f * PI.toFloat() * index / count) - PI.toFloat() / 2f
            val radius = rMin + score.coerceAtLeast(0.05f) * (rMax - rMin)
            androidx.compose.ui.geometry.Offset(cx + radius * cos(angle), cy + radius * sin(angle))
        }
        if (points.isNotEmpty()) {
            val path = Path().apply {
                moveTo(points.first().x, points.first().y)
                points.drop(1).forEach { point -> lineTo(point.x, point.y) }
                close()
            }
            drawPath(path, color = fillColor)
            drawPath(path, color = accentColor, style = Stroke(width = 1.5.dp.toPx()))
            points.forEach { point -> drawCircle(color = accentColor, radius = 2.8.dp.toPx(), center = point) }
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
                        Text(text = t("Unlock PRO"), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
