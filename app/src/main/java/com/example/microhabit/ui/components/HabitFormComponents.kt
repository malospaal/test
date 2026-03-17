package com.example.microhabit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import com.example.microhabit.i18n.LocalAppLanguage
import com.example.microhabit.i18n.weekdayLabels
import com.example.microhabit.ui.theme.AppTheme

data class ChoiceOption<T>(
    val value: T,
    val label: String
)

@Composable
fun FormSection(
    title: String,
    subtitle: String? = null,
    content: @Composable () -> Unit
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors

    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.x0_5)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }
        }
        content()
    }
}

@Composable
fun <T> SingleSelectChips(
    options: List<ChoiceOption<T>>,
    selected: T,
    onSelect: (T) -> Unit
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val radius = AppTheme.radius

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.x1)
    ) {
        options.chunked(2).forEach { rowOptions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.x1)
            ) {
                rowOptions.forEach { option ->
                    val isSelected = option.value == selected
                    Button(
                        onClick = { onSelect(option.value) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(radius.md),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) colors.primaryMuted else colors.backgroundSurfaceMuted,
                            contentColor = if (isSelected) colors.primary else colors.textSecondary
                        )
                    ) {
                        Text(option.label)
                    }
                }
                if (rowOptions.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun ColorSwatchPicker(
    colorsHex: List<String>,
    selectedHex: String,
    onSelect: (String) -> Unit
) {
    val spacing = AppTheme.spacing
    val stroke = AppTheme.stroke
    val radius = AppTheme.radius
    val semantic = AppTheme.colors

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.x1)
    ) {
        colorsHex.chunked(6).forEach { rowColors ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.x1)
            ) {
                rowColors.forEach { hex ->
                    val parsed = parseColorHex(hex)
                    val selected = hex.equals(selectedHex, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .size(spacing.x4)
                            .clip(RoundedCornerShape(radius.md))
                            .background(parsed)
                            .clickable { onSelect(hex) }
                            .border(
                                width = if (selected) stroke.medium else stroke.thin,
                                color = if (selected) semantic.primary.copy(alpha = 0.75f) else semantic.borderSubtle.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(radius.md)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selected) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = if (parsed.luminance() > 0.5f) Color.Black else Color.White,
                                modifier = Modifier.size(spacing.x2)
                            )
                        }
                    }
                }
                repeat(6 - rowColors.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun WeekdaySelector(
    selectedDays: Set<Int>,
    onToggle: (Int) -> Unit
) {
    val labels = weekdayLabels(LocalAppLanguage.current)
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val radius = AppTheme.radius

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.x1)
    ) {
        labels.withIndex().toList().chunked(4).forEach { rowEntries ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.x1)
            ) {
                rowEntries.forEach { entry ->
                    val day = entry.index + 1
                    val selected = day in selectedDays
                    Button(
                        onClick = { onToggle(day) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(radius.md),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selected) colors.primaryMuted else colors.backgroundSurfaceMuted,
                            contentColor = if (selected) colors.primary else colors.textSecondary
                        )
                    ) {
                        Text(entry.value)
                    }
                }
                repeat(4 - rowEntries.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun Stepper(
    label: String,
    value: Int,
    min: Int,
    max: Int,
    onValueChange: (Int) -> Unit
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val radius = AppTheme.radius

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.x1), verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = { onValueChange((value - 1).coerceAtLeast(min)) },
                enabled = value > min,
                shape = RoundedCornerShape(radius.md),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.backgroundSurfaceMuted,
                    contentColor = colors.textPrimary,
                    disabledContainerColor = colors.backgroundSurfaceMuted.copy(alpha = 0.55f),
                    disabledContentColor = colors.textTertiary
                )
            ) {
                Text("-")
            }
            Text("$value", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
            Button(
                onClick = { onValueChange((value + 1).coerceAtMost(max)) },
                enabled = value < max,
                shape = RoundedCornerShape(radius.md),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.backgroundSurfaceMuted,
                    contentColor = colors.textPrimary,
                    disabledContainerColor = colors.backgroundSurfaceMuted.copy(alpha = 0.55f),
                    disabledContentColor = colors.textTertiary
                )
            ) {
                Text("+")
            }
        }
    }
}

fun parseColorHex(hex: String): Color {
    return runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrDefault(Color(0xFF1F6F64))
}
