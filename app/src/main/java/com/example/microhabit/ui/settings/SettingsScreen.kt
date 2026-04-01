package com.example.microhabit.ui.settings

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.example.microhabit.HabitUiState
import com.example.microhabit.data.AppLanguage
import com.example.microhabit.data.AppThemeMode
import com.example.microhabit.data.hasPremiumAccess
import com.example.microhabit.i18n.LocalAppLanguage
import com.example.microhabit.i18n.formatTranslate
import com.example.microhabit.i18n.languageNativeLabel
import com.example.microhabit.i18n.t
import com.example.microhabit.i18n.translate
import com.example.microhabit.ui.components.SettingsDivider
import com.example.microhabit.ui.components.SettingsGroup
import com.example.microhabit.ui.components.SettingsRow
import com.example.microhabit.ui.theme.AppTheme


@Composable
internal fun SettingsPage(
    state: HabitUiState,
    onSetTheme: (AppThemeMode) -> Unit,
    onSetLanguage: (AppLanguage) -> Unit,
    onSetMinimumCompletionPercent: (Int) -> Unit,
    onOpenPaywall: () -> Unit,
    onExportData: () -> Result<String>,
    onResetProgress: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val context = LocalContext.current
    val language = LocalAppLanguage.current
    var showThemeDialog by rememberSaveable { mutableStateOf(false) }
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }
    var showResetConfirm by rememberSaveable { mutableStateOf(false) }
    var showDeleteConfirm by rememberSaveable { mutableStateOf(false) }
    var showCompletionThresholdDialog by rememberSaveable { mutableStateOf(false) }
    var completionPercentInput by rememberSaveable(state.minimumCompletionPercent) {
        mutableStateOf(state.minimumCompletionPercent.coerceIn(1, 100).toString())
    }
    val supportedLanguages = listOf(
        AppLanguage.EN,
        AppLanguage.RU,
        AppLanguage.UK,
        AppLanguage.DE,
        AppLanguage.CS
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(spacing.x2),
        verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
    ) {
        item {
            SettingsGroup(title = t("Appearance")) {
                SettingsRow(
                    title = t("Theme"),
                    value = themeLabel(state.themeMode, language),
                    onClick = { showThemeDialog = true }
                )
            }
        }

        item {
            SettingsGroup(title = t("Language")) {
                SettingsRow(
                    title = t("Language"),
                    value = languageNativeLabel(state.language),
                    onClick = { showLanguageDialog = true }
                )
            }
        }

        item {
            SettingsGroup(title = t("Tracking")) {
                SettingsRow(
                    title = t("Completion threshold"),
                    value = "${state.minimumCompletionPercent}%",
                    onClick = {
                        completionPercentInput = state.minimumCompletionPercent.coerceIn(1, 100).toString()
                        showCompletionThresholdDialog = true
                    }
                )
            }
        }

        item {
            SettingsGroup(title = t("Subscription")) {
                SettingsRow(
                    title = t("Manage subscription"),
                    value = if (state.plan.hasPremiumAccess()) t("Premium") else t("Free"),
                    onClick = onOpenPaywall
                )
            }
        }

        item {
            SettingsGroup(title = t("Data")) {
                SettingsRow(
                    title = t("Export data"),
                    onClick = {
                        val result = onExportData()
                        val message = result.fold(
                            onSuccess = { formatTranslate(language, "Data exported: %s", it) },
                            onFailure = {
                                formatTranslate(
                                    language,
                                    "Export failed: %s",
                                    it.message ?: translate(language, "Unknown error")
                                )
                            }
                        )
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    }
                )
                SettingsDivider()
                SettingsRow(
                    title = t("Reset progress"),
                    onClick = { showResetConfirm = true }
                )
            }
        }

        item {
            SettingsGroup(title = t("Danger zone")) {
                SettingsRow(
                    title = t("Delete account"),
                    destructive = true,
                    onClick = { showDeleteConfirm = true }
                )
            }
        }
    }

    if (showThemeDialog) {
        SelectionDialog(
            title = t("Select theme"),
            options = listOf(
                AppThemeMode.SYSTEM to t("System"),
                AppThemeMode.LIGHT to t("Light"),
                AppThemeMode.DARK to t("Dark")
            ),
            selected = state.themeMode,
            onDismiss = { showThemeDialog = false },
            onSelect = onSetTheme
        )
    }

    if (showLanguageDialog) {
        SelectionDialog(
            title = t("Select language"),
            options = supportedLanguages.map { it to languageNativeLabel(it) },
            selected = state.language,
            onDismiss = { showLanguageDialog = false },
            onSelect = onSetLanguage
        )
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text(t("Reset progress?")) },
            text = { Text(t("This will remove all completion history and keep your habits.")) },
            confirmButton = {
                Button(
                    onClick = {
                        showResetConfirm = false
                        onResetProgress()
                        Toast.makeText(
                            context,
                            translate(language, "Progress reset."),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {
                    Text(t("Reset"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text(t("Cancel"))
                }
            }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(t("Delete account?")) },
            text = { Text(t("This action removes all habits, progress and settings.")) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDeleteAccount()
                        Toast.makeText(
                            context,
                            translate(language, "Account data deleted."),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(t("Delete"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(t("Cancel"))
                }
            }
        )
    }

    if (showCompletionThresholdDialog) {
        val parsedThreshold = completionPercentInput.toIntOrNull()
        val isValidThreshold = parsedThreshold != null && parsedThreshold in 1..100
        val showError = completionPercentInput.isNotBlank() && !isValidThreshold
        AlertDialog(
            onDismissRequest = { showCompletionThresholdDialog = false },
            title = { Text(t("Minimum completion percent")) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                    Text(
                        text = t("Applies to count and duration habits"),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = t("Completion threshold"),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                    ) {
                        OutlinedTextField(
                            value = completionPercentInput,
                            onValueChange = { value ->
                                completionPercentInput = value.filter { it.isDigit() }.take(3)
                            },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = showError
                        )
                        Text(
                            text = "%",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.textSecondary
                        )
                    }
                    Text(
                        text = t("For example: 100%"),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                    if (showError) {
                        Text(
                            text = t("Value must be between 1 and 100"),
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.danger
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (!isValidThreshold) return@Button
                        val threshold = parsedThreshold ?: return@Button
                        showCompletionThresholdDialog = false
                        onSetMinimumCompletionPercent(threshold.coerceIn(1, 100))
                    },
                    enabled = isValidThreshold
                ) {
                    Text(t("Save changes"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCompletionThresholdDialog = false }) {
                    Text(t("Cancel"))
                }
            }
        )
    }
}


internal fun themeLabel(mode: AppThemeMode, language: AppLanguage): String {
    return when (mode) {
        AppThemeMode.SYSTEM -> translate(language, "System")
        AppThemeMode.LIGHT -> translate(language, "Light")
        AppThemeMode.DARK -> translate(language, "Dark")
    }
}

@Composable
internal fun <T> SelectionDialog(
    title: String,
    options: List<Pair<T, String>>,
    selected: T,
    onDismiss: () -> Unit,
    onSelect: (T) -> Unit
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.x0_5)) {
                options.forEach { option ->
                    val isSelected = option.first == selected
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(AppTheme.radius.md))
                            .clickable {
                                onSelect(option.first)
                                onDismiss()
                            },
                        color = if (isSelected) colors.primaryMuted else colors.backgroundSurfaceMuted
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = spacing.x1_5, vertical = spacing.x1),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option.second,
                                style = MaterialTheme.typography.bodyLarge,
                                color = colors.textPrimary
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = t("Selected"),
                                    tint = colors.primary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(t("Close"))
            }
        }
    )
}


