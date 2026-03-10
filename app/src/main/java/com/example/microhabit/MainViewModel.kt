package com.example.microhabit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.microhabit.data.AppLanguage
import com.example.microhabit.data.AppThemeMode
import com.example.microhabit.data.HabitRepository
import com.example.microhabit.data.HabitTask
import com.example.microhabit.data.SubscriptionPlan
import com.example.microhabit.data.TaskFrequency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

data class HabitUiState(
    val tasks: List<HabitTask> = emptyList(),
    val selectedTaskId: String? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val currentMonth: YearMonth = YearMonth.now(),
    val selectedDateDone: Boolean = false,
    val selectedDateScheduled: Boolean = false,
    val streak: Int = 0,
    val progressPercent: Int = 0,
    val last7Days: List<Int> = List(7) { 0 },
    val doneDatesInCurrentMonth: Set<LocalDate> = emptySet(),
    val showEditor: Boolean = false,
    val editingTaskId: String? = null,
    val editorTitle: String = "",
    val editorFrequency: TaskFrequency = TaskFrequency.DAILY,
    val editorCustomDays: Set<Int> = setOf(1, 2, 3, 4, 5),
    val plan: SubscriptionPlan = SubscriptionPlan.FREE,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val language: AppLanguage = AppLanguage.RU
)

class MainViewModel(private val repository: HabitRepository) : ViewModel() {
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
            _state.update { it.copy(language = language) }
        }
    }

    fun toggleSelectedDateDone() {
        val current = _state.value
        val task = current.tasks.firstOrNull { it.id == current.selectedTaskId } ?: return
        if (!repository.isScheduledOn(task, current.selectedDate)) return

        viewModelScope.launch {
            val alreadyDone = repository.isDone(task.id, current.selectedDate)
            repository.setDone(task.id, current.selectedDate, !alreadyDone)
            repository.refreshWidget()
            refresh()
        }
    }

    fun openCreateTask() {
        _state.update {
            it.copy(
                showEditor = true,
                editingTaskId = null,
                editorTitle = "",
                editorFrequency = TaskFrequency.DAILY,
                editorCustomDays = setOf(1, 2, 3, 4, 5)
            )
        }
    }

    fun openEditTask(taskId: String) {
        val task = _state.value.tasks.firstOrNull { it.id == taskId } ?: return
        _state.update {
            it.copy(
                showEditor = true,
                editingTaskId = task.id,
                editorTitle = task.title,
                editorFrequency = task.frequency,
                editorCustomDays = if (task.customDays.isEmpty()) setOf(1, 2, 3, 4, 5) else task.customDays
            )
        }
    }

    fun closeEditor() {
        if (_state.value.tasks.isNotEmpty()) {
            _state.update { it.copy(showEditor = false) }
        }
    }

    fun setEditorTitle(value: String) {
        _state.update { it.copy(editorTitle = value) }
    }

    fun setEditorFrequency(value: TaskFrequency) {
        _state.update { it.copy(editorFrequency = value) }
    }

    fun toggleEditorCustomDay(day: Int) {
        if (day !in 1..7) return
        _state.update {
            val next = it.editorCustomDays.toMutableSet()
            if (!next.add(day)) next.remove(day)
            it.copy(editorCustomDays = next)
        }
    }

    fun saveEditor() {
        val current = _state.value
        val title = current.editorTitle.trim()
        if (title.isEmpty()) return

        if (current.editingTaskId == null && !canCreateTask(current.tasks.size, current.plan)) {
            return
        }

        val frequency = current.editorFrequency
        val customDays = if (frequency == TaskFrequency.CUSTOM) {
            current.editorCustomDays.ifEmpty { setOf(1) }
        } else {
            emptySet()
        }

        viewModelScope.launch {
            if (current.editingTaskId == null) {
                val task = repository.createTask(title, frequency, customDays)
                repository.setSelectedTask(task.id)
            } else {
                repository.updateTask(current.editingTaskId, title, frequency, customDays)
            }
            repository.refreshWidget()
            refresh()
            _state.update { it.copy(showEditor = false) }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
            repository.refreshWidget()
            refresh()
        }
    }

    fun canCreateTask(): Boolean {
        val s = _state.value
        return canCreateTask(s.tasks.size, s.plan)
    }

    private fun refresh() {
        viewModelScope.launch {
            val tasks = repository.getTasks()
            val plan = repository.getPlan()
            val themeMode = repository.getThemeMode()
            val language = repository.getLanguage()

            var selectedId = repository.getSelectedTaskId()
            if (selectedId == null || tasks.none { it.id == selectedId }) {
                selectedId = tasks.firstOrNull()?.id
                repository.setSelectedTask(selectedId)
            }

            val selectedTask = tasks.firstOrNull { it.id == selectedId }
            val date = _state.value.selectedDate
            _state.update { state ->
                state.copy(
                    tasks = tasks,
                    selectedTaskId = selectedId,
                    showEditor = if (tasks.isEmpty()) true else state.showEditor,
                    selectedDateScheduled = selectedTask?.let { repository.isScheduledOn(it, date) } ?: false,
                    selectedDateDone = selectedTask?.let { repository.isDone(it.id, date) } ?: false,
                    streak = selectedTask?.let { repository.calculateStreak(it) } ?: 0,
                    progressPercent = selectedTask?.let { repository.progressForLast30Days(it) } ?: 0,
                    last7Days = selectedTask?.let { repository.last7Days(it, date) } ?: List(7) { 0 },
                    doneDatesInCurrentMonth = selectedTask?.let { task ->
                        doneDatesForMonth(task, state.currentMonth)
                    } ?: emptySet(),
                    plan = plan,
                    themeMode = themeMode,
                    language = language
                )
            }
        }
    }

    private fun refreshDerivedOnly() {
        val current = _state.value
        val selectedTask = current.tasks.firstOrNull { it.id == current.selectedTaskId }
        val date = current.selectedDate
        _state.update {
            it.copy(
                selectedDateScheduled = selectedTask?.let { task -> repository.isScheduledOn(task, date) } ?: false,
                selectedDateDone = selectedTask?.let { task -> repository.isDone(task.id, date) } ?: false,
                streak = selectedTask?.let { repository.calculateStreak(it) } ?: 0,
                progressPercent = selectedTask?.let { repository.progressForLast30Days(it) } ?: 0,
                last7Days = selectedTask?.let { repository.last7Days(it, date) } ?: List(7) { 0 },
                doneDatesInCurrentMonth = selectedTask?.let { task ->
                    doneDatesForMonth(task, it.currentMonth)
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

    private fun canCreateTask(taskCount: Int, plan: SubscriptionPlan): Boolean {
        return plan == SubscriptionPlan.PRO || taskCount < 1
    }

    class Factory(private val repository: HabitRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
    }
}
