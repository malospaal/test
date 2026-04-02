package com.example.microhabit.ui.settings

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.microhabit.HabitUiState
import com.example.microhabit.data.AppLanguage
import com.example.microhabit.data.AppThemeMode
import com.example.microhabit.i18n.LocalAppLanguage
import com.example.microhabit.i18n.formatTranslate
import com.example.microhabit.i18n.languageNativeLabel
import com.example.microhabit.i18n.t
import com.example.microhabit.i18n.translate
import com.example.microhabit.ui.theme.AppTheme
import kotlin.math.roundToInt

@Suppress("UNUSED_PARAMETER")
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
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current
    val language = LocalAppLanguage.current
    var showThemeSheet by rememberSaveable { mutableStateOf(false) }
    var showLanguageSheet by rememberSaveable { mutableStateOf(false) }
    var showThresholdSheet by rememberSaveable { mutableStateOf(false) }
    var showResetConfirm by rememberSaveable { mutableStateOf(false) }
    var showDeleteConfirm by rememberSaveable { mutableStateOf(false) }
    var thresholdDraft by rememberSaveable(state.minimumCompletionPercent) {
        mutableIntStateOf(state.minimumCompletionPercent.coerceIn(50, 100))
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
            SettingsSectionHeader(title = t("Appearance"))
            SettingsCard {
                SettingsItemRow(
                    title = t("Theme"),
                    value = themeLabel(state.themeMode, language),
                    onClick = { showThemeSheet = true }
                )
            }
        }

        item {
            SettingsSectionHeader(title = t("Language"))
            SettingsCard {
                SettingsItemRow(
                    title = t("Language"),
                    value = languageNativeLabel(state.language),
                    onClick = { showLanguageSheet = true }
                )
            }
        }

        item {
            SettingsSectionHeader(title = t("Tracking"))
            SettingsCard {
                SettingsItemRow(
                    title = t("Completion threshold"),
                    subtitle = t("settings_threshold_sub"),
                    value = "${state.minimumCompletionPercent}%",
                    onClick = {
                        thresholdDraft = state.minimumCompletionPercent.coerceIn(50, 100)
                        showThresholdSheet = true
                    }
                )
            }
        }

        item {
            SettingsSectionHeader(title = t("Data"))
            SettingsCard {
                SettingsItemRow(
                    title = t("Export data"),
                    subtitle = t("settings_export_sub"),
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
                SettingsRowDivider()
                SettingsItemRow(
                    title = t("Reset progress"),
                    subtitle = t("settings_reset_sub"),
                    onClick = { showResetConfirm = true }
                )
            }
        }

        item {
            SettingsSectionHeader(
                title = t("Danger zone"),
                color = colorScheme.error
            )
            SettingsCard(
                containerColor = colorScheme.error.copy(alpha = 0.08f),
                borderColor = colorScheme.error.copy(alpha = 0.45f)
            ) {
                SettingsItemRow(
                    title = t("Delete account"),
                    titleColor = colorScheme.error,
                    chevronColor = colorScheme.error,
                    onClick = { showDeleteConfirm = true }
                )
            }
        }
    }

    if (showThemeSheet) {
        SelectionBottomSheet(
            title = t("Select theme"),
            options = listOf(
                AppThemeMode.DARK to t("Dark"),
                AppThemeMode.LIGHT to t("Light"),
                AppThemeMode.SYSTEM to t("settings_theme_system_default")
            ),
            selected = state.themeMode,
            onDismiss = { showThemeSheet = false },
            onSelect = {
                onSetTheme(it)
                showThemeSheet = false
            }
        )
    }

    if (showLanguageSheet) {
        SelectionBottomSheet(
            title = t("Select language"),
            options = supportedLanguages.map { it to languageNativeLabel(it) },
            selected = state.language,
            onDismiss = { showLanguageSheet = false },
            onSelect = {
                onSetLanguage(it)
                showLanguageSheet = false
            }
        )
    }

    if (showThresholdSheet) {
        CompletionThresholdBottomSheet(
            value = thresholdDraft,
            onValueChange = { thresholdDraft = it.coerceIn(50, 100) },
            onDismiss = {
                showThresholdSheet = false
                onSetMinimumCompletionPercent(thresholdDraft.coerceIn(1, 100))
            }
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
        DeleteAccountBottomSheet(
            onDismiss = { showDeleteConfirm = false },
            onConfirmDelete = {
                showDeleteConfirm = false
                onDeleteAccount()
                Toast.makeText(
                    context,
                    translate(language, "Account data deleted."),
                    Toast.LENGTH_SHORT
                ).show()
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
private fun SettingsSectionHeader(
    title: String,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = color,
        letterSpacing = 0.04.em,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun SettingsCard(
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surface,
    borderColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.72f),
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.fillMaxWidth(), content = content)
    }
}

@Composable
private fun SettingsItemRow(
    title: String,
    subtitle: String? = null,
    value: String? = null,
    titleColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    chevronColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = titleColor
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (!value.isNullOrBlank()) {
            Text(
                text = value,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = ">",
            fontSize = 14.sp,
            color = chevronColor
        )
    }
}

@Composable
private fun BottomSheetDivider() {
    HorizontalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.72f)
    )
}

@Composable
private fun SettingsRowDivider() {
    HorizontalDivider(
        thickness = 0.8.dp,
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.58f)
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun <T> SelectionBottomSheet(
    title: String,
    options: List<Pair<T, String>>,
    selected: T,
    onDismiss: () -> Unit,
    onSelect: (T) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { SheetDragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
            )
            BottomSheetDivider()
            options.forEachIndexed { index, (value, label) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelect(value)
                        }
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    SelectionIndicator(isSelected = value == selected)
                }

                if (index < options.lastIndex) {
                    BottomSheetDivider()
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CompletionThresholdBottomSheet(
    value: Int,
    onValueChange: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val steppedValue = value.coerceIn(50, 100)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { SheetDragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Text(
                text = t("Completion threshold"),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Text(
                text = t("settings_threshold_sub"),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            BottomSheetDivider()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$steppedValue%",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (steppedValue == 100) {
                        t("settings_threshold_desc_100")
                    } else {
                        t("settings_threshold_desc_partial")
                    },
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Slider(
                    value = steppedValue.toFloat(),
                    onValueChange = { raw ->
                        val stepped = (50 + (((raw - 50f) / 5f).roundToInt() * 5)).coerceIn(50, 100)
                        onValueChange(stepped)
                    },
                    valueRange = 50f..100f,
                    steps = 9,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "50%",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "100%",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DeleteAccountBottomSheet(
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = colorScheme.surface,
        dragHandle = { SheetDragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = t("Delete account?"),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface
            )
            Text(
                text = t("This action removes all habits, progress and settings."),
                fontSize = 13.sp,
                color = colorScheme.onSurfaceVariant
            )
            Button(
                onClick = onConfirmDelete,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.error,
                    contentColor = colorScheme.onError
                )
            ) {
                Text(t("Delete"))
            }
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(t("Cancel"))
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun SheetDragHandle() {
    Box(
        modifier = Modifier
            .padding(top = 8.dp, bottom = 4.dp)
            .size(width = 36.dp, height = 4.dp)
            .background(
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(999.dp)
            )
    )
}

@Composable
private fun SelectionIndicator(isSelected: Boolean) {
    if (isSelected) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(12.dp)
            )
        }
    } else {
        Box(
            modifier = Modifier
                .size(18.dp)
                .border(
                    width = 1.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = CircleShape
                )
        )
    }
}
