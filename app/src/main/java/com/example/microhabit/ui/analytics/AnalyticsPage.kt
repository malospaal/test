package com.example.microhabit.ui.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.microhabit.ui.shared.GlassCard
import com.example.microhabit.HabitUiState
import com.example.microhabit.SurfaceTone
import com.example.microhabit.ui.shared.TaskSelector
import com.example.microhabit.i18n.LocalAppLanguage
import com.example.microhabit.i18n.appLocale
import com.example.microhabit.i18n.t
import com.example.microhabit.i18n.weekdayLabels
import com.example.microhabit.ui.components.AnalyticsMetricTile
import com.example.microhabit.ui.components.HorizontalPercentBars
import com.example.microhabit.ui.components.VerticalPercentBars
import com.example.microhabit.ui.theme.AppTheme
import java.time.LocalDate
import java.time.format.TextStyle


@Composable
internal fun AnalyticsPage(
    state: HabitUiState,
    onSelectTask: (String) -> Unit
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val selectedTask = state.tasks.firstOrNull { it.id == state.selectedTaskId }

    if (selectedTask == null) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(spacing.x2),
            verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
        ) {
            if (state.tasks.isNotEmpty()) {
                item {
                    TaskSelector(
                        tasks = state.tasks,
                        selectedTaskId = state.selectedTaskId,
                        onSelect = onSelectTask
                    )
                }
            }
            item {
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                        Text(
                            text = t("Analytics"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = t("Create and select a habit to view analytics."),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }
        return
    }

    val locale = appLocale()
    val chartAnchorDate = LocalDate.now()
    val weeklyValues = state.last7Days.map { it.coerceIn(0, 100) }
    val weeklyLabels = (6 downTo 0).map { offset ->
        chartAnchorDate.minusDays(offset.toLong()).dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
    }
    val monthlyValues = if (state.monthlyProgress.isEmpty()) listOf(0, 0, 0, 0) else state.monthlyProgress
    val monthlyLabels = monthlyValues.indices.map { "${t("W")}${it + 1}" }
    val weekdayLabels = weekdayLabels(LocalAppLanguage.current)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(spacing.x2),
        verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
    ) {
        item {
            TaskSelector(
                tasks = state.tasks,
                selectedTaskId = state.selectedTaskId,
                onSelect = onSelectTask
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.x1)
            ) {
                AnalyticsMetricTile(
                    label = t("Current streak"),
                    value = "${state.streak}d",
                    modifier = Modifier.weight(1f)
                )
                AnalyticsMetricTile(
                    label = t("Best streak"),
                    value = "${state.bestStreak}d",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.x1)
            ) {
                AnalyticsMetricTile(
                    label = t("7 day completion"),
                    value = "${state.completionRate7Day}%",
                    modifier = Modifier.weight(1f)
                )
                AnalyticsMetricTile(
                    label = t("30 day completion"),
                    value = "${state.completionRate30Day}%",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            AnalyticsMetricTile(
                label = t("Total completions"),
                value = state.totalCompletions.toString(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            GlassCard(tone = SurfaceTone.SECONDARY) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                    Text(
                        text = t("Weekly completion chart"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    VerticalPercentBars(
                        values = weeklyValues,
                        labels = weeklyLabels,
                        highlightIndex = 6
                    )
                }
            }
        }

        item {
            GlassCard(tone = SurfaceTone.SECONDARY) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                    Text(
                        text = t("Monthly progress chart"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    VerticalPercentBars(
                        values = monthlyValues,
                        labels = monthlyLabels
                    )
                }
            }
        }

        item {
            GlassCard(tone = SurfaceTone.SECONDARY) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                    Text(
                        text = t("Weekday consistency"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    HorizontalPercentBars(
                        values = state.weekdayConsistency,
                        labels = weekdayLabels
                    )
                }
            }
        }
    }
}


