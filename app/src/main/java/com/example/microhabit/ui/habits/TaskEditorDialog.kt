package com.example.microhabit.ui.habits

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.microhabit.*
import com.example.microhabit.R
import com.example.microhabit.data.TaskFrequency
import com.example.microhabit.data.TrackingType
import com.example.microhabit.i18n.appLocale
import com.example.microhabit.i18n.t
import com.example.microhabit.i18n.tf
import com.example.microhabit.ui.components.ChoiceOption
import com.example.microhabit.ui.components.FormSection
import com.example.microhabit.ui.components.SettingsSwitchRow
import com.example.microhabit.ui.components.Stepper
import com.example.microhabit.ui.components.WeekdaySelector
import com.example.microhabit.ui.components.parseColorHex
import com.example.microhabit.ui.shared.SelectChip
import com.example.microhabit.ui.shared.formatTimeForDevice
import com.example.microhabit.ui.shared.showThemedDatePicker
import com.example.microhabit.ui.shared.showThemedTimePicker
import com.example.microhabit.ui.theme.AppTheme
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
    val colors = AppTheme.colors
    val stroke = AppTheme.stroke
    val context = LocalContext.current
    val locale = appLocale()
    val is24HourView = android.text.format.DateFormat.is24HourFormat(context)
    val selectedColor = parseColorHex(state.editorColorHex)
    val pickerTheme = R.style.ThemeOverlay_MicroHabit_Picker
    val datePickerTheme = R.style.ThemeOverlay_MicroHabit_DatePicker
    val pickerActionColor = colors.primary.toArgb()
    var showEmojiPicker by rememberSaveable { mutableStateOf(false) }
    val trackingCards = listOf(
        Triple(TrackingType.YES_NO, t("tracking_type_yesno"), t("tracking_type_yesno_desc")),
        Triple(TrackingType.COUNT, t("tracking_type_count"), t("tracking_type_count_desc")),
        Triple(TrackingType.DURATION, t("tracking_type_duration"), t("tracking_type_duration_desc"))
    )
    val frequencyOptions = listOf(
        ChoiceOption(TaskFrequency.DAILY, t("freq_every_day")),
        ChoiceOption(TaskFrequency.SELECTED_DAYS, t("freq_selected_days")),
        ChoiceOption(TaskFrequency.TIMES_PER_WEEK, t("freq_times_per_week"))
    )
    val palette = listOf("#1F6F64", "#3B7EA1", "#7B6BC9", "#3E8E5F", "#B36A3C", "#C65C74", "#5D6D7E")
    val emojiSuggestions = listOf(
        "✨", "💧", "💊", "🥗", "🌿", "😴", "🏃", "🏋️", "🚴", "🤸",
        "👟", "🧘", "📵", "🙏", "📚", "✍️", "🎓", "💼", "🎯", "🔥",
        "☀️", "🌙", "💡", "📈", "🍎", "🧠", "🫶", "🎵", "🧹", "🧴"
    )

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
                                shape = RoundedCornerShape(AppTheme.radius.md),
                                color = colors.backgroundSurfaceMuted,
                                border = BorderStroke(stroke.thin, colors.borderSubtle.copy(alpha = 0.6f)),
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

    BackHandler(onBack = onDismiss)
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.backgroundCanvas
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
                        containerColor = colors.backgroundSurface
                    )
                )
            },
            bottomBar = {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding(),
                    color = colors.backgroundSurface,
                    tonalElevation = AppTheme.elevation.sm
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.x2, vertical = spacing.x1_5),
                        verticalArrangement = Arrangement.spacedBy(spacing.x0_5)
                    ) {
                        if (!vm.canSaveEditor()) {
                            Text(
                                text = t("Fill required fields to continue."),
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textSecondary
                            )
                        }
                        Button(
                            onClick = onSaveRequest,
                            enabled = vm.canSaveEditor(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(AppTheme.radius.md)
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
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(stroke.thin, colors.borderSubtle.copy(alpha = 0.7f), RoundedCornerShape(AppTheme.radius.md))
                                .padding(horizontal = spacing.x1, vertical = spacing.x0_5),
                            horizontalArrangement = Arrangement.spacedBy(spacing.x1),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                onClick = { showEmojiPicker = true },
                                shape = RoundedCornerShape(8.dp),
                                color = colors.backgroundSurfaceMuted,
                                border = BorderStroke(stroke.thin, colors.borderSubtle.copy(alpha = 0.6f)),
                                modifier = Modifier
                                    .size(36.dp)
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
                                textStyle = MaterialTheme.typography.bodyLarge.copy(color = colors.textPrimary),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                singleLine = true,
                                decorationBox = { inner ->
                                    if (state.editorTitle.isBlank()) {
                                        Text(
                                            text = t("Habit name"),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = colors.textSecondary.copy(alpha = 0.5f)
                                        )
                                    }
                                    inner()
                                }
                            )
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(RoundedCornerShape(AppTheme.radius.full))
                                    .background(selectedColor.copy(alpha = 0.85f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Spacer(modifier = Modifier.size(1.dp))
                            }
                        }
                        Row(
                            modifier = Modifier.padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(spacing.x1),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            palette.forEach { hex ->
                                val color = parseColorHex(hex)
                                val selected = hex.equals(state.editorColorHex, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(AppTheme.radius.full))
                                        .background(color)
                                        .border(
                                            width = if (selected) 2.dp else stroke.thin,
                                            color = if (selected) color else colors.borderSubtle.copy(alpha = 0.6f),
                                            shape = RoundedCornerShape(AppTheme.radius.full)
                                        )
                                        .clickable { vm.setEditorColorHex(hex) }
                                )
                            }
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
                                    .clip(RoundedCornerShape(AppTheme.radius.md))
                                    .clickable { vm.setEditorTrackingType(type) },
                                shape = RoundedCornerShape(AppTheme.radius.md),
                                color = if (selected) {
                                    colors.primary.copy(alpha = 0.14f)
                                } else {
                                    colors.backgroundSurfaceMuted
                                },
                                border = BorderStroke(
                                    width = if (selected) stroke.medium else stroke.thin,
                                    color = if (selected) colors.primary else colors.borderSubtle
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
                                            color = colors.textPrimary
                                        )
                                    }
                                    if (!selected) {
                                        Text(
                                            text = description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = colors.textSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                when (state.editorTrackingType) {
                    TrackingType.YES_NO -> Unit
                    TrackingType.COUNT -> {
                        FormSection(title = t("Count target")) {
                            Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                                Stepper(
                                    label = t("Daily target"),
                                    value = state.editorDailyTarget,
                                    min = 1,
                                    max = 999,
                                    onValueChange = vm::setEditorDailyTarget
                                )
                                OutlinedTextField(
                                    value = state.editorUnitLabel,
                                    onValueChange = vm::setEditorUnitLabel,
                                    label = { Text(t("Unit label")) },
                                    placeholder = {
                                        Text(
                                            if (state.editorDailyTarget <= 10) {
                                                t("unit_label_hint_small")
                                            } else {
                                                t("unit_label_hint_large")
                                            }
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }
                        }
                    }
                    TrackingType.DURATION -> {
                        FormSection(title = t("Duration target")) {
                            Stepper(
                                label = t("Daily minute goal"),
                                value = state.editorDailyTarget,
                                min = 1,
                                max = 600,
                                onValueChange = vm::setEditorDailyTarget
                            )
                        }
                    }
                }

                FormSection(title = t("label_frequency")) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(spacing.x0_5)
                        ) {
                            items(frequencyOptions, key = { it.value }) { option ->
                                SelectChip(
                                    title = option.label,
                                    selected = option.value == state.editorFrequency,
                                    onClick = { vm.setEditorFrequency(option.value) }
                                )
                            }
                        }

                        when (state.editorFrequency) {
                            TaskFrequency.SELECTED_DAYS -> {
                                Text(
                                    text = t("freq_selected_days_desc"),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.textSecondary
                                )
                            }
                            TaskFrequency.TIMES_PER_WEEK -> {
                                Text(
                                    text = t("freq_times_per_week_desc"),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.textSecondary
                                )
                            }
                            TaskFrequency.DAILY -> Unit
                        }

                        if (state.editorFrequency == TaskFrequency.SELECTED_DAYS) {
                            WeekdaySelector(
                                selectedDays = state.editorCustomDays,
                                onToggle = vm::toggleEditorCustomDay
                            )
                            if (state.editorCustomDays.isEmpty()) {
                                Text(
                                    text = t("Select at least one weekday."),
                                    color = colors.danger,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        if (state.editorFrequency == TaskFrequency.TIMES_PER_WEEK) {
                            Stepper(
                                label = t("Times per week"),
                                value = state.editorTimesPerWeek,
                                min = 1,
                                max = 7,
                                onValueChange = vm::setEditorTimesPerWeek
                            )
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
                        color = colors.textSecondary
                    )
                    Text(
                        text = state.editorStartDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    TextButton(
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
                        },
                        contentPadding = PaddingValues(horizontal = spacing.x0_5, vertical = spacing.x0)
                    ) {
                        Text(t("Edit"))
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { vm.setEditorShowAdvanced(!state.editorShowAdvanced) }
                        .padding(vertical = spacing.x0_5),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (state.editorShowAdvanced) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(spacing.x0_5))
                    Text(
                        text = if (state.editorShowAdvanced) t("hide_advanced") else t("show_advanced"),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.primary
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
                            OutlinedButton(
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
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(AppTheme.radius.md),
                                border = BorderStroke(stroke.thin, colors.primary),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = colors.primary
                                )
                            ) {
                                Text(
                                    tf(
                                        "End date: %s",
                                        (state.editorEndDate ?: state.editorStartDate)
                                            .format(DateTimeFormatter.ofPattern(t("dd MMM yyyy"), locale))
                                    )
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
                            OutlinedButton(
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
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(AppTheme.radius.md),
                                border = BorderStroke(stroke.thin, colors.primary),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = colors.primary
                                )
                            ) {
                                Text(
                                    tf(
                                        "Reminder: %s",
                                        formatTimeForDevice(
                                            context,
                                            state.editorReminderHour,
                                            state.editorReminderMinute
                                        )
                                    )
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



