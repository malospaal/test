package com.example.microhabit.ui.tracker

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.microhabit.ActionEmphasis
import com.example.microhabit.HABIT_EASING
import com.example.microhabit.HABIT_TRANSITION_MS
import com.example.microhabit.HabitUiState
import com.example.microhabit.MainViewModel
import com.example.microhabit.StreakMilestoneQueue
import com.example.microhabit.StreakOverlayModel
import com.example.microhabit.data.AppLanguage
import com.example.microhabit.i18n.t
import com.example.microhabit.i18n.translate
import com.example.microhabit.i18n.formatTranslate
import com.example.microhabit.ui.onboarding.OnboardingCard
import com.example.microhabit.ui.shared.HabitSelectorRow
import com.example.microhabit.ui.shared.StreakRewardOverlay
import com.example.microhabit.ui.shared.isStreakMilestone
import com.example.microhabit.ui.theme.AppTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.coroutines.delay
@Composable
internal fun TrackerPage(
    state: HabitUiState,
    vm: MainViewModel,
    onOpenCreateHabit: () -> Unit,
    onOpenDetails: () -> Unit,
    highlightCompletionButton: Boolean,
    onHighlightConsumed: () -> Unit,
    scrollToTopSignal: Int
) {
    val spacing = AppTheme.spacing
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val selectorListState = rememberLazyListState()
    var previousTotalCompletions by remember(state.selectedTaskId) { mutableStateOf(state.totalCompletions) }
    var streakOverlay by remember { mutableStateOf<StreakOverlayModel?>(null) }
    var overlayVisible by remember { mutableStateOf(false) }
    var habitSwitchDirection by remember { mutableStateOf(0) }

    LaunchedEffect(state.selectedTaskId) {
        previousTotalCompletions = state.totalCompletions
        streakOverlay = null
        overlayVisible = false
        if (habitSwitchDirection != 0) {
            delay(16L)
            habitSwitchDirection = 0
        }
    }
    LaunchedEffect(state.totalCompletions, state.selectedDateDone, state.streak) {
        if (state.selectedDateDone && state.totalCompletions > previousTotalCompletions) {
            val model = StreakOverlayModel(
                streak = state.streak,
                milestone = isStreakMilestone(state.streak)
            )
            streakOverlay = model
            overlayVisible = true
            if (model.milestone) {
                state.selectedTaskId?.let { taskId ->
                    StreakMilestoneQueue.enqueueIfEligible(context, taskId, state.streak)
                }
            }
        }
        previousTotalCompletions = state.totalCompletions
    }
    LaunchedEffect(streakOverlay, overlayVisible) {
        if (overlayVisible && streakOverlay != null) {
            delay(1200)
            overlayVisible = false
        }
    }
    LaunchedEffect(highlightCompletionButton) {
        if (highlightCompletionButton) {
            delay(8000)
            onHighlightConsumed()
        }
    }
    LaunchedEffect(scrollToTopSignal) {
        if (scrollToTopSignal > 0) {
            listState.animateScrollToItem(0)
        }
    }
    val selectedHabitIndex = state.tasks
        .indexOfFirst { it.id == state.selectedTaskId }
        .let { if (it < 0) 0 else it }
    LaunchedEffect(selectedHabitIndex, state.tasks.size) {
        if (state.tasks.isEmpty()) return@LaunchedEffect
        val targetIndex = selectedHabitIndex + 1 // + tile is first item in tracker selector.
        val layoutInfo = selectorListState.layoutInfo
        val visibleItem = layoutInfo.visibleItemsInfo.firstOrNull { item -> item.index == targetIndex }
        val isFullyVisible = visibleItem != null &&
            visibleItem.offset >= layoutInfo.viewportStartOffset &&
            visibleItem.offset + visibleItem.size <= layoutInfo.viewportEndOffset
        if (!isFullyVisible) {
            selectorListState.animateScrollToItem(
                index = targetIndex,
                scrollOffset = -40
            )
        }
    }

    fun switchToTask(taskId: String, direction: Int = 0) {
        if (taskId == state.selectedTaskId) return
        habitSwitchDirection = direction
        vm.selectTask(taskId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(start = spacing.x2, top = spacing.x1, end = spacing.x2, bottom = spacing.x2),
            verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
        ) {
            if (state.tasks.isEmpty()) {
                item {
                    HabitSelectorRow(
                        habits = emptyList(),
                        selectedId = null,
                        onHabitSelected = {},
                        onCreateHabit = onOpenCreateHabit,
                        listState = selectorListState
                    )
                }
                item { OnboardingCard(vm) }
            } else {
                item {
                    HabitSelectorRow(
                        habits = state.tasks,
                        selectedId = state.selectedTaskId,
                        onHabitSelected = { taskId -> switchToTask(taskId, 0) },
                        onCreateHabit = onOpenCreateHabit,
                        listState = selectorListState
                    )
                }
                item {
                    fun switchHabitBy(delta: Int) {
                        if (state.tasks.size <= 1) return
                        val currentIndex = state.tasks
                            .indexOfFirst { it.id == state.selectedTaskId }
                            .let { if (it < 0) 0 else it }
                        val nextIndex = (currentIndex + delta + state.tasks.size) % state.tasks.size
                        val nextId = state.tasks[nextIndex].id
                        if (nextId != state.selectedTaskId) {
                            switchToTask(nextId, if (delta > 0) 1 else -1)
                        }
                    }
                    val currentHabitIndex = state.tasks
                        .indexOfFirst { it.id == state.selectedTaskId }
                        .let { if (it < 0) 0 else it }
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1_5)) {
                        AnimatedContent(
                            targetState = state.selectedTaskId,
                            transitionSpec = {
                                if (habitSwitchDirection != 0) {
                                    // +1 = next habit (left swipe), -1 = previous habit (right swipe).
                                    val sign = if (habitSwitchDirection > 0) 1 else -1
                                    (
                                        slideInHorizontally(
                                            animationSpec = tween(HABIT_TRANSITION_MS, easing = HABIT_EASING)
                                        ) { fullWidth -> sign * fullWidth / 3 } +
                                            fadeIn(
                                                animationSpec = tween(HABIT_TRANSITION_MS, easing = HABIT_EASING)
                                            )
                                        ) togetherWith (
                                        slideOutHorizontally(
                                            animationSpec = tween(HABIT_TRANSITION_MS, easing = HABIT_EASING)
                                        ) { fullWidth -> -sign * fullWidth / 3 } +
                                            fadeOut(
                                                animationSpec = tween(HABIT_TRANSITION_MS, easing = HABIT_EASING)
                                            )
                                        )
                                } else {
                                    fadeIn(
                                        animationSpec = tween(HABIT_TRANSITION_MS, easing = HABIT_EASING)
                                    ) togetherWith fadeOut(
                                        animationSpec = tween(HABIT_TRANSITION_MS, easing = HABIT_EASING)
                                    )
                                }
                            },
                            label = "trackerHeroSwitch"
                        ) { taskId ->
                            val animatedTask = state.tasks.firstOrNull { it.id == taskId }
                            HeroCard(
                                task = animatedTask,
                                selectedDate = state.selectedDate,
                                done = state.selectedDateDone,
                                scheduled = state.selectedDateScheduled,
                                nextScheduledDate = state.selectedDateNextScheduled,
                                selectedValue = state.selectedDateValue,
                                selectedTarget = state.selectedDateTarget,
                                selectedUnit = state.selectedDateUnit,
                                streak = state.streak,
                                bestStreak = state.bestStreak,
                                weeklyRingProgress = state.weeklyRingProgress,
                                weeklyRingCompleted = state.weeklyRingCompleted,
                                weeklyRingScheduled = state.weeklyRingScheduled,
                                last7Days = state.last7Days,
                                last7DaysScheduled = state.last7DaysScheduled,
                                last7DaysManualOverride = state.last7DaysManualOverride,
                                onDone = vm::toggleSelectedDateDone,
                                onMarkAnyway = vm::markSelectedDateAnyway,
                                onSetValue = vm::setSelectedDateValue,
                                onIncrementValue = vm::incrementSelectedDateValue,
                                onNavigateToDetail = onOpenDetails,
                                highlightMarkButton = highlightCompletionButton,
                                onHighlightConsumed = onHighlightConsumed,
                                appThemeMode = state.themeMode,
                                swipeEnabled = state.tasks.size > 1,
                                onSwipeNext = { switchHabitBy(1) },
                                onSwipePrevious = { switchHabitBy(-1) }
                            )
                        }
                        HabitPageDots(
                            total = state.tasks.size,
                            current = currentHabitIndex
                        )
                        Crossfade(
                            targetState = state.selectedTaskId,
                            animationSpec = tween(
                                durationMillis = HABIT_TRANSITION_MS,
                                easing = HABIT_EASING
                            ),
                            label = "trackerCalendarCrossfade"
                        ) { taskId ->
                            val animatedTask = state.tasks.firstOrNull { it.id == taskId }
                            CalendarCard(
                                month = state.currentMonth,
                                selectedDate = state.selectedDate,
                                selectedTask = animatedTask,
                                doneDates = state.doneDatesInCurrentMonth,
                                partialDates = state.partialDatesInCurrentMonth,
                                scheduledDates = state.scheduledDatesInCurrentMonth,
                                onMoveMonth = vm::moveMonth,
                                onToday = vm::jumpToToday,
                                onDateSelect = vm::selectDate
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = overlayVisible && streakOverlay != null,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = spacing.x1_5),
            enter = fadeIn(tween(180)) +
                slideInVertically(initialOffsetY = { -it / 2 }, animationSpec = tween(180, easing = FastOutSlowInEasing)) +
                scaleIn(initialScale = 0.92f, animationSpec = tween(180, easing = FastOutSlowInEasing)),
            exit = fadeOut(tween(180)) +
                slideOutVertically(targetOffsetY = { -it / 3 }, animationSpec = tween(180, easing = FastOutSlowInEasing)) +
                scaleOut(targetScale = 0.96f, animationSpec = tween(180, easing = FastOutSlowInEasing))
        ) {
            streakOverlay?.let { model ->
                StreakRewardOverlay(model = model)
            }
        }
    }

}

@Composable
internal fun TaskControlsRow(
    canAddTask: Boolean,
    onCreate: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    canEditDelete: Boolean
) {
    val spacing = AppTheme.spacing
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.x1)
    ) {
        TaskControlButton(
            modifier = Modifier.weight(1f),
            label = if (canAddTask) t("New") else t("Premium"),
            icon = if (canAddTask) Icons.Rounded.AddCircle else Icons.Rounded.WorkspacePremium,
            onClick = onCreate,
            emphasis = ActionEmphasis.PRIMARY
        )
        TaskControlButton(
            modifier = Modifier.weight(1f),
            label = t("Edit"),
            icon = Icons.Rounded.Edit,
            onClick = onEdit,
            enabled = canEditDelete,
            emphasis = ActionEmphasis.SECONDARY
        )
        TaskControlButton(
            modifier = Modifier.weight(1f),
            label = t("Delete"),
            icon = Icons.Rounded.Delete,
            onClick = onDelete,
            enabled = canEditDelete,
            emphasis = ActionEmphasis.DANGER
        )
    }
}

