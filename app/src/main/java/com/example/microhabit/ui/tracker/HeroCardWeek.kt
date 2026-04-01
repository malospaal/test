package com.example.microhabit.ui.tracker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.microhabit.data.HabitTask
import com.example.microhabit.data.TrackingType
import com.example.microhabit.i18n.LocalAppLanguage
import com.example.microhabit.i18n.appLocale
import com.example.microhabit.i18n.t
import com.example.microhabit.i18n.weekdayLabels
import com.example.microhabit.ui.calendar.dayStateFor
import com.example.microhabit.ui.calendar.localizedMonthYear
import com.example.microhabit.ui.calendar.monthGrid
import com.example.microhabit.ui.components.CalendarDay
import com.example.microhabit.ui.shared.CalendarHeaderRow
import com.example.microhabit.ui.shared.GlassCard
import com.example.microhabit.ui.theme.AppTheme
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
@Composable
internal fun HeroMiniWeekRow(
    points: List<Int>,
    scheduled: List<Boolean>,
    manualOverride: List<Boolean>,
    trackingType: TrackingType,
    anchorDate: LocalDate,
    todayShortLabel: String
) {
    val semantic = AppTheme.colors
    val locale = appLocale()
    val isDark = isSystemInDarkTheme()
    val lightMissedFill = Color(0xFFC0392B).copy(alpha = 0.08f)
    val lightMissedBorder = Color(0xFFC0392B).copy(alpha = 0.35f)
    val normalizedPoints = points.takeLast(7).let { if (it.size == 7) it else List(7 - it.size) { 0 } + it }
    val normalizedScheduled = scheduled.takeLast(7).let { if (it.size == 7) it else List(7 - it.size) { false } + it }
    val normalizedManualOverride = manualOverride.takeLast(7).let {
        if (it.size == 7) it else List(7 - it.size) { false } + it
    }
    val dates = (6L downTo 0L).map { offset -> anchorDate.minusDays(offset) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        for (index in dates.indices) {
            val date = dates[index]
            val value = normalizedPoints.getOrElse(index) { 0 }.coerceIn(0, 100)
            val isScheduled = normalizedScheduled.getOrElse(index) { false }
            val isManualOverride = normalizedManualOverride.getOrElse(index) { false }
            val isToday = date == LocalDate.now()
            val isFuture = date.isAfter(LocalDate.now())
            val isCompleted = when (trackingType) {
                TrackingType.YES_NO -> value >= 100
                TrackingType.COUNT, TrackingType.DURATION -> value >= 100
            }
            val isPartial = !isCompleted && value > 0 && isScheduled && !isFuture
            val isMissed = isScheduled && !isFuture && !isToday && !isCompleted && !isPartial
            val dayColor = when {
                isFuture -> MaterialTheme.colorScheme.surfaceVariant
                !isScheduled && isManualOverride -> semantic.success.copy(alpha = 0.55f)
                !isScheduled -> MaterialTheme.colorScheme.surfaceVariant
                isCompleted -> semantic.success
                isPartial -> semantic.success.copy(alpha = 0.45f)
                isToday -> Color.Transparent
                isMissed && !isDark -> lightMissedFill
                else -> semantic.chartMissed
            }
            val missedBorderColor = if (isMissed && !isDark) lightMissedBorder else null
            val showTodayBorder = isToday && isScheduled && !isFuture && !isCompleted && !isPartial
            val dayLabel = if (isToday) {
                todayShortLabel
            } else {
                date.dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
            }
            DayDot(
                modifier = Modifier.weight(1f),
                label = dayLabel,
                fillColor = dayColor,
                isToday = isToday,
                showTodayBorder = showTodayBorder,
                todayBorderColor = semantic.primary,
                customBorderColor = missedBorderColor
            )
        }
    }
}

@Composable
private fun DayDot(
    modifier: Modifier = Modifier,
    label: String,
    fillColor: Color,
    isToday: Boolean,
    showTodayBorder: Boolean,
    todayBorderColor: Color,
    customBorderColor: Color? = null
) {
    val semantic = AppTheme.colors
    val borderModifier = when {
        showTodayBorder -> Modifier.border(1.dp, todayBorderColor, RoundedCornerShape(6.dp))
        customBorderColor != null -> Modifier.border(1.5.dp, customBorderColor, RoundedCornerShape(6.dp))
        else -> Modifier
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.76f)
                .align(Alignment.CenterHorizontally)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(6.dp))
                .background(fillColor)
                .then(borderModifier)
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (isToday) todayBorderColor else semantic.textTertiary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Clip
        )
    }
}

@Composable
internal fun CalendarCard(
    month: YearMonth,
    selectedDate: LocalDate,
    selectedTask: HabitTask?,
    doneDates: Set<LocalDate>,
    partialDates: Set<LocalDate>,
    scheduledDates: Set<LocalDate>,
    onMoveMonth: (Long) -> Unit,
    onToday: () -> Unit,
    onDateSelect: (LocalDate) -> Unit
) {
    val spacing = AppTheme.spacing
    val semantic = AppTheme.colors
    val language = LocalAppLanguage.current
    val locale = appLocale()
    val today = LocalDate.now()

    GlassCard(
        modifier = Modifier.height(416.dp),
        contentPadding = PaddingValues(spacing.x2)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
            CalendarHeaderRow(
                monthLabel = localizedMonthYear(month, language, locale),
                isTodaySelected = selectedDate == today && month == YearMonth.now(),
                onPrev = { onMoveMonth(-1) },
                onToday = onToday,
                onNext = { onMoveMonth(1) }
            )

            val days = weekdayLabels(LocalAppLanguage.current)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.x1)
            ) {
                days.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        color = semantic.textSecondary
                    )
                }
            }

            monthGrid(month).forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    week.forEachIndexed { _, day ->
                        val state = dayStateFor(
                            date = day,
                            doneDates = doneDates,
                            partialDates = partialDates,
                            scheduledDates = scheduledDates,
                            today = today
                        )
                        val dayDate = day
                        val done = dayDate != null && dayDate in doneDates
                        CalendarDay(
                            modifier = Modifier.weight(1f),
                            date = dayDate,
                            state = state,
                            selected = dayDate == selectedDate,
                            today = dayDate == today,
                            connectLeft = done && dayDate != null && dayDate.minusDays(1) in doneDates,
                            connectRight = done && dayDate != null && dayDate.plusDays(1) in doneDates,
                            enabled = selectedTask != null,
                            onClick = { dayDate?.let(onDateSelect) }
                        )
                    }
                }
            }
        }
    }
}



