package com.example.microhabit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.microhabit.ui.theme.AppTheme
import java.time.LocalDate

enum class CalendarDayState {
    COMPLETED,
    MISSED,
    TODAY,
    FUTURE
}

@Composable
fun CalendarDay(
    date: LocalDate?,
    state: CalendarDayState,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val stroke = AppTheme.stroke
    val colors = AppTheme.colors
    val height = spacing.x5 + spacing.x0_5

    if (date == null) {
        Box(modifier = modifier.height(height))
        return
    }

    val borderColor = when {
        selected -> colors.primary
        state == CalendarDayState.TODAY -> colors.calendarTodayRing
        else -> Color.Transparent
    }
    val backgroundColor = when {
        selected -> colors.primary
        state == CalendarDayState.COMPLETED -> colors.successMuted
        state == CalendarDayState.MISSED -> colors.danger.copy(alpha = 0.12f)
        state == CalendarDayState.TODAY -> colors.primaryMuted
        else -> Color.Transparent
    }
    val textColor = when {
        selected -> MaterialTheme.colorScheme.onPrimary
        state == CalendarDayState.COMPLETED -> colors.success
        state == CalendarDayState.MISSED -> colors.danger
        state == CalendarDayState.TODAY -> colors.primary
        else -> colors.textSecondary
    }
    val markerColor = when {
        selected -> MaterialTheme.colorScheme.onPrimary
        state == CalendarDayState.COMPLETED -> colors.success
        state == CalendarDayState.MISSED -> colors.danger
        state == CalendarDayState.TODAY -> colors.primary
        else -> Color.Transparent
    }

    Column(
        modifier = modifier
            .height(height)
            .border(stroke.thin, borderColor, RoundedCornerShape(radius.sm))
            .background(backgroundColor, RoundedCornerShape(radius.sm))
            .clickable(enabled = enabled, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            modifier = Modifier.padding(top = spacing.x0_5)
        )
        Box(
            modifier = Modifier
                .padding(top = spacing.x0_5)
                .size(spacing.x0_5)
                .background(markerColor, CircleShape)
        )
    }
}
