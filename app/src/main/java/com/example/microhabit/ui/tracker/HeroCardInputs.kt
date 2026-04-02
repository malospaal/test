package com.example.microhabit.ui.tracker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.microhabit.R
import com.example.microhabit.i18n.t
import com.example.microhabit.ui.theme.AppTheme
@Composable
internal fun HeroDetailsButton(onClick: () -> Unit) {
    val semantic = AppTheme.colors
    val detailsLabel = t("More details →").replace("→", "").trim()
    TextButton(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = detailsLabel,
                style = MaterialTheme.typography.labelMedium,
                fontSize = 14.sp,
                color = semantic.primary,
                fontWeight = FontWeight.Medium,
                lineHeight = 16.sp
            )
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                tint = semantic.primary,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
internal fun EditValueButton(
    onClick: () -> Unit,
    textSize: androidx.compose.ui.unit.TextUnit = 9.sp
) {
    val colors = AppTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = colors.primary.copy(alpha = 0.4f),
                shape = RoundedCornerShape(10.dp)
            )
            .background(colors.primaryMuted.copy(alpha = 0.8f))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_pencil),
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier.size(10.dp)
        )
        Text(
            text = t("edit"),
            style = MaterialTheme.typography.labelSmall,
            fontSize = textSize,
            fontWeight = FontWeight.Medium,
            color = colors.primary
        )
    }
}

@Composable
internal fun ValueNumpad(
    input: String,
    unitLabel: String,
    onInputChange: (String) -> Unit,
    onBackspace: () -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = AppTheme.spacing
    val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0", "⌫")
    val keySize = 52.dp

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.x1)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.x1)
        ) {
            Text(
                text = if (input.isEmpty()) "—" else input,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (input.isEmpty()) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                text = unitLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        keys.chunked(3).forEach { rowKeys ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.x1, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowKeys.forEach { key ->
                    when (key) {
                        "" -> Spacer(Modifier.size(keySize))
                        "⌫" -> {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(keySize)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable(onClick = onBackspace)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.Backspace,
                                    contentDescription = t("Backspace"),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        else -> {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(keySize)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable {
                                        if (input.length < 4) {
                                            onInputChange(input + key)
                                        }
                                    }
                            ) {
                                Text(
                                    text = key,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Text(t("Cancel"))
            }
            Button(
                onClick = onSave,
                modifier = Modifier.weight(2f),
                enabled = input.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppTheme.colors.primary,
                    contentColor = Color.White,
                    disabledContainerColor = AppTheme.colors.primaryMuted,
                    disabledContentColor = AppTheme.colors.primary.copy(alpha = 0.4f)
                )
            ) {
                Text(
                    text = t("Save"),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

}