@Composable
private fun TaskControlButton(
    modifier: Modifier = Modifier,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = true,
    emphasis: ActionEmphasis = ActionEmphasis.SECONDARY
) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val semantic = AppTheme.colors
    val container = when {
        !enabled -> semantic.backgroundSurfaceMuted
        emphasis == ActionEmphasis.PRIMARY -> semantic.primary
        emphasis == ActionEmphasis.SECONDARY -> semantic.primaryMuted.copy(alpha = 0.9f)
        emphasis == ActionEmphasis.DANGER -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.78f)
        else -> semantic.backgroundSurfaceMuted
    }
    val content = when {
        !enabled -> semantic.textTertiary
        emphasis == ActionEmphasis.PRIMARY -> MaterialTheme.colorScheme.onPrimary
        emphasis == ActionEmphasis.SECONDARY -> semantic.primary
        emphasis == ActionEmphasis.DANGER -> MaterialTheme.colorScheme.onErrorContainer
        else -> semantic.textSecondary
    }
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(spacing.x6),
        shape = RoundedCornerShape(radius.md),
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = content,
            disabledContainerColor = semantic.backgroundSurfaceMuted,
            disabledContentColor = semantic.textTertiary
        ),
        contentPadding = PaddingValues(horizontal = spacing.x1, vertical = spacing.x0)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.x0_5)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(spacing.x2))
            Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
