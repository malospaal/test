package com.example.microhabit.ui.tracker

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.compose.animation.*
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.example.microhabit.DurationSheetMode
import com.example.microhabit.R
import com.example.microhabit.TimerUiState
import com.example.microhabit.data.AppThemeMode
import com.example.microhabit.data.HabitTask
import com.example.microhabit.data.TrackingType
import com.example.microhabit.i18n.appLocale
import com.example.microhabit.i18n.t
import com.example.microhabit.i18n.tf
import com.example.microhabit.ui.shared.GlassCard
import com.example.microhabit.ui.theme.AppTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HeroCard(
    task: HabitTask?,
    selectedDate: LocalDate,
    done: Boolean,
    scheduled: Boolean,
    nextScheduledDate: LocalDate?,
    selectedValue: Int,
    selectedTarget: Int,
    selectedUnit: String,
    streak: Int,
    bestStreak: Int,
    weeklyRingProgress: Float,
    weeklyRingCompleted: Int,
    weeklyRingScheduled: Int,
    last7Days: List<Int>,
    last7DaysScheduled: List<Boolean>,
    last7DaysManualOverride: List<Boolean>,
    onDone: () -> Unit,
    onMarkAnyway: () -> Unit,
    onSetValue: (Int) -> Unit,
    onIncrementValue: (Int) -> Unit,
    onNavigateToDetail: () -> Unit,
    highlightMarkButton: Boolean,
    onHighlightConsumed: () -> Unit,
    appThemeMode: AppThemeMode,
    swipeEnabled: Boolean = false,
    onSwipeNext: () -> Unit = {},
    onSwipePrevious: () -> Unit = {}
) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val stroke = AppTheme.stroke
    val semantic = AppTheme.colors
    val locale = appLocale()
    val trackingType = task?.trackingType ?: TrackingType.YES_NO
    val isValueTracking = trackingType != TrackingType.YES_NO
    val isDurationTracking = trackingType == TrackingType.DURATION
    val isCountTracking = trackingType == TrackingType.COUNT
    val canMarkForSelectedDate = scheduled
    val highlightActive = highlightMarkButton && !done && canMarkForSelectedDate
    val pressScaleTarget = 0.985f
    val pressScaleDuration = 80
    val density = LocalDensity.current
    val systemDark = isSystemInDarkTheme()
    val useDarkCompletedLottie = when (appThemeMode) {
        AppThemeMode.SYSTEM -> systemDark
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
    }
    val useDarkPalette = useDarkCompletedLottie
    val swipeThresholdPx = with(density) { 56.dp.toPx() }
    var horizontalDragDistance by remember(task?.id, selectedDate) { mutableStateOf(0f) }
    val completedButtonLottieResId = remember { R.raw.completed_button_lottie }
    val completedButtonComposition by if (completedButtonLottieResId != 0) {
        rememberLottieComposition(LottieCompositionSpec.RawRes(completedButtonLottieResId))
    } else {
        remember { mutableStateOf(null) }
    }
    val completedCheckmarkFilter = remember(useDarkCompletedLottie, semantic.lottieCheckmarkTint) {
        if (!useDarkCompletedLottie) {
            null
        } else {
            PorterDuffColorFilter(semantic.lottieCheckmarkTint.toArgb(), PorterDuff.Mode.SRC_ATOP)
        }
    }
    val completedButtonDynamicProperties = if (completedCheckmarkFilter != null) {
        rememberLottieDynamicProperties(
            rememberLottieDynamicProperty(
                property = LottieProperty.COLOR_FILTER,
                value = completedCheckmarkFilter,
                keyPath = arrayOf("line1", "Group 4", "Stroke 1")
            ),
            rememberLottieDynamicProperty(
                property = LottieProperty.COLOR_FILTER,
                value = completedCheckmarkFilter,
                keyPath = arrayOf("line2", "Group 3", "Stroke 1")
            )
        )
    } else {
        null
    }
    val completedTargetFrame = 22f
    val completedTargetProgress = remember(completedButtonComposition) {
        val durationFrames = completedButtonComposition?.durationFrames ?: 1f
        (completedTargetFrame / durationFrames.coerceAtLeast(1f)).coerceIn(0f, 1f)
    }
    val completionPulseScale = remember(task?.id, selectedDate) { Animatable(1f) }
    var completedAnimationPlaying by remember(task?.id, selectedDate) { mutableStateOf(false) }
    var completedLottieReady by remember(task?.id, selectedDate) { mutableStateOf(done) }
    var previousDoneState by remember(task?.id, selectedDate) { mutableStateOf(done) }
    val completedAnimationProgress by animateLottieCompositionAsState(
        composition = completedButtonComposition,
        iterations = 1,
        isPlaying = completedAnimationPlaying,
        speed = 1f
    )
    val completedButtonLottieProgress = when {
        done && completedAnimationPlaying -> completedAnimationProgress
        done && completedLottieReady -> completedTargetProgress
        else -> 0f
    }
    LaunchedEffect(done, task?.id, selectedDate) {
        if (done && !previousDoneState) {
            completedAnimationPlaying = false
            completedLottieReady = false
            completionPulseScale.snapTo(1f)
            completionPulseScale.animateTo(
                targetValue = 1.018f,
                animationSpec = tween(durationMillis = 95, easing = FastOutSlowInEasing)
            )
            delay(40)
            completedAnimationPlaying = true
            completionPulseScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 130, easing = FastOutSlowInEasing)
            )
        }
        if (!done) {
            completedAnimationPlaying = false
            completionPulseScale.snapTo(1f)
            completedLottieReady = false
        }
        previousDoneState = done
    }
    LaunchedEffect(completedAnimationPlaying, completedAnimationProgress, completedTargetProgress) {
        if (completedAnimationPlaying && completedAnimationProgress >= completedTargetProgress) {
            completedAnimationPlaying = false
            completedLottieReady = true
        }
    }
    val highlightPulseTransition = rememberInfiniteTransition(label = "heroHighlightPulse")
    val highlightPulse by highlightPulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.012f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heroHighlightPulseScale"
    )
    val durationSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var durationSheetMode by remember(task?.id, selectedDate) { mutableStateOf<DurationSheetMode?>(null) }
    var manualMinutesInput by remember(task?.id, selectedDate) { mutableStateOf(selectedValue.coerceAtLeast(0).toString()) }
    var timerUiState by remember(task?.id, selectedDate) { mutableStateOf(TimerUiState.IDLE) }
    var timerElapsedSeconds by remember(task?.id, selectedDate) { mutableStateOf(0) }
    var pendingTimerAddMinutes by remember(task?.id, selectedDate) { mutableStateOf<Int?>(null) }
    var showValueNumpad by rememberSaveable(task?.id, selectedDate) { mutableStateOf(false) }
    var valueNumpadInput by rememberSaveable(task?.id, selectedDate) { mutableStateOf("") }
    val unitLabel = when {
        trackingType == TrackingType.DURATION -> t("min")
        trackingType == TrackingType.COUNT && selectedUnit.isNotBlank() -> selectedUnit
        trackingType == TrackingType.COUNT -> t("times")
        else -> ""
    }
    val timerLabel = remember(timerElapsedSeconds) {
        val totalSeconds = timerElapsedSeconds.coerceAtLeast(0)
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        String.format("%02d:%02d", minutes, seconds)
    }
    val addMinutesQuestionTemplate = t("Add %d minutes?")
    val progressLabel = when (trackingType) {
        TrackingType.YES_NO -> ""
        TrackingType.COUNT -> "$selectedValue / $selectedTarget $unitLabel"
        TrackingType.DURATION -> "$selectedValue / $selectedTarget ${t("min")}"
    }
    val rawProgress = (selectedValue.toFloat() / selectedTarget.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "heroLinearProgress"
    )
    val displayPercent = (rawProgress * 100f).roundToInt()
    val remaining = selectedTarget - selectedValue
    val statusUnit = when (trackingType) {
        TrackingType.DURATION -> t("min")
        TrackingType.COUNT -> selectedUnit.ifBlank { t("times") }
        TrackingType.YES_NO -> ""
    }
    val goalStatusText = when {
        done && remaining >= 0 -> t("Goal reached! 🎉")
        remaining < 0 -> tf("+%d %s beyond goal", -remaining, statusUnit)
        else -> tf("%d %s to go", remaining, statusUnit)
    }
    val goalStatusColor = when {
        done -> MaterialTheme.colorScheme.primary
        remaining < 0 -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
    }
    val ringProgress = weeklyRingProgress.coerceIn(0f, 1f)
    val ringPercent = (ringProgress * 100f).roundToInt()
    val ringArcColor = when {
        weeklyRingScheduled > 0 && weeklyRingCompleted >= weeklyRingScheduled -> semantic.success
        trackingType == TrackingType.YES_NO -> semantic.primary
        else -> semantic.primary
    }
    val ringTrackColor = MaterialTheme.colorScheme.surfaceVariant
    val ringCenterLabel = "${ringPercent}%"
    val streakMetaText = when {
        !canMarkForSelectedDate -> {
            val nextLabel = nextScheduledDate?.format(
                DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
            )
            if (nextLabel != null) {
                "😴 ${t("Rest day")} · ${tf("Next scheduled date: %s", nextLabel)}"
            } else {
                "😴 ${t("Rest day")}"
            }
        }
        streak <= 0 -> t("Start today 🌱")
        else -> tf("🔥 %dd streak · ⭐ %dd best · %d%% week", streak, bestStreak, ringPercent)
    }
    LaunchedEffect(durationSheetMode, timerUiState, task?.id, selectedDate) {
        while (durationSheetMode == DurationSheetMode.TIMER && timerUiState == TimerUiState.RUNNING) {
            delay(1000)
            timerElapsedSeconds += 1
        }
    }
    LaunchedEffect(showValueNumpad) {
        if (showValueNumpad) valueNumpadInput = ""
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(16.dp),
        color = semantic.backgroundSurface,
        border = BorderStroke(stroke.thin, semantic.borderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .then(
                    if (!swipeEnabled) {
                        Modifier
                    } else {
                        Modifier.pointerInput(task?.id, selectedDate, swipeEnabled) {
                            detectHorizontalDragGestures(
                                onHorizontalDrag = { change, dragAmount ->
                                    change.consume()
                                    horizontalDragDistance += dragAmount
                                },
                                onDragEnd = {
                                    when {
                                        horizontalDragDistance <= -swipeThresholdPx -> onSwipeNext()
                                        horizontalDragDistance >= swipeThresholdPx -> onSwipePrevious()
                                    }
                                    horizontalDragDistance = 0f
                                },
                                onDragCancel = { horizontalDragDistance = 0f }
                            )
                        }
                    }
                ),
            verticalArrangement = Arrangement.spacedBy(spacing.x1)
        ) {
            Text(
                text = formatHeroDate(selectedDate, locale),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 12.sp,
                color = semantic.textSecondary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.x1)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(spacing.x1)
                ) {
                    Text(
                        text = "${task?.emoji?.ifBlank { "✨" } ?: "✨"}  ${task?.title ?: t("No active habit")}",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = semantic.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = streakMetaText,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 12.sp,
                        color = semantic.textSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                ProgressRing(
                    percent = ringProgress,
                    centerLabel = ringCenterLabel,
                    centerLabelColor = ringArcColor,
                    color = ringArcColor,
                    trackColor = ringTrackColor,
                    modifier = Modifier.offset(x = (-4).dp)
                )
            }
            Spacer(Modifier.height(spacing.x0_5))

            HeroMiniWeekRow(
                points = last7Days,
                scheduled = last7DaysScheduled,
                manualOverride = last7DaysManualOverride,
                trackingType = trackingType,
                anchorDate = LocalDate.now(),
                todayShortLabel = t("Today short")
            )

            if (!canMarkForSelectedDate) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(spacing.x1)
                ) {
                    Text(
                        text = t("This habit is not scheduled for this date."),
                        style = MaterialTheme.typography.bodySmall,
                        color = semantic.textSecondary
                    )
                    if (!isValueTracking && done) {
                        val interactionSource = remember { MutableInteractionSource() }
                        val pressed by interactionSource.collectIsPressedAsState()
                        val pressScale by animateFloatAsState(
                            targetValue = if (pressed) pressScaleTarget else 1f,
                            animationSpec = tween(durationMillis = pressScaleDuration, easing = FastOutSlowInEasing),
                            label = "restDayCompleteButtonPressScale"
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, bottom = 0.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val showCompletedLottie = completedButtonComposition != null &&
                                (completedAnimationPlaying || completedLottieReady)
                            val completedLottieSize = (spacing.x2 + spacing.x1) * 1.5f
                            val completedLottieSlotWidth by animateDpAsState(
                                targetValue = if (showCompletedLottie) completedLottieSize else 0.dp,
                                animationSpec = tween(durationMillis = 170, easing = FastOutSlowInEasing),
                                label = "restDayCompletedLottieSlotWidth"
                            )
                            Button(
                                onClick = onDone,
                                interactionSource = interactionSource,
                                modifier = Modifier
                                    .fillMaxWidth(0.94f)
                                    .height(56.dp)
                                    .graphicsLayer {
                                        scaleX = pressScale * completionPulseScale.value
                                        scaleY = pressScale * completionPulseScale.value
                                    },
                                shape = RoundedCornerShape(radius.full),
                                border = if (useDarkPalette) {
                                    BorderStroke(stroke.thin, semantic.borderSubtle)
                                } else {
                                    null
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (useDarkPalette) semantic.successMuted else semantic.success,
                                    contentColor = if (useDarkPalette) semantic.success else Color.White
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(1.5.dp)
                                ) {
                                    Text(
                                        text = t("Completed"),
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.W600)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .width(completedLottieSlotWidth)
                                            .height(completedLottieSize)
                                            .clip(RoundedCornerShape(AppTheme.radius.sm)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (completedButtonComposition != null && completedLottieSlotWidth > 0.dp) {
                                            LottieAnimation(
                                                composition = completedButtonComposition,
                                                progress = { completedButtonLottieProgress },
                                                dynamicProperties = completedButtonDynamicProperties,
                                                modifier = Modifier.size(completedLottieSize)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Button(
                            onClick = onMarkAnyway,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(radius.full)
                        ) {
                            Text(t("Mark anyway"))
                        }
                    }
                }
            } else if (!isValueTracking && done) {
                val interactionSource = remember { MutableInteractionSource() }
                val pressed by interactionSource.collectIsPressedAsState()
                val pressScale by animateFloatAsState(
                    targetValue = if (pressed) pressScaleTarget else 1f,
                    animationSpec = tween(durationMillis = pressScaleDuration, easing = FastOutSlowInEasing),
                    label = "completeButtonPressScale"
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 0.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val showCompletedLottie = completedButtonComposition != null &&
                        (completedAnimationPlaying || completedLottieReady)
                    val completedLottieSize = (spacing.x2 + spacing.x1) * 1.5f
                    val completedLottieSlotWidth by animateDpAsState(
                        targetValue = if (showCompletedLottie) completedLottieSize else 0.dp,
                        animationSpec = tween(durationMillis = 170, easing = FastOutSlowInEasing),
                        label = "completedLottieSlotWidth"
                    )
                    Button(
                        onClick = onDone,
                        interactionSource = interactionSource,
                        modifier = Modifier
                            .fillMaxWidth(0.94f)
                            .height(56.dp)
                            .graphicsLayer {
                                scaleX = pressScale * completionPulseScale.value
                                scaleY = pressScale * completionPulseScale.value
                            },
                        shape = RoundedCornerShape(radius.full),
                        border = if (useDarkPalette) {
                            BorderStroke(stroke.thin, semantic.borderSubtle)
                        } else {
                            null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (useDarkPalette) semantic.successMuted else semantic.success,
                            contentColor = if (useDarkPalette) semantic.success else Color.White
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(1.5.dp)
                        ) {
                            Text(
                                text = t("Completed"),
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.W600)
                            )
                            Box(
                                modifier = Modifier
                                    .width(completedLottieSlotWidth)
                                    .height(completedLottieSize)
                                    .clip(RoundedCornerShape(AppTheme.radius.sm)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (completedButtonComposition != null && completedLottieSlotWidth > 0.dp) {
                                    LottieAnimation(
                                        composition = completedButtonComposition,
                                        progress = { completedButtonLottieProgress },
                                        dynamicProperties = completedButtonDynamicProperties,
                                        modifier = Modifier.size(completedLottieSize)
                                    )
                                }
                            }
                        }
                    }
                }
            } else if (!isValueTracking) {
                val interactionSource = remember { MutableInteractionSource() }
                val pressed by interactionSource.collectIsPressedAsState()
                val pressScale by animateFloatAsState(
                    targetValue = if (pressed) pressScaleTarget else 1f,
                    animationSpec = tween(durationMillis = pressScaleDuration, easing = FastOutSlowInEasing),
                    label = "markButtonPressScale"
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 0.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            if (highlightActive) onHighlightConsumed()
                            onDone()
                        },
                        enabled = canMarkForSelectedDate,
                        interactionSource = interactionSource,
                        modifier = Modifier
                            .fillMaxWidth(0.94f)
                            .height(56.dp)
                            .graphicsLayer {
                                val pulse = if (highlightActive && !pressed) highlightPulse else 1f
                                scaleX = pressScale * pulse * completionPulseScale.value
                                scaleY = pressScale * pulse * completionPulseScale.value
                            },
                        shape = RoundedCornerShape(radius.full),
                        border = BorderStroke(
                            stroke.thin * if (highlightActive) 2.4f else 1.5f,
                            when {
                                highlightActive -> semantic.success
                                useDarkPalette -> semantic.borderSubtle
                                else -> Color.Transparent
                            }
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (useDarkPalette) semantic.primaryMuted else semantic.primary,
                            contentColor = if (useDarkPalette) semantic.primary else Color.White,
                            disabledContainerColor = semantic.backgroundSurfaceMuted,
                            disabledContentColor = semantic.textSecondary
                        )
                    ) {
                        Text(
                            text = when {
                                !canMarkForSelectedDate -> t("Not scheduled for this date")
                                else -> t("Mark as done")
                            },
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            } else {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = spacing.x1, bottom = 2.dp),
                    contentPadding = PaddingValues(spacing.x1_5)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(spacing.x1),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                if (isDurationTracking) {
                                    AnimatedContent(
                                        targetState = selectedValue,
                                        transitionSpec = {
                                            (fadeIn(animationSpec = tween(170, easing = FastOutSlowInEasing)) +
                                                slideInVertically(
                                                    initialOffsetY = { it / 3 },
                                                    animationSpec = tween(170, easing = FastOutSlowInEasing)
                                                )) togetherWith
                                                (fadeOut(animationSpec = tween(120, easing = FastOutSlowInEasing)) +
                                                    slideOutVertically(
                                                        targetOffsetY = { -it / 4 },
                                                        animationSpec = tween(120, easing = FastOutSlowInEasing)
                                                    ))
                                        },
                                        label = "durationProgressValue"
                                    ) { animatedValue ->
                                        Text(
                                            text = "$animatedValue / $selectedTarget ${t("min")}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = semantic.textPrimary
                                        )
                                    }
                                } else {
                                    Text(
                                        text = progressLabel,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = semantic.textPrimary
                                    )
                                }
                            }
                            if (selectedValue > 0) {
                                EditValueButton(
                                    onClick = { showValueNumpad = true },
                                    textSize = if (isCountTracking) 10.sp else 9.sp
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = !showValueNumpad,
                            enter = expandVertically(animationSpec = tween(190, easing = FastOutSlowInEasing)) +
                                fadeIn(animationSpec = tween(170, easing = FastOutSlowInEasing)),
                            exit = shrinkVertically(animationSpec = tween(160, easing = FastOutSlowInEasing)) +
                                fadeOut(animationSpec = tween(130, easing = FastOutSlowInEasing))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(spacing.x1)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                ) {
                                    LinearProgressIndicator(
                                        progress = { animatedProgress },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = semantic.primary,
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Text(
                                        text = "$displayPercent%",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = if (isCountTracking || isDurationTracking) 12.sp else 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.widthIn(min = 32.dp),
                                        textAlign = TextAlign.End
                                    )
                                }
                                Text(
                                    text = goalStatusText,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = if (isCountTracking || isDurationTracking) 12.sp else 11.sp,
                                    color = goalStatusColor,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                )

                                if (isCountTracking) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(spacing.x1),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedButton(
                                            onClick = { onIncrementValue(-1) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(radius.full)
                                        ) {
                                            Text("−", fontSize = 18.sp)
                                        }
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(48.dp),
                                            shape = RoundedCornerShape(radius.md),
                                            color = semantic.backgroundSurfaceMuted
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = selectedValue.toString(),
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontWeight = FontWeight.Bold,
                                                    color = semantic.textPrimary
                                                )
                                            }
                                        }
                                        OutlinedButton(
                                            onClick = { onIncrementValue(1) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(radius.full)
                                        ) {
                                            Text("+", fontSize = 18.sp)
                                        }
                                    }
                                }

                                if (isDurationTracking) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                                    ) {
                                        listOf(5, 10, 20).forEach { delta ->
                                            OutlinedButton(
                                                onClick = { onIncrementValue(delta) },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(radius.full)
                                            ) {
                                                Text("+$delta")
                                            }
                                        }
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                showValueNumpad = true
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(radius.md)
                                        ) {
                                            Text(t("Enter manually"))
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                timerElapsedSeconds = 0
                                                timerUiState = TimerUiState.IDLE
                                                durationSheetMode = DurationSheetMode.TIMER
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(radius.md)
                                        ) {
                                            Text(t("Timer"))
                                        }
                                    }
                                }
                            }
                        }

                        AnimatedVisibility(
                            visible = showValueNumpad,
                            enter = expandVertically(animationSpec = tween(190, easing = FastOutSlowInEasing)) +
                                fadeIn(animationSpec = tween(170, easing = FastOutSlowInEasing)),
                            exit = shrinkVertically(animationSpec = tween(160, easing = FastOutSlowInEasing)) +
                                fadeOut(animationSpec = tween(130, easing = FastOutSlowInEasing))
                        ) {
                            ValueNumpad(
                                input = valueNumpadInput,
                                unitLabel = unitLabel,
                                onInputChange = { updated -> valueNumpadInput = updated },
                                onBackspace = {
                                    if (valueNumpadInput.isNotEmpty()) {
                                        valueNumpadInput = valueNumpadInput.dropLast(1)
                                    }
                                },
                                onSave = {
                                    val newValue = valueNumpadInput.toIntOrNull() ?: return@ValueNumpad
                                    onSetValue(newValue.coerceAtLeast(0))
                                    showValueNumpad = false
                                },
                                onDismiss = { showValueNumpad = false }
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                HeroDetailsButton(onClick = onNavigateToDetail)
            }
        }
    }

    if (durationSheetMode == DurationSheetMode.MANUAL && isDurationTracking) {
        val manualParsed = manualMinutesInput.toIntOrNull()
        val manualValid = manualParsed != null && manualParsed >= 0
        ModalBottomSheet(
            onDismissRequest = { durationSheetMode = null },
            sheetState = durationSheetState,
            containerColor = semantic.backgroundSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.x2, vertical = spacing.x1_5)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
            ) {
                Text(
                    text = t("Enter minutes manually"),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = semantic.textPrimary
                )
                OutlinedTextField(
                    value = manualMinutesInput,
                    onValueChange = { value ->
                        manualMinutesInput = value.filter { it.isDigit() }.take(4)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text(t("Manual minutes")) },
                    placeholder = { Text("20") }
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                ) {
                    OutlinedButton(
                        onClick = { durationSheetMode = null },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(radius.md)
                    ) {
                        Text(t("Cancel"))
                    }
                    Button(
                        onClick = {
                            val value = manualParsed ?: return@Button
                            onSetValue(value.coerceAtLeast(0))
                            durationSheetMode = null
                        },
                        enabled = manualValid,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(radius.md)
                    ) {
                        Text(t("Save changes"))
                    }
                }
            }
        }
    }

    if (durationSheetMode == DurationSheetMode.TIMER && isDurationTracking) {
        ModalBottomSheet(
            onDismissRequest = { durationSheetMode = null },
            sheetState = durationSheetState,
            containerColor = semantic.backgroundSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.x2, vertical = spacing.x1_5)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
            ) {
                Text(
                    text = t("Timer"),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = semantic.textPrimary
                )
                Text(
                    text = timerLabel,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = semantic.textPrimary
                )

                when (timerUiState) {
                    TimerUiState.IDLE -> {
                        Button(
                            onClick = { timerUiState = TimerUiState.RUNNING },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(radius.md)
                        ) {
                            Text(t("Start"))
                        }
                    }
                    TimerUiState.RUNNING -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                        ) {
                            OutlinedButton(
                                onClick = { timerUiState = TimerUiState.PAUSED },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(radius.md)
                            ) {
                                Text(t("Pause"))
                            }
                            OutlinedButton(
                                onClick = {
                                    timerUiState = TimerUiState.PAUSED
                                    if (timerElapsedSeconds > 0) {
                                        val minutesToAdd = ceil(timerElapsedSeconds / 60.0).toInt().coerceAtLeast(1)
                                        pendingTimerAddMinutes = minutesToAdd
                                    } else {
                                        durationSheetMode = null
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(radius.md)
                            ) {
                                Text(t("Stop"))
                            }
                        }
                    }
                    TimerUiState.PAUSED -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                        ) {
                            OutlinedButton(
                                onClick = { timerUiState = TimerUiState.RUNNING },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(radius.md)
                            ) {
                                Text(t("Resume"))
                            }
                            OutlinedButton(
                                onClick = {
                                    if (timerElapsedSeconds > 0) {
                                        val minutesToAdd = ceil(timerElapsedSeconds / 60.0).toInt().coerceAtLeast(1)
                                        pendingTimerAddMinutes = minutesToAdd
                                    } else {
                                        durationSheetMode = null
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(radius.md)
                            ) {
                                Text(t("Stop"))
                            }
                        }
                    }
                }
            }
        }
    }

    pendingTimerAddMinutes?.let { minutes ->
        AlertDialog(
            onDismissRequest = { pendingTimerAddMinutes = null },
            title = { Text(t("Timer")) },
            text = { Text(tf(addMinutesQuestionTemplate, minutes)) },
            confirmButton = {
                Button(
                    onClick = {
                        onIncrementValue(minutes)
                        pendingTimerAddMinutes = null
                        timerUiState = TimerUiState.IDLE
                        timerElapsedSeconds = 0
                        durationSheetMode = null
                    }
                ) {
                    Text(t("Add"))
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingTimerAddMinutes = null }) {
                    Text(t("Cancel"))
                }
            }
        )
    }
}

