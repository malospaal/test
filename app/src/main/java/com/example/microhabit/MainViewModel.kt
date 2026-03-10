package com.example.microhabit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.microhabit.data.AppLanguage
import com.example.microhabit.data.AppThemeMode
import com.example.microhabit.data.HabitCategory
import com.example.microhabit.data.MAX_HABIT_TITLE_LENGTH
import com.example.microhabit.data.HabitRepository
import com.example.microhabit.data.HabitTask
import com.example.microhabit.data.HabitTemplate
import com.example.microhabit.data.HabitTemplateCatalog
import com.example.microhabit.data.SubscriptionPlan
import com.example.microhabit.data.TaskFrequency
import com.example.microhabit.data.TrackingType
import com.example.microhabit.i18n.localeForLanguage
import com.example.microhabit.i18n.translate
import com.example.microhabit.notifications.HabitReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle

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
    val isArchived: Boolean
)

data class HabitUiState(
    val tasks: List<HabitTask> = emptyList(),
    val allTasks: List<HabitTask> = emptyList(),
    val habits: List<HabitListItem> = emptyList(),
    val selectedTaskId: String? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val currentMonth: YearMonth = YearMonth.now(),
    val selectedDateDone: Boolean = false,
    val selectedDateScheduled: Boolean = false,
    val selectedDateInFuture: Boolean = false,
    val todayDone: Boolean = false,
    val todayScheduled: Boolean = false,
    val streak: Int = 0,
    val bestStreak: Int = 0,
    val completionRate7Day: Int = 0,
    val completionRate30Day: Int = 0,
    val totalCompletions: Int = 0,
    val streakHistory: List<Int> = emptyList(),
    val mostConsistentWeekday: Int? = null,
    val hardestWeekday: Int? = null,
    val completionConsistency: Int = 0,
    val selectedTaskNote: String = "",
    val progressPercent: Int = 0,
    val last7Days: List<Int> = List(7) { 0 },
    val last7DaysScheduled: List<Boolean> = List(7) { false },
    val monthlyProgress: List<Int> = emptyList(),
    val weekdayConsistency: List<Int> = List(7) { 0 },
    val doneDatesInCurrentMonth: Set<LocalDate> = emptySet(),
    val scheduledDatesInCurrentMonth: Set<LocalDate> = emptySet(),
    val showEditor: Boolean = false,
    val editingTaskId: String? = null,
    val editorTitle: String = "",
    val editorEmoji: String = "✨",
    val editorColorHex: String = "#1F6F64",
    val editorTrackingType: TrackingType = TrackingType.YES_NO,
    val editorFrequency: TaskFrequency = TaskFrequency.DAILY,
    val editorTimesPerWeek: Int = 3,
    val editorCustomDays: Set<Int> = setOf(1, 2, 3, 4, 5),
    val editorReminderHour: Int = 8,
    val editorReminderMinute: Int = 0,
    val editorReminderEnabled: Boolean = false,
    val editorStartDate: LocalDate = LocalDate.now(),
    val editorShowAdvanced: Boolean = false,
    val plan: SubscriptionPlan = SubscriptionPlan.FREE,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val language: AppLanguage = AppLanguage.RU,
    val notificationsEnabled: Boolean = true,
    val defaultReminderHour: Int = 8,
    val defaultReminderMinute: Int = 0,
    val onboardingCompleted: Boolean = false,
    val isLoaded: Boolean = false
)

private data class EditorSavePayload(
    val current: HabitUiState,
    val title: String,
    val frequency: TaskFrequency,
    val customDays: Set<Int>
)

