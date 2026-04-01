package com.example.microhabit.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.microhabit.DEFAULT_REMINDER_HOUR
import com.example.microhabit.DEFAULT_REMINDER_MINUTE
import com.example.microhabit.HabitUiState
import com.example.microhabit.MainViewModel
import com.example.microhabit.OnboardingHabitDraft
import com.example.microhabit.OnboardingStep
import com.example.microhabit.R
import com.example.microhabit.StreakOverlayModel
import com.example.microhabit.data.HabitCategory
import com.example.microhabit.data.HabitTemplateCatalog
import com.example.microhabit.data.MAX_HABIT_TITLE_LENGTH
import com.example.microhabit.data.TaskFrequency
import com.example.microhabit.data.TrackingType
import com.example.microhabit.i18n.t
import com.example.microhabit.i18n.tf
import com.example.microhabit.i18n.translate
import com.example.microhabit.ui.components.FormSection
import com.example.microhabit.ui.components.SettingsSwitchRow
import com.example.microhabit.ui.components.Stepper
import com.example.microhabit.ui.components.WeekdaySelector
import com.example.microhabit.ui.shared.GlassCard
import com.example.microhabit.ui.shared.SelectChip
import com.example.microhabit.ui.shared.StreakRewardOverlay
import com.example.microhabit.ui.shared.formatTimeForDevice
import com.example.microhabit.ui.shared.showThemedTimePicker
import com.example.microhabit.ui.theme.AppTheme
import kotlinx.coroutines.delay
@Composable
internal fun OnboardingCard(vm: MainViewModel) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val canAdd = vm.canCreateTask()
    GlassCard {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
            Text(t("Create your first habit"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(
                text = t("Set up the basics first and add advanced options if needed."),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )
            Button(
                onClick = vm::openCreateTask,
                enabled = canAdd,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (canAdd) t("Create habit") else t("Get Premium"))
            }
        }
    }
}

