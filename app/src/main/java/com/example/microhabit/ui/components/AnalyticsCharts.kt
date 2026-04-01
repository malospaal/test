package com.example.microhabit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.microhabit.ui.theme.AppTheme

@Composable
fun AnalyticsMetricTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val colors = AppTheme.colors
    val stroke = AppTheme.stroke

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(radius.md))
            .background(colors.backgroundSurfaceMuted.copy(alpha = 0.82f))
            .border(stroke.thin, colors.borderSubtle.copy(alpha = 0.55f), RoundedCornerShape(radius.md))
            .padding(horizontal = spacing.x2, vertical = spacing.x2),
        verticalArrangement = Arrangement.spacedBy(spacing.x0_5)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary
        )
    }
}

@Composable
fun VerticalPercentBars(
    values: List<Int>,
    labels: List<String>,
    highlightIndex: Int? = null,
    modifier: Modifier = Modifier
) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val colors = AppTheme.colors

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.x1)
    ) {
        values.forEachIndexed { index, raw ->
            val value = raw.coerceIn(0, 100)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.x1)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(spacing.x6)
                        .clip(RoundedCornerShape(radius.sm))
                        .background(if (highlightIndex == index) colors.primaryMuted else colors.backgroundSurfaceMuted),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    val heightFraction = (value / 100f).coerceAtLeast(0.12f)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(spacing.x6 * heightFraction)
                            .clip(RoundedCornerShape(radius.sm))
                            .background(colors.chartDone)
                    )
                }
                Text(
                    text = labels.getOrElse(index) { "" },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (highlightIndex == index) colors.primary else colors.textSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun HorizontalPercentBars(
    values: List<Int>,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val colors = AppTheme.colors

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.x1)
    ) {
        values.forEachIndexed { index, raw ->
            val value = raw.coerceIn(0, 100)
            Column(verticalArrangement = Arrangement.spacedBy(spacing.x0_5)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = labels.getOrElse(index) { "" },
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "$value%",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(spacing.x1)
                        .clip(RoundedCornerShape(radius.full))
                        .background(colors.backgroundSurfaceMuted)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(value / 100f)
                            .height(spacing.x1)
                            .clip(RoundedCornerShape(radius.full))
                            .background(colors.chartDone)
                    )
                }
            }
        }
    }
}

