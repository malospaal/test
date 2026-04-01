package com.example.microhabit.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.microhabit.ui.theme.AppTheme

@Composable
fun SettingsGroup(
    title: String,
    subtitle: String? = null,
    borderColorOverride: Color? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val spacing = AppTheme.spacing
    val semantic = AppTheme.colors
    val radius = AppTheme.radius
    val stroke = AppTheme.stroke
    val isDark = semantic.backgroundCanvas.red < 0.2f
    val cardBackground = if (isDark) semantic.backgroundSurfaceMuted else Color(0xFFFFFFFF)
    val cardBorderColor = borderColorOverride ?: if (isDark) {
        semantic.borderSubtle.copy(alpha = 0.55f)
    } else {
        Color(0xFFC8D9CA)
    }

    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = if (isDark) semantic.textSecondary else Color(0xFF5A7A5E)
        )
        if (!subtitle.isNullOrBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (isDark) semantic.textTertiary else Color(0xFF5A7A5E)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            shape = RoundedCornerShape(radius.lg),
            border = BorderStroke(stroke.thin, cardBorderColor),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) AppTheme.elevation.none else 1.dp)
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
    destructive: Boolean = false,
    leadingIcon: ImageVector? = null
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val isDark = colors.backgroundCanvas.red < 0.2f
    val normalTextColor = if (isDark) colors.textPrimary else Color(0xFF0D1F12)
    val normalSecondaryColor = if (isDark) colors.textSecondary else Color(0xFF5A7A5E)
    val normalChevronColor = if (isDark) colors.textSecondary else Color(0xFF5A7A5E)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled && onClick != null, onClick = { onClick?.invoke() })
            .padding(horizontal = spacing.x2, vertical = spacing.x1_5),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.x1)
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = if (enabled) normalSecondaryColor else colors.textTertiary
            )
        }
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
                    else -> normalTextColor
                }
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (enabled) normalSecondaryColor else colors.textTertiary
                )
            }
        }

        if (!value.isNullOrBlank()) {
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                color = if (enabled) normalSecondaryColor else colors.textTertiary
            )
        }

        if (onClick != null) {
            Text(
                text = ">",
                style = MaterialTheme.typography.labelLarge,
                color = if (destructive) colors.danger else if (enabled) normalChevronColor else colors.textTertiary
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
    val semantic = AppTheme.colors
    val isDark = semantic.backgroundCanvas.red < 0.2f
    HorizontalDivider(color = if (isDark) semantic.borderSubtle.copy(alpha = 0.6f) else Color(0xFFE4EDE5))
}