@Composable
internal fun OnboardingWizard(
    state: HabitUiState,
    onSkip: () -> Unit,
    onCreateHabit: (OnboardingHabitDraft) -> Unit,
    onHabitCreated: () -> Unit,
    onFinish: () -> Unit
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val stroke = AppTheme.stroke
    val radius = AppTheme.radius
    val context = LocalContext.current
    val is24HourView = android.text.format.DateFormat.is24HourFormat(context)
    val pickerTheme = R.style.ThemeOverlay_MicroHabit_Picker
    val pickerActionColor = colors.primary.toArgb()
    var step by rememberSaveable { mutableStateOf(OnboardingStep.WELCOME) }
    var selectedCategory by rememberSaveable { mutableStateOf(HabitCategory.HEALTH) }
    var selectedTemplateId by rememberSaveable { mutableStateOf(HabitTemplateCatalog.templatesFor(HabitCategory.HEALTH).first().id) }
    var habitName by rememberSaveable { mutableStateOf("") }
    var onboardingTrackingType by rememberSaveable { mutableStateOf(TrackingType.YES_NO) }
    var onboardingDurationTarget by rememberSaveable { mutableStateOf(20) }
    var frequency by rememberSaveable { mutableStateOf(TaskFrequency.DAILY) }
    var customDays by rememberSaveable { mutableStateOf(listOf(1, 2, 3, 4, 5)) }
    var reminderEnabled by rememberSaveable { mutableStateOf(false) }
    var reminderHour by rememberSaveable {
        mutableStateOf(DEFAULT_REMINDER_HOUR)
    }
    var reminderMinute by rememberSaveable {
        mutableStateOf(DEFAULT_REMINDER_MINUTE)
    }
    var miniCalendarFill by remember(step) { mutableStateOf(0) }
    var templateRevealCount by remember(step, selectedCategory) { mutableStateOf(0) }
    var pendingCreate by remember { mutableStateOf(false) }
    var completionOverlayVisible by remember(step) { mutableStateOf(false) }
    var habitCreatedNotified by remember { mutableStateOf(false) }
    val templates = remember(selectedCategory) { HabitTemplateCatalog.templatesFor(selectedCategory) }
    val selectedTemplate = templates.firstOrNull { it.id == selectedTemplateId } ?: templates.first()
    val setupValid = remember(habitName, frequency, customDays, onboardingTrackingType, onboardingDurationTarget) {
        habitName.trim().isNotEmpty() &&
            (frequency != TaskFrequency.SELECTED_DAYS || customDays.isNotEmpty()) &&
            (onboardingTrackingType != TrackingType.DURATION || onboardingDurationTarget > 0)
    }

    LaunchedEffect(step) {
        if (step == OnboardingStep.WELCOME) {
            miniCalendarFill = 0
            repeat(5) { index ->
                delay(90)
                miniCalendarFill = index + 1
            }
        }
        if (step == OnboardingStep.READY) {
            completionOverlayVisible = true
        } else {
            completionOverlayVisible = false
        }
    }

    LaunchedEffect(step, selectedCategory) {
        if (step == OnboardingStep.TEMPLATE) {
            templateRevealCount = 0
            val total = templates.size
            repeat(total) { index ->
                delay(55)
                templateRevealCount = index + 1
            }
        }
    }

    LaunchedEffect(selectedCategory) {
        selectedTemplateId = HabitTemplateCatalog.templatesFor(selectedCategory).first().id
    }

    LaunchedEffect(pendingCreate, state.tasks.size) {
        if (pendingCreate && state.tasks.isNotEmpty()) {
            pendingCreate = false
            if (!habitCreatedNotified) {
                onHabitCreated()
                habitCreatedNotified = true
            }
            step = OnboardingStep.READY
        }
    }
    LaunchedEffect(pendingCreate) {
        if (pendingCreate) {
            delay(3500)
            if (state.tasks.isEmpty()) {
                pendingCreate = false
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.backgroundCanvas
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing.x2, vertical = spacing.x1_5),
            verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
        ) {
            OnboardingHeader(
                onSkip = if (step == OnboardingStep.READY) null else onSkip
            )

            AnimatedContent(targetState = step, label = "onboardingStepTransition") { currentStep ->
                when (currentStep) {
                    OnboardingStep.WELCOME -> {
                        GlassCard(
                            contentPadding = PaddingValues(spacing.x2)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(spacing.x1_5),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Micro Habit",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = t("Build powerful habits one day at a time"),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.textSecondary,
                                    textAlign = TextAlign.Center
                                )
                                OnboardingMiniCalendar(filledDays = miniCalendarFill)
                                Button(
                                    onClick = { step = OnboardingStep.CATEGORY },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(radius.md)
                                ) {
                                    Text(t("Start"))
                                }
                            }
                        }
                    }

                    OnboardingStep.CATEGORY -> {
                        GlassCard(
                            contentPadding = PaddingValues(spacing.x2)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
                            ) {
                                Text(
                                    text = t("What do you want to improve?"),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                HabitTemplateCatalog.categories.chunked(2).forEach { row ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                                    ) {
                                        row.forEach { category ->
                                            OnboardingCategoryCard(
                                                modifier = Modifier.weight(1f),
                                                title = t(HabitTemplateCatalog.categoryTitleKey(category)),
                                                selected = selectedCategory == category,
                                                onClick = { selectedCategory = category }
                                            )
                                        }
                                        if (row.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                                ) {
                                    OutlinedButton(
                                        onClick = { step = OnboardingStep.WELCOME },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(radius.md)
                                    ) {
                                        Text(t("Back"))
                                    }
                                    Button(
                                        onClick = { step = OnboardingStep.TEMPLATE },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(radius.md)
                                    ) {
                                        Text(t("Next"))
                                    }
                                }
                            }
                        }
                    }

                    OnboardingStep.TEMPLATE -> {
                        GlassCard(
                            contentPadding = PaddingValues(spacing.x2)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(spacing.x1)
                            ) {
                                Text(
                                    text = t("Pick a template"),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                templates.forEachIndexed { index, template ->
                                    AnimatedVisibility(
                                        visible = index < templateRevealCount,
                                        enter = fadeIn(tween(180)) + slideInVertically(
                                            initialOffsetY = { it / 5 },
                                            animationSpec = tween(180)
                                        ),
                                        exit = fadeOut(tween(120))
                                    ) {
                                        OnboardingTemplateCard(
                                            title = t(template.titleKey),
                                            emoji = template.emoji,
                                            selected = template.id == selectedTemplateId,
                                            onClick = { selectedTemplateId = template.id }
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                                ) {
                                    OutlinedButton(
                                        onClick = { step = OnboardingStep.CATEGORY },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(radius.md)
                                    ) {
                                        Text(t("Back"))
                                    }
                                    Button(
                                        onClick = {
                                            if (habitName.isBlank() && selectedTemplate.id != HabitTemplateCatalog.CUSTOM_TEMPLATE.id) {
                                                habitName = translate(state.language, selectedTemplate.titleKey)
                                                    .take(MAX_HABIT_TITLE_LENGTH)
                                            }
                                            step = OnboardingStep.SETUP
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(radius.md)
                                    ) {
                                        Text(t("Next"))
                                    }
                                }
                            }
                        }
                    }

                    OnboardingStep.SETUP -> {
                        GlassCard(
                            contentPadding = PaddingValues(spacing.x2)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
                            ) {
                                Text(
                                    text = t("Set up your habit"),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                OutlinedTextField(
                                    value = habitName,
                                    onValueChange = { habitName = it.take(MAX_HABIT_TITLE_LENGTH) },
                                    label = { Text(t("Habit name")) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                FormSection(title = t("Tracking type")) {
                                    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                                        listOf(
                                            Triple(
                                                TrackingType.YES_NO,
                                                t("Do once"),
                                                t("Just mark whether you did it today")
                                            ),
                                            Triple(
                                                TrackingType.DURATION,
                                                t("Do N minutes"),
                                                t("Set a daily time target")
                                            )
                                        ).forEach { (type, title, description) ->
                                            val selected = onboardingTrackingType == type
                                            Surface(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(radius.md))
                                                    .clickable { onboardingTrackingType = type },
                                                shape = RoundedCornerShape(radius.md),
                                                color = if (selected) colors.primary.copy(alpha = 0.14f) else colors.backgroundSurfaceMuted,
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
                                                    Text(
                                                        text = title,
                                                        style = MaterialTheme.typography.titleSmall,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                    Text(
                                                        text = description,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = colors.textSecondary
                                                    )
                                                }
                                            }
                                        }
                                        if (onboardingTrackingType == TrackingType.DURATION) {
                                            Stepper(
                                                label = t("Daily minute goal"),
                                                value = onboardingDurationTarget,
                                                min = 1,
                                                max = 600,
                                                onValueChange = { onboardingDurationTarget = it }
                                            )
                                        }
                                    }
                                }

                                FormSection(title = t("Frequency")) {
                                    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                                        ) {
                                            SelectChip(
                                                title = t("Every day"),
                                                selected = frequency == TaskFrequency.DAILY,
                                                onClick = { frequency = TaskFrequency.DAILY }
                                            )
                                            SelectChip(
                                                title = t("Selected weekdays"),
                                                selected = frequency == TaskFrequency.SELECTED_DAYS,
                                                onClick = { frequency = TaskFrequency.SELECTED_DAYS }
                                            )
                                        }
                                        if (frequency == TaskFrequency.SELECTED_DAYS) {
                                            WeekdaySelector(
                                                selectedDays = customDays.toSet(),
                                                onToggle = { day ->
                                                    val next = customDays.toMutableSet()
                                                    if (!next.add(day)) {
                                                        next.remove(day)
                                                    }
                                                    customDays = next.toList().sorted()
                                                }
                                            )
                                            if (customDays.isEmpty()) {
                                                Text(
                                                    text = t("Select at least one weekday."),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = colors.danger
                                                )
                                            }
                                        }
                                    }
                                }

                                FormSection(title = t("Reminders")) {
                                    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                                        SettingsSwitchRow(
                                            title = t("Reminders"),
                                            subtitle = t("Enable habit reminder notifications"),
                                            checked = reminderEnabled,
                                            onCheckedChange = { reminderEnabled = it }
                                        )
                                        AnimatedVisibility(visible = reminderEnabled) {
                                            OutlinedButton(
                                                onClick = {
                                                    showThemedTimePicker(
                                                        context = context,
                                                        themeResId = pickerTheme,
                                                        initialHour = reminderHour,
                                                        initialMinute = reminderMinute,
                                                        is24HourView = is24HourView,
                                                        actionColorArgb = pickerActionColor,
                                                        onTimeSet = { hour, minute ->
                                                            reminderHour = hour.coerceIn(0, 23)
                                                            reminderMinute = minute.coerceIn(0, 59)
                                                        }
                                                    )
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(radius.md),
                                                border = BorderStroke(stroke.thin, colors.primary),
                                                colors = ButtonDefaults.outlinedButtonColors(
                                                    containerColor = Color.Transparent,
                                                    contentColor = colors.primary
                                                )
                                            ) {
                                                Text(
                                                    tf(
                                                        "Reminder: %s",
                                                        formatTimeForDevice(context, reminderHour, reminderMinute)
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                                ) {
                                    OutlinedButton(
                                        onClick = { step = OnboardingStep.TEMPLATE },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(radius.md)
                                    ) {
                                        Text(t("Back"))
                                    }
                                    Button(
                                        onClick = {
                                            if (pendingCreate) return@Button
                                            if (!setupValid) return@Button
                                            pendingCreate = true
                                            onCreateHabit(
                                                OnboardingHabitDraft(
                                                    name = habitName.trim(),
                                                    category = selectedCategory,
                                                    template = selectedTemplate,
                                                    trackingType = onboardingTrackingType,
                                                    dailyTarget = if (onboardingTrackingType == TrackingType.DURATION) {
                                                        onboardingDurationTarget
                                                    } else {
                                                        1
                                                    },
                                                    unitLabel = "",
                                                    frequency = frequency,
                                                    customDays = customDays.toSet(),
                                                    reminderEnabled = reminderEnabled,
                                                    reminderHour = reminderHour,
                                                    reminderMinute = reminderMinute
                                                )
                                            )
                                        },
                                        enabled = setupValid,
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(radius.md)
                                    ) {
                                        Text(t("Create habit"))
                                    }
                                }
                            }
                        }
                    }

                    OnboardingStep.READY -> {
                        GlassCard(
                            contentPadding = PaddingValues(spacing.x2)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(spacing.x1_5),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    tint = colors.success,
                                    modifier = Modifier.size(spacing.x6)
                                )
                                Text(
                                    text = t("Your habit is ready"),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = t("We highlighted the completion button so you can log your first win."),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.textSecondary,
                                    textAlign = TextAlign.Center
                                )
                                AnimatedVisibility(
                                    visible = completionOverlayVisible,
                                    enter = fadeIn(tween(180)) + scaleIn(initialScale = 0.9f, animationSpec = tween(180)),
                                    exit = fadeOut(tween(140))
                                ) {
                                    StreakRewardOverlay(
                                        model = StreakOverlayModel(streak = 1, milestone = false)
                                    )
                                }
                                Button(
                                    onClick = onFinish,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(radius.md)
                                ) {
                                    Text(t("Go to tracker"))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            OnboardingProgressDots(
                step = step,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 25.dp)
            )
        }
    }
}

@Composable
internal fun OnboardingHeader(onSkip: (() -> Unit)?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onSkip != null) {
            TextButton(onClick = onSkip) {
                Text(t("Skip"))
            }
        }
    }
}

@Composable
internal fun OnboardingProgressDots(step: OnboardingStep, modifier: Modifier = Modifier) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val current = when (step) {
        OnboardingStep.WELCOME -> 1
        OnboardingStep.CATEGORY -> 2
        OnboardingStep.TEMPLATE -> 3
        OnboardingStep.SETUP -> 4
        OnboardingStep.READY -> 4
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing.x0_5),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(4) { index ->
            Box(
                modifier = Modifier
                    .size(if (index < current) spacing.x1 else spacing.x0_5 + spacing.x1)
                    .clip(RoundedCornerShape(AppTheme.radius.full))
                    .background(if (index < current) colors.primary else colors.borderSubtle.copy(alpha = 0.55f))
            )
        }
    }
}

@Composable
internal fun OnboardingMiniCalendar(filledDays: Int) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val labels = remember { listOf("M", "T", "W", "T", "F", "S", "S") }
    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.x0_5)) {
            labels.forEachIndexed { index, label ->
                val filled = index < filledDays
                val scale by animateFloatAsState(
                    targetValue = if (filled) 1f else 0.95f,
                    animationSpec = tween(durationMillis = 180),
                    label = "onboardingCalendarScale$index"
                )
                Box(
                    modifier = Modifier
                        .size(spacing.x3)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .clip(RoundedCornerShape(AppTheme.radius.md))
                        .background(
                            if (filled) colors.success.copy(alpha = 0.9f)
                            else colors.backgroundSurfaceMuted
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (filled) Color.White else colors.textSecondary
                    )
                }
            }
        }
        Text(
            text = tf("%d day streak", filledDays.coerceAtLeast(1)),
            style = MaterialTheme.typography.labelLarge,
            color = colors.textSecondary
        )
    }
}



