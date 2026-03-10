package com.example.microhabit.data

import android.content.Context
import com.example.microhabit.widget.HabitWidgetProvider
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.ceil
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.time.YearMonth

enum class TaskFrequency {
    DAILY,
    SELECTED_DAYS,
    TIMES_PER_WEEK
}

enum class TrackingType {
    YES_NO,
    COUNT,
    DURATION
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
    val emoji: String = "✨",
    val colorHex: String = "#1F6F64",
    val trackingType: TrackingType = TrackingType.YES_NO,
    val frequency: TaskFrequency,
    val timesPerWeek: Int = 3,
    val reminderHour: Int = 8,
    val reminderMinute: Int = 0,
    val startDate: LocalDate = LocalDate.now(),
    val customDays: Set<Int> = emptySet(),
    val isArchived: Boolean = false
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

                val rawFrequency = obj.optString("frequency", TaskFrequency.DAILY.name)
                val frequency = runCatching {
                    when (rawFrequency) {
                        "WEEKDAYS", "CUSTOM" -> TaskFrequency.SELECTED_DAYS
                        else -> TaskFrequency.valueOf(rawFrequency)
                    }
                }.getOrDefault(TaskFrequency.DAILY)

                val trackingType = runCatching {
                    TrackingType.valueOf(obj.optString("trackingType", TrackingType.YES_NO.name))
                }.getOrDefault(TrackingType.YES_NO)

                val custom = mutableSetOf<Int>()
                if (rawFrequency == "WEEKDAYS") {
                    custom += setOf(1, 2, 3, 4, 5)
                }
                val customJson = obj.optJSONArray("customDays") ?: JSONArray()
                for (j in 0 until customJson.length()) {
                    val day = customJson.optInt(j, -1)
                    if (day in 1..7) custom += day
                }

                val startDate = runCatching {
                    LocalDate.parse(obj.optString("startDate", LocalDate.now().format(formatter)), formatter)
                }.getOrDefault(LocalDate.now())

