package com.example.microhabit.ui.tracker

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.microhabit.DOTS_SPRING_DAMPING
import com.example.microhabit.DOTS_SPRING_STIFFNESS
import com.example.microhabit.ui.theme.AppTheme

private data class HabitPageDotModel(
    val widthDp: Float,
    val color: Color
)

@Composable
fun HabitPageDots(
    total: Int,
    current: Int,
    modifier: Modifier = Modifier
) {
    if (total <= 1) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 2.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val dotItems = buildDotItems(total = total, current = current)
        dotItems.forEachIndexed { index, item ->
            if (index > 0) Spacer(Modifier.width(5.dp))
            HabitPageDot(item = item)
        }
    }
}

@Composable
private fun buildDotItems(total: Int, current: Int): List<HabitPageDotModel> {
    val maxDots = 5
    val activeColor = AppTheme.colors.primary
    val inactiveColor = if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.56f)
    }

    return if (total <= maxDots) {
        List(total) { index ->
            HabitPageDotModel(
                widthDp = if (index == current) 16f else 6f,
                color = if (index == current) activeColor else inactiveColor
            )
        }
    } else {
        val clampedCurrent = current.coerceIn(0, total - 1)
        val windowStart = (clampedCurrent - 2).coerceIn(0, total - maxDots)
        val windowEnd = windowStart + maxDots - 1

        (windowStart..windowEnd).map { index ->
            val isActive = index == clampedCurrent
            val isLeadFade = index == windowStart && windowStart > 0
            val isTrailFade = index == windowEnd && windowEnd < total - 1
            val isFade = isLeadFade || isTrailFade

            HabitPageDotModel(
                widthDp = when {
                    isActive -> 16f
                    isFade -> 4f
                    else -> 6f
                },
                color = if (isActive) activeColor else inactiveColor
            )
        }
    }
}

@Composable
private fun HabitPageDot(item: HabitPageDotModel) {
    val animatedWidth = animateFloatAsState(
        targetValue = item.widthDp,
        animationSpec = spring(
            dampingRatio = DOTS_SPRING_DAMPING,
            stiffness = DOTS_SPRING_STIFFNESS
        ),
        label = "habitPageDotWidth"
    )
    Box(
        modifier = Modifier
            .width(animatedWidth.value.dp)
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(item.color)
    )
}

