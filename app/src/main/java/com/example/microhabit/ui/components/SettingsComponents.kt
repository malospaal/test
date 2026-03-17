package com.example.microhabit.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.microhabit.ui.theme.AppTheme

@Composable
fun SettingsGroup(
    title: String,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val radius = AppTheme.radius
    val stroke = AppTheme.stroke

    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = colors.textSecondary
        )
        if (!subtitle.isNullOrBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textTertiary
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colors.backgroundSurfaceMuted),
            shape = RoundedCornerShape(radius.lg),
            border = BorderStroke(stroke.thin, colors.borderSubtle.copy(alpha = 0.55f)),
            elevation = CardDefaults.cardElevation(defaultElevation = AppTheme.elevation.none)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

@Composable
fun SettingsRow(
    title: String,
    subtitle: String? = null,
    value: String? = null,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    destructive: Boolean = false
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled && onClick != null, onClick = { onClick?.invoke() })
            .padding(horizontal = spacing.x2, vertical = spacing.x1_5),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.x1)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(spacing.x0_5)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = when {
                    !enabled -> colors.textTertiary
                    destructive -> colors.danger
                    else -> colors.textPrimary
                }
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (enabled) colors.textSecondary else colors.textTertiary
                )
            }
        }

        if (!value.isNullOrBlank()) {
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                color = if (enabled) colors.textSecondary else colors.textTertiary
            )
        }

        if (onClick != null) {
            Text(
                text = ">",
                style = MaterialTheme.typography.labelLarge,
                color = if (enabled) colors.textSecondary else colors.textTertiary
            )
        }
    }
}

@Composable
fun SettingsSwitchRow(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.x2, vertical = spacing.x1_5),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.x1)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(spacing.x0_5)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) colors.textPrimary else colors.textTertiary
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (enabled) colors.textSecondary else colors.textTertiary
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}

@Composable
fun SettingsDivider() {
    val colors = AppTheme.colors
    HorizontalDivider(color = colors.borderSubtle.copy(alpha = 0.6f))
}
