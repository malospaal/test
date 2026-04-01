package com.example.microhabit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.microhabit.data.AppLanguage
import com.example.microhabit.data.AppThemeMode
import com.example.microhabit.data.AnalyticsWeekSummary
import com.example.microhabit.data.HabitCategory
import com.example.microhabit.data.HabitLifecycleState
import com.example.microhabit.data.MAX_HABIT_TITLE_LENGTH
import com.example.microhabit.data.HabitRepository
import com.example.microhabit.data.HabitTask
import com.example.microhabit.data.HabitTemplate
import com.example.microhabit.data.HabitTemplateCatalog
import com.example.microhabit.data.ProAccessSource
import com.example.microhabit.data.PremiumPlan
import com.example.microhabit.data.SubscriptionState
import com.example.microhabit.data.SubscriptionPlan
import com.example.microhabit.data.TaskFrequency
import com.example.microhabit.data.TrackingType
import com.example.microhabit.data.hasPremiumAccess
import com.example.microhabit.i18n.localeForLanguage
import com.example.microhabit.i18n.translate
import com.example.microhabit.notifications.HabitReminderScheduler
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import kotlin.math.roundToInt

data class HabitListItem(
    val id: String,
    val emoji: String,
    val name: String,
    val colorHex: String,
    val trackingType: TrackingType,
    val streak: Int,
    val frequency: String,
    val completionRate: Int,
    val reminderEnabled: Boolean,
    val reminderHour: Int,
    val reminderMinute: Int,
    val isCompleted: Boolean,
    val isArchived: Boolean,
    val displayOrder: Int
)

data class CalendarFilterOption(
    val taskId: String,
    val title: String,
    val emoji: String
)

enum class CalendarBreakdownStatus {
    COMPLETED,
    PARTIAL,
    MISSED,
    NOT_SCHEDULED,
    TODAY_PENDING,
    FUTURE
}

data class CalendarBreakdownItem(
    val taskId: String,
    val title: String,
    val emoji: String,
    val trackingType: TrackingType,
    val scheduled: Boolean,
    val status: CalendarBreakdownStatus,
    val value: Int,
    val target: Int,
    val unitLabel: String
)

data class TaskEditorPrefill(
    val title: String,
    val emoji: String,
    val colorHex: String,
    val trackingType: TrackingType,
    val dailyTarget: Int,
    val unitLabel: String,
    val frequency: TaskFrequency,
    val customDays: Set<Int> = emptySet(),
    val timesPerWeek: Int = 3,
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate? = null,
    val reminderEnabled: Boolean = false,
    val reminderHour: Int = 8,
    val reminderMinute: Int = 0
)

data class HabitUiState(
    val tasks: List<HabitTask> = emptyList(),
    val allTasks: List<HabitTask> = emptyList(),
    val habits: List<HabitListItem> = emptyList(),
    val selectedTaskId: String? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val currentMonth: YearMonth = YearMonth.now(),
    val selectedDateDone: Boolean = false,
    val selectedDatePartial: Boolean = false,
    val selectedDateScheduled: Boolean = false,
    val selectedDateNextScheduled: LocalDate? = null,
    val selectedDateInFuture: Boolean = false,
    val selectedDateValue: Int = 0,
    val selectedDateTarget: Int = 1,
    val selectedDateUnit: String = "",
    val selectedDateCompletionPercent: Int = 0,
    val todayDone: Boolean = false,
    val todayScheduled: Boolean = false,
    val streakSaverCount: Int = 0,
    val showStreakSaverDialog: Boolean = false,
    val streakSaverMissedDate: LocalDate? = null,
    val streak: Int = 0,
    val bestStreak: Int = 0,
    val completionRate7Day: Int = 0,
    val completionRate30Day: Int = 0,
    val analyticsAggregateCompletionRate7Day: Int = 0,
    val analyticsAggregateCompletionRate30Day: Int = 0,
    val analyticsAggregateTotalCompletions: Int = 0,
    val analyticsAggregateCurrentStreak: Int = 0,
    val analyticsAggregateBestStreak: Int = 0,
    val analyticsAggregateWeekdayConsistency: List<Int> = List(7) { 0 },
    val analyticsSelectedWeekSummaries: List<AnalyticsWeekSummary> = emptyList(),
    val analyticsAggregateWeekSummaries: List<AnalyticsWeekSummary> = emptyList(),
    val analyticsSelectedHourlyCounts: List<Int> = List(12) { 0 },
    val analyticsAggregateHourlyCounts: List<Int> = List(12) { 0 },
    val weeklyRingProgress: Float = 0f,
    val weeklyRingCompleted: Int = 0,
    val weeklyRingScheduled: Int = 0,
    val totalCompletions: Int = 0,
    val totalTrackedValue: Int = 0,
    val averageTrackedValue: Int = 0,
    val streakHistory: List<Int> = emptyList(),
    val mostConsistentWeekday: Int? = null,
    val hardestWeekday: Int? = null,
    val completionConsistency: Int = 0,
    val selectedTaskNote: String = "",
    val progressPercent: Int = 0,
    val last7Days: List<Int> = List(7) { 0 },
    val last7DaysScheduled: List<Boolean> = List(7) { false },
    val last7DaysManualOverride: List<Boolean> = List(7) { false },
    val monthlyProgress: List<Int> = emptyList(),
    val weekdayConsistency: List<Int> = List(7) { 0 },
    val doneDatesInCurrentMonth: Set<LocalDate> = emptySet(),
    val partialDatesInCurrentMonth: Set<LocalDate> = emptySet(),
    val scheduledDatesInCurrentMonth: Set<LocalDate> = emptySet(),
    val calendarFilterTaskId: String? = null,
    val calendarFilterOptions: List<CalendarFilterOption> = emptyList(),
    val calendarCompletedCountByDate: Map<LocalDate, Int> = emptyMap(),
    val calendarScheduledCountByDate: Map<LocalDate, Int> = emptyMap(),
    val calendarManualOverrideCountByDate: Map<LocalDate, Int> = emptyMap(),
    val calendarBreakdownCompletedCount: Int = 0,
    val calendarBreakdownScheduledCount: Int = 0,
    val calendarBreakdownItems: List<CalendarBreakdownItem> = emptyList(),
    val showEditor: Boolean = false,
    val editingTaskId: String? = null,
    val editorTitle: String = "",
    val editorEmoji: String = "✨",
    val editorColorHex: String = "#1F6F64",
    val editorTrackingType: TrackingType = TrackingType.YES_NO,
    val editorDailyTarget: Int = 1,
    val editorUnitLabel: String = "",
    val editorFrequency: TaskFrequency = TaskFrequency.DAILY,
    val editorTimesPerWeek: Int = 3,
    val editorCustomDays: Set<Int> = setOf(1, 2, 3, 4, 5),
    val editorReminderHour: Int = 8,
    val editorReminderMinute: Int = 0,
    val editorReminderEnabled: Boolean = false,
    val editorStartDate: LocalDate = LocalDate.now(),
    val editorEndDate: LocalDate? = null,
    val editorShowAdvanced: Boolean = false,
    val plan: SubscriptionPlan = SubscriptionPlan.FREE,
    val proAccessSource: ProAccessSource = ProAccessSource.NONE,
    val subscriptionState: SubscriptionState = SubscriptionState.Free,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val language: AppLanguage = AppLanguage.RU,
    val minimumCompletionPercent: Int = 100,
    val durationTimerRunning: Boolean = false,
    val durationTimerElapsedSeconds: Int = 0,
    val onboardingCompleted: Boolean = false,
    val isLoaded: Boolean = false,
    val completedPromptTaskId: String? = null,
    val completedPromptTaskTitle: String = "",
    val completedPromptTaskEndDate: LocalDate? = null
)

private data class EditorSavePayload(
    val current: HabitUiState,
    val title: String,
    val dailyTarget: Int,
    val unitLabel: String,
    val frequency: TaskFrequency,
    val customDays: Set<Int>
)

private data class HeroRolling7Snapshot(
    val points: List<Int>,
    val scheduled: List<Boolean>,
    val manualOverride: List<Boolean>,
    val completedScheduled: Int,
    val scheduledCount: Int,
    val progress: Float
)