                HabitTask(
                    id = id,
                    title = title,
                    emoji = obj.optString("emoji", "✨").ifBlank { "✨" },
                    colorHex = obj.optString("colorHex", "#1F6F64").ifBlank { "#1F6F64" },
                    trackingType = trackingType,
                    frequency = frequency,
                    timesPerWeek = obj.optInt("timesPerWeek", 3).coerceIn(1, 7),
                    reminderHour = obj.optInt("reminderHour", 8).coerceIn(0, 23),
                    reminderMinute = obj.optInt("reminderMinute", 0).coerceIn(0, 59),
                    startDate = startDate,
                    customDays = custom,
                    isArchived = obj.optBoolean("isArchived", false)
                )
            }
        }.getOrDefault(emptyList())
    }

    fun createTask(
        title: String,
        emoji: String,
        colorHex: String,
        trackingType: TrackingType,
        frequency: TaskFrequency,
        customDays: Set<Int>,
        timesPerWeek: Int,
        reminderHour: Int,
        reminderMinute: Int,
        startDate: LocalDate
    ): HabitTask {
        val task = HabitTask(
            id = UUID.randomUUID().toString(),
            title = title.trim(),
            emoji = emoji.ifBlank { "✨" },
            colorHex = colorHex.ifBlank { "#1F6F64" },
            trackingType = trackingType,
            frequency = frequency,
            timesPerWeek = timesPerWeek.coerceIn(1, 7),
            reminderHour = reminderHour.coerceIn(0, 23),
            reminderMinute = reminderMinute.coerceIn(0, 59),
            startDate = startDate,
            customDays = sanitizeCustomDays(customDays),
            isArchived = false
        )
        val tasks = getTasks().toMutableList().apply { add(task) }
        saveTasks(tasks)
        if (getSelectedTaskId() == null) setSelectedTask(task.id)
        return task
    }

    fun updateTask(
        taskId: String,
        title: String,
        emoji: String,
        colorHex: String,
        trackingType: TrackingType,
        frequency: TaskFrequency,
        customDays: Set<Int>,
        timesPerWeek: Int,
        reminderHour: Int,
        reminderMinute: Int,
        startDate: LocalDate
    ) {
        val updated = getTasks().map { task ->
            if (task.id == taskId) {
                task.copy(
                    title = title.trim(),
                    emoji = emoji.ifBlank { "✨" },
                    colorHex = colorHex.ifBlank { "#1F6F64" },
                    trackingType = trackingType,
                    frequency = frequency,
                    timesPerWeek = timesPerWeek.coerceIn(1, 7),
                    reminderHour = reminderHour.coerceIn(0, 23),
                    reminderMinute = reminderMinute.coerceIn(0, 59),
                    startDate = startDate,
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

    fun archiveTask(taskId: String, archived: Boolean = true) {
        val updated = getTasks().map { task ->
            if (task.id == taskId) task.copy(isArchived = archived) else task
        }
        saveTasks(updated)

        val selectedId = getSelectedTaskId()
        if (selectedId == taskId && archived) {
            val nextActive = updated.firstOrNull { !it.isArchived }?.id
            setSelectedTask(nextActive)
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
        if (date.isBefore(task.startDate)) return false
        return when (task.frequency) {
            TaskFrequency.DAILY -> true
            TaskFrequency.SELECTED_DAYS -> date.dayOfWeek.value in task.customDays
            TaskFrequency.TIMES_PER_WEEK -> true
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
        if (task.frequency == TaskFrequency.TIMES_PER_WEEK) {
            // For times-per-week habits we keep a simple day streak over completed days.
            return calculateDailyLikeStreak(task, fromDate)
        }
        return calculateDailyLikeStreak(task, fromDate)
    }

    private fun calculateDailyLikeStreak(task: HabitTask, fromDate: LocalDate): Int {
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
        if (task.frequency == TaskFrequency.TIMES_PER_WEEK) {
            var completed = 0
            for (offset in 0L until 30L) {
                val day = anchorDate.minusDays(offset)
                if (!day.isBefore(task.startDate) && isDone(task.id, day)) completed += 1
            }
            val target = ((task.timesPerWeek / 7f) * 30f).toInt().coerceAtLeast(1)
            return (completed * 100 / target).coerceIn(0, 100)
        }

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

    fun completionRate(task: HabitTask, days: Int, anchorDate: LocalDate = LocalDate.now()): Int {
        if (days <= 0) return 0
        if (task.frequency == TaskFrequency.TIMES_PER_WEEK) {
            var completed = 0
            for (offset in 0L until days.toLong()) {
                val day = anchorDate.minusDays(offset)
                if (!day.isBefore(task.startDate) && isDone(task.id, day)) completed += 1
            }
            val target = ceil((task.timesPerWeek / 7f) * days).toInt().coerceAtLeast(1)
            return (completed * 100 / target).coerceIn(0, 100)
        }

        var scheduled = 0
        var completed = 0
        for (offset in 0L until days.toLong()) {
            val day = anchorDate.minusDays(offset)
            if (isScheduledOn(task, day)) {
                scheduled += 1
                if (isDone(task.id, day)) completed += 1
            }
        }
        if (scheduled == 0) return 0
        return (completed * 100 / scheduled)
    }

    fun bestStreak(task: HabitTask, upToDate: LocalDate = LocalDate.now()): Int {
        var best = 0
        var current = 0
        var cursor = upToDate
        val earliest = task.startDate.minusDays(1)

        while (cursor.isAfter(earliest)) {
            if (!isScheduledOn(task, cursor)) {
                cursor = cursor.minusDays(1)
                continue
            }

            if (isDone(task.id, cursor)) {
                current += 1
                if (current > best) best = current
            } else {
                current = 0
            }
            cursor = cursor.minusDays(1)
        }
        return best
    }

    fun totalCompletions(task: HabitTask): Int {
        val prefix = "done_${task.id}_"
        return prefs.all.keys.count { it.startsWith(prefix) }
    }

    fun monthlyWeeklyProgress(task: HabitTask, month: YearMonth): List<Int> {
        val firstWeekday = month.atDay(1).dayOfWeek.value - 1
        val totalDays = month.lengthOfMonth()
        val weeks = ((firstWeekday + totalDays + 6) / 7).coerceAtLeast(4)
        val scheduled = IntArray(weeks)
        val done = IntArray(weeks)

        for (day in 1..totalDays) {
            val date = month.atDay(day)
            val weekIndex = (firstWeekday + day - 1) / 7
            if (isScheduledOn(task, date)) {
                scheduled[weekIndex] += 1
                if (isDone(task.id, date)) done[weekIndex] += 1
            }
        }

        return (0 until weeks).map { idx ->
            val sched = scheduled[idx]
            if (sched == 0) 0 else (done[idx] * 100 / sched).coerceIn(0, 100)
        }
    }

    fun weekdayConsistency(task: HabitTask, days: Int = 84, anchorDate: LocalDate = LocalDate.now()): List<Int> {
        val scheduled = IntArray(7)
        val done = IntArray(7)

        for (offset in 0L until days.toLong()) {
            val date = anchorDate.minusDays(offset)
            if (isScheduledOn(task, date)) {
                val index = date.dayOfWeek.value - 1
                scheduled[index] += 1
                if (isDone(task.id, date)) done[index] += 1
            }
        }

        return (0..6).map { idx ->
            val sched = scheduled[idx]
            if (sched == 0) 0 else (done[idx] * 100 / sched).coerceIn(0, 100)
        }
    }

    fun selectedTaskWidgetSummary(): Triple<String, Int, Int> {
        val tasks = getTasks().filterNot { it.isArchived }
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
                .put("emoji", task.emoji)
                .put("colorHex", task.colorHex)
                .put("trackingType", task.trackingType.name)
                .put("frequency", task.frequency.name)
                .put("timesPerWeek", task.timesPerWeek)
                .put("reminderHour", task.reminderHour)
                .put("reminderMinute", task.reminderMinute)
                .put("startDate", task.startDate.format(formatter))
                .put("isArchived", task.isArchived)
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
