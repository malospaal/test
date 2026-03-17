package com.example.microhabit.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.microhabit.CalendarBreakdownItem
import com.example.microhabit.data.TrackingType
import com.example.microhabit.i18n.appLocale
import com.example.microhabit.i18n.t
import com.example.microhabit.i18n.tf
import com.example.microhabit.ui.theme.AppTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun BreakdownCard(
    selectedDate: LocalDate,
    completedCount: Int,
    scheduledCount: Int,
    items: List<CalendarBreakdownItem>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = AppTheme.colors.backgroundSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.borderSubtle)
    ) {
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 10.dp)
        ) {
            BreakdownHeader(
                date = selectedDate,
                completedCount = completedCount,
                scheduledCount = scheduledCount
            )
            Spacer(Modifier.height(12.dp))
            items.forEachIndexed { index, item ->
                BreakdownHabitRow(item = item)
                if (index < items.lastIndex) {
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                    )
                }
            }
        }
    }
}

@Composable
private fun BreakdownHeader(
    date: LocalDate,
    completedCount: Int,
    scheduledCount: Int
) {
    val locale = appLocale()
    Text(
        text = t("Day breakdown"),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(Modifier.height(4.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (scheduledCount > 0) {
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(3.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            )
            Spacer(Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AppTheme.colors.primary.copy(alpha = 0.12f)
            ) {
                Text(
                    text = tf("Done %d of %d", completedCount, scheduledCount),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = AppTheme.colors.primary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
private fun BreakdownHabitRow(item: CalendarBreakdownItem) {
    val rowStatus = item.status.toBreakdownRowStatus()
    val displayTitle = if (item.title.startsWith("tmpl_")) t(item.title) else item.title
    val unitLabel = when {
        item.unitLabel.isNotBlank() -> item.unitLabel
        item.trackingType == TrackingType.DURATION -> t("min")
        else -> ""
    }
    val valueLabel = when {
        !item.scheduled || item.trackingType == TrackingType.YES_NO -> null
        unitLabel.isBlank() -> "${item.value} / ${item.target}"
        else -> "${item.value} / ${item.target} $unitLabel"
    }
    val progress = (item.value.toFloat() / item.target.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HabitIconContainer(
            emoji = item.emoji.ifBlank { "✨" },
            status = rowStatus
        )
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = displayTitle,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            valueLabel?.let { label ->
                Spacer(Modifier.height(2.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .widthIn(max = 100.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .background(
                                if (rowStatus == BreakdownRowStatus.COMPLETED) {
                                    AppTheme.colors.success
                                } else {
                                    AppTheme.colors.primary.copy(alpha = 0.5f)
                                }
                            )
                    )
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        BreakdownStatusBadge(status = rowStatus)
    }
}

@Composable
private fun HabitIconContainer(emoji: String, status: BreakdownRowStatus) {
    val bgColor = when (status) {
        BreakdownRowStatus.COMPLETED -> AppTheme.colors.success.copy(alpha = 0.15f)
        BreakdownRowStatus.MISSED -> AppTheme.colors.danger.copy(alpha = 0.10f)
        BreakdownRowStatus.PARTIAL -> AppTheme.colors.primary.copy(alpha = 0.10f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(text = emoji, fontSize = 15.sp)
    }
}

@Composable
private fun BreakdownStatusBadge(status: BreakdownRowStatus) {
    if (status == BreakdownRowStatus.NOT_SCHEDULED) {
        Text(
            text = "—",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.20f)
        )
        return
    }

    val (label, bgColor, textColor) = when (status) {
        BreakdownRowStatus.COMPLETED -> Triple(
            t("Completed"),
            AppTheme.colors.success.copy(alpha = 0.14f),
            AppTheme.colors.success
        )
        BreakdownRowStatus.MISSED -> Triple(
            t("Missed"),
            AppTheme.colors.danger.copy(alpha = 0.10f),
            AppTheme.colors.danger
        )
        BreakdownRowStatus.PARTIAL -> Triple(
            t("Partial"),
            AppTheme.colors.primary.copy(alpha = 0.10f),
            AppTheme.colors.primary
        )
        BreakdownRowStatus.TODAY_PENDING -> Triple(
            t("Today pending"),
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
        BreakdownRowStatus.FUTURE -> Triple(
            t("Upcoming"),
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
        BreakdownRowStatus.NOT_SCHEDULED -> return
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
