package com.example.microhabit.ui.habitdetail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.microhabit.*
import com.example.microhabit.data.HabitTask
import com.example.microhabit.data.TrackingType
import com.example.microhabit.i18n.*
import com.example.microhabit.ui.components.AnalyticsMetricTile
import com.example.microhabit.ui.theme.AppTheme
import java.time.DayOfWeek
import java.time.format.TextStyle
import kotlinx.coroutines.delay
import com.example.microhabit.ui.shared.GlassCard
import com.example.microhabit.ui.shared.StreakRewardOverlay
import com.example.microhabit.ui.shared.isStreakMilestone


@Composable
internal fun HabitDetailPage(
    state: HabitUiState,
    vm: MainViewModel
) {
    val spacing = AppTheme.spacing
    val selectedTask = state.tasks.firstOrNull { it.id == state.selectedTaskId }
    var noteDraft by remember(state.selectedTaskId, state.selectedTaskNote) {
        mutableStateOf(state.selectedTaskNote)
    }
    var streakOverlay by remember { mutableStateOf<StreakOverlayModel?>(null) }
    var overlayVisible by remember { mutableStateOf(false) }
    var previousTotalCompletions by remember(state.selectedTaskId) { mutableStateOf(state.totalCompletions) }

    LaunchedEffect(state.selectedTaskId, state.selectedTaskNote) {
        noteDraft = state.selectedTaskNote
    }
    LaunchedEffect(state.selectedTaskId) {
        previousTotalCompletions = state.totalCompletions
        overlayVisible = false
        streakOverlay = null
    }
    LaunchedEffect(state.totalCompletions, state.streak) {
        if (state.totalCompletions > previousTotalCompletions) {
            streakOverlay = StreakOverlayModel(
                streak = state.streak,
                milestone = isStreakMilestone(state.streak)
            )
            overlayVisible = true
        }
        previousTotalCompletions = state.totalCompletions
    }
    LaunchedEffect(overlayVisible, streakOverlay) {
        if (overlayVisible && streakOverlay != null) {
            delay(1100)
            overlayVisible = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (selectedTask == null) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(spacing.x2),
                verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
            ) {
                item {
                    GlassCard {
                        Text(
                            text = t("No active habit"),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(spacing.x2),
                verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
            ) {
                item {
                    HabitDepthHero(
                        task = selectedTask,
                        streak = state.streak,
                        weeklyCompletion = state.completionRate7Day
                    )
                }
                item {
                    HabitLevelProgressCard(
                        streak = state.streak,
                        bestStreak = state.bestStreak
                    )
                }
                item {
                    HabitDepthStats(
                        trackingType = selectedTask.trackingType,
                        streak = state.streak,
                        bestStreak = state.bestStreak,
                        completion30Day = state.completionRate30Day,
                        totalCompletions = state.totalCompletions,
                        totalTrackedValue = state.totalTrackedValue,
                        averageTrackedValue = state.averageTrackedValue,
                        unitLabel = if (selectedTask.trackingType == TrackingType.DURATION) t("min") else selectedTask.unitLabel
                    )
                }
                item {
                    HabitStreakHistoryCard(
                        bestStreak = state.bestStreak,
                        history = state.streakHistory
                    )
                }
                item {
                    HabitInsightsCard(
                        mostConsistentWeekday = state.mostConsistentWeekday,
                        hardestWeekday = state.hardestWeekday,
                        completionConsistency = state.completionConsistency
                    )
                }
                item {
                    val notePlaceholder = when {
                        state.todayDone -> t("notes_placeholder_done")
                        state.todayScheduled && !state.todayDone -> t("notes_placeholder_missed")
                        else -> t("notes_placeholder_default")
                    }
                    HabitNotesCard(
                        note = noteDraft,
                        placeholder = notePlaceholder,
                        onNoteChange = { value ->
                            noteDraft = value
                            vm.setSelectedTaskNote(value)
                        },
                        onSave = vm::saveSelectedTaskNote
                    )
                }
            }

            AnimatedVisibility(
                visible = overlayVisible && streakOverlay != null,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = spacing.x1_5),
                enter = fadeIn(tween(170)) + slideInVertically(initialOffsetY = { -it / 3 }, animationSpec = tween(170)),
                exit = fadeOut(tween(170)) + slideOutVertically(targetOffsetY = { -it / 4 }, animationSpec = tween(170))
            ) {
                streakOverlay?.let { model ->
                    StreakRewardOverlay(model = model)
                }
            }
        }
    }
}

internal data class HabitLevelInfo(
    val achievedLevel: Int,
    val nextLevel: Int,
    val progressToNext: Float,
    val daysToNext: Int
)

internal fun levelInfoForStreak(streak: Int): HabitLevelInfo {
    val value = streak.coerceAtLeast(0)
    val baseMilestones = listOf(7, 30, 60, 120)

    fun thresholdForLevel(level: Int): Int {
        if (level <= 0) return 0
        if (level <= baseMilestones.size) return baseMilestones[level - 1]
        return 120 + (level - baseMilestones.size) * 30
    }

    val achievedLevel = generateSequence(1) { it + 1 }
        .takeWhile { thresholdForLevel(it) <= value }
        .lastOrNull() ?: 0
    val nextLevel = achievedLevel + 1
    val currentThreshold = thresholdForLevel(achievedLevel)
    val nextThreshold = thresholdForLevel(nextLevel)
    val progressToNext = ((value - currentThreshold).toFloat() / (nextThreshold - currentThreshold).coerceAtLeast(1))
        .coerceIn(0f, 1f)
    val daysToNext = (nextThreshold - value).coerceAtLeast(0)

    return HabitLevelInfo(
        achievedLevel = achievedLevel,
        nextLevel = nextLevel,
        progressToNext = progressToNext,
        daysToNext = daysToNext
    )
}

@Composable
internal fun HabitDepthHero(
    task: HabitTask,
    streak: Int,
    weeklyCompletion: Int
) {
    val spacing = AppTheme.spacing
    val semantic = AppTheme.colors
    val animatedRing by animateFloatAsState(
        targetValue = (weeklyCompletion / 100f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing),
        label = "habitsDepthRingProgress"
    )

    GlassCard(contentPadding = PaddingValues(spacing.x2)) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.x1_5)) {
            Text(
                text = "${task.emoji.ifBlank { "✨" }} ${task.title}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = semantic.textPrimary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.x2),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(132.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.fillMaxSize(),
                        color = semantic.borderSubtle,
                        strokeWidth = 8.dp
                    )
                    CircularProgressIndicator(
                        progress = { animatedRing },
                        modifier = Modifier.fillMaxSize(),
                        color = semantic.success,
                        strokeWidth = 9.dp
                    )
                    AnimatedContent(targetState = streak, label = "habitsDepthStreakValue") { value ->
                        Text(
                            text = "🔥 $value",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = semantic.textPrimary
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(spacing.x1)
                ) {
                    Text(
                        text = tf("%d day streak", streak),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = semantic.textPrimary
                    )
                    Text(
                        text = tf("Weekly completion: %d%%", weeklyCompletion),
                        style = MaterialTheme.typography.bodyMedium,
                        color = semantic.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
internal fun HabitLevelProgressCard(
    streak: Int,
    bestStreak: Int
) {
    val spacing = AppTheme.spacing
    val semantic = AppTheme.colors
    val info = remember(streak) { levelInfoForStreak(streak) }
    val animatedProgress by animateFloatAsState(
        targetValue = info.progressToNext,
        animationSpec = tween(durationMillis = 360, easing = FastOutSlowInEasing),
        label = "habitLevelProgress"
    )

    GlassCard(contentPadding = PaddingValues(spacing.x2)) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
            Text(
                text = t("Level progress"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = semantic.textPrimary
            )
            Text(
                text = tf("Level %d", info.achievedLevel),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = semantic.textPrimary
            )
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(AppTheme.radius.full)),
                color = semantic.success,
                trackColor = semantic.backgroundSurfaceMuted
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = tf("Best streak: %d", bestStreak),
                    style = MaterialTheme.typography.bodySmall,
                    color = semantic.textSecondary
                )
                Text(
                    text = if (info.daysToNext > 0) {
                        tf("%d days to level %d", info.daysToNext, info.nextLevel)
                    } else {
                        tf("Level %d unlocked", info.nextLevel)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = semantic.textSecondary
                )
            }
        }
    }
}

@Composable
internal fun HabitDepthStats(
    trackingType: TrackingType,
    streak: Int,
    bestStreak: Int,
    completion30Day: Int,
    totalCompletions: Int,
    totalTrackedValue: Int,
    averageTrackedValue: Int,
    unitLabel: String
) {
    val spacing = AppTheme.spacing

    GlassCard(contentPadding = PaddingValues(spacing.x1)) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
            Text(
                text = t("Stats"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.x1)
            ) {
                AnalyticsMetricTile(
                    label = t("Current streak"),
                    value = "${streak}d",
                    modifier = Modifier.weight(1f)
                )
                AnalyticsMetricTile(
                    label = t("Best streak"),
                    value = "${bestStreak}d",
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.x1)
            ) {
                AnalyticsMetricTile(
                    label = t("30 day completion"),
                    value = "$completion30Day%",
                    modifier = Modifier.weight(1f)
                )
                AnalyticsMetricTile(
                    label = t("Total completions"),
                    value = totalCompletions.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
            if (trackingType != TrackingType.YES_NO) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                ) {
                    AnalyticsMetricTile(
                        label = t("Total value"),
                        value = if (unitLabel.isBlank()) {
                            totalTrackedValue.toString()
                        } else {
                            "$totalTrackedValue $unitLabel"
                        },
                        modifier = Modifier.weight(1f)
                    )
                    AnalyticsMetricTile(
                        label = t("Average per day"),
                        value = if (unitLabel.isBlank()) {
                            averageTrackedValue.toString()
                        } else {
                            "$averageTrackedValue $unitLabel"
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
internal fun HabitStreakHistoryCard(
    bestStreak: Int,
    history: List<Int>
) {
    val spacing = AppTheme.spacing
    val semantic = AppTheme.colors
    val previous = remember(history) {
        history.filter { it > 0 }.take(3)
    }

    GlassCard(contentPadding = PaddingValues(spacing.x2)) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
            Text(
                text = t("Streak history"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = semantic.textPrimary
            )
            Text(
                text = "🔥 ${bestStreak} ${t("days")}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = semantic.textPrimary
            )
            Text(
                text = t("Previous streaks"),
                style = MaterialTheme.typography.bodySmall,
                color = semantic.textSecondary
            )
            if (previous.isEmpty()) {
                Text(
                    text = t("No streak history yet"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = semantic.textSecondary
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                ) {
                    previous.forEach { value ->
                        Surface(
                            color = semantic.backgroundSurfaceMuted,
                            shape = RoundedCornerShape(AppTheme.radius.full)
                        ) {
                            Text(
                                text = "🔥 $value",
                                modifier = Modifier.padding(horizontal = spacing.x1, vertical = spacing.x0_5),
                                style = MaterialTheme.typography.bodyMedium,
                                color = semantic.textPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun HabitInsightsCard(
    mostConsistentWeekday: Int?,
    hardestWeekday: Int?,
    completionConsistency: Int
) {
    val spacing = AppTheme.spacing
    val semantic = AppTheme.colors
    val locale = appLocale()
    val noDataLabel = t("No data")

    fun weekdayLabelOrFallback(value: Int?): String {
        if (value == null || value !in 1..7) return noDataLabel
        return DayOfWeek.of(value).getDisplayName(TextStyle.SHORT, locale).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(locale) else it.toString()
        }
    }

    GlassCard(contentPadding = PaddingValues(spacing.x2)) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
            Text(
                text = t("Insights"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = semantic.textPrimary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.x1)
            ) {
                AnalyticsMetricTile(
                    label = t("Most consistent day"),
                    value = weekdayLabelOrFallback(mostConsistentWeekday),
                    modifier = Modifier.weight(1f)
                )
                AnalyticsMetricTile(
                    label = t("Hardest day"),
                    value = weekdayLabelOrFallback(hardestWeekday),
                    modifier = Modifier.weight(1f)
                )
            }
            AnalyticsMetricTile(
                label = t("Completion consistency"),
                value = "$completionConsistency%",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
internal fun HabitNotesCard(
    note: String,
    placeholder: String,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit
) {
    val spacing = AppTheme.spacing
    val semantic = AppTheme.colors

    GlassCard(contentPadding = PaddingValues(spacing.x2)) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
            Text(
                text = t("Habit notes"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = semantic.textPrimary
            )
            OutlinedTextField(
                value = note,
                onValueChange = { onNoteChange(it.take(180)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4,
                label = { Text(placeholder) },
                supportingText = {
                    Text(
                        text = "${note.length}/180",
                        color = semantic.textSecondary
                    )
                }
            )
            Button(
                onClick = onSave,
                shape = RoundedCornerShape(AppTheme.radius.md),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(t("Save note"))
            }
        }
    }
}