class MainViewModel(
    private val repository: HabitRepository,
    private val reminderScheduler: HabitReminderScheduler
) : ViewModel() {
    private val _state = MutableStateFlow(HabitUiState())
    val state: StateFlow<HabitUiState> = _state.asStateFlow()
    private val dismissedStreakSaverPromptDates = mutableMapOf<String, LocalDate>()
    private val saverRewardedCompletionDates = mutableSetOf<String>()
    private var durationTimerJob: Job? = null
    private var timerTaskId: String? = null
    private var timerDate: LocalDate? = null

    init {
        refresh()
    }

    fun selectTask(taskId: String) {
        viewModelScope.launch {
            _state.update { it.copy(selectedTaskId = taskId) }
            repository.setSelectedTask(taskId)
            refresh()
        }
    }

    fun selectDate(date: LocalDate) {
        _state.update { it.copy(selectedDate = date, currentMonth = YearMonth.from(date)) }
        refreshDerivedOnly()
    }

    fun moveMonth(delta: Long) {
        _state.update { it.copy(currentMonth = it.currentMonth.plusMonths(delta)) }
        refreshDerivedOnly()
    }

    fun jumpToToday() {
        _state.update {
            it.copy(
                selectedDate = LocalDate.now(),
                currentMonth = YearMonth.now()
            )
        }
        refreshDerivedOnly()
    }

    fun setCalendarFilterTask(taskId: String?) {
        _state.update { it.copy(calendarFilterTaskId = taskId) }
        refreshDerivedOnly()
    }

    fun setPlan(plan: SubscriptionPlan) {
        viewModelScope.launch {
            repository.setPlan(plan)
            refresh()
        }
    }

    fun setProAccessSource(source: ProAccessSource) {
        viewModelScope.launch {
            repository.setProAccessSource(source)
            refresh()
        }
    }

    fun cancelSubscription() {
        viewModelScope.launch {
            repository.cancelSubscription()
            refresh()
        }
    }

    fun renewSubscription() {
        viewModelScope.launch {
            repository.renewSubscription()
            refresh()
        }
    }

    fun changeSubscriptionPlan(targetPlan: PremiumPlan) {
        viewModelScope.launch {
            repository.changeSubscriptionPlan(targetPlan)
            refresh()
        }
    }

    fun debugForceFreePlan() {
        viewModelScope.launch {
            repository.debugForceFreePlan()
            refresh()
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            repository.setThemeMode(mode)
            _state.update { it.copy(themeMode = mode) }
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            repository.setLanguage(language)
            repository.refreshWidget()
            refresh()
        }
    }

    fun setOnboardingCompleted(completed: Boolean) {
        viewModelScope.launch {
            repository.setOnboardingCompleted(completed)
            _state.update { it.copy(onboardingCompleted = completed) }
        }
    }

    fun prepareOnboardingDraft(
        name: String,
        category: HabitCategory,
        template: HabitTemplate,
        trackingType: TrackingType,
        dailyTarget: Int,
        unitLabel: String,
        frequency: TaskFrequency,
        customDays: Set<Int>,
        reminderEnabled: Boolean,
        reminderHour: Int,
        reminderMinute: Int
    ) {
        val normalizedName = name.trim().take(MAX_HABIT_TITLE_LENGTH)
        val normalizedFrequency = if (frequency == TaskFrequency.SELECTED_DAYS) {
            TaskFrequency.SELECTED_DAYS
        } else {
            TaskFrequency.DAILY
        }
        val normalizedCustomDays = if (normalizedFrequency == TaskFrequency.SELECTED_DAYS) {
            customDays.filter { it in 1..7 }.toSet().ifEmpty { setOf(1) }
        } else {
            emptySet()
        }
        _state.update {
            it.copy(
                showEditor = false,
                editingTaskId = null,
                editorTitle = normalizedName,
                editorEmoji = template.emoji.ifBlank { "✨" }.take(2),
                editorColorHex = HabitTemplateCatalog.defaultColorHex(category),
                editorTrackingType = trackingType,
                editorDailyTarget = when (trackingType) {
                    TrackingType.YES_NO -> 1
                    TrackingType.COUNT -> dailyTarget.coerceAtLeast(1)
                    TrackingType.DURATION -> dailyTarget.coerceAtLeast(1)
                },
                editorUnitLabel = if (trackingType == TrackingType.COUNT) unitLabel.trim().take(20) else "",
                editorFrequency = normalizedFrequency,
                editorTimesPerWeek = 3,
                editorCustomDays = normalizedCustomDays,
                editorReminderEnabled = reminderEnabled,
                editorReminderHour = reminderHour.coerceIn(0, 23),
                editorReminderMinute = reminderMinute.coerceIn(0, 59),
                editorStartDate = LocalDate.now(),
                editorEndDate = null,
                editorShowAdvanced = false
            )
        }
    }

    fun exportData(): Result<String> = repository.exportData()

    fun resetProgress() {
        viewModelScope.launch {
            repository.resetProgress()
            repository.refreshWidget()
            refresh()
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            val existingIds = repository.getTasks().map { it.id }
            repository.deleteAccount()
            existingIds.forEach(reminderScheduler::cancelReminder)
            repository.refreshWidget()
            refresh()
        }
    }

    fun toggleSelectedDateDone() {
        toggleSelectedDateDoneInternal(allowNotScheduledOverride = false)
    }

    private fun toggleSelectedDateDoneInternal(allowNotScheduledOverride: Boolean) {
        val current = _state.value
        val task = current.tasks.firstOrNull { it.id == current.selectedTaskId } ?: return
        val selectedDate = current.selectedDate
        val shouldShiftStartDate =
            selectedDate.isBefore(task.startDate) && repository.isScheduledByFrequency(task, selectedDate)
        val isScheduled = repository.isScheduledOn(task, selectedDate)
        val hasManualOverride = !isScheduled && repository.getDayValue(task, selectedDate) > 0
        if (!isScheduled && !shouldShiftStartDate && !allowNotScheduledOverride && !hasManualOverride) return

        viewModelScope.launch {
            val previousStreak = repository.calculateStreak(task)
            if (shouldShiftStartDate) {
                repository.updateTaskStartDate(task.id, selectedDate)
            }
            val alreadyDone = if (!isScheduled && !shouldShiftStartDate) {
                repository.getDayValue(task, selectedDate) > 0
            } else {
                repository.isCompletedOn(task, selectedDate)
            }
            if (alreadyDone) {
                repository.setDayValue(task, selectedDate, 0)
            } else {
                repository.setDayValue(task, selectedDate, 1)
            }
            if (!alreadyDone && selectedDate == LocalDate.now() && (isScheduled || shouldShiftStartDate)) {
                maybeAwardStreakSaver(task.id, previousStreak)
            }
            repository.refreshWidget()
            refresh()
        }
    }

    fun completeSelectedHabitToday() {
        val taskId = _state.value.selectedTaskId ?: return
        viewModelScope.launch {
            val task = repository.getTasks()
                .firstOrNull { it.id == taskId && repository.isHabitActive(it) }
                ?: return@launch
            val today = LocalDate.now()
            if (!repository.isScheduledOn(task, today) && !(today.isBefore(task.startDate) && repository.isScheduledByFrequency(task, today))) {
                return@launch
            }
            if (!repository.isCompletedOn(task, today)) {
                val previousStreak = repository.calculateStreak(task)
                if (today.isBefore(task.startDate) && repository.isScheduledByFrequency(task, today)) {
                    repository.updateTaskStartDate(task.id, today)
                }
                repository.setDone(task.id, today, true)
                maybeAwardStreakSaver(task.id, previousStreak)
                repository.refreshWidget()
            }
            _state.update { it.copy(selectedDate = today, currentMonth = YearMonth.from(today)) }
            refresh()
        }
    }

    fun dismissStreakSaverDialog() {
        val current = _state.value
        val taskId = current.selectedTaskId ?: return
        val missedDate = current.streakSaverMissedDate ?: return
        dismissedStreakSaverPromptDates[taskId] = missedDate
        _state.update { it.copy(showStreakSaverDialog = false) }
    }

    fun useStreakSaverForYesterday() {
        val current = _state.value
        val task = current.tasks.firstOrNull { it.id == current.selectedTaskId } ?: return
        val missedDate = current.streakSaverMissedDate ?: return
        val yesterday = LocalDate.now().minusDays(1)
        if (missedDate != yesterday) return

        viewModelScope.launch {
            val consumed = repository.consumeStreakSaver(task.id, missedDate)
            if (!consumed) return@launch
            dismissedStreakSaverPromptDates.remove(task.id)
            refresh()
        }
    }

    fun openCreateTask(prefill: TaskEditorPrefill? = null) {
        val now = LocalDate.now()
        val reminderHour = prefill?.reminderHour ?: DEFAULT_REMINDER_HOUR
        val reminderMinute = prefill?.reminderMinute ?: DEFAULT_REMINDER_MINUTE
        val startDate = prefill?.startDate ?: now
        val endDate = prefill?.endDate?.takeIf { !it.isBefore(startDate) }
        val normalizedTrackingType = prefill?.trackingType ?: TrackingType.YES_NO
        val normalizedTarget = when (normalizedTrackingType) {
            TrackingType.YES_NO -> 1
            TrackingType.COUNT, TrackingType.DURATION -> (prefill?.dailyTarget ?: 1).coerceAtLeast(1)
        }
        val normalizedFrequency = prefill?.frequency ?: TaskFrequency.DAILY
        val normalizedCustomDays = if (normalizedFrequency == TaskFrequency.SELECTED_DAYS) {
            prefill?.customDays?.filter { it in 1..7 }?.toSet().orEmpty().ifEmpty { setOf(1, 2, 3, 4, 5) }
        } else {
            setOf(1, 2, 3, 4, 5)
        }
        val normalizedShowAdvanced = false
        _state.update {
            it.copy(
                showEditor = true,
                editingTaskId = null,
                editorTitle = prefill?.title?.trim()?.take(MAX_HABIT_TITLE_LENGTH).orEmpty(),
                editorEmoji = prefill?.emoji?.trim()?.ifBlank { "✨" }?.take(2) ?: "✨",
                editorColorHex = prefill?.colorHex ?: "#1F6F64",
                editorTrackingType = normalizedTrackingType,
                editorDailyTarget = normalizedTarget,
                editorUnitLabel = if (normalizedTrackingType == TrackingType.COUNT) {
                    prefill?.unitLabel?.trim()?.take(20).orEmpty()
                } else {
                    ""
                },
                editorFrequency = normalizedFrequency,
                editorTimesPerWeek = (prefill?.timesPerWeek ?: 3).coerceIn(1, 7),
                editorCustomDays = normalizedCustomDays,
                editorReminderHour = reminderHour,
                editorReminderMinute = reminderMinute,
                editorReminderEnabled = prefill?.reminderEnabled ?: false,
                editorStartDate = startDate,
                editorEndDate = endDate,
                editorShowAdvanced = normalizedShowAdvanced
            )
        }
    }

    fun openEditTask(taskId: String) {
        val task = _state.value.allTasks.firstOrNull { it.id == taskId } ?: return
        _state.update {
            it.copy(
                showEditor = true,
                editingTaskId = task.id,
                editorTitle = task.title.take(MAX_HABIT_TITLE_LENGTH),
                editorEmoji = task.emoji,
                editorColorHex = task.colorHex,
                editorTrackingType = task.trackingType,
                editorDailyTarget = task.dailyTarget.coerceAtLeast(1),
                editorUnitLabel = task.unitLabel,
                editorFrequency = task.frequency,
                editorTimesPerWeek = task.timesPerWeek,
                editorCustomDays = if (task.customDays.isEmpty()) setOf(1, 2, 3, 4, 5) else task.customDays
                    .filter { it in 1..7 }
                    .toSet(),
                editorReminderHour = task.reminderHour,
                editorReminderMinute = task.reminderMinute,
                editorReminderEnabled = task.reminderEnabled,
                editorStartDate = task.startDate,
                editorEndDate = task.endDate,
                editorShowAdvanced = false
            )
        }
    }

    fun closeEditor() {
        _state.update { it.copy(showEditor = false) }
    }

    fun setSelectedTaskNote(value: String) {
        _state.update { it.copy(selectedTaskNote = value.take(180)) }
    }

    fun saveSelectedTaskNote() {
        val selectedTaskId = _state.value.selectedTaskId ?: return
        val note = _state.value.selectedTaskNote
        viewModelScope.launch {
            repository.setTaskNote(selectedTaskId, note)
            _state.update { it.copy(selectedTaskNote = repository.getTaskNote(selectedTaskId)) }
        }
    }

    fun setEditorTitle(value: String) {
        _state.update { it.copy(editorTitle = value.take(MAX_HABIT_TITLE_LENGTH)) }
    }

    fun setEditorEmoji(value: String) {
        val normalized = value.trim().ifEmpty { "✨" }.take(2)
        _state.update { it.copy(editorEmoji = normalized) }
    }

    fun setEditorColorHex(value: String) {
        _state.update { it.copy(editorColorHex = value) }
    }

    fun setEditorTrackingType(value: TrackingType) {
        _state.update {
            val target = when (value) {
                TrackingType.YES_NO -> 1
                TrackingType.COUNT -> if (it.editorTrackingType == TrackingType.COUNT) {
                    it.editorDailyTarget.coerceAtLeast(1)
                } else {
                    8
                }
                TrackingType.DURATION -> if (it.editorTrackingType == TrackingType.DURATION) {
                    it.editorDailyTarget.coerceAtLeast(1)
                } else {
                    20
                }
            }
            it.copy(
                editorTrackingType = value,
                editorDailyTarget = target,
                editorUnitLabel = if (value == TrackingType.COUNT) it.editorUnitLabel else ""
            )
        }
    }

    fun setEditorDailyTarget(value: Int) {
        _state.update { it.copy(editorDailyTarget = value.coerceAtLeast(1)) }
    }

    fun setEditorUnitLabel(value: String) {
        _state.update { it.copy(editorUnitLabel = value.trimStart().take(20)) }
    }

    fun setEditorFrequency(value: TaskFrequency) {
        _state.update { it.copy(editorFrequency = value) }
    }

    fun setEditorTimesPerWeek(value: Int) {
        _state.update { it.copy(editorTimesPerWeek = value.coerceIn(1, 7)) }
    }

    fun toggleEditorCustomDay(day: Int) {
        if (day !in 1..7) return
        _state.update {
            val next = it.editorCustomDays.toMutableSet()
            if (!next.add(day)) next.remove(day)
            it.copy(editorCustomDays = next)
        }
    }

    fun setEditorReminder(hour: Int, minute: Int) {
        _state.update {
            it.copy(
                editorReminderHour = hour.coerceIn(0, 23),
                editorReminderMinute = minute.coerceIn(0, 59)
            )
        }
    }

    fun setEditorReminderEnabled(value: Boolean) {
        _state.update { it.copy(editorReminderEnabled = value) }
    }

    fun setMinimumCompletionPercent(value: Int) {
        viewModelScope.launch {
            repository.setMinimumCompletionPercent(value)
            refreshDerivedOnly()
        }
    }

    fun setSelectedDateValue(value: Int) {
        val current = _state.value
        val task = current.tasks.firstOrNull { it.id == current.selectedTaskId } ?: return
        val selectedDate = current.selectedDate
        val shouldShiftStartDate =
            selectedDate.isBefore(task.startDate) && repository.isScheduledByFrequency(task, selectedDate)
        if (!repository.isScheduledOn(task, selectedDate) && !shouldShiftStartDate) return
        viewModelScope.launch {
            val previousStreak = repository.calculateStreak(task)
            if (shouldShiftStartDate) {
                repository.updateTaskStartDate(task.id, selectedDate)
            }
            repository.setDayValue(task, selectedDate, value.coerceAtLeast(0))
            if (selectedDate == LocalDate.now()) {
                maybeAwardStreakSaver(task.id, previousStreak)
            }
            repository.refreshWidget()
            refresh()
        }
    }

    fun incrementSelectedDateValue(delta: Int) {
        val current = _state.value
        val task = current.tasks.firstOrNull { it.id == current.selectedTaskId } ?: return
        val selectedDate = current.selectedDate
        val shouldShiftStartDate =
            selectedDate.isBefore(task.startDate) && repository.isScheduledByFrequency(task, selectedDate)
        if (!repository.isScheduledOn(task, selectedDate) && !shouldShiftStartDate) return
        viewModelScope.launch {
            val previousStreak = repository.calculateStreak(task)
            if (shouldShiftStartDate) {
                repository.updateTaskStartDate(task.id, selectedDate)
            }
            repository.addToDayValue(task, selectedDate, delta)
            if (selectedDate == LocalDate.now() && delta > 0) {
                maybeAwardStreakSaver(task.id, previousStreak)
            }
            repository.refreshWidget()
            refresh()
        }
    }

    fun markSelectedDateAnyway() {
        toggleSelectedDateDoneInternal(allowNotScheduledOverride = true)
    }

    fun startDurationTimer(): Boolean {
        val current = _state.value
        val task = current.tasks.firstOrNull { it.id == current.selectedTaskId } ?: return false
        if (task.trackingType != TrackingType.DURATION) return false
        if (!current.plan.hasPremiumAccess()) return false
        if (durationTimerJob != null) return true
        timerTaskId = task.id
        timerDate = current.selectedDate
        _state.update { it.copy(durationTimerRunning = true, durationTimerElapsedSeconds = 0) }
        durationTimerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _state.update { state ->
                    state.copy(durationTimerElapsedSeconds = state.durationTimerElapsedSeconds + 1)
                }
            }
        }
        return true
    }

    fun stopDurationTimerAndApply(): Int {
        val current = _state.value
        val elapsed = current.durationTimerElapsedSeconds
        durationTimerJob?.cancel()
        durationTimerJob = null
        _state.update { it.copy(durationTimerRunning = false, durationTimerElapsedSeconds = 0) }
        val targetTaskId = timerTaskId ?: return 0
        val targetDate = timerDate ?: return 0
        timerTaskId = null
        timerDate = null
        if (elapsed <= 0) return 0
        val minutesToAdd = (elapsed / 60f).roundToInt().coerceAtLeast(1)
        viewModelScope.launch {
            val task = repository.getTasks().firstOrNull { it.id == targetTaskId } ?: return@launch
            val previousStreak = repository.calculateStreak(task)
            repository.addToDayValue(task, targetDate, minutesToAdd)
            if (targetDate == LocalDate.now()) {
                maybeAwardStreakSaver(task.id, previousStreak)
            }
            repository.refreshWidget()
            refresh()
        }
        return minutesToAdd
    }

    fun cancelDurationTimer() {
        durationTimerJob?.cancel()
        durationTimerJob = null
        timerTaskId = null
        timerDate = null
        _state.update { it.copy(durationTimerRunning = false, durationTimerElapsedSeconds = 0) }
    }

    fun setEditorStartDate(value: LocalDate) {
        _state.update {
            val adjustedEndDate = it.editorEndDate?.takeIf { end -> !end.isBefore(value) }
            it.copy(
                editorStartDate = value,
                editorEndDate = adjustedEndDate
            )
        }
    }

    fun setEditorEndDate(value: LocalDate) {
        _state.update {
            it.copy(editorEndDate = value.takeIf { date -> !date.isBefore(it.editorStartDate) })
        }
    }

    fun setEditorEndDateEnabled(enabled: Boolean) {
        _state.update {
            it.copy(
                editorEndDate = if (enabled) {
                    it.editorEndDate ?: maxOf(LocalDate.now().plusDays(30), it.editorStartDate)
                } else {
                    null
                }
            )
        }
    }

    fun setEditorShowAdvanced(value: Boolean) {
        _state.update { it.copy(editorShowAdvanced = value) }
    }

    fun saveEditor() {
        val payload = editorSavePayloadOrNull() ?: return
        viewModelScope.launch {
            persistEditor(payload)
        }
    }

    fun saveEditorWithNotificationsEnabled() {
        val payload = editorSavePayloadOrNull() ?: return
        viewModelScope.launch {
            persistEditor(payload)
        }
    }

    private fun editorSavePayloadOrNull(): EditorSavePayload? {
        val current = _state.value
        if (!canSaveEditor()) return null
        if (current.editingTaskId == null && !canCreateTask(current.tasks.size, current.plan)) return null

        val frequency = current.editorFrequency
        val customDays = if (frequency == TaskFrequency.SELECTED_DAYS) {
            current.editorCustomDays.ifEmpty { setOf(1) }
        } else {
            emptySet()
        }
        return EditorSavePayload(
            current = current,
            title = current.editorTitle.trim().take(MAX_HABIT_TITLE_LENGTH),
            dailyTarget = current.editorDailyTarget.coerceAtLeast(1),
            unitLabel = current.editorUnitLabel.trim().take(20),
            frequency = frequency,
            customDays = customDays
        )
    }

    private fun persistEditor(payload: EditorSavePayload) {
        val current = payload.current
        val affectedTaskId = if (current.editingTaskId == null) {
                val task = repository.createTask(
                    title = payload.title,
                    emoji = current.editorEmoji,
                    colorHex = current.editorColorHex,
                    trackingType = current.editorTrackingType,
                    dailyTarget = payload.dailyTarget,
                    unitLabel = payload.unitLabel,
                    frequency = payload.frequency,
                    customDays = payload.customDays,
                    timesPerWeek = current.editorTimesPerWeek,
                    reminderHour = current.editorReminderHour,
                    reminderMinute = current.editorReminderMinute,
                    reminderEnabled = current.editorReminderEnabled,
                    startDate = current.editorStartDate,
                    endDate = current.editorEndDate
                )
                repository.setSelectedTask(task.id)
                _state.update { it.copy(selectedTaskId = task.id) }
                task.id
            } else {
                repository.updateTask(
                    taskId = current.editingTaskId,
                    title = payload.title,
                    emoji = current.editorEmoji,
                    colorHex = current.editorColorHex,
                    trackingType = current.editorTrackingType,
                    dailyTarget = payload.dailyTarget,
                    unitLabel = payload.unitLabel,
                    frequency = payload.frequency,
                    customDays = payload.customDays,
                    timesPerWeek = current.editorTimesPerWeek,
                    reminderHour = current.editorReminderHour,
                    reminderMinute = current.editorReminderMinute,
                    reminderEnabled = current.editorReminderEnabled,
                    startDate = current.editorStartDate,
                    endDate = current.editorEndDate
                )
                current.editingTaskId
        }
        reminderScheduler.syncReminderForTask(affectedTaskId)
        repository.refreshWidget()
        refresh()
        _state.update { it.copy(showEditor = false) }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
            reminderScheduler.cancelReminder(taskId)
            repository.refreshWidget()
            refresh()
        }
    }

    fun archiveTask(taskId: String) {
        viewModelScope.launch {
            repository.archiveTask(taskId, archived = true)
            reminderScheduler.cancelReminder(taskId)
            repository.refreshWidget()
            refresh()
        }
    }

    fun unarchiveTask(taskId: String): Boolean {
        val current = _state.value
        val task = current.allTasks.firstOrNull { it.id == taskId } ?: return false
        val willBeActive = repository.lifecycleState(task.copy(isArchived = false)) == HabitLifecycleState.ACTIVE
        if (!current.plan.hasPremiumAccess() && willBeActive) {
            val activeCount = current.allTasks.count { repository.lifecycleState(it) == HabitLifecycleState.ACTIVE }
            if (activeCount >= FREE_ACTIVE_HABIT_LIMIT) {
                return false
            }
        }
        viewModelScope.launch {
            repository.archiveTask(taskId, archived = false)
            reminderScheduler.syncReminderForTask(taskId)
            repository.refreshWidget()
            refresh()
        }
        return true
    }

    fun reorderActiveHabits(orderedActiveIds: List<String>) {
        if (orderedActiveIds.isEmpty()) return
        viewModelScope.launch {
            repository.reorderActiveTasks(orderedActiveIds)
            refresh()
        }
    }

    fun dismissCompletedHabitDialog() {
        val current = _state.value
        val taskId = current.completedPromptTaskId ?: return
        val endDate = current.completedPromptTaskEndDate ?: return
        repository.markCompletedPromptShown(taskId, endDate)
        refresh()
    }

    fun continueCompletedHabitIndefinite() {
        val taskId = _state.value.completedPromptTaskId ?: return
        viewModelScope.launch {
            repository.updateTaskEndDate(taskId, null)
            reminderScheduler.syncReminderForTask(taskId)
            repository.refreshWidget()
            refresh()
        }
    }

    fun continueCompletedHabitWithEndDate(newEndDate: LocalDate) {
        val current = _state.value
        val taskId = current.completedPromptTaskId ?: return
        val task = current.allTasks.firstOrNull { it.id == taskId } ?: return
        if (newEndDate.isBefore(task.startDate) || newEndDate.isBefore(LocalDate.now())) return
        viewModelScope.launch {
            repository.updateTaskEndDate(taskId, newEndDate)
            reminderScheduler.syncReminderForTask(taskId)
            repository.refreshWidget()
            refresh()
        }
    }

    fun archiveCompletedHabitFromDialog() {
        val taskId = _state.value.completedPromptTaskId ?: return
        archiveTask(taskId)
    }

    fun deleteCompletedHabitFromDialog() {
        val taskId = _state.value.completedPromptTaskId ?: return
        deleteTask(taskId)
    }

    fun canCreateTask(): Boolean {
        val s = _state.value
        return canCreateTask(s.tasks.size, s.plan)
    }

    fun canSaveEditor(): Boolean {
        val s = _state.value
        if (s.editorTitle.trim().isEmpty()) return false
        if (s.editorTitle.trim().length > MAX_HABIT_TITLE_LENGTH) return false
        if (s.editorDailyTarget <= 0) return false
        if (s.editorEndDate != null && s.editorEndDate.isBefore(s.editorStartDate)) return false
        if (s.editorEmoji.trim().isEmpty()) return false
        if (s.editorColorHex.trim().isEmpty()) return false
        if (s.editorFrequency == TaskFrequency.SELECTED_DAYS && s.editorCustomDays.isEmpty()) return false
        if (s.editorFrequency == TaskFrequency.TIMES_PER_WEEK && s.editorTimesPerWeek !in 1..7) return false
        return true
    }

    fun onHostResumed() {
        refresh()
    }

    private fun refresh() {
        viewModelScope.launch {
            val allTasks = repository.getTasks()
            val tasks = allTasks.filter { repository.lifecycleState(it) == HabitLifecycleState.ACTIVE }
            val plan = repository.getPlan()
            val proAccessSource = repository.getProAccessSource()
            val subscriptionState = repository.getSubscriptionState()
            val themeMode = repository.getThemeMode()
            val language = repository.getLanguage()
            val minimumCompletionPercent = repository.getMinimumCompletionPercent()
            val onboardingCompleted = repository.isOnboardingCompleted()

            val selectedId = _state.value.selectedTaskId
                ?.takeIf { candidate -> tasks.any { it.id == candidate } }
                ?: tasks.firstOrNull()?.id
            if (repository.getSelectedTaskId() != selectedId) {
                repository.setSelectedTask(selectedId)
            }

            val selectedTask = tasks.firstOrNull { it.id == selectedId }
            val date = _state.value.selectedDate
            val currentMonth = _state.value.currentMonth
            val today = LocalDate.now()
            val metricsAnchorDate = today
            val yesterday = today.minusDays(1)
            val isFutureDate = date.isAfter(LocalDate.now())
            val calendarScopeTasks = allTasks.filter {
                repository.lifecycleState(it, today) != HabitLifecycleState.ARCHIVED
            }
            val calendarFilterOptions = calendarScopeTasks.map { task ->
                CalendarFilterOption(
                    taskId = task.id,
                    title = task.title,
                    emoji = task.emoji
                )
            }
            val resolvedCalendarFilterId = _state.value.calendarFilterTaskId
                ?.takeIf { candidate -> calendarFilterOptions.any { it.taskId == candidate } }
            val filteredCalendarTasks = resolvedCalendarFilterId?.let { filterId ->
                calendarScopeTasks.filter { it.id == filterId }
            } ?: calendarScopeTasks
            val (calendarCompletedCountByDate, calendarScheduledCountByDate) =
                buildCalendarMonthCounts(filteredCalendarTasks, currentMonth)
            val calendarManualOverrideCountByDate =
                buildCalendarMonthManualOverrideCounts(filteredCalendarTasks, currentMonth)
            val (
                calendarBreakdownCompletedCount,
                calendarBreakdownScheduledCount,
                calendarBreakdownItems
            ) = buildCalendarBreakdown(filteredCalendarTasks, date, today)
            val selectedDateValue = selectedTask?.let { repository.getDayValue(it, date) } ?: 0
            val selectedDateTarget = selectedTask?.let { repository.dailyTarget(it) } ?: 1
            val selectedDateUnit = selectedTask?.let { repository.unitLabel(it) }.orEmpty()
            val selectedDateCompletionPercent = selectedTask?.let { repository.completionPercent(it, date) } ?: 0
            val selectedDateScheduled = selectedTask?.let { task ->
                repository.isScheduledOn(task, date) ||
                    (date.isBefore(task.startDate) && repository.isScheduledByFrequency(task, date))
            } ?: false
            val selectedDateDone = selectedTask?.let { task ->
                if (selectedDateScheduled) {
                    repository.isCompletedOn(task, date)
                } else {
                    repository.getDayValue(task, date) > 0
                }
            } ?: false
            val selectedDatePartial = selectedTask?.let { task ->
                if (selectedDateScheduled) repository.isPartialOn(task, date) else false
            } ?: false
            val selectedDateNextScheduled = selectedTask?.let { task ->
                if (repository.isScheduledOn(task, date)) null else repository.nextScheduledDate(task, date)
            }
            val heroRolling7Snapshot = buildHeroRolling7Snapshot(selectedTask, metricsAnchorDate)
            val weekdayConsistency = selectedTask?.let { repository.weekdayConsistency(it, 84, metricsAnchorDate) } ?: List(7) { 0 }
            val selectedWeekSummaries = selectedTask?.let { task ->
                repository.getWeekSummaries(listOf(task), weeksBack = 3)
            } ?: emptyList()
            val aggregateWeekSummaries = repository.getWeekSummaries(tasks, weeksBack = 3)
            val selectedHourlyCounts = selectedTask?.let { task ->
                repository.getHourlyCompletionData(listOf(task), days = 30).toList()
            } ?: List(12) { 0 }
            val aggregateHourlyCounts = repository.getHourlyCompletionData(tasks, days = 30).toList()
            val aggregateCompletionRate7Day = (repository.aggregateCompletionRate(tasks, 7) * 100f)
                .roundToInt()
                .coerceIn(0, 100)
            val aggregateCompletionRate30Day = (repository.aggregateCompletionRate(tasks, 30) * 100f)
                .roundToInt()
                .coerceIn(0, 100)
            val aggregateWeekdayConsistency = repository.aggregateWeekdayConsistency(tasks, 84).toList()
            val aggregateTotalCompletions = tasks.sumOf { task -> repository.totalCompletions(task) }
            val aggregateCurrentStreak = tasks.maxOfOrNull { task -> repository.calculateStreak(task) } ?: 0
            val aggregateBestStreak = tasks.maxOfOrNull { task -> repository.bestStreak(task) } ?: 0
            val consistencyNonZero = weekdayConsistency
                .mapIndexed { index, value -> index to value }
                .filter { (_, value) -> value > 0 }
            val mostConsistentWeekday = consistencyNonZero.maxByOrNull { it.second }?.first?.plus(1)
            val hardestWeekday = consistencyNonZero.minByOrNull { it.second }?.first?.plus(1)
            val completionConsistency = if (consistencyNonZero.isEmpty()) {
                0
            } else {
                consistencyNonZero.sumOf { it.second } / consistencyNonZero.size
            }
            val streakSaverCount = selectedTask?.let { repository.getStreakSaverCount(it.id) } ?: 0
            val streakSaverMissedDate = selectedTask?.let { task ->
                if (
                    task.frequency != TaskFrequency.TIMES_PER_WEEK &&
                    repository.isScheduledOn(task, yesterday) &&
                    !repository.isDone(task.id, yesterday) &&
                    !repository.isMissedDaySaved(task.id, yesterday)
                ) {
                    yesterday
                } else {
                    null
                }
            }
            val showStreakSaverDialog = selectedTask?.let { task ->
                streakSaverMissedDate != null &&
                    streakSaverCount > 0 &&
                    dismissedStreakSaverPromptDates[task.id] != streakSaverMissedDate
            } ?: false
            val completedPromptTask = allTasks.firstOrNull { repository.shouldShowCompletedPrompt(it, today) }
            _state.update { state ->
                state.copy(
                    tasks = tasks,
                    allTasks = allTasks,
                    habits = allTasks
                        .map { task ->
                            HabitListItem(
                                id = task.id,
                                emoji = task.emoji,
                                name = task.title,
                                colorHex = task.colorHex,
                                trackingType = task.trackingType,
                                streak = repository.calculateStreak(task),
                                frequency = frequencyLabel(task, language),
                                completionRate = repository.progressForLast30Days(task),
                                reminderEnabled = task.reminderEnabled,
                                reminderHour = task.reminderHour,
                                reminderMinute = task.reminderMinute,
                                isCompleted = repository.lifecycleState(task) == HabitLifecycleState.COMPLETED,
                                isArchived = task.isArchived,
                                displayOrder = task.displayOrder
                            )
                        }
                        .sortedBy { it.displayOrder },
                    selectedTaskId = selectedId,
                    showEditor = state.showEditor,
                    selectedDateInFuture = isFutureDate,
                    selectedDateValue = selectedDateValue,
                    selectedDateTarget = selectedDateTarget,
                    selectedDateUnit = selectedDateUnit,
                    selectedDateCompletionPercent = selectedDateCompletionPercent,
                    todayDone = selectedTask?.let { repository.isDone(it.id, today) } ?: false,
                    todayScheduled = selectedTask?.let { task ->
                        repository.isScheduledOn(task, today) ||
                            (today.isBefore(task.startDate) && repository.isScheduledByFrequency(task, today))
                    } ?: false,
                    streakSaverCount = streakSaverCount,
                    showStreakSaverDialog = showStreakSaverDialog,
                    streakSaverMissedDate = streakSaverMissedDate,
                    selectedDateScheduled = selectedDateScheduled,
                    selectedDateNextScheduled = selectedDateNextScheduled,
                    selectedDateDone = selectedDateDone,
                    selectedDatePartial = selectedDatePartial,
                    streak = selectedTask?.let { repository.calculateStreak(it) } ?: 0,
                    bestStreak = selectedTask?.let { repository.bestStreak(it) } ?: 0,
                    streakHistory = selectedTask?.let { task ->
                        repository.streakHistory(task, limit = 4)
                    } ?: emptyList(),
                    mostConsistentWeekday = mostConsistentWeekday,
                    hardestWeekday = hardestWeekday,
                    completionConsistency = completionConsistency,
                    selectedTaskNote = selectedTask?.let { repository.getTaskNote(it.id) }.orEmpty(),
                    completionRate7Day = selectedTask?.let { repository.completionRate(it, 7, metricsAnchorDate) } ?: 0,
                    completionRate30Day = selectedTask?.let { repository.completionRate(it, 30, metricsAnchorDate) } ?: 0,
                    analyticsAggregateCompletionRate7Day = aggregateCompletionRate7Day,
                    analyticsAggregateCompletionRate30Day = aggregateCompletionRate30Day,
                    analyticsAggregateTotalCompletions = aggregateTotalCompletions,
                    analyticsAggregateCurrentStreak = aggregateCurrentStreak,
                    analyticsAggregateBestStreak = aggregateBestStreak,
                    analyticsAggregateWeekdayConsistency = aggregateWeekdayConsistency,
                    analyticsSelectedWeekSummaries = selectedWeekSummaries,
                    analyticsAggregateWeekSummaries = aggregateWeekSummaries,
                    analyticsSelectedHourlyCounts = selectedHourlyCounts,
                    analyticsAggregateHourlyCounts = aggregateHourlyCounts,
                    weeklyRingProgress = heroRolling7Snapshot.progress,
                    weeklyRingCompleted = heroRolling7Snapshot.completedScheduled,
                    weeklyRingScheduled = heroRolling7Snapshot.scheduledCount,
                    totalCompletions = selectedTask?.let { repository.totalCompletions(it) } ?: 0,
                    totalTrackedValue = selectedTask?.let { repository.totalTrackedValue(it) } ?: 0,
                    averageTrackedValue = selectedTask?.let { repository.averageTrackedValue(it, 30, metricsAnchorDate) } ?: 0,
                    progressPercent = selectedTask?.let { repository.progressForLast30Days(it, metricsAnchorDate) } ?: 0,
                    last7Days = heroRolling7Snapshot.points,
                    last7DaysScheduled = heroRolling7Snapshot.scheduled,
                    last7DaysManualOverride = heroRolling7Snapshot.manualOverride,
                    monthlyProgress = selectedTask?.let { repository.monthlyWeeklyProgress(it, state.currentMonth) } ?: emptyList(),
                    weekdayConsistency = weekdayConsistency,
                    doneDatesInCurrentMonth = selectedTask?.let { task ->
                        doneDatesForMonth(task, state.currentMonth)
                    } ?: emptySet(),
                    partialDatesInCurrentMonth = selectedTask?.let { task ->
                        partialDatesForMonth(task, state.currentMonth)
                    } ?: emptySet(),
                    scheduledDatesInCurrentMonth = selectedTask?.let { task ->
                        scheduledDatesForMonth(task, state.currentMonth)
                    } ?: emptySet(),
                    calendarFilterTaskId = resolvedCalendarFilterId,
                    calendarFilterOptions = calendarFilterOptions,
                    calendarCompletedCountByDate = calendarCompletedCountByDate,
                    calendarScheduledCountByDate = calendarScheduledCountByDate,
                    calendarManualOverrideCountByDate = calendarManualOverrideCountByDate,
                    calendarBreakdownCompletedCount = calendarBreakdownCompletedCount,
                    calendarBreakdownScheduledCount = calendarBreakdownScheduledCount,
                    calendarBreakdownItems = calendarBreakdownItems,
                    plan = plan,
                    proAccessSource = proAccessSource,
                    subscriptionState = subscriptionState,
                    themeMode = themeMode,
                    language = language,
                    minimumCompletionPercent = minimumCompletionPercent,
                    onboardingCompleted = onboardingCompleted,
                    isLoaded = true,
                    completedPromptTaskId = completedPromptTask?.id,
                    completedPromptTaskTitle = completedPromptTask?.title.orEmpty(),
                    completedPromptTaskEndDate = completedPromptTask?.endDate
                )
            }
        }
    }

    private fun refreshDerivedOnly() {
        val current = _state.value
        val selectedTask = current.tasks.firstOrNull { it.id == current.selectedTaskId }
        val date = current.selectedDate
        val month = current.currentMonth
        val today = LocalDate.now()
        val metricsAnchorDate = today
        val minimumCompletionPercent = repository.getMinimumCompletionPercent()
        val yesterday = today.minusDays(1)
        val isFutureDate = date.isAfter(LocalDate.now())
        val calendarScopeTasks = current.allTasks.filter {
            repository.lifecycleState(it, today) != HabitLifecycleState.ARCHIVED
        }
        val calendarFilterOptions = calendarScopeTasks.map { task ->
            CalendarFilterOption(
                taskId = task.id,
                title = task.title,
                emoji = task.emoji
            )
        }
        val resolvedCalendarFilterId = current.calendarFilterTaskId
            ?.takeIf { candidate -> calendarFilterOptions.any { it.taskId == candidate } }
        val filteredCalendarTasks = resolvedCalendarFilterId?.let { filterId ->
            calendarScopeTasks.filter { it.id == filterId }
        } ?: calendarScopeTasks
        val (calendarCompletedCountByDate, calendarScheduledCountByDate) =
            buildCalendarMonthCounts(filteredCalendarTasks, month)
        val calendarManualOverrideCountByDate =
            buildCalendarMonthManualOverrideCounts(filteredCalendarTasks, month)
        val (
            calendarBreakdownCompletedCount,
            calendarBreakdownScheduledCount,
            calendarBreakdownItems
        ) = buildCalendarBreakdown(filteredCalendarTasks, date, today)
        val selectedDateValue = selectedTask?.let { repository.getDayValue(it, date) } ?: 0
        val selectedDateTarget = selectedTask?.let { repository.dailyTarget(it) } ?: 1
        val selectedDateUnit = selectedTask?.let { repository.unitLabel(it) }.orEmpty()
        val selectedDateCompletionPercent = selectedTask?.let { repository.completionPercent(it, date) } ?: 0
        val selectedDateScheduled = selectedTask?.let { task ->
            repository.isScheduledOn(task, date) ||
                (date.isBefore(task.startDate) && repository.isScheduledByFrequency(task, date))
        } ?: false
        val selectedDateDone = selectedTask?.let { task ->
            if (selectedDateScheduled) {
                repository.isCompletedOn(task, date)
            } else {
                repository.getDayValue(task, date) > 0
            }
        } ?: false
        val selectedDatePartial = selectedTask?.let { task ->
            if (selectedDateScheduled) repository.isPartialOn(task, date) else false
        } ?: false
        val selectedDateNextScheduled = selectedTask?.let { task ->
            if (repository.isScheduledOn(task, date)) null else repository.nextScheduledDate(task, date)
        }
        val heroRolling7Snapshot = buildHeroRolling7Snapshot(selectedTask, metricsAnchorDate)
        val weekdayConsistency = selectedTask?.let { repository.weekdayConsistency(it, 84, metricsAnchorDate) } ?: List(7) { 0 }
        val selectedWeekSummaries = selectedTask?.let { task ->
            repository.getWeekSummaries(listOf(task), weeksBack = 3)
        } ?: emptyList()
        val aggregateWeekSummaries = repository.getWeekSummaries(current.tasks, weeksBack = 3)
        val selectedHourlyCounts = selectedTask?.let { task ->
            repository.getHourlyCompletionData(listOf(task), days = 30).toList()
        } ?: List(12) { 0 }
        val aggregateHourlyCounts = repository.getHourlyCompletionData(current.tasks, days = 30).toList()
        val aggregateCompletionRate7Day = (repository.aggregateCompletionRate(current.tasks, 7) * 100f)
            .roundToInt()
            .coerceIn(0, 100)
        val aggregateCompletionRate30Day = (repository.aggregateCompletionRate(current.tasks, 30) * 100f)
            .roundToInt()
            .coerceIn(0, 100)
        val aggregateWeekdayConsistency = repository.aggregateWeekdayConsistency(current.tasks, 84).toList()
        val aggregateTotalCompletions = current.tasks.sumOf { task -> repository.totalCompletions(task) }
        val aggregateCurrentStreak = current.tasks.maxOfOrNull { task -> repository.calculateStreak(task) } ?: 0
        val aggregateBestStreak = current.tasks.maxOfOrNull { task -> repository.bestStreak(task) } ?: 0
        val consistencyNonZero = weekdayConsistency
            .mapIndexed { index, value -> index to value }
            .filter { (_, value) -> value > 0 }
        val mostConsistentWeekday = consistencyNonZero.maxByOrNull { it.second }?.first?.plus(1)
        val hardestWeekday = consistencyNonZero.minByOrNull { it.second }?.first?.plus(1)
        val completionConsistency = if (consistencyNonZero.isEmpty()) {
            0
        } else {
            consistencyNonZero.sumOf { it.second } / consistencyNonZero.size
        }
        val streakSaverCount = selectedTask?.let { repository.getStreakSaverCount(it.id) } ?: 0
        val streakSaverMissedDate = selectedTask?.let { task ->
            if (
                task.frequency != TaskFrequency.TIMES_PER_WEEK &&
                repository.isScheduledOn(task, yesterday) &&
                !repository.isDone(task.id, yesterday) &&
                !repository.isMissedDaySaved(task.id, yesterday)
            ) {
                yesterday
            } else {
                null
            }
        }
        val showStreakSaverDialog = selectedTask?.let { task ->
            streakSaverMissedDate != null &&
                streakSaverCount > 0 &&
                dismissedStreakSaverPromptDates[task.id] != streakSaverMissedDate
        } ?: false
        _state.update {
            it.copy(
                selectedDateInFuture = isFutureDate,
                selectedDateValue = selectedDateValue,
                selectedDateTarget = selectedDateTarget,
                selectedDateUnit = selectedDateUnit,
                selectedDateCompletionPercent = selectedDateCompletionPercent,
                todayDone = selectedTask?.let { repository.isDone(it.id, today) } ?: false,
                todayScheduled = selectedTask?.let { task ->
                    repository.isScheduledOn(task, today) ||
                        (today.isBefore(task.startDate) && repository.isScheduledByFrequency(task, today))
                } ?: false,
                streakSaverCount = streakSaverCount,
                showStreakSaverDialog = showStreakSaverDialog,
                streakSaverMissedDate = streakSaverMissedDate,
                selectedDateScheduled = selectedDateScheduled,
                selectedDateNextScheduled = selectedDateNextScheduled,
                selectedDateDone = selectedDateDone,
                selectedDatePartial = selectedDatePartial,
                streak = selectedTask?.let { repository.calculateStreak(it) } ?: 0,
                bestStreak = selectedTask?.let { repository.bestStreak(it) } ?: 0,
                streakHistory = selectedTask?.let { task ->
                    repository.streakHistory(task, limit = 4)
                } ?: emptyList(),
                mostConsistentWeekday = mostConsistentWeekday,
                hardestWeekday = hardestWeekday,
                completionConsistency = completionConsistency,
                completionRate7Day = selectedTask?.let { repository.completionRate(it, 7, metricsAnchorDate) } ?: 0,
                completionRate30Day = selectedTask?.let { repository.completionRate(it, 30, metricsAnchorDate) } ?: 0,
                analyticsAggregateCompletionRate7Day = aggregateCompletionRate7Day,
                analyticsAggregateCompletionRate30Day = aggregateCompletionRate30Day,
                analyticsAggregateTotalCompletions = aggregateTotalCompletions,
                analyticsAggregateCurrentStreak = aggregateCurrentStreak,
                analyticsAggregateBestStreak = aggregateBestStreak,
                analyticsAggregateWeekdayConsistency = aggregateWeekdayConsistency,
                analyticsSelectedWeekSummaries = selectedWeekSummaries,
                analyticsAggregateWeekSummaries = aggregateWeekSummaries,
                analyticsSelectedHourlyCounts = selectedHourlyCounts,
                analyticsAggregateHourlyCounts = aggregateHourlyCounts,
                weeklyRingProgress = heroRolling7Snapshot.progress,
                weeklyRingCompleted = heroRolling7Snapshot.completedScheduled,
                weeklyRingScheduled = heroRolling7Snapshot.scheduledCount,
                totalCompletions = selectedTask?.let { repository.totalCompletions(it) } ?: 0,
                totalTrackedValue = selectedTask?.let { repository.totalTrackedValue(it) } ?: 0,
                averageTrackedValue = selectedTask?.let { repository.averageTrackedValue(it, 30, metricsAnchorDate) } ?: 0,
                progressPercent = selectedTask?.let { repository.progressForLast30Days(it, metricsAnchorDate) } ?: 0,
                last7Days = heroRolling7Snapshot.points,
                last7DaysScheduled = heroRolling7Snapshot.scheduled,
                last7DaysManualOverride = heroRolling7Snapshot.manualOverride,
                monthlyProgress = selectedTask?.let { repository.monthlyWeeklyProgress(it, month) } ?: emptyList(),
                weekdayConsistency = weekdayConsistency,
                doneDatesInCurrentMonth = selectedTask?.let { task ->
                    doneDatesForMonth(task, it.currentMonth)
                } ?: emptySet(),
                partialDatesInCurrentMonth = selectedTask?.let { task ->
                    partialDatesForMonth(task, it.currentMonth)
                } ?: emptySet(),
                scheduledDatesInCurrentMonth = selectedTask?.let { task ->
                    scheduledDatesForMonth(task, it.currentMonth)
                } ?: emptySet(),
                calendarFilterTaskId = resolvedCalendarFilterId,
                calendarFilterOptions = calendarFilterOptions,
                calendarCompletedCountByDate = calendarCompletedCountByDate,
                calendarScheduledCountByDate = calendarScheduledCountByDate,
                calendarManualOverrideCountByDate = calendarManualOverrideCountByDate,
                calendarBreakdownCompletedCount = calendarBreakdownCompletedCount,
                calendarBreakdownScheduledCount = calendarBreakdownScheduledCount,
                calendarBreakdownItems = calendarBreakdownItems,
                minimumCompletionPercent = minimumCompletionPercent
            )
        }
    }

    private fun buildHeroRolling7Snapshot(
        task: HabitTask?,
        anchorDate: LocalDate
    ): HeroRolling7Snapshot {
        if (task == null) {
            return HeroRolling7Snapshot(
                points = List(7) { 0 },
                scheduled = List(7) { false },
                manualOverride = List(7) { false },
                completedScheduled = 0,
                scheduledCount = 0,
                progress = 0f
            )
        }

        val dates = (6L downTo 0L).map { offset -> anchorDate.minusDays(offset) }
        val scheduled = dates.map { day -> repository.isScheduledOn(task, day) }
        val points = dates.map { day -> repository.progressPercentForWidget(task, day) }
        val manualOverride = dates.map { day ->
            !repository.isScheduledOn(task, day) && repository.getDayValue(task, day) > 0
        }
        val scheduledCount = scheduled.count { it }
        val completedScheduled = scheduled.indices.count { index ->
            scheduled[index] && points[index] >= 100
        }
        val progress = if (scheduledCount == 0) {
            0f
        } else {
            completedScheduled.toFloat() / scheduledCount.toFloat()
        }

        return HeroRolling7Snapshot(
            points = points,
            scheduled = scheduled,
            manualOverride = manualOverride,
            completedScheduled = completedScheduled,
            scheduledCount = scheduledCount,
            progress = progress.coerceIn(0f, 1f)
        )
    }

    private fun doneDatesForMonth(task: HabitTask, month: YearMonth): Set<LocalDate> {
        val done = mutableSetOf<LocalDate>()
        for (day in 1..month.lengthOfMonth()) {
            val date = month.atDay(day)
            if (repository.isCompletedOn(task, date)) done += date
        }
        return done
    }

    private fun partialDatesForMonth(task: HabitTask, month: YearMonth): Set<LocalDate> {
        val partial = mutableSetOf<LocalDate>()
        for (day in 1..month.lengthOfMonth()) {
            val date = month.atDay(day)
            if (repository.isPartialOn(task, date)) partial += date
        }
        return partial
    }

    private fun scheduledDatesForMonth(task: HabitTask, month: YearMonth): Set<LocalDate> {
        val scheduled = mutableSetOf<LocalDate>()
        for (day in 1..month.lengthOfMonth()) {
            val date = month.atDay(day)
            if (repository.isScheduledOn(task, date)) scheduled += date
        }
        return scheduled
    }

    private fun buildCalendarMonthCounts(
        tasks: List<HabitTask>,
        month: YearMonth
    ): Pair<Map<LocalDate, Int>, Map<LocalDate, Int>> {
        if (tasks.isEmpty()) return emptyMap<LocalDate, Int>() to emptyMap()
        val completedByDate = mutableMapOf<LocalDate, Int>()
        val scheduledByDate = mutableMapOf<LocalDate, Int>()
        for (day in 1..month.lengthOfMonth()) {
            val date = month.atDay(day)
            var completed = 0
            var scheduled = 0
            tasks.forEach { task ->
                if (repository.isScheduledOn(task, date)) {
                    scheduled += 1
                    if (repository.isCompletedOn(task, date)) {
                        completed += 1
                    }
                }
            }
            completedByDate[date] = completed
            scheduledByDate[date] = scheduled
        }
        return completedByDate to scheduledByDate
    }

    private fun buildCalendarMonthManualOverrideCounts(
        tasks: List<HabitTask>,
        month: YearMonth
    ): Map<LocalDate, Int> {
        if (tasks.isEmpty()) return emptyMap()
        val manualByDate = mutableMapOf<LocalDate, Int>()
        for (day in 1..month.lengthOfMonth()) {
            val date = month.atDay(day)
            var manual = 0
            tasks.forEach { task ->
                if (!repository.isScheduledOn(task, date) && repository.getDayValue(task, date) > 0) {
                    manual += 1
                }
            }
            manualByDate[date] = manual
        }
        return manualByDate
    }

    private fun buildCalendarBreakdown(
        tasks: List<HabitTask>,
        date: LocalDate,
        today: LocalDate
    ): Triple<Int, Int, List<CalendarBreakdownItem>> {
        if (tasks.isEmpty()) return Triple(0, 0, emptyList())
        val items = tasks.map { task ->
            val scheduled = repository.isScheduledOn(task, date)
            val completed = repository.isCompletedOn(task, date)
            val partial = repository.isPartialOn(task, date)
            val status = when {
                date.isAfter(today) -> CalendarBreakdownStatus.FUTURE
                completed -> CalendarBreakdownStatus.COMPLETED
                partial -> CalendarBreakdownStatus.PARTIAL
                !scheduled -> CalendarBreakdownStatus.NOT_SCHEDULED
                date.isBefore(today) -> CalendarBreakdownStatus.MISSED
                else -> CalendarBreakdownStatus.TODAY_PENDING
            }
            CalendarBreakdownItem(
                taskId = task.id,
                title = task.title,
                emoji = task.emoji,
                trackingType = task.trackingType,
                scheduled = scheduled,
                status = status,
                value = repository.getDayValue(task, date),
                target = repository.dailyTarget(task),
                unitLabel = repository.unitLabel(task)
            )
        }
        val completedCount = items.count { it.status == CalendarBreakdownStatus.COMPLETED }
        val scheduledCount = items.count { it.scheduled }
        return Triple(completedCount, scheduledCount, items)
    }

    private fun canCreateTask(taskCount: Int, plan: SubscriptionPlan): Boolean {
        return plan.hasPremiumAccess() || taskCount < FREE_ACTIVE_HABIT_LIMIT
    }

    private fun maybeAwardStreakSaver(taskId: String, previousStreak: Int) {
        val today = LocalDate.now()
        val guardKey = "$taskId|$today"
        if (guardKey in saverRewardedCompletionDates) return
        val task = repository.getTasks().firstOrNull { it.id == taskId } ?: return
        val currentStreak = repository.calculateStreak(task)
        val previousMilestones = previousStreak / 7
        val currentMilestones = currentStreak / 7
        val earned = (currentMilestones - previousMilestones).coerceAtLeast(0)
        if (earned > 0) {
            repository.addStreakSavers(taskId, earned)
            saverRewardedCompletionDates += guardKey
        }
    }

    private fun frequencyLabel(task: HabitTask, language: AppLanguage): String {
        val locale = localeForLanguage(language)
        return when (task.frequency) {
            TaskFrequency.DAILY -> translate(language, "Every day")
            TaskFrequency.TIMES_PER_WEEK -> translate(language, "X / week").replace("X", task.timesPerWeek.toString())
            TaskFrequency.SELECTED_DAYS -> {
                if (task.customDays.isEmpty()) return translate(language, "Selected weekdays")
                task.customDays
                    .toList()
                    .sorted()
                    .joinToString(", ") {
                        DayOfWeek.of(it).getDisplayName(TextStyle.SHORT, locale)
                    }
            }
        }
    }

    override fun onCleared() {
        cancelDurationTimer()
        super.onCleared()
    }

    class Factory(
        private val repository: HabitRepository,
        private val reminderScheduler: HabitReminderScheduler
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository, reminderScheduler) as T
        }
    }

    companion object {
        private const val FREE_ACTIVE_HABIT_LIMIT = 3
        private const val DEFAULT_REMINDER_HOUR = 8
        private const val DEFAULT_REMINDER_MINUTE = 0
    }
}
