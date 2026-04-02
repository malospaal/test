package com.example.microhabit.ui.habits

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
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
import com.example.microhabit.ui.shared.formatTimeForDevice
import com.example.microhabit.ui.shared.showThemedDatePicker
import com.example.microhabit.ui.shared.showThemedTimePicker
import com.example.microhabit.ui.theme.AppTheme
import com.example.microhabit.ui.tracker.ValueNumpad
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlinx.coroutines.delay

private object HabitEditorMotion {
    val Quick = tween<Float>(durationMillis = 150, easing = FastOutSlowInEasing)
    val QuickDp = tween<Dp>(durationMillis = 150, easing = FastOutSlowInEasing)
    val QuickInt = tween<Int>(durationMillis = 150, easing = FastOutSlowInEasing)
    val Medium = tween<Float>(durationMillis = 220, easing = FastOutSlowInEasing)
    val Standard = tween<Float>(durationMillis = 280, easing = FastOutSlowInEasing)
    val StandardDp = tween<Dp>(durationMillis = 280, easing = FastOutSlowInEasing)
    val StandardInt = tween<Int>(durationMillis = 280, easing = FastOutSlowInEasing)
    val QuickColor = tween<Color>(durationMillis = 150, easing = FastOutSlowInEasing)
    val SaveColor = tween<Color>(durationMillis = 200, easing = FastOutSlowInEasing)
    val ErrorEnterFloat = tween<Float>(durationMillis = 150, easing = FastOutSlowInEasing)
    val ErrorExitFloat = tween<Float>(durationMillis = 120, easing = FastOutSlowInEasing)
    val ErrorEnterInt = tween<Int>(durationMillis = 150, easing = FastOutSlowInEasing)
    val ErrorExitInt = tween<Int>(durationMillis = 120, easing = FastOutSlowInEasing)
    val ErrorEnterSize = tween<IntSize>(durationMillis = 150, easing = FastOutSlowInEasing)
    val ErrorExitSize = tween<IntSize>(durationMillis = 120, easing = FastOutSlowInEasing)

    val enterMedium =
        expandVertically(tween(220, easing = FastOutSlowInEasing)) +
            fadeIn(tween(220, easing = FastOutSlowInEasing))
    val exitMedium =
        shrinkVertically(tween(220, easing = FastOutSlowInEasing)) +
            fadeOut(tween(220, easing = FastOutSlowInEasing))

    val enterStandard =
        expandVertically(tween(280, easing = FastOutSlowInEasing)) +
            fadeIn(tween(280, easing = FastOutSlowInEasing))
    val exitStandard =
        shrinkVertically(tween(280, easing = FastOutSlowInEasing)) +
            fadeOut(tween(280, easing = FastOutSlowInEasing))

