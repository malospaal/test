package com.example.microhabit.data

import android.content.Context
import com.example.microhabit.widget.HabitWidgetProvider
import org.json.JSONArray
import org.json.JSONObject
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

enum class TaskFrequency {
    DAILY,
    WEEKDAYS,
    CUSTOM
}

enum class SubscriptionPlan {
    FREE,
    PRO
}

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class AppLanguage(val label: String) {
    EN("English"),
    DE("Deutsch"),
    FR("Français"),
    ES("Español"),
    IT("Italiano"),
    RU("Русский")
}

data class HabitTask(
    val id: String,
    val title: String,
    val frequency: TaskFrequency,
    val customDays: Set<Int> = emptySet()
)

class HabitRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences("habit_prefs", Context.MODE_PRIVATE)
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun getTasks(): List<HabitTask> {
        val raw = prefs.getString(KEY_TASKS_JSON, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            (0 until array.length()).mapNotNull { i ->
                val obj = array.optJSONObject(i) ?: return@mapNotNull null
                val id = obj.optString("id")
                val title = obj.optString("title")
                if (id.isBlank() || title.isBlank()) return@mapNotNull null

                val frequency = runCatching {
                    TaskFrequency.valueOf(obj.optString("frequency", TaskFrequency.DAILY.name))
                }.getOrDefault(TaskFrequency.DAILY)

                val custom = mutableSetOf<Int>()
                val customJson = obj.optJSONArray("customDays") ?: JSONArray()
                for (j in 0 until customJson.length()) {
                    val day = customJson.optInt(j, -1)
                    if (day in 1..7) custom += day
                }

                HabitTask(id = id, title = title, frequency = frequency, customDays = custom)
            }
        }.getOrDefault(emptyList())
    }

    fun createTask(title: String, frequency: TaskFrequency, customDays: Set<Int>): HabitTask {
        val task = HabitTask(
            id = UUID.randomUUID().toString(),
            title = title.trim(),
            frequency = frequency,
            customDays = sanitizeCustomDays(customDays)
        )
        val tasks = getTasks().toMutableList().apply { add(task) }
        saveTasks(tasks)
        if (getSelectedTaskId() == null) setSelectedTask(task.id)
        return task
    }

    fun updateTask(taskId: String, title: String, frequency: TaskFrequency, customDays: Set<Int>) {
        val updated = getTasks().map { task ->
            if (task.id == taskId) {
                task.copy(
                    title = title.trim(),
                    frequency = frequency,
                    customDays = sanitizeCustomDays(customDays)
                )
            } else {
                task
            }
        }
        saveTasks(updated)
    }

    fun deleteTask(taskId: String) {
        val updated = getTasks().filterNot { it.id == taskId }
        saveTasks(updated)

        prefs.all.keys
            .filter { it.startsWith("done_${taskId}_") }
            .forEach { key -> prefs.edit().remove(key).apply() }

        if (getSelectedTaskId() == taskId) {
            prefs.edit().putString(KEY_SELECTED_TASK, updated.firstOrNull()?.id).apply()
        }
    }

    fun setSelectedTask(taskId: String?) {
        prefs.edit().putString(KEY_SELECTED_TASK, taskId).apply()
    }

    fun getSelectedTaskId(): String? = prefs.getString(KEY_SELECTED_TASK, null)

    fun getPlan(): SubscriptionPlan {
        val raw = prefs.getString(KEY_PLAN, SubscriptionPlan.FREE.name) ?: SubscriptionPlan.FREE.name
        return runCatching { SubscriptionPlan.valueOf(raw) }.getOrDefault(SubscriptionPlan.FREE)
    }

    fun setPlan(plan: SubscriptionPlan) {
        prefs.edit().putString(KEY_PLAN, plan.name).apply()
    }

    fun getThemeMode(): AppThemeMode {
        val raw = prefs.getString(KEY_THEME_MODE, AppThemeMode.SYSTEM.name) ?: AppThemeMode.SYSTEM.name
        return runCatching { AppThemeMode.valueOf(raw) }.getOrDefault(AppThemeMode.SYSTEM)
    }

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
    }

    fun getLanguage(): AppLanguage {
        val raw = prefs.getString(KEY_LANGUAGE, AppLanguage.RU.name) ?: AppLanguage.RU.name
        return runCatching { AppLanguage.valueOf(raw) }.getOrDefault(AppLanguage.RU)
    }

    fun setLanguage(language: AppLanguage) {
        prefs.edit().putString(KEY_LANGUAGE, language.name).apply()
    }

    fun isScheduledOn(task: HabitTask, date: LocalDate): Boolean {
        return when (task.frequency) {
            TaskFrequency.DAILY -> true
            TaskFrequency.WEEKDAYS -> date.dayOfWeek != DayOfWeek.SATURDAY && date.dayOfWeek != DayOfWeek.SUNDAY
            TaskFrequency.CUSTOM -> date.dayOfWeek.value in task.customDays
        }
    }

    fun isDone(taskId: String, date: LocalDate): Boolean {
        return prefs.getBoolean(doneKey(taskId, date), false)
    }

    fun setDone(taskId: String, date: LocalDate, done: Boolean) {
        val key = doneKey(taskId, date)
        if (done) {
            prefs.edit().putBoolean(key, true).apply()
        } else {
            prefs.edit().remove(key).apply()
        }
    }

    fun calculateStreak(task: HabitTask, fromDate: LocalDate = LocalDate.now()): Int {
        var streak = 0
        var cursor = fromDate

        repeat(3650) {
            if (!isScheduledOn(task, cursor)) {
                cursor = cursor.minusDays(1)
                return@repeat
            }

            if (isDone(task.id, cursor)) {
                streak += 1
                cursor = cursor.minusDays(1)
            } else {
                return streak
            }
        }
        return streak
    }

    fun last7Days(task: HabitTask, anchorDate: LocalDate = LocalDate.now()): List<Int> {
        return (6L downTo 0L).map { offset ->
            val day = anchorDate.minusDays(offset)
            if (isDone(task.id, day)) 1 else 0
        }
    }

    fun progressForLast30Days(task: HabitTask, anchorDate: LocalDate = LocalDate.now()): Int {
        var scheduled = 0
        var completed = 0
        for (offset in 0L until 30L) {
            val day = anchorDate.minusDays(offset)
            if (isScheduledOn(task, day)) {
                scheduled += 1
                if (isDone(task.id, day)) completed += 1
            }
        }
        if (scheduled == 0) return 0
        return (completed * 100 / scheduled)
    }

    fun selectedTaskWidgetSummary(): Triple<String, Int, Int> {
        val tasks = getTasks()
        val selected = tasks.firstOrNull { it.id == getSelectedTaskId() } ?: tasks.firstOrNull()
        if (selected == null) return Triple("Создай задачу", 0, 0)

        val streak = calculateStreak(selected)
        val progress = progressForLast30Days(selected)
        return Triple(selected.title, streak, progress)
    }

    fun refreshWidget() {
        HabitWidgetProvider.refreshAll(context)
    }

    private fun saveTasks(tasks: List<HabitTask>) {
        val array = JSONArray()
        tasks.forEach { task ->
            val obj = JSONObject()
                .put("id", task.id)
                .put("title", task.title)
                .put("frequency", task.frequency.name)
            val custom = JSONArray()
            task.customDays.sorted().forEach { custom.put(it) }
            obj.put("customDays", custom)
            array.put(obj)
        }
        prefs.edit().putString(KEY_TASKS_JSON, array.toString()).apply()
    }

    private fun sanitizeCustomDays(days: Set<Int>): Set<Int> {
        return days.filter { it in 1..7 }.toSet()
    }

    private fun doneKey(taskId: String, date: LocalDate): String {
        return "done_${taskId}_${date.format(formatter)}"
    }

    companion object {
        private const val KEY_TASKS_JSON = "tasks_json"
        private const val KEY_SELECTED_TASK = "selected_task"
        private const val KEY_PLAN = "user_plan"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_LANGUAGE = "app_language"
    }
}
