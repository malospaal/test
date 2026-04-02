package com.example.microhabit.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.microhabit.data.TaskFrequency
import com.example.microhabit.data.TrackingType
import com.example.microhabit.i18n.LocalAppLanguage
import com.example.microhabit.i18n.appLocale
import com.example.microhabit.i18n.t
import com.example.microhabit.i18n.translate
import com.example.microhabit.i18n.weekdayLabels
import com.example.microhabit.ui.components.Stepper
import com.example.microhabit.ui.components.WeekdaySelector
import com.example.microhabit.ui.create.CreateHabitTemplate
import com.example.microhabit.ui.create.CreateHabitTemplateCatalog
import com.example.microhabit.ui.create.TemplateConfirmDraft
import com.example.microhabit.ui.shared.formatTimeForDevice
import com.example.microhabit.ui.theme.AppTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HabitTemplateConfirmScreen(
    initial: TemplateConfirmDraft,
    onBack: () -> Unit,
    onStateChange: (TemplateConfirmDraft) -> Unit,
    onCreateHabit: (TemplateConfirmDraft) -> Unit,
    onConfigureMore: (TemplateConfirmDraft) -> Unit,
    onPickStartDate: (LocalDate, (LocalDate) -> Unit) -> Unit,
    onRequestReminderTime: (Int, Int, (Int, Int) -> Unit) -> Unit
) {
    val spacing = AppTheme.spacing
    val colorScheme = MaterialTheme.colorScheme
    val locale = appLocale()
    val language = LocalAppLanguage.current
    val template = initial.template
    val habitName = remember(template.id, initial.habitName, language) {
        initial.habitName.ifBlank { translate(language, template.nameKey) }
    }
    val dailyTarget = remember(template.id, initial.dailyTarget) { initial.dailyTarget.coerceAtLeast(1) }
    var frequency by rememberSaveable(template.id) { mutableStateOf(initial.frequency) }
    var customDays by rememberSaveable(template.id) { mutableStateOf(initial.customDays) }
    var timesPerWeek by rememberSaveable(template.id) { mutableStateOf(initial.timesPerWeek.coerceIn(1, 7)) }
    var startDate by rememberSaveable(template.id) { mutableStateOf(initial.startDate) }
    var reminderEnabled by rememberSaveable(template.id) { mutableStateOf(initial.reminderEnabled) }
    var reminderHour by rememberSaveable(template.id) { mutableStateOf(initial.reminderHour.coerceIn(0, 23)) }
    var reminderMinute by rememberSaveable(template.id) { mutableStateOf(initial.reminderMinute.coerceIn(0, 59)) }
    var expandedParam by rememberSaveable(template.id) { mutableStateOf<ExpandedConfirmParam?>(null) }
    val currentDraft = remember(
        template,
        habitName,
        dailyTarget,
        frequency,
        customDays,
        timesPerWeek,
        startDate,
        reminderEnabled,
        reminderHour,
        reminderMinute
    ) {
        TemplateConfirmDraft(
            template = template,
            habitName = habitName.trim().ifBlank { translate(language, template.nameKey) },
            dailyTarget = dailyTarget.coerceAtLeast(1),
            frequency = frequency,
            customDays = if (frequency == TaskFrequency.SELECTED_DAYS) {
                customDays.ifEmpty { template.defaultDays.ifEmpty { setOf(1) } }
            } else {
                customDays
            },
            timesPerWeek = timesPerWeek.coerceIn(1, 7),
            startDate = startDate,
            reminderEnabled = reminderEnabled,
            reminderHour = reminderHour,
            reminderMinute = reminderMinute
        )
    }

    LaunchedEffect(currentDraft) {
        onStateChange(currentDraft)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorScheme.background
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = t("screen_create_habit"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = t("Back")
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = colorScheme.surface
                    )
                )
            },
            bottomBar = {
                Surface(
                    color = colorScheme.surface,
                    tonalElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onConfigureMore(currentDraft) }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = t("customize_more"),
                                fontSize = 14.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        Button(
                            onClick = { onCreateHabit(currentDraft) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.primary,
                                contentColor = colorScheme.onPrimary
                            ),
                            contentPadding = PaddingValues(vertical = 15.dp)
                        ) {
                            Text(
                                text = t("btn_create_habit"),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = spacing.x2, vertical = spacing.x2)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(spacing.x2)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(colorScheme.primaryContainer.copy(alpha = 0.45f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = template.emoji, fontSize = 52.sp)
                    }
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = habitName,
                        fontSize = 29.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onBackground
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = buildTemplateSubtitle(template),
                        fontSize = 14.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = colorScheme.surface,
                    border = BorderStroke(AppTheme.stroke.thin, colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val isFrequencyExpanded = expandedParam == ExpandedConfirmParam.FREQUENCY
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 2.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = t("label_frequency"),
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorScheme.onSurfaceVariant
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (!isFrequencyExpanded) {
                                    Text(
                                        text = templateFrequencyLabel(frequency),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colorScheme.onSurface
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            color = colorScheme.primaryContainer,
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .clickable {
                                            expandedParam = if (isFrequencyExpanded) null else ExpandedConfirmParam.FREQUENCY
                                        }
                                        .padding(horizontal = 9.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = if (isFrequencyExpanded) t("action_done") else t("edit_label"),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colorScheme.primary
                                    )
                                }
                            }
                        }

                        AnimatedVisibility(
                            visible = isFrequencyExpanded,
                            enter = expandVertically(animationSpec = tween(200)) + fadeIn(),
                            exit = shrinkVertically(animationSpec = tween(200)) + fadeOut()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 2.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TemplateFrequencyOption(
                                    label = t("freq_every_day"),
                                    selected = frequency == TaskFrequency.DAILY,
                                    onClick = { frequency = TaskFrequency.DAILY }
                                )
                                TemplateFrequencyOption(
                                    label = t("freq_selected_days"),
                                    selected = frequency == TaskFrequency.SELECTED_DAYS,
                                    description = t("freq_selected_days_desc"),
                                    onClick = {
                                        frequency = TaskFrequency.SELECTED_DAYS
                                        if (customDays.isEmpty()) {
                                            customDays = template.defaultDays.ifEmpty { setOf(1, 2, 3, 4, 5) }
                                        }
                                    }
                                )
                                AnimatedVisibility(visible = frequency == TaskFrequency.SELECTED_DAYS) {
                                    WeekdaySelector(
                                        selectedDays = customDays,
                                        onToggle = { day ->
                                            val next = customDays.toMutableSet()
                                            if (!next.add(day)) next.remove(day)
                                            customDays = next
                                        }
                                    )
                                }
                                TemplateFrequencyOption(
                                    label = t("freq_times_per_week"),
                                    selected = frequency == TaskFrequency.TIMES_PER_WEEK,
                                    description = t("freq_times_per_week_desc"),
                                    onClick = { frequency = TaskFrequency.TIMES_PER_WEEK }
                                )
                                AnimatedVisibility(visible = frequency == TaskFrequency.TIMES_PER_WEEK) {
                                    TimesPerWeekStepper(
                                        value = timesPerWeek,
                                        onValueChange = { timesPerWeek = it.coerceIn(1, 7) }
                                    )
                                }
                            }
                        }

                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = colorScheme.outlineVariant
                        )
                        TemplateParamRow(
                            label = t("label_start_date"),
                            value = startDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)),
                            action = t("edit_label"),
                            onAction = {
                                onPickStartDate(startDate) { picked ->
                                    startDate = picked
                                }
                            }
                        )
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = colorScheme.outlineVariant
                        )
                        TemplateParamRow(
                            label = t("label_reminder"),
                            value = if (reminderEnabled) {
                                formatTimeForDevice(LocalContext.current, reminderHour, reminderMinute)
                            } else {
                                t("label_reminder_off")
                            },
                            action = if (reminderEnabled) t("edit_label") else t("enable_label"),
                            onAction = {
                                onRequestReminderTime(reminderHour, reminderMinute) { hour, minute ->
                                    reminderEnabled = true
                                    reminderHour = hour
                                    reminderMinute = minute
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

private enum class ExpandedConfirmParam {
    FREQUENCY
}

@Composable
private fun TimesPerWeekStepper(
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Stepper(
        label = t("Times per week"),
        value = value.coerceIn(1, 7),
        min = 1,
        max = 7,
        onValueChange = { onValueChange(it.coerceIn(1, 7)) }
    )
}

@Composable
private fun TemplateFrequencyOption(
    label: String,
    selected: Boolean,
    description: String? = null,
    onClick: () -> Unit
) {
    val spacing = AppTheme.spacing
    val colorScheme = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.radius.md))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(AppTheme.radius.md),
        color = if (selected) colorScheme.primaryContainer else colorScheme.surfaceVariant,
        border = BorderStroke(
            if (selected) 1.5.dp else AppTheme.stroke.thin,
            if (selected) colorScheme.primary else colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = spacing.x1, vertical = spacing.x0_5),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = colorScheme.onSurface
            )
            if (!description.isNullOrBlank()) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TemplateParamRow(
    label: String,
    value: String,
    action: String,
    onAction: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(
                    color = colorScheme.primaryContainer,
                    shape = RoundedCornerShape(6.dp)
                )
                .clickable(onClick = onAction)
                .padding(horizontal = 9.dp, vertical = 3.dp)
        ) {
            Text(
                text = action,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.primary
            )
        }
    }
}