internal fun formatHeroDate(date: LocalDate, locale: Locale): String {
    val today = LocalDate.now()
    val formattedDate = date.format(
        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
    )
    return when (date) {
        today -> "${t("Today")}, $formattedDate"
        today.minusDays(1) -> "${t("Yesterday")}, $formattedDate"
        else -> {
            val dayLabel = date.dayOfWeek
                .getDisplayName(TextStyle.SHORT, locale)
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
            "$dayLabel, $formattedDate"
        }
    }
}

internal fun activeHabitsCountLabel(count: Int, language: AppLanguage): String {
    if (count <= 0) return translate(language, "No habits")
    return when (language) {
        AppLanguage.RU, AppLanguage.UK -> {
            val mod10 = count % 10
            val mod100 = count % 100
            val key = when {
                mod10 == 1 && mod100 != 11 -> "%d habit"
                mod10 in 2..4 && mod100 !in 12..14 -> "%d habits few"
                else -> "%d habits"
            }
            formatTranslate(language, key, count)
        }
        AppLanguage.CS -> {
            val key = when (count) {
                1 -> "%d habit"
                2, 3, 4 -> "%d habits few"
                else -> "%d habits"
            }
            formatTranslate(language, key, count)
        }
        else -> {
            val key = if (count == 1) "%d habit" else "%d habits"
            formatTranslate(language, key, count)
        }
    }
}





