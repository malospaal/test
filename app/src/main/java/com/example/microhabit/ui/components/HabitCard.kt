package com.example.microhabit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.example.microhabit.data.AppLanguage
import com.example.microhabit.i18n.LocalAppLanguage
import com.example.microhabit.i18n.t
import com.example.microhabit.i18n.translate
import com.example.microhabit.data.TrackingType
import com.example.microhabit.ui.theme.AppTheme

data class HabitCardModel(
    val emoji: String,
    val name: String,
    val colorHex: String,
    val trackingType: TrackingType,
    val streak: Int,
    val frequency: String,
    val reminderStatus: String,
    val completionRate: Int,
    val isCompleted: Boolean,
    val isArchived: Boolean
)

@Composable
fun HabitCard(
    habit: HabitCardModel,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onArchive: () -> Unit,
    onUnarchive: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val elevation = AppTheme.elevation
    val stroke = AppTheme.stroke
    val colors = AppTheme.colors
    val language = LocalAppLanguage.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        colors = CardDefaults.cardColors(containerColor = colors.backgroundSurface),
        shape = RoundedCornerShape(radius.lg),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.sm)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.x2),
            verticalArrangement = Arrangement.spacedBy(spacing.x1)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.x1)
            ) {
                Box(
                    modifier = Modifier
                        .size(spacing.x4)
                        .clip(RoundedCornerShape(radius.md))
                        .background(parseColorHex(habit.colorHex)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = habit.emoji,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(spacing.x0_5)
                ) {
                    Text(
                        text = habit.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = habit.frequency,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = habit.reminderStatus,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textTertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = trackingLabel(habit.trackingType, language),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textTertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            when {
                                habit.isArchived -> colors.neutralMuted
                                habit.isCompleted -> colors.primary.copy(alpha = 0.16f)
                                else -> colors.successMuted
                            }
                        )
                        .padding(horizontal = spacing.x1, vertical = spacing.x0_5)
                ) {
                    Text(
                        text = when {
                            habit.isArchived -> t("Archived")
                            habit.isCompleted -> t("Completed")
                            else -> t("Active")
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = when {
                            habit.isArchived -> colors.textSecondary
                            habit.isCompleted -> colors.primary
                            else -> colors.success
                        }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.x1)
            ) {
                HabitMetricTile(
                    modifier = Modifier.weight(1f),
                    label = t("Streak"),
                    value = "${habit.streak}d"
                )
                HabitMetricTile(
                    modifier = Modifier.weight(1f),
                    label = t("Completion"),
                    value = "${habit.completionRate}%"
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(radius.sm))
                    .background(colors.backgroundSurfaceMuted)
                    .border(stroke.thin, colors.borderSubtle, RoundedCornerShape(radius.sm))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(habit.completionRate.coerceIn(0, 100) / 100f)
                        .background(colors.primary)
                        .padding(vertical = spacing.x0_5)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Text(t("Edit"))
                }
                if (habit.isArchived) {
                    TextButton(onClick = onUnarchive) {
                        Text(t("Unarchive"))
                    }
                } else {
                    TextButton(onClick = onArchive) {
                        Text(t("Archive"))
                    }
                }
                TextButton(onClick = onDelete) {
                    Text(t("Delete"), color = colors.danger)
                }
            }
        }
    }
}

private fun trackingLabel(type: TrackingType, language: AppLanguage): String {
    return when (type) {
        TrackingType.YES_NO -> translate(language, "Do once")
        TrackingType.COUNT -> translate(language, "Do N times")
        TrackingType.DURATION -> translate(language, "Do N minutes")
    }
}

@Composable
private fun HabitMetricTile(
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
            .background(colors.backgroundSurfaceMuted)
            .border(stroke.thin, colors.borderSubtle, RoundedCornerShape(radius.md))
            .padding(horizontal = spacing.x1, vertical = spacing.x1),
        verticalArrangement = Arrangement.spacedBy(spacing.x0_5)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary
        )
    }
}
