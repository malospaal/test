package com.example.microhabit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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

    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing.x1),
        modifier = Modifier.horizontalScroll(rememberScrollState())
    ) {
        options.forEach { option ->
            val isSelected = option.value == selected
            Button(
                onClick = { onSelect(option.value) },
                shape = RoundedCornerShape(radius.md),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) colors.primary else colors.backgroundSurfaceMuted,
                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else colors.textPrimary
                )
            ) {
                Text(option.label)
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

    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing.x1),
        modifier = Modifier.horizontalScroll(rememberScrollState())
    ) {
        colorsHex.forEach { hex ->
            val parsed = parseColorHex(hex)
            val selected = hex.equals(selectedHex, ignoreCase = true)
            Box(
                modifier = Modifier
                    .size(spacing.x4)
                    .clip(RoundedCornerShape(radius.md))
                    .background(parsed)
                    .clickable { onSelect(hex) }
                    .border(
                        width = if (selected) stroke.medium else stroke.thin,
                        color = if (selected) semantic.textPrimary else semantic.borderSubtle,
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
    }
}

@Composable
fun WeekdaySelector(
    selectedDays: Set<Int>,
    onToggle: (Int) -> Unit
) {
    val labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val radius = AppTheme.radius

    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing.x1),
        modifier = Modifier.horizontalScroll(rememberScrollState())
    ) {
        labels.forEachIndexed { index, label ->
            val day = index + 1
            val selected = day in selectedDays
            Button(
                onClick = { onToggle(day) },
                shape = RoundedCornerShape(radius.md),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selected) colors.primary else colors.backgroundSurfaceMuted,
                    contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else colors.textPrimary
                )
            ) {
                Text(label)
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
                shape = RoundedCornerShape(radius.md)
            ) {
                Text("-")
            }
            Text("$value", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
            Button(
                onClick = { onValueChange((value + 1).coerceAtMost(max)) },
                enabled = value < max,
                shape = RoundedCornerShape(radius.md)
            ) {
                Text("+")
            }
        }
    }
}

fun parseColorHex(hex: String): Color {
    return runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrDefault(Color(0xFF1F6F64))
}