@Composable
private fun buildTemplateSubtitle(template: CreateHabitTemplate): String {
    val category = t(CreateHabitTemplateCatalog.categoryLabelKey(template.category))
    return "$category · ${templateMetaLabel(template)}"
}

@Composable
private fun templateTrackingTypeLabel(type: TrackingType): String = when (type) {
    TrackingType.YES_NO -> t("tracking_type_do")
    TrackingType.COUNT -> t("tracking_type_count")
    TrackingType.DURATION -> t("tracking_type_time")
}

@Composable
internal fun templateMetaLabel(template: CreateHabitTemplate): String {
    val trackingLabel = when (template.trackingType) {
        TrackingType.YES_NO -> templateTrackingTypeLabel(template.trackingType)
        TrackingType.COUNT, TrackingType.DURATION -> {
            val unit = if (template.unitLabelKey.isBlank()) "" else t(template.unitLabelKey)
            "${template.dailyTarget} $unit".trim()
        }
    }
    val frequencyLabel = templateFrequencyMetaLabel(template)
    return "$trackingLabel · $frequencyLabel"
}

@Composable
private fun templateFrequencyLabel(frequency: TaskFrequency): String = when (frequency) {
    TaskFrequency.DAILY -> t("freq_every_day")
    TaskFrequency.SELECTED_DAYS -> t("freq_selected_days")
    TaskFrequency.TIMES_PER_WEEK -> t("freq_times_per_week")
}

@Composable
private fun templateFrequencyMetaLabel(template: CreateHabitTemplate): String = when (template.frequency) {
    TaskFrequency.DAILY -> t("freq_every_day")
    TaskFrequency.SELECTED_DAYS -> selectedDaysShortLabel(template.defaultDays)
    TaskFrequency.TIMES_PER_WEEK -> "${template.defaultTimesPerWeek} ${t("freq_times_short")}"
}

@Composable
private fun selectedDaysShortLabel(days: Set<Int>): String {
    val labels = weekdayLabels(LocalAppLanguage.current)
    val normalized = days.filter { it in 1..7 }.sorted()
    if (normalized.isEmpty()) return t("freq_selected_days")
    if (normalized.size >= 2) {
        val expectedRange = (normalized.first()..normalized.last()).toList()
        if (normalized == expectedRange) {
            val first = labels[normalized.first() - 1]
            val last = labels[normalized.last() - 1]
            return "$first–$last"
        }
    }
    return normalized.joinToString(" ") { day -> labels[day - 1] }
}