    val enterDelayed =
        expandVertically(tween(280, easing = FastOutSlowInEasing)) +
            fadeIn(tween(250, delayMillis = 80, easing = FastOutSlowInEasing))
    val exitFast =
        shrinkVertically(tween(180, easing = FastOutSlowInEasing)) +
            fadeOut(tween(130, easing = FastOutSlowInEasing))
}

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
    var durationInput by rememberSaveable(state.editingTaskId) {
        mutableStateOf(state.editorDailyTarget.coerceIn(1, 600).toString())
    }
    var wasSaveAttempted by rememberSaveable(state.editingTaskId) { mutableStateOf(false) }

    val isDurationTargetInvalid =
        state.editorTrackingType == TrackingType.DURATION && durationInput.isBlank()
    val canSave = vm.canSaveEditor() && !isDurationTargetInvalid
    val isTitleInvalid = wasSaveAttempted && state.editorTitle.isBlank()
    val shortWeekdays = remember(language) { weekdayLabels(language).map { it.take(2) } }
    val editorScrollState = rememberScrollState()

    val saveBtnBg by animateColorAsState(
        targetValue = if (canSave) colorScheme.primary else colorScheme.surfaceVariant,
        animationSpec = HabitEditorMotion.SaveColor,
        label = "saveBtnBg"
    )
    val saveBtnText by animateColorAsState(
        targetValue = if (canSave) colorScheme.onPrimary else colorScheme.onSurfaceVariant,
        animationSpec = HabitEditorMotion.SaveColor,
        label = "saveBtnText"
    )

    var frequencyDescriptionTarget by remember { mutableStateOf(state.editorFrequency) }
    var showFrequencyDescription by remember { mutableStateOf(true) }
    var lastTargetTrackingType by remember {
        mutableStateOf(
            when (state.editorTrackingType) {
                TrackingType.DURATION -> TrackingType.DURATION
                else -> TrackingType.COUNT
            }
        )
    }

    var previousAdvancedVisible by remember { mutableStateOf(state.editorShowAdvanced) }
    var previousReminderEnabled by remember { mutableStateOf(state.editorReminderEnabled) }
    var previousEndDateEnabled by remember { mutableStateOf(state.editorEndDate != null) }

    suspend fun scrollToBottomSmooth(animationSpec: androidx.compose.animation.core.AnimationSpec<Float>) {
        val target = editorScrollState.maxValue
        if (target > editorScrollState.value) {
            editorScrollState.animateScrollTo(target, animationSpec = animationSpec)
        }
    }

    suspend fun clampScrollAfterCollapse(
        animationSpec: androidx.compose.animation.core.AnimationSpec<Float> = HabitEditorMotion.Medium,
        repeats: Int = 2,
        settleDelayMillis: Long = 70L
    ) {
        repeat(repeats) {
            val clamped = editorScrollState.value.coerceAtMost(editorScrollState.maxValue)
            if (clamped != editorScrollState.value) {
                editorScrollState.animateScrollTo(clamped, animationSpec = animationSpec)
            }
            delay(settleDelayMillis)
        }
    }


    val trackingCards = listOf(
        Triple(TrackingType.YES_NO, t("tracking_type_do_it"), t("tracking_type_do_it_sub")),
        Triple(TrackingType.COUNT, t("tracking_type_count"), t("tracking_type_count_sub")),
        Triple(TrackingType.DURATION, t("tracking_type_time_it"), t("tracking_type_time_it_sub"))
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

    LaunchedEffect(state.editorFrequency) {
        if (frequencyDescriptionTarget == state.editorFrequency) return@LaunchedEffect
        showFrequencyDescription = false
        delay(130)
        frequencyDescriptionTarget = state.editorFrequency
        showFrequencyDescription = true
    }

    LaunchedEffect(state.editorFrequency, state.editorTimesPerWeek) {
        if (state.editorFrequency == TaskFrequency.TIMES_PER_WEEK && state.editorTimesPerWeek > 6) {
            vm.setEditorTimesPerWeek(6)
        }
    }
    LaunchedEffect(state.editorTrackingType) {
        if (state.editorTrackingType != TrackingType.YES_NO) {
            lastTargetTrackingType = state.editorTrackingType
        }
        if (state.editorTrackingType == TrackingType.DURATION && durationInput.isBlank()) {
            durationInput = state.editorDailyTarget.coerceIn(1, 600).toString()
        }
    }

    LaunchedEffect(state.editorShowAdvanced) {
        if (state.editorShowAdvanced && !previousAdvancedVisible) {
            delay(320)
            scrollToBottomSmooth(HabitEditorMotion.Standard)
        } else if (!state.editorShowAdvanced && previousAdvancedVisible) {
            delay(300)
            clampScrollAfterCollapse(
                animationSpec = HabitEditorMotion.Standard,
                repeats = 1,
                settleDelayMillis = 0L
            )
        }
        previousAdvancedVisible = state.editorShowAdvanced
    }

    LaunchedEffect(state.editorReminderEnabled) {
        if (state.editorReminderEnabled && !previousReminderEnabled) {
            delay(260)
            scrollToBottomSmooth(HabitEditorMotion.Medium)
        } else if (!state.editorReminderEnabled && previousReminderEnabled) {
            // Single-phase hide: no manual post-scroll to avoid two-step feel.
        }
        previousReminderEnabled = state.editorReminderEnabled
    }

    LaunchedEffect(state.editorEndDate != null) {
        val endDateEnabled = state.editorEndDate != null
        if (endDateEnabled && !previousEndDateEnabled) {
            delay(260)
            scrollToBottomSmooth(HabitEditorMotion.Medium)
        } else if (!endDateEnabled && previousEndDateEnabled) {
            // Single-phase hide: no manual post-scroll to avoid two-step feel.
        }
        previousEndDateEnabled = endDateEnabled
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
                        AnimatedVisibility(
                            visible = wasSaveAttempted && (state.editorTitle.isBlank() || isDurationTargetInvalid),
                            enter = fadeIn(HabitEditorMotion.ErrorEnterFloat) + expandVertically(HabitEditorMotion.ErrorEnterSize),
                            exit = fadeOut(HabitEditorMotion.ErrorExitFloat) + shrinkVertically(HabitEditorMotion.ErrorExitSize)
                        ) {
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
                                containerColor = saveBtnBg,
                                contentColor = saveBtnText
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
                    .verticalScroll(editorScrollState),
                verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
            ) {
                FormSection(title = t("Habit name")) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.x0_5)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    stroke.thin,
                                    if (isTitleInvalid) colorScheme.error else colorScheme.outlineVariant,
                                    RoundedCornerShape(radius.md)
                                )
                                .heightIn(min = 52.dp)
                                .padding(horizontal = spacing.x1, vertical = spacing.x1),
                            horizontalArrangement = Arrangement.spacedBy(spacing.x1),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                onClick = { showEmojiPicker = true },
                                shape = RoundedCornerShape(8.dp),
                                color = colorScheme.surfaceVariant,
                                border = BorderStroke(stroke.thin, colorScheme.outlineVariant),
                                modifier = Modifier.size(38.dp)
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

                        AnimatedVisibility(
                            visible = isTitleInvalid,
                            enter = fadeIn(HabitEditorMotion.ErrorEnterFloat) + expandVertically(HabitEditorMotion.ErrorEnterSize),
                            exit = fadeOut(HabitEditorMotion.ErrorExitFloat) + shrinkVertically(HabitEditorMotion.ErrorExitSize)
                        ) {
                            Text(
                                text = t("This field is required."),
                                color = colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                FormSection(title = t("Tracking type")) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 1.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        trackingCards.forEach { (type, title, description) ->
                            val selected = state.editorTrackingType == type
                            val icon = when (type) {
                                TrackingType.YES_NO -> Icons.Default.CheckCircle
                                TrackingType.COUNT -> Icons.Default.Tag
                                TrackingType.DURATION -> Icons.Default.AccessTime
                            }

                            TrackingTypeCard(
                                icon = icon,
                                title = title,
                                subtitle = description,
                                selected = selected,
                                onClick = { vm.setEditorTrackingType(type) }
                            )
                        }
                    }
                }

                Column {
                val showTargetSection =
                    state.editorTrackingType == TrackingType.COUNT || state.editorTrackingType == TrackingType.DURATION
                val targetTrackingType =
                    if (showTargetSection) state.editorTrackingType else lastTargetTrackingType
                AnimatedVisibility(
                    visible = showTargetSection,
                    enter = HabitEditorMotion.enterStandard,
                    exit = HabitEditorMotion.exitStandard
                ) {
                    AnimatedContent(
                        targetState = targetTrackingType,
                        transitionSpec = {
                            fadeIn(animationSpec = HabitEditorMotion.Medium) togetherWith
                                fadeOut(animationSpec = HabitEditorMotion.Quick)
                        },
                        contentAlignment = Alignment.TopStart,
                        label = "trackingTypeTargetSwap"
                    ) { activeTrackingType ->
                        if (activeTrackingType == TrackingType.COUNT) {
                            FormSection(title = t("Count target")) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    color = colorScheme.surface,
                                    border = BorderStroke(1.dp, colorScheme.outline)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 13.dp, vertical = 13.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text(
                                            text = t("Daily target").uppercase(locale),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = colorScheme.onSurfaceVariant
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = t("editor_how_many_per_day"),
                                                fontSize = 13.sp,
                                                color = colorScheme.onSurfaceVariant
                                            )
                                            EditorInlineStepper(
                                                value = state.editorDailyTarget.coerceAtLeast(1),
                                                min = 1,
                                                canIncrease = true,
                                                onDecrease = {
                                                    val current = state.editorDailyTarget.coerceAtLeast(1)
                                                    val next = if (current > 99) 99 else (current - 1).coerceAtLeast(1)
                                                    vm.setEditorDailyTarget(next)
                                                },
                                                onIncrease = {
                                                    val current = state.editorDailyTarget.coerceAtLeast(1)
                                                    if (current >= 99) {
                                                        countNumpadInput = current.toString()
                                                        showCountNumpad = true
                                                    } else {
                                                        vm.setEditorDailyTarget(current + 1)
                                                    }
                                                }
                                            )
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
                        }

                        if (activeTrackingType == TrackingType.DURATION) {
                            FormSection(title = t("Duration target")) {
                                OutlinedTextField(
                                    value = durationInput,
                                    onValueChange = { raw ->
                                        val digits = raw.filter { it.isDigit() }.take(3)
                                        if (digits.isEmpty()) {
                                            durationInput = ""
                                        } else {
                                            val parsed = digits.toIntOrNull() ?: 1
                                            val clamped = parsed.coerceIn(1, 600)
                                            durationInput = clamped.toString()
                                            vm.setEditorDailyTarget(clamped)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text(t("Daily minute goal")) },
                                    isError = wasSaveAttempted && isDurationTargetInvalid,
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    supportingText = {
                                        if (wasSaveAttempted && isDurationTargetInvalid) {
                                            Text(
                                                text = t("This field is required."),
                                                color = colorScheme.error
                                            )
                                        } else {
                                            Text(
                                                text = "1-600",
                                                color = colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                    val targetSectionGap by animateDpAsState(
                        targetValue = if (showTargetSection) spacing.x1_5 else 0.dp,
                        animationSpec = HabitEditorMotion.StandardDp,
                        label = "targetSectionGap"
                    )
                    Spacer(modifier = Modifier.height(targetSectionGap))
                FormSection(title = t("label_frequency")) {
                    Column(
                        modifier = Modifier,
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 1.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FrequencyOptionCard(
                                modifier = Modifier.weight(1f),
                                title = t("freq_daily"),
                                selected = state.editorFrequency == TaskFrequency.DAILY,
                                onClick = { vm.setEditorFrequency(TaskFrequency.DAILY) }
                            )
                            FrequencyOptionCard(
                                modifier = Modifier.weight(1f),
                                title = t("freq_selected_days"),
                                selected = state.editorFrequency == TaskFrequency.SELECTED_DAYS,
                                onClick = {
                                    if (state.editorFrequency != TaskFrequency.SELECTED_DAYS) {
                                        vm.setEditorCustomDays(setOf(1, 2, 3, 4, 5))
                                    }
                                    vm.setEditorFrequency(TaskFrequency.SELECTED_DAYS)
                                }
                            )
                            FrequencyOptionCard(
                                modifier = Modifier.weight(1f),
                                title = t("freq_times_per_week"),
                                selected = state.editorFrequency == TaskFrequency.TIMES_PER_WEEK,
                                onClick = {
                                    vm.setEditorFrequency(TaskFrequency.TIMES_PER_WEEK)
                                    if (state.editorTimesPerWeek > 6) vm.setEditorTimesPerWeek(6)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        AnimatedVisibility(
                            visible = showFrequencyDescription,
                            enter = HabitEditorMotion.enterDelayed,
                            exit = HabitEditorMotion.exitFast
                        ) {
                            key(frequencyDescriptionTarget) {
                                val descriptionText = when (frequencyDescriptionTarget) {
                                    TaskFrequency.DAILY -> t("freq_daily_desc")
                                    TaskFrequency.SELECTED_DAYS -> t("freq_selected_days_desc")
                                    TaskFrequency.TIMES_PER_WEEK -> t("freq_times_per_week_desc")
                                }
                                FrequencyDescriptionCard(text = descriptionText)
                            }
                        }

                        AnimatedVisibility(
                            visible = state.editorFrequency == TaskFrequency.SELECTED_DAYS,
                            enter = HabitEditorMotion.enterStandard,
                            exit = HabitEditorMotion.exitStandard
                        ) {
                            Column(
                                modifier = Modifier.padding(top = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    shortWeekdays.forEachIndexed { index, label ->
                                        val day = index + 1
                                        val selected = day in state.editorCustomDays
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .requiredHeight(34.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { vm.toggleEditorCustomDay(day) },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (selected) colorScheme.primary.copy(alpha = 0.12f) else colorScheme.surface,
                                            border = BorderStroke(
                                                width = if (selected) 1.5.dp else 1.dp,
                                                color = if (selected) colorScheme.primary else colorScheme.outline
                                            )
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = label,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = if (selected) colorScheme.primary else colorScheme.onSurfaceVariant
                                                )
                                            }
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
                            enter = HabitEditorMotion.enterStandard,
                            exit = HabitEditorMotion.exitStandard
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = t("editor_times_per_week"),
                                    fontSize = 13.sp,
                                    color = colorScheme.onSurfaceVariant
                                )
                                EditorInlineStepper(
                                    value = state.editorTimesPerWeek.coerceIn(1, 6),
                                    min = 1,
                                    canIncrease = state.editorTimesPerWeek < 6,
                                    onDecrease = {
                                        vm.setEditorTimesPerWeek((state.editorTimesPerWeek - 1).coerceIn(1, 6))
                                    },
                                    onIncrease = {
                                        vm.setEditorTimesPerWeek((state.editorTimesPerWeek + 1).coerceIn(1, 6))
                                    }
                                )
                            }
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
                        imageVector = if (state.editorShowAdvanced) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
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
                    enter = HabitEditorMotion.enterStandard,
                    exit = HabitEditorMotion.exitStandard
                ) {
                    Column {
                        SettingsSwitchRow(
                            title = t("End date"),
                            subtitle = t("Optional challenge finish date"),
                            checked = state.editorEndDate != null,
                            onCheckedChange = { enabled ->
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
                                else {
                                    vm.setEditorEndDateEnabled(false)
                                }
                            }
                        )

                        AnimatedVisibility(
                            visible = state.editorEndDate != null,
                            enter = HabitEditorMotion.enterMedium,
                            exit = HabitEditorMotion.exitMedium
                        ) {
                            Column {
                                Spacer(modifier = Modifier.height(spacing.x1_5))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = t("label_finish_on"),
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
                        }

                        Spacer(modifier = Modifier.height(spacing.x1_5))

                        SettingsSwitchRow(
                            title = t("label_reminder"),
                            subtitle = t("Enable habit reminder notifications"),
                            checked = state.editorReminderEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled) {
                                    showThemedTimePicker(
                                        context = context,
                                        themeResId = pickerTheme,
                                        initialHour = state.editorReminderHour,
                                        initialMinute = state.editorReminderMinute,
                                        is24HourView = is24HourView,
                                        actionColorArgb = pickerActionColor,
                                        onTimeSet = { hour, minute ->
                                            vm.setEditorReminder(hour, minute)
                                            vm.setEditorReminderEnabled(true)
                                        }
                                    )
                                } else {
                                    vm.setEditorReminderEnabled(false)
                                }
                            }
                        )

                        AnimatedVisibility(
                            visible = state.editorReminderEnabled,
                            enter = HabitEditorMotion.enterMedium,
                            exit = HabitEditorMotion.exitMedium
                        ) {
                            Column {
                                Spacer(modifier = Modifier.height(spacing.x1_5))
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
                }

                Spacer(modifier = Modifier.height(spacing.x2))
            }
        }
    }
}

@Composable
private fun TrackingTypeCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val cardBorderColor by animateColorAsState(
        targetValue = if (selected) colorScheme.primary else colorScheme.outlineVariant,
        animationSpec = HabitEditorMotion.QuickColor,
        label = "trackingCardBorder_$title"
    )
    val cardBgColor by animateColorAsState(
        targetValue = if (selected) colorScheme.primary.copy(alpha = 0.12f) else colorScheme.surface,
        animationSpec = HabitEditorMotion.QuickColor,
        label = "trackingCardBg_$title"
    )
    val iconBgColor by animateColorAsState(
        targetValue = if (selected) colorScheme.primary.copy(alpha = 0.2f) else colorScheme.surfaceVariant,
        animationSpec = HabitEditorMotion.QuickColor,
        label = "trackingIconBg_$title"
    )
    val iconTint by animateColorAsState(
        targetValue = if (selected) colorScheme.primary else colorScheme.onSurfaceVariant,
        animationSpec = HabitEditorMotion.QuickColor,
        label = "trackingIconTint_$title"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = cardBgColor,
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = cardBorderColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 13.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun FrequencyOptionCard(
    modifier: Modifier = Modifier,
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val freqCardBorder by animateColorAsState(
        targetValue = if (selected) colorScheme.primary else colorScheme.outlineVariant,
        animationSpec = HabitEditorMotion.QuickColor,
        label = "freqCardBorder_$title"
    )
    val freqCardBg by animateColorAsState(
        targetValue = if (selected) colorScheme.primary.copy(alpha = 0.12f) else colorScheme.surface,
        animationSpec = HabitEditorMotion.QuickColor,
        label = "freqCardBg_$title"
    )
    val freqTitleColor by animateColorAsState(
        targetValue = if (selected) colorScheme.primary else colorScheme.onSurfaceVariant,
        animationSpec = HabitEditorMotion.QuickColor,
        label = "freqTitleColor_$title"
    )

    Surface(
        modifier = modifier
            .requiredHeight(60.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = freqCardBg,
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = freqCardBorder
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                maxLines = 2,
                lineHeight = 17.sp,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = freqTitleColor
            )
        }
    }
}

@Composable
private fun FrequencyDescriptionCard(text: String) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = colorScheme.primary.copy(alpha = 0.07f),
        border = BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.18f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 11.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(colorScheme.primary)
            )
            Text(
                text = text,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EditorInlineStepper(
    value: Int,
    min: Int,
    canIncrease: Boolean,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            StepperAction(
                symbol = "-",
                enabled = value > min,
                onClick = onDecrease
            )
            Text(
                text = value.toString(),
                modifier = Modifier.widthIn(min = 32.dp),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = colorScheme.onSurface
            )
            StepperAction(
                symbol = "+",
                enabled = canIncrease,
                onClick = onIncrease
            )
        }
    }
}

@Composable
private fun StepperAction(
    symbol: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            fontSize = 18.sp,
            color = colorScheme.primary.copy(alpha = if (enabled) 1f else 0.3f)
        )
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
        color = colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = text,
                fontSize = 13.sp,
                color = colorScheme.onSurface
            )
            Icon(
                imageVector = Icons.Default.EditCalendar,
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}









































