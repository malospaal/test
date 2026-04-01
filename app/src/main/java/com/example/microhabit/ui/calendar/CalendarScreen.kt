package com.example.microhabit.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.microhabit.ui.shared.GlassCard
import com.example.microhabit.HabitUiState
import com.example.microhabit.MainViewModel
import com.example.microhabit.SurfaceTone
import com.example.microhabit.ui.shared.HabitSelectorRow
import com.example.microhabit.ui.components.CalendarDayState
import com.example.microhabit.ui.theme.AppTheme
import com.example.microhabit.i18n.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import com.example.microhabit.ui.shared.CalendarHeaderRow
import com.example.microhabit.data.AppLanguage


@Composable
internal fun CalendarScreen(state: HabitUiState, vm: MainViewModel) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val isDarkTheme = AppTheme.colors.backgroundCanvas.red < 0.2f
    val locale = appLocale()
    val today = LocalDate.now()
    val taskById = remember(state.allTasks) { state.allTasks.associateBy { it.id } }
    val calendarFilterHabits = remember(state.calendarFilterOptions, taskById) {
        state.calendarFilterOptions.mapNotNull { option -> taskById[option.taskId] }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(spacing.x2),
        verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
    ) {
        item {
            if (calendarFilterHabits.isEmpty()) {
                GlassCard(tone = SurfaceTone.SECONDARY) {
                    Text(
                        text = t("No active or completed habits yet."),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                }
            } else {
                HabitSelectorRow(
                    habits = calendarFilterHabits,
                    selectedId = state.calendarFilterTaskId,
                    onHabitSelected = vm::setCalendarFilterTask,
                    onCreateHabit = null,
                    showAllHabitsOption = true,
                    onSelectAll = { vm.setCalendarFilterTask(null) },
                    showCountLabel = false
                )
            }
        }

        item {
            GlassCard(modifier = Modifier.height(416.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {

                    CalendarHeaderRow(
                        monthLabel = localizedMonthYear(state.currentMonth, state.language, locale),
                        isTodaySelected = state.selectedDate == today && state.currentMonth == YearMonth.now(),
                        onPrev = { vm.moveMonth(-1) },
                        onToday = vm::jumpToToday,
                        onNext = { vm.moveMonth(1) },
                        todayButtonBorderColor = colors.borderSubtle.copy(alpha = if (isDarkTheme) 0.90f else 0.95f),
                        todayButtonTextColor = colors.textSecondary,
                        todayButtonHeight = 34.dp,
                        todayButtonBorderWidth = 1.2.dp
                    )
                    val weekdayLabels = weekdayLabels(LocalAppLanguage.current)
                    val weekdayLabelColor = colors.textSecondary
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                    ) {
                        weekdayLabels.forEach { label ->
                            Text(
                                text = label,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall,
                                color = weekdayLabelColor
                            )
                        }
                    }

                    monthGrid(state.currentMonth).forEach { week ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            week.forEach { dayDate ->
                                GlobalCalendarHeatCell(
                                    modifier = Modifier.weight(1f),
                                    date = dayDate,
                                    selected = dayDate == state.selectedDate,
                                    today = dayDate == today,
                                    completedCount = if (dayDate != null) {
                                        state.calendarCompletedCountByDate[dayDate] ?: 0
                                    } else {
                                        0
                                    },
                                    scheduledCount = if (dayDate != null) {
                                        state.calendarScheduledCountByDate[dayDate] ?: 0
                                    } else {
                                        0
                                    },
                                    manualOverrideCount = if (dayDate != null) {
                                        state.calendarManualOverrideCountByDate[dayDate] ?: 0
                                    } else {
                                        0
                                    },
                                    onClick = { dayDate?.let(vm::selectDate) }
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            BreakdownCard(
                selectedDate = state.selectedDate,
                completedCount = state.calendarBreakdownCompletedCount,
                scheduledCount = state.calendarBreakdownScheduledCount,
                items = state.calendarBreakdownItems
            )
        }
    }
}

@Composable
internal fun GlobalCalendarHeatCell(
    date: LocalDate?,
    selected: Boolean,
    today: Boolean,
    completedCount: Int,
    scheduledCount: Int,
    manualOverrideCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val stroke = AppTheme.stroke
    val colors = AppTheme.colors

    if (date == null) {
        Box(
            modifier = modifier
                .height(spacing.x5 + spacing.x0_5)
        )
        return
    }

    val todayDate = LocalDate.now()
    val isToday = today || date.isEqual(todayDate)
    val isFuture = date.isAfter(todayDate)
    val hasManualOverride = manualOverrideCount > 0 && scheduledCount <= 0 && !isFuture && !isToday

    val fillColor = when {
        isFuture -> colors.backgroundSurfaceMuted.copy(alpha = 0.25f)
        completedCount > 0 -> colors.success.copy(alpha = 0.90f)
        hasManualOverride -> colors.successMuted.copy(alpha = 0.70f)
        scheduledCount > 0 -> colors.danger.copy(alpha = 0.08f)
        else -> colors.neutralMuted.copy(alpha = 0.40f)
    }

    val baseBorderColor = when {
        isToday -> colors.calendarTodayRing
        isFuture -> colors.borderSubtle.copy(alpha = 0.35f)
        completedCount > 0 -> Color.Transparent
        hasManualOverride -> colors.success.copy(alpha = 0.28f)
        scheduledCount > 0 -> colors.danger.copy(alpha = 0.28f)
        else -> colors.borderSubtle.copy(alpha = 0.70f)
    }

    val baseBorderWidth = when {
        isToday -> stroke.thin
        baseBorderColor.alpha > 0f -> stroke.thin
        else -> 0.dp
    }

    val borderColor = if (selected) colors.primary else baseBorderColor
    val borderWidth = if (selected) stroke.medium else baseBorderWidth

    val textColor = when {
        isToday -> colors.primary
        isFuture -> colors.textTertiary
        completedCount > 0 -> MaterialTheme.colorScheme.onPrimary
        hasManualOverride -> colors.success
        scheduledCount > 0 -> colors.danger
        else -> colors.textSecondary
    }

    Box(
        modifier = modifier
            .height(spacing.x5 + spacing.x0_5)
            .clip(RoundedCornerShape(radius.md))
            .background(fillColor, RoundedCornerShape(radius.md))
            .then(
                if (borderWidth > 0.dp) {
                    Modifier.border(
                        width = borderWidth,
                        color = borderColor,
                        shape = RoundedCornerShape(radius.md)
                    )
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            fontWeight = if (selected || isToday || completedCount > 0 || hasManualOverride) {
                FontWeight.SemiBold
            } else {
                FontWeight.Medium
            }
        )
    }
}

internal fun dayStateFor(
    date: LocalDate?,
    doneDates: Set<LocalDate>,
    partialDates: Set<LocalDate>,
    scheduledDates: Set<LocalDate>,
    today: LocalDate
): CalendarDayState {
    if (date == null) return CalendarDayState.FUTURE
    if (date.isAfter(today)) return CalendarDayState.FUTURE
    if (date in doneDates) return CalendarDayState.COMPLETED
    if (date in partialDates) return CalendarDayState.PARTIAL
    if (date in scheduledDates && date.isBefore(today)) return CalendarDayState.MISSED
    return CalendarDayState.NOT_SCHEDULED
}

internal fun statusLabel(state: CalendarDayState, language: AppLanguage): String {
    return when (state) {
        CalendarDayState.COMPLETED -> translate(language, "Completed")
        CalendarDayState.PARTIAL -> translate(language, "Partial")
        CalendarDayState.MISSED -> translate(language, "Missed")
        CalendarDayState.NOT_SCHEDULED -> translate(language, "Not scheduled")
        CalendarDayState.FUTURE -> translate(language, "Future")
    }
}


internal fun localizedMonthYear(month: YearMonth, language: AppLanguage, locale: Locale): String {
    val raw = month.format(DateTimeFormatter.ofPattern(translate(language, "LLLL yyyy"), locale))
    val shouldCapitalize = language == AppLanguage.RU || language == AppLanguage.UK || language == AppLanguage.CS
    if (!shouldCapitalize || raw.isEmpty()) return raw
    return raw.replaceFirstChar { first ->
        if (first.isLowerCase()) first.titlecase(locale) else first.toString()
    }
}

@Composable

internal fun monthGrid(month: YearMonth): List<List<LocalDate?>> {
    val firstDay = month.atDay(1)
    val leadingEmpty = firstDay.dayOfWeek.value - 1
    val days = mutableListOf<LocalDate?>()
    repeat(leadingEmpty) { days += null }
    repeat(month.lengthOfMonth()) { index -> days += month.atDay(index + 1) }
    while (days.size % 7 != 0) days += null
    return days.chunked(7)
}

