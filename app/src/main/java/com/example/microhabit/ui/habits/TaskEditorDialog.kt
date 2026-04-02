package com.example.microhabit.ui.habits

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.animation.animateContentSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.microhabit.HabitUiState
import com.example.microhabit.MainViewModel
import com.example.microhabit.R
import com.example.microhabit.data.TaskFrequency
import com.example.microhabit.data.TrackingType
import com.example.microhabit.i18n.LocalAppLanguage
import com.example.microhabit.i18n.appLocale
import com.example.microhabit.i18n.t
import com.example.microhabit.i18n.weekdayLabels
import com.example.microhabit.ui.components.FormSection
import com.example.microhabit.ui.components.SettingsSwitchRow
import com.example.microhabit.ui.onboarding.TimesPerWeekStepper
import com.example.microhabit.ui.shared.formatTimeForDevice
import com.example.microhabit.ui.shared.showThemedDatePicker
import com.example.microhabit.ui.shared.showThemedTimePicker
import com.example.microhabit.ui.theme.AppTheme
import com.example.microhabit.ui.tracker.ValueNumpad
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun TaskEditorDialog(
    state: HabitUiState,
    onDismiss: () -> Unit,
    vm: MainViewModel,
    onSaveRequest: () -> Unit
) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val stroke = AppTheme.stroke
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current
    val locale = appLocale()
    val language = LocalAppLanguage.current
    val is24HourView = android.text.format.DateFormat.is24HourFormat(context)
    val pickerTheme = R.style.ThemeOverlay_MicroHabit_Picker
    val datePickerTheme = R.style.ThemeOverlay_MicroHabit_DatePicker
    val pickerActionColor = colorScheme.primary.toArgb()

    var showEmojiPicker by rememberSaveable { mutableStateOf(false) }
    var showCountNumpad by rememberSaveable { mutableStateOf(false) }
    var countNumpadInput by rememberSaveable { mutableStateOf("") }
    var wasSaveAttempted by rememberSaveable(state.editingTaskId) { mutableStateOf(false) }

    val canSave = vm.canSaveEditor()
    val isTitleInvalid = wasSaveAttempted && state.editorTitle.isBlank()
    val shortWeekdays = remember(language) { weekdayLabels(language).map { it.take(2) } }

    val trackingCards = listOf(
        Triple(TrackingType.YES_NO, t("tracking_type_yesno"), t("tracking_type_yesno_desc")),
        Triple(TrackingType.COUNT, t("tracking_type_count"), t("tracking_type_count_desc")),
        Triple(TrackingType.DURATION, t("tracking_type_duration"), t("tracking_type_duration_desc"))
    )

    val emojiSuggestions = listOf(
        "✨", "💧", "💊", "🥗", "🌿", "😴", "🏃", "🏋️", "🚴", "🤸",
        "👟", "🧘", "📵", "🙏", "📚", "✍️", "🎓", "💼", "🎯", "🔥",
        "☀️", "🌙", "💡", "📈", "🍎", "🧠", "🫶", "🎵", "🧹", "🧴",
        "🥦", "🍷", "🍔", "☕", "⏱️", "🙅", "🪙", "🛍️", "📊", "💳",
        "🎨", "🎸", "📷", "🔨", "📞", "❤️", "😊", "🌟", "🦷", "🌞",
        "🪑", "🚿", "🚶", "⚖️", "🧘‍♀️", "🏊", "💪", "🏋️‍♂️", "💨", "🌈",
        "🔕", "🗣️", "📅", "📬", "⏰", "💻"
    )

    LaunchedEffect(canSave) {
        if (canSave) wasSaveAttempted = false
    }

    LaunchedEffect(state.editorFrequency, state.editorTimesPerWeek) {
        if (state.editorFrequency == TaskFrequency.TIMES_PER_WEEK && state.editorTimesPerWeek > 6) {
            vm.setEditorTimesPerWeek(6)
        }
    }

    if (showEmojiPicker) {
        ModalBottomSheet(
            onDismissRequest = { showEmojiPicker = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.x2, vertical = spacing.x1_5),
                verticalArrangement = Arrangement.spacedBy(spacing.x1)
            ) {
                Text(
                    text = t("Choose emoji"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                emojiSuggestions.chunked(6).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                    ) {
                        row.forEach { emoji ->
                            Surface(
                                onClick = {
                                    vm.setEditorEmoji(emoji)
                                    showEmojiPicker = false
                                },
                                shape = RoundedCornerShape(radius.md),
                                color = colorScheme.surfaceVariant,
                                border = BorderStroke(stroke.thin, colorScheme.outlineVariant),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = spacing.x1),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = emoji, fontSize = 22.sp)
                                }
                            }
                        }
                        repeat((6 - row.size).coerceAtLeast(0)) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(spacing.x1))
            }
        }
    }

    if (showCountNumpad) {
        ModalBottomSheet(
            onDismissRequest = { showCountNumpad = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.x2, vertical = spacing.x1_5)
            ) {
                ValueNumpad(
                    input = countNumpadInput,
                    unitLabel = state.editorUnitLabel.ifBlank { t("Daily target") },
                    onInputChange = { countNumpadInput = it.filter { ch -> ch.isDigit() }.take(4) },
                    onBackspace = { countNumpadInput = countNumpadInput.dropLast(1) },
                    onSave = {
                        val parsed = countNumpadInput.toIntOrNull()?.coerceAtLeast(1)
                            ?: state.editorDailyTarget.coerceAtLeast(1)
                        vm.setEditorDailyTarget(parsed)
                        showCountNumpad = false
                    },
                    onDismiss = { showCountNumpad = false }
                )
            }
        }
    }

    BackHandler(onBack = onDismiss)
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorScheme.background
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            if (state.editingTaskId == null) t("screen_create_habit") else t("Edit Habit"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Rounded.Close, contentDescription = t("Close"))
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = colorScheme.surface
                    )
                )
            },
            bottomBar = {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding(),
                    color = colorScheme.surface,
                    tonalElevation = AppTheme.elevation.sm
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.x2, vertical = spacing.x1_5),
                        verticalArrangement = Arrangement.spacedBy(spacing.x0_5)
                    ) {
                        if (wasSaveAttempted && !canSave) {
                            Text(
                                text = t("Fill required fields to continue."),
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.error
                            )
                        }

                        Button(
                            onClick = {
                                if (canSave) {
                                    onSaveRequest()
                                } else {
                                    wasSaveAttempted = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(radius.md),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (canSave) colorScheme.primary else colorScheme.surfaceVariant,
                                contentColor = if (canSave) colorScheme.onPrimary else colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text(if (state.editingTaskId == null) t("Save habit") else t("Save changes"))
                        }
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = spacing.x2, vertical = spacing.x1_5)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
            ) {
                FormSection(title = t("Basic setup")) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.x0_5)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    stroke.thin,
                                    if (isTitleInvalid) colorScheme.error else colorScheme.outlineVariant,
                                    RoundedCornerShape(radius.md)
                                )
                                .padding(horizontal = spacing.x1, vertical = spacing.x0_5),
                            horizontalArrangement = Arrangement.spacedBy(spacing.x1),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                onClick = { showEmojiPicker = true },
                                shape = RoundedCornerShape(8.dp),
                                color = colorScheme.surfaceVariant,
                                border = BorderStroke(stroke.thin, colorScheme.outlineVariant),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = state.editorEmoji.ifBlank { "✨" },
                                        fontSize = 20.sp
                                    )
                                }
                            }
                            BasicTextField(
                                value = state.editorTitle,
                                onValueChange = vm::setEditorTitle,
                                modifier = Modifier.weight(1f),
                                textStyle = MaterialTheme.typography.bodyLarge.copy(color = colorScheme.onSurface),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                singleLine = true,
                                decorationBox = { inner ->
                                    if (state.editorTitle.isBlank()) {
                                        Text(
                                            text = t("Habit name"),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                        )
                                    }
                                    inner()
                                }
                            )
                            Spacer(modifier = Modifier.size(20.dp))
                        }

                        AnimatedVisibility(visible = isTitleInvalid) {
                            Text(
                                text = t("Fill required fields to continue."),
                                color = colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                FormSection(title = t("Tracking type")) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                        trackingCards.forEach { (type, title, description) ->
                            val selected = state.editorTrackingType == type
                            val icon = when (type) {
                                TrackingType.YES_NO -> "✓"
                                TrackingType.COUNT -> "#"
                                TrackingType.DURATION -> "⏱"
                            }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(radius.md))
                                    .clickable { vm.setEditorTrackingType(type) },
                                shape = RoundedCornerShape(radius.md),
                                color = if (selected) colorScheme.primaryContainer else colorScheme.surfaceVariant,
                                border = BorderStroke(
                                    width = if (selected) stroke.medium else stroke.thin,
                                    color = if (selected) colorScheme.primary else colorScheme.outlineVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = spacing.x1_5, vertical = spacing.x1),
                                    verticalArrangement = Arrangement.spacedBy(spacing.x0_5)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(spacing.x0_5),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = icon, style = MaterialTheme.typography.titleSmall)
                                        Text(
                                            text = title,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = colorScheme.onSurface
                                        )
                                    }

                                    AnimatedVisibility(
                                        visible = selected,
                                        enter = expandVertically() + fadeIn(),
                                        exit = shrinkVertically() + fadeOut()
                                    ) {
                                        Text(
                                            text = description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = state.editorTrackingType == TrackingType.COUNT || state.editorTrackingType == TrackingType.DURATION,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1_5)) {
                        if (state.editorTrackingType == TrackingType.COUNT) {
                            FormSection(title = t("Count target")) {
                                Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = t("Daily target"),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = colorScheme.onSurface
                                        )

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(spacing.x1),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Button(
                                                onClick = {
                                                    val current = state.editorDailyTarget
                                                    val next = if (current > 99) 99 else (current - 1).coerceAtLeast(1)
                                                    vm.setEditorDailyTarget(next)
                                                },
                                                enabled = state.editorDailyTarget > 1,
                                                shape = RoundedCornerShape(radius.md),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = colorScheme.surfaceVariant,
                                                    contentColor = colorScheme.onSurface,
                                                    disabledContainerColor = colorScheme.surfaceVariant,
                                                    disabledContentColor = colorScheme.onSurfaceVariant
                                                ),
                                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                                            ) {
                                                Text("-")
                                            }

                                            Surface(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(radius.md))
                                                    .clickable {
                                                        countNumpadInput = state.editorDailyTarget.coerceAtLeast(1).toString()
                                                        showCountNumpad = true
                                                    },
                                                shape = RoundedCornerShape(radius.md),
                                                color = colorScheme.surfaceVariant,
                                                border = BorderStroke(stroke.thin, colorScheme.outlineVariant)
                                            ) {
                                                Text(
                                                    text = state.editorDailyTarget.coerceAtLeast(1).toString(),
                                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = colorScheme.onSurface
                                                )
                                            }

                                            Button(
                                                onClick = {
                                                    val next = (state.editorDailyTarget.coerceAtLeast(1) + 1).coerceAtMost(99)
                                                    vm.setEditorDailyTarget(next)
                                                },
                                                enabled = state.editorDailyTarget < 99,
                                                shape = RoundedCornerShape(radius.md),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = colorScheme.surfaceVariant,
                                                    contentColor = colorScheme.onSurface,
                                                    disabledContainerColor = colorScheme.surfaceVariant,
                                                    disabledContentColor = colorScheme.onSurfaceVariant
                                                ),
                                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                                            ) {
                                                Text("+")
                                            }
                                        }
                                    }

                                    OutlinedTextField(
                                        value = state.editorUnitLabel,
                                        onValueChange = vm::setEditorUnitLabel,
                                        label = { Text(t("Unit label")) },
                                        placeholder = {
                                            val unitHint = when {
                                                state.editorDailyTarget <= 10 -> t("editor_unit_hint_small")
                                                state.editorDailyTarget <= 100 -> t("editor_unit_hint_medium")
                                                else -> t("editor_unit_hint_large")
                                            }
                                            Text(unitHint)
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                }
                            }
                        }

                        if (state.editorTrackingType == TrackingType.DURATION) {
                            FormSection(title = t("Duration target")) {
                                OutlinedTextField(
                                    value = state.editorDailyTarget.toString(),
                                    onValueChange = { raw ->
                                        val parsed = raw.filter { it.isDigit() }.take(3).toIntOrNull()
                                        if (parsed != null) {
                                            vm.setEditorDailyTarget(parsed.coerceIn(1, 600))
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text(t("Daily minute goal")) },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    supportingText = {
                                        Text(
                                            text = "1-600",
                                            color = colorScheme.onSurfaceVariant
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
                FormSection(title = t("label_frequency")) {
                    Column(
                        modifier = Modifier.animateContentSize(animationSpec = tween(220)),
                        verticalArrangement = Arrangement.spacedBy(spacing.x1)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                        ) {
                            FrequencyCard(
                                modifier = Modifier.weight(1f),
                                title = t("editor_freq_daily_title"),
                                subtitle = t("editor_freq_daily_subtitle"),
                                selected = state.editorFrequency == TaskFrequency.DAILY,
                                filledDots = setOf(0, 1, 2, 3, 4, 5, 6),
                                onClick = { vm.setEditorFrequency(TaskFrequency.DAILY) }
                            )
                            FrequencyCard(
                                modifier = Modifier.weight(1f),
                                title = t("editor_freq_set_days_title"),
                                subtitle = t("editor_freq_set_days_subtitle"),
                                selected = state.editorFrequency == TaskFrequency.SELECTED_DAYS,
                                filledDots = setOf(0, 2, 4),
                                onClick = {
                                    if (state.editorFrequency != TaskFrequency.SELECTED_DAYS && state.editorCustomDays.isNotEmpty()) {
                                        vm.setEditorCustomDays(emptySet())
                                    }
                                    vm.setEditorFrequency(TaskFrequency.SELECTED_DAYS)
                                }
                            )
                            FrequencyCard(
                                modifier = Modifier.weight(1f),
                                title = t("editor_freq_n_times_title"),
                                subtitle = t("editor_freq_n_times_subtitle"),
                                selected = state.editorFrequency == TaskFrequency.TIMES_PER_WEEK,
                                filledDots = setOf(1, 3, 5),
                                onClick = {
                                    vm.setEditorFrequency(TaskFrequency.TIMES_PER_WEEK)
                                    if (state.editorTimesPerWeek > 6) vm.setEditorTimesPerWeek(6)
                                }
                            )
                        }

                        AnimatedVisibility(
                            visible = state.editorFrequency == TaskFrequency.SELECTED_DAYS,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(spacing.x0_5)) {
                                Text(
                                    text = t("freq_selected_days_desc"),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colorScheme.onSurfaceVariant
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(spacing.x0_5)
                                ) {
                                    shortWeekdays.forEachIndexed { index, label ->
                                        val day = index + 1
                                        val selected = day in state.editorCustomDays
                                        Button(
                                            onClick = { vm.toggleEditorCustomDay(day) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(radius.md),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (selected) colorScheme.primaryContainer else colorScheme.surfaceVariant,
                                                contentColor = if (selected) colorScheme.primary else colorScheme.onSurfaceVariant
                                            ),
                                            contentPadding = PaddingValues(vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = label,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }

                                if (wasSaveAttempted && state.editorCustomDays.isEmpty()) {
                                    Text(
                                        text = t("Select at least one weekday."),
                                        color = colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }

                        AnimatedVisibility(
                            visible = state.editorFrequency == TaskFrequency.TIMES_PER_WEEK,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(spacing.x0_5)) {
                                Text(
                                    text = t("freq_times_per_week_desc"),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colorScheme.onSurfaceVariant
                                )
                                TimesPerWeekStepper(
                                    value = state.editorTimesPerWeek.coerceIn(1, 6),
                                    onValueChange = { vm.setEditorTimesPerWeek(it.coerceIn(1, 6)) },
                                    min = 1,
                                    max = 6
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = t("label_start_date"),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                    DateChip(
                        text = state.editorStartDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)),
                        onClick = {
                            showThemedDatePicker(
                                context = context,
                                themeResId = datePickerTheme,
                                initialDate = state.editorStartDate,
                                minDate = LocalDate.now(),
                                actionColorArgb = pickerActionColor,
                                onDateSet = { year, month, day ->
                                    vm.setEditorStartDate(LocalDate.of(year, month + 1, day))
                                }
                            )
                        }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { vm.setEditorShowAdvanced(!state.editorShowAdvanced) }
                        .padding(vertical = spacing.x0_5),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (state.editorShowAdvanced) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = null,
                        tint = colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(spacing.x0_5))
                    Text(
                        text = if (state.editorShowAdvanced) t("editor_advanced_hide") else t("editor_advanced_show"),
                        style = MaterialTheme.typography.labelMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                AnimatedVisibility(
                    visible = state.editorShowAdvanced,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1_5)) {
                        SettingsSwitchRow(
                            title = t("End date"),
                            subtitle = t("Optional challenge finish date"),
                            checked = state.editorEndDate != null,
                            onCheckedChange = { enabled ->
                                vm.setEditorEndDateEnabled(enabled)
                                if (enabled) {
                                    val minDate = maxOf(LocalDate.now(), state.editorStartDate)
                                    val initialDate = state.editorEndDate ?: maxOf(LocalDate.now().plusDays(30), state.editorStartDate)
                                    showThemedDatePicker(
                                        context = context,
                                        themeResId = datePickerTheme,
                                        initialDate = initialDate,
                                        minDate = minDate,
                                        actionColorArgb = pickerActionColor,
                                        onDateSet = { year, month, day ->
                                            vm.setEditorEndDate(LocalDate.of(year, month + 1, day))
                                        }
                                    )
                                }
                            }
                        )

                        AnimatedVisibility(visible = state.editorEndDate != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = t("End date"),
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colorScheme.onSurfaceVariant
                                )
                                DateChip(
                                    text = (state.editorEndDate ?: state.editorStartDate)
                                        .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)),
                                    onClick = {
                                        val minDate = maxOf(LocalDate.now(), state.editorStartDate)
                                        showThemedDatePicker(
                                            context = context,
                                            themeResId = datePickerTheme,
                                            initialDate = state.editorEndDate ?: minDate,
                                            minDate = minDate,
                                            actionColorArgb = pickerActionColor,
                                            onDateSet = { year, month, day ->
                                                vm.setEditorEndDate(LocalDate.of(year, month + 1, day))
                                            }
                                        )
                                    }
                                )
                            }
                        }

                        SettingsSwitchRow(
                            title = t("label_reminder"),
                            subtitle = t("Enable habit reminder notifications"),
                            checked = state.editorReminderEnabled,
                            onCheckedChange = vm::setEditorReminderEnabled
                        )

                        AnimatedVisibility(visible = state.editorReminderEnabled) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = t("editor_remind_me_at"),
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colorScheme.onSurfaceVariant
                                )
                                DateChip(
                                    text = formatTimeForDevice(
                                        context,
                                        state.editorReminderHour,
                                        state.editorReminderMinute
                                    ),
                                    onClick = {
                                        showThemedTimePicker(
                                            context = context,
                                            themeResId = pickerTheme,
                                            initialHour = state.editorReminderHour,
                                            initialMinute = state.editorReminderMinute,
                                            is24HourView = is24HourView,
                                            actionColorArgb = pickerActionColor,
                                            onTimeSet = vm::setEditorReminder
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(spacing.x2))
            }
        }
    }
}

@Composable
private fun FrequencyCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    selected: Boolean,
    filledDots: Set<Int>,
    onClick: () -> Unit
) {
    val radius = AppTheme.radius
    val stroke = AppTheme.stroke
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(radius.md))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(radius.md),
        color = if (selected) colorScheme.primaryContainer else colorScheme.surface,
        border = BorderStroke(
            if (selected) stroke.medium else stroke.thin,
            if (selected) colorScheme.primary else colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(7) { index ->
                    val filled = index in filledDots
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(
                                color = when {
                                    filled && selected -> colorScheme.primary
                                    filled -> colorScheme.onSurface.copy(alpha = 0.5f)
                                    else -> colorScheme.surfaceVariant
                                }
                            )
                    )
                }
            }

            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DateChip(
    text: String,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = text,
                fontSize = 13.sp,
                color = colorScheme.onSurface
            )
            Icon(
                imageVector = Icons.Filled.EditCalendar,
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}