class MainViewModel(
    private val repository: HabitRepository,
    private val reminderScheduler: HabitReminderScheduler
) : ViewModel() {
    private val _state = MutableStateFlow(HabitUiState())
    val state: StateFlow<HabitUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun selectTask(taskId: String) {
        viewModelScope.launch {
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

    fun setPlan(plan: SubscriptionPlan) {
        viewModelScope.launch {
            repository.setPlan(plan)
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
            refresh()
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setNotificationsEnabled(enabled)
            reminderScheduler.syncAllReminders()
            _state.update { it.copy(notificationsEnabled = enabled) }
        }
    }

    fun setDefaultReminder(hour: Int, minute: Int) {
        viewModelScope.launch {
            repository.setDefaultReminder(hour, minute)
            _state.update {
                it.copy(
                    defaultReminderHour = hour.coerceIn(0, 23),
                    defaultReminderMinute = minute.coerceIn(0, 59)
                )
            }
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
                editorTrackingType = TrackingType.YES_NO,
                editorFrequency = normalizedFrequency,
                editorTimesPerWeek = 3,
                editorCustomDays = normalizedCustomDays,
                editorReminderEnabled = reminderEnabled,
                editorReminderHour = reminderHour.coerceIn(0, 23),
                editorReminderMinute = reminderMinute.coerceIn(0, 59),
                editorStartDate = LocalDate.now(),
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
        val current = _state.value
        val task = current.tasks.firstOrNull { it.id == current.selectedTaskId } ?: return
        val selectedDate = current.selectedDate
        val shouldShiftStartDate =
            selectedDate.isBefore(task.startDate) && repository.isScheduledByFrequency(task, selectedDate)
        if (!repository.isScheduledOn(task, selectedDate) && !shouldShiftStartDate) return

        viewModelScope.launch {
            if (shouldShiftStartDate) {
                repository.updateTaskStartDate(task.id, selectedDate)
            }
            val alreadyDone = repository.isDone(task.id, selectedDate)
            repository.setDone(task.id, selectedDate, !alreadyDone)
            repository.refreshWidget()
            refresh()
        }
    }

    fun completeSelectedHabitToday() {
        val taskId = _state.value.selectedTaskId ?: return
        viewModelScope.launch {
            val task = repository.getTasks()
                .firstOrNull { it.id == taskId && !it.isArchived }
                ?: return@launch
            val today = LocalDate.now()
            if (!repository.isScheduledOn(task, today) && !(today.isBefore(task.startDate) && repository.isScheduledByFrequency(task, today))) {
                return@launch
            }
            if (!repository.isDone(task.id, today)) {
                if (today.isBefore(task.startDate) && repository.isScheduledByFrequency(task, today)) {
                    repository.updateTaskStartDate(task.id, today)
                }
                repository.setDone(task.id, today, true)
                repository.refreshWidget()
            }
            _state.update { it.copy(selectedDate = today, currentMonth = YearMonth.from(today)) }
            refresh()
        }
    }

    fun openCreateTask() {
        val reminderHour = _state.value.defaultReminderHour
        val reminderMinute = _state.value.defaultReminderMinute
        _state.update {
            it.copy(
                showEditor = true,
                editingTaskId = null,
                editorTitle = "",
                editorEmoji = "✨",
                editorColorHex = "#1F6F64",
                editorTrackingType = TrackingType.YES_NO,
                editorFrequency = TaskFrequency.DAILY,
                editorTimesPerWeek = 3,
                editorCustomDays = setOf(1, 2, 3, 4, 5),
                editorReminderHour = reminderHour,
                editorReminderMinute = reminderMinute,
                editorReminderEnabled = false,
                editorStartDate = LocalDate.now(),
                editorShowAdvanced = false
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
                editorFrequency = task.frequency,
                editorTimesPerWeek = task.timesPerWeek,
                editorCustomDays = if (task.customDays.isEmpty()) setOf(1, 2, 3, 4, 5) else task.customDays
                    .filter { it in 1..7 }
                    .toSet(),
                editorReminderHour = task.reminderHour,
                editorReminderMinute = task.reminderMinute,
                editorReminderEnabled = task.reminderEnabled,
                editorStartDate = task.startDate,
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
        _state.update { it.copy(editorTrackingType = value) }
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

    fun setEditorStartDate(value: LocalDate) {
        _state.update { it.copy(editorStartDate = value) }
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
            if (!repository.getNotificationsEnabled()) {
                repository.setNotificationsEnabled(true)
                reminderScheduler.syncAllReminders()
                _state.update { it.copy(notificationsEnabled = true) }
            }
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
                    frequency = payload.frequency,
                    customDays = payload.customDays,
                    timesPerWeek = current.editorTimesPerWeek,
                    reminderHour = current.editorReminderHour,
                    reminderMinute = current.editorReminderMinute,
                    reminderEnabled = current.editorReminderEnabled,
                    startDate = current.editorStartDate
                )
                repository.setSelectedTask(task.id)
                task.id
            } else {
                repository.updateTask(
                    taskId = current.editingTaskId,
                    title = payload.title,
                    emoji = current.editorEmoji,
                    colorHex = current.editorColorHex,
                    trackingType = current.editorTrackingType,
                    frequency = payload.frequency,
                    customDays = payload.customDays,
                    timesPerWeek = current.editorTimesPerWeek,
                    reminderHour = current.editorReminderHour,
                    reminderMinute = current.editorReminderMinute,
                    reminderEnabled = current.editorReminderEnabled,
                    startDate = current.editorStartDate
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

    fun unarchiveTask(taskId: String) {
        viewModelScope.launch {
            repository.archiveTask(taskId, archived = false)
            reminderScheduler.syncReminderForTask(taskId)
            repository.refreshWidget()
            refresh()
        }
    }

    fun canCreateTask(): Boolean {
        val s = _state.value
        return canCreateTask(s.tasks.size, s.plan)
    }

    fun canSaveEditor(): Boolean {
        val s = _state.value
        if (s.editorTitle.trim().isEmpty()) return false
        if (s.editorTitle.trim().length > MAX_HABIT_TITLE_LENGTH) return false
        if (s.editorEmoji.trim().isEmpty()) return false
        if (s.editorColorHex.trim().isEmpty()) return false
        if (s.editorFrequency == TaskFrequency.SELECTED_DAYS && s.editorCustomDays.isEmpty()) return false
        if (s.editorFrequency == TaskFrequency.TIMES_PER_WEEK && s.editorTimesPerWeek !in 1..7) return false
        return true
    }

    private fun refresh() {
        viewModelScope.launch {
            val allTasks = repository.getTasks()
            val tasks = allTasks.filterNot { it.isArchived }
            val plan = repository.getPlan()
            val themeMode = repository.getThemeMode()
            val language = repository.getLanguage()
            val notificationsEnabled = repository.getNotificationsEnabled()
            val defaultReminderHour = repository.getDefaultReminderHour()
            val defaultReminderMinute = repository.getDefaultReminderMinute()
            val onboardingCompleted = repository.isOnboardingCompleted()

            var selectedId = repository.getSelectedTaskId()
            if (selectedId == null || tasks.none { it.id == selectedId }) {
                selectedId = tasks.firstOrNull()?.id
                repository.setSelectedTask(selectedId)
            }

            val selectedTask = tasks.firstOrNull { it.id == selectedId }
            val date = _state.value.selectedDate
            val today = LocalDate.now()
            val isFutureDate = date.isAfter(LocalDate.now())
            val weekdayConsistency = selectedTask?.let { repository.weekdayConsistency(it, 84, date) } ?: List(7) { 0 }
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
                                isArchived = task.isArchived
                            )
                        }
                        .sortedBy { it.isArchived },
                    selectedTaskId = selectedId,
                    showEditor = state.showEditor,
                    selectedDateInFuture = isFutureDate,
                    todayDone = selectedTask?.let { repository.isDone(it.id, today) } ?: false,
                    todayScheduled = selectedTask?.let { task ->
                        repository.isScheduledOn(task, today) ||
                            (today.isBefore(task.startDate) && repository.isScheduledByFrequency(task, today))
                    } ?: false,
                    selectedDateScheduled = selectedTask?.let { task ->
                        repository.isScheduledOn(task, date) ||
                            (date.isBefore(task.startDate) && repository.isScheduledByFrequency(task, date))
                    } ?: false,
                    selectedDateDone = selectedTask?.let { repository.isDone(it.id, date) } ?: false,
                    streak = selectedTask?.let { repository.calculateStreak(it) } ?: 0,
                    bestStreak = selectedTask?.let { repository.bestStreak(it) } ?: 0,
                    streakHistory = selectedTask?.let { task ->
                        repository.streakHistory(task, limit = 4)
                    } ?: emptyList(),
                    mostConsistentWeekday = mostConsistentWeekday,
                    hardestWeekday = hardestWeekday,
                    completionConsistency = completionConsistency,
                    selectedTaskNote = selectedTask?.let { repository.getTaskNote(it.id) }.orEmpty(),
                    completionRate7Day = selectedTask?.let { repository.completionRate(it, 7, date) } ?: 0,
                    completionRate30Day = selectedTask?.let { repository.completionRate(it, 30, date) } ?: 0,
                    totalCompletions = selectedTask?.let { repository.totalCompletions(it) } ?: 0,
                    progressPercent = selectedTask?.let { repository.progressForLast30Days(it, date) } ?: 0,
                    last7Days = selectedTask?.let { repository.last7Days(it, date) } ?: List(7) { 0 },
                    last7DaysScheduled = selectedTask?.let { task ->
                        (6L downTo 0L).map { offset ->
                            val day = date.minusDays(offset)
                            repository.isScheduledOn(task, day)
                        }
                    } ?: List(7) { false },
                    monthlyProgress = selectedTask?.let { repository.monthlyWeeklyProgress(it, state.currentMonth) } ?: emptyList(),
                    weekdayConsistency = weekdayConsistency,
                    doneDatesInCurrentMonth = selectedTask?.let { task ->
                        doneDatesForMonth(task, state.currentMonth)
                    } ?: emptySet(),
                    scheduledDatesInCurrentMonth = selectedTask?.let { task ->
                        scheduledDatesForMonth(task, state.currentMonth)
                    } ?: emptySet(),
                    plan = plan,
                    themeMode = themeMode,
                    language = language,
                    notificationsEnabled = notificationsEnabled,
                    defaultReminderHour = defaultReminderHour,
                    defaultReminderMinute = defaultReminderMinute,
                    onboardingCompleted = onboardingCompleted,
                    isLoaded = true
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
        val isFutureDate = date.isAfter(LocalDate.now())
        val weekdayConsistency = selectedTask?.let { repository.weekdayConsistency(it, 84, date) } ?: List(7) { 0 }
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
        _state.update {
            it.copy(
                selectedDateInFuture = isFutureDate,
                todayDone = selectedTask?.let { repository.isDone(it.id, today) } ?: false,
                todayScheduled = selectedTask?.let { task ->
                    repository.isScheduledOn(task, today) ||
                        (today.isBefore(task.startDate) && repository.isScheduledByFrequency(task, today))
                } ?: false,
                selectedDateScheduled = selectedTask?.let { task ->
                    repository.isScheduledOn(task, date) ||
                        (date.isBefore(task.startDate) && repository.isScheduledByFrequency(task, date))
                } ?: false,
                selectedDateDone = selectedTask?.let { task -> repository.isDone(task.id, date) } ?: false,
                streak = selectedTask?.let { repository.calculateStreak(it) } ?: 0,
                bestStreak = selectedTask?.let { repository.bestStreak(it) } ?: 0,
                streakHistory = selectedTask?.let { task ->
                    repository.streakHistory(task, limit = 4)
                } ?: emptyList(),
                mostConsistentWeekday = mostConsistentWeekday,
                hardestWeekday = hardestWeekday,
                completionConsistency = completionConsistency,
                completionRate7Day = selectedTask?.let { repository.completionRate(it, 7, date) } ?: 0,
                completionRate30Day = selectedTask?.let { repository.completionRate(it, 30, date) } ?: 0,
                totalCompletions = selectedTask?.let { repository.totalCompletions(it) } ?: 0,
                progressPercent = selectedTask?.let { repository.progressForLast30Days(it, date) } ?: 0,
                last7Days = selectedTask?.let { repository.last7Days(it, date) } ?: List(7) { 0 },
                last7DaysScheduled = selectedTask?.let { task ->
                    (6L downTo 0L).map { offset ->
                        val day = date.minusDays(offset)
                        repository.isScheduledOn(task, day)
                    }
                } ?: List(7) { false },
                monthlyProgress = selectedTask?.let { repository.monthlyWeeklyProgress(it, month) } ?: emptyList(),
                weekdayConsistency = weekdayConsistency,
                doneDatesInCurrentMonth = selectedTask?.let { task ->
                    doneDatesForMonth(task, it.currentMonth)
                } ?: emptySet(),
                scheduledDatesInCurrentMonth = selectedTask?.let { task ->
                    scheduledDatesForMonth(task, it.currentMonth)
                } ?: emptySet()
            )
        }
    }

    private fun doneDatesForMonth(task: HabitTask, month: YearMonth): Set<LocalDate> {
        val done = mutableSetOf<LocalDate>()
        for (day in 1..month.lengthOfMonth()) {
            val date = month.atDay(day)
            if (repository.isDone(task.id, date)) done += date
        }
        return done
    }

    private fun scheduledDatesForMonth(task: HabitTask, month: YearMonth): Set<LocalDate> {
        val scheduled = mutableSetOf<LocalDate>()
        for (day in 1..month.lengthOfMonth()) {
            val date = month.atDay(day)
            if (repository.isScheduledOn(task, date)) scheduled += date
        }
        return scheduled
    }

    private fun canCreateTask(taskCount: Int, plan: SubscriptionPlan): Boolean {
        return plan == SubscriptionPlan.PRO || taskCount < 1
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

    class Factory(
        private val repository: HabitRepository,
        private val reminderScheduler: HabitReminderScheduler
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository, reminderScheduler) as T
        }
    }
}
