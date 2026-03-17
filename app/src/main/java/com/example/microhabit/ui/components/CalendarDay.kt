package com.example.microhabit.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.microhabit.ui.theme.AppTheme
import androidx.compose.ui.unit.dp
import java.time.LocalDate

enum class CalendarDayState {
    COMPLETED,
    PARTIAL,
    MISSED,
    NOT_SCHEDULED,
    FUTURE
}

@Composable
fun CalendarDay(
    date: LocalDate?,
    state: CalendarDayState,
    selected: Boolean,
    today: Boolean,
    connectLeft: Boolean,
    connectRight: Boolean,
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

    val completionProgress by animateFloatAsState(
        targetValue = if (state == CalendarDayState.COMPLETED) 1f else 0f,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "calendarCompletionProgress"
    )
    val connectorProgress by animateFloatAsState(
        targetValue = if (
            state == CalendarDayState.COMPLETED &&
            (connectLeft || connectRight)
        ) {
            if (connectLeft && connectRight) 1f else 0.7f
        } else {
            0f
        },
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "calendarConnectorProgress"
    )

    val fillColor = when (state) {
        CalendarDayState.COMPLETED -> colors.success.copy(alpha = 0.90f)
        CalendarDayState.PARTIAL -> colors.successMuted.copy(alpha = 0.70f)
        CalendarDayState.MISSED -> colors.danger.copy(alpha = 0.08f)
        CalendarDayState.NOT_SCHEDULED -> colors.neutralMuted.copy(alpha = 0.40f)
        CalendarDayState.FUTURE -> colors.backgroundSurfaceMuted.copy(alpha = 0.25f)
    }
    val borderColor = when (state) {
        CalendarDayState.COMPLETED -> Color.Transparent
        CalendarDayState.PARTIAL -> colors.success.copy(alpha = 0.28f)
        CalendarDayState.MISSED -> colors.danger.copy(alpha = 0.28f)
        CalendarDayState.NOT_SCHEDULED -> colors.borderSubtle.copy(alpha = 0.7f)
        CalendarDayState.FUTURE -> colors.borderSubtle.copy(alpha = 0.35f)
    }
    val textColor = when (state) {
        CalendarDayState.COMPLETED -> MaterialTheme.colorScheme.onPrimary
        CalendarDayState.PARTIAL -> colors.success
        CalendarDayState.MISSED -> colors.danger
        CalendarDayState.NOT_SCHEDULED -> colors.textSecondary
        CalendarDayState.FUTURE -> colors.textTertiary
    }
    val cellShape = RoundedCornerShape(
        topStart = if (state == CalendarDayState.COMPLETED && connectLeft) 4.dp else radius.md,
        topEnd = if (state == CalendarDayState.COMPLETED && connectRight) 4.dp else radius.md,
        bottomEnd = if (state == CalendarDayState.COMPLETED && connectRight) 4.dp else radius.md,
        bottomStart = if (state == CalendarDayState.COMPLETED && connectLeft) 4.dp else radius.md
    )
    val popScale = remember(date) { Animatable(1f) }
    var wasCompleted by remember(date) { mutableStateOf(state == CalendarDayState.COMPLETED) }
    LaunchedEffect(state, today, date) {
        val isCompletedNow = state == CalendarDayState.COMPLETED
        if (today && isCompletedNow && !wasCompleted) {
            popScale.snapTo(0.8f)
            popScale.animateTo(
                targetValue = 1.1f,
                animationSpec = tween(durationMillis = 130, easing = FastOutSlowInEasing)
            )
            popScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 130, easing = FastOutSlowInEasing)
            )
        } else if (!isCompletedNow) {
            popScale.snapTo(1f)
        }
        wasCompleted = isCompletedNow
    }
    val completionScale = if (state == CalendarDayState.COMPLETED) 0.94f + (completionProgress * 0.06f) else 1f
    val cellScale = completionScale * popScale.value

    val selectedRingWidthPx = with(androidx.compose.ui.platform.LocalDensity.current) {
        stroke.medium.toPx()
    }
    val todayRingWidthPx = with(androidx.compose.ui.platform.LocalDensity.current) {
        stroke.thin.toPx()
    }

    Box(
        modifier = modifier
            .height(height)
            .scale(cellScale)
            .clickable(enabled = enabled, onClick = onClick)
            .drawBehind {
                if (today) {
                    drawRoundRect(
                        color = colors.calendarTodayRing,
                        cornerRadius = CornerRadius(radius.md.toPx(), radius.md.toPx()),
                        style = Stroke(width = todayRingWidthPx)
                    )
                }
                if (selected) {
                    drawRoundRect(
                        color = colors.primary,
                        cornerRadius = CornerRadius(radius.md.toPx(), radius.md.toPx()),
                        style = Stroke(width = selectedRingWidthPx)
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(connectorProgress)
                .height(spacing.x1)
                .background(colors.successMuted.copy(alpha = completionProgress), RoundedCornerShape(radius.full))
                .align(Alignment.Center)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .padding(horizontal = spacing.x0_5)
                .background(fillColor, cellShape)
                .drawBehind {
                    if (borderColor.alpha > 0f) {
                        drawRoundRect(
                            color = borderColor,
                            cornerRadius = CornerRadius(radius.md.toPx(), radius.md.toPx()),
                            style = Stroke(width = stroke.thin.toPx())
                        )
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                fontWeight = if (selected || today || state == CalendarDayState.COMPLETED || state == CalendarDayState.PARTIAL) {
                    androidx.compose.ui.text.font.FontWeight.SemiBold
                } else {
                    androidx.compose.ui.text.font.FontWeight.Medium
                }
            )
        }
    }
}
