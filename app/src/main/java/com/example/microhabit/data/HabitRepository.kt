package com.example.microhabit.data

import android.content.Context
import com.example.microhabit.widget.HabitWidgetProvider
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.ceil
import kotlin.math.roundToInt
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.time.YearMonth
import java.time.temporal.ChronoUnit

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

enum class HabitLifecycleState {
    ACTIVE,
    COMPLETED,
    ARCHIVED
}

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class AppLanguage(val label: String) {
    EN("English"),
    CS("Čeština"),
    DE("Deutsch"),
    FR("Français"),
    ES("Español"),
    IT("Italiano"),
    RU("Russian"),
    UK("Українська")
}

data class HabitTask(
    val id: String,
    val title: String,
    val emoji: String = "✨",
    val colorHex: String = "#1F6F64",
    val trackingType: TrackingType = TrackingType.YES_NO,
    val dailyTarget: Int = 1,
    val unitLabel: String = "",
    val frequency: TaskFrequency,
    val timesPerWeek: Int = 3,
    val reminderHour: Int = 8,
    val reminderMinute: Int = 0,
    val reminderEnabled: Boolean = true,
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate? = null,
    val customDays: Set<Int> = emptySet(),
    val isArchived: Boolean = false
)

data class StreakSegment(
    val length: Int
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
                val title = sanitizeTitle(obj.optString("title"))
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
                val target = sanitizeDailyTarget(
                    trackingType = trackingType,
                    target = obj.optInt("dailyTarget", defaultTargetForType(trackingType))
                )
                val unitLabel = sanitizeUnitLabel(
                    trackingType = trackingType,
                    unitLabel = obj.optString("unitLabel", "")
                )

                val custom = mutableSetOf<Int>()
                if (rawFrequency == "WEEKDAYS") {
                    custom += setOf(1, 2, 3, 4, 5)
                }
                val customJson = obj.optJSONArray("customDays") ?: JSONArray()
                for (j in 0 until customJson.length()) {
                    val day = customJson.optInt(j, -1)
                    if (day in 1..7) custom += day
                }

                val startDate = parseTaskStartDate(
                    taskId = id,
                    rawValue = obj.optString("startDate", "")
                )
                val endDate = obj.optString("endDate", "")
                    .takeIf { it.isNotBlank() && it != "null" }
                    ?.let { parseLocalDateLenient(it) }

                HabitTask(
                    id = id,
                    title = title,
                    emoji = obj.optString("emoji", "✨").ifBlank { "✨" },
                    colorHex = obj.optString("colorHex", "#1F6F64").ifBlank { "#1F6F64" },
                    trackingType = trackingType,
                    dailyTarget = target,
                    unitLabel = unitLabel,
                    frequency = frequency,
                    timesPerWeek = obj.optInt("timesPerWeek", 3).coerceIn(1, 7),
                    reminderHour = obj.optInt("reminderHour", 8).coerceIn(0, 23),
                    reminderMinute = obj.optInt("reminderMinute", 0).coerceIn(0, 59),
                    reminderEnabled = obj.optBoolean("reminderEnabled", true),
                    startDate = startDate,
                    endDate = endDate?.takeIf { !it.isBefore(startDate) },
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
        dailyTarget: Int,
        unitLabel: String,
        frequency: TaskFrequency,
        customDays: Set<Int>,
        timesPerWeek: Int,
        reminderHour: Int,
        reminderMinute: Int,
        reminderEnabled: Boolean,
        startDate: LocalDate,
        endDate: LocalDate?
    ): HabitTask {
        val normalizedEndDate = endDate?.takeIf { !it.isBefore(startDate) }
        val task = HabitTask(
            id = UUID.randomUUID().toString(),
            title = sanitizeTitle(title),
            emoji = emoji.ifBlank { "✨" },
            colorHex = colorHex.ifBlank { "#1F6F64" },
            trackingType = trackingType,
            dailyTarget = sanitizeDailyTarget(trackingType, dailyTarget),
            unitLabel = sanitizeUnitLabel(trackingType, unitLabel),
            frequency = frequency,
            timesPerWeek = timesPerWeek.coerceIn(1, 7),
            reminderHour = reminderHour.coerceIn(0, 23),
            reminderMinute = reminderMinute.coerceIn(0, 59),
            reminderEnabled = reminderEnabled,
            startDate = startDate,
            endDate = normalizedEndDate,
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
        dailyTarget: Int,
        unitLabel: String,
        frequency: TaskFrequency,
        customDays: Set<Int>,
        timesPerWeek: Int,
        reminderHour: Int,
        reminderMinute: Int,
        reminderEnabled: Boolean,
        startDate: LocalDate,
        endDate: LocalDate?
    ) {
        val updated = getTasks().map { task ->
            if (task.id == taskId) {
                val normalizedEndDate = endDate?.takeIf { !it.isBefore(startDate) }
                task.copy(
                    title = sanitizeTitle(title),
                    emoji = emoji.ifBlank { "✨" },
                    colorHex = colorHex.ifBlank { "#1F6F64" },
                    trackingType = trackingType,
                    dailyTarget = sanitizeDailyTarget(trackingType, dailyTarget),
                    unitLabel = sanitizeUnitLabel(trackingType, unitLabel),
                    frequency = frequency,
                    timesPerWeek = timesPerWeek.coerceIn(1, 7),
                    reminderHour = reminderHour.coerceIn(0, 23),
                    reminderMinute = reminderMinute.coerceIn(0, 59),
                    reminderEnabled = reminderEnabled,
                    startDate = startDate,
                    endDate = normalizedEndDate,
                    customDays = sanitizeCustomDays(customDays)
                )
            } else {
                task
            }
        }
        if (updated.any { it.id == taskId }) {
            clearCompletionPromptMarker(taskId)
        }
        saveTasks(updated)
    }

    fun deleteTask(taskId: String) {
        val updated = getTasks().filterNot { it.id == taskId }
        saveTasks(updated)

        prefs.all.keys
            .filter { it.startsWith("done_${taskId}_") }
            .forEach { key -> prefs.edit().remove(key).apply() }
        prefs.all.keys
            .filter { it.startsWith("${KEY_DAY_VALUE_PREFIX}${taskId}_") }
            .forEach { key -> prefs.edit().remove(key).apply() }
        prefs.edit().remove(noteKey(taskId)).apply()
        prefs.edit().remove(streakSaverKey(taskId)).apply()
        prefs.edit().remove(savedMissedDatesKey(taskId)).apply()
        prefs.edit().remove(completedPromptKey(taskId)).apply()

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
            val nextActive = updated.firstOrNull { isHabitActive(it) }?.id
            setSelectedTask(nextActive)
        } else if (!archived) {
            val hasSelectedActive = updated.any { it.id == selectedId && isHabitActive(it) }
            if (!hasSelectedActive) {
                setSelectedTask(taskId)
            }
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

    fun getNotificationsEnabled(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }

    fun getDefaultReminderHour(): Int {
        return prefs.getInt(KEY_DEFAULT_REMINDER_HOUR, 8).coerceIn(0, 23)
    }

    fun getDefaultReminderMinute(): Int {
        return prefs.getInt(KEY_DEFAULT_REMINDER_MINUTE, 0).coerceIn(0, 59)
    }

    fun setDefaultReminder(hour: Int, minute: Int) {
        prefs.edit()
            .putInt(KEY_DEFAULT_REMINDER_HOUR, hour.coerceIn(0, 23))
            .putInt(KEY_DEFAULT_REMINDER_MINUTE, minute.coerceIn(0, 59))
            .apply()
    }

    fun getMinimumCompletionPercent(): Int {
        return prefs.getInt(KEY_MIN_COMPLETION_PERCENT, DEFAULT_MINIMUM_COMPLETION_PERCENT)
            .coerceIn(1, 100)
    }

    fun setMinimumCompletionPercent(value: Int) {
        prefs.edit().putInt(KEY_MIN_COMPLETION_PERCENT, value.coerceIn(1, 100)).apply()
    }

    fun isOnboardingCompleted(): Boolean = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }

    fun exportData(): Result<String> = runCatching {
        val tasksArray = runCatching {
            JSONArray(prefs.getString(KEY_TASKS_JSON, "[]") ?: "[]")
        }.getOrDefault(JSONArray())

        val progress = JSONObject()
        prefs.all
            .filterKeys { it.startsWith("done_") || it.startsWith(KEY_DAY_VALUE_PREFIX) }
            .forEach { (key, value) ->
                when (value) {
                    is Boolean -> progress.put(key, value)
                    is Number -> progress.put(key, value.toInt())
                }
            }

        val payload = JSONObject()
            .put("exportedAt", LocalDateTime.now().toString())
            .put("plan", getPlan().name)
            .put("themeMode", getThemeMode().name)
            .put("language", getLanguage().name)
            .put("notificationsEnabled", getNotificationsEnabled())
            .put("defaultReminderHour", getDefaultReminderHour())
            .put("defaultReminderMinute", getDefaultReminderMinute())
            .put("minimumCompletionPercent", getMinimumCompletionPercent())
            .put("selectedTaskId", getSelectedTaskId())
            .put("tasks", tasksArray)
            .put("progress", progress)

        val file = File(context.filesDir, "micro_habit_export_${System.currentTimeMillis()}.json")
        file.writeText(payload.toString(2))
        file.absolutePath
    }

    fun resetProgress() {
        val doneKeys = prefs.all.keys.filter { it.startsWith("done_") }
        val valueKeys = prefs.all.keys.filter { it.startsWith(KEY_DAY_VALUE_PREFIX) }
        val editor = prefs.edit()
        doneKeys.forEach { editor.remove(it) }
        valueKeys.forEach { editor.remove(it) }
        editor.apply()
    }

    fun deleteAccount() {
        prefs.edit().clear().apply()
    }

    fun isScheduledOn(task: HabitTask, date: LocalDate): Boolean {
        if (date.isBefore(task.startDate)) return false
        val endDate = task.endDate
        if (endDate != null && date.isAfter(endDate)) return false
        return isScheduledByFrequency(task, date)
    }

    fun isHabitCompleted(task: HabitTask, today: LocalDate = LocalDate.now()): Boolean {
        if (task.isArchived) return false
        val endDate = task.endDate ?: return false
        return today.isAfter(endDate)
    }

    fun isHabitActive(task: HabitTask, today: LocalDate = LocalDate.now()): Boolean {
        if (task.isArchived) return false
        return !isHabitCompleted(task, today)
    }

    fun lifecycleState(task: HabitTask, today: LocalDate = LocalDate.now()): HabitLifecycleState {
        return when {
            task.isArchived -> HabitLifecycleState.ARCHIVED
            isHabitCompleted(task, today) -> HabitLifecycleState.COMPLETED
            else -> HabitLifecycleState.ACTIVE
        }
    }

    fun isScheduledByFrequency(task: HabitTask, date: LocalDate): Boolean {
        return when (task.frequency) {
            TaskFrequency.DAILY -> true
            TaskFrequency.SELECTED_DAYS -> date.dayOfWeek.value in task.customDays
            TaskFrequency.TIMES_PER_WEEK -> true
        }
    }

    fun dailyTarget(task: HabitTask): Int = sanitizeDailyTarget(task.trackingType, task.dailyTarget)

    fun unitLabel(task: HabitTask): String = sanitizeUnitLabel(task.trackingType, task.unitLabel)

    fun getDayValue(task: HabitTask, date: LocalDate): Int {
        val valueKey = valueKey(task.id, date)
        if (prefs.contains(valueKey)) {
            return prefs.getInt(valueKey, 0).coerceAtLeast(0)
        }
        return if (prefs.getBoolean(doneKey(task.id, date), false)) {
            defaultCompletedValue(task)
        } else {
            0
        }
    }

    fun setDayValue(task: HabitTask, date: LocalDate, value: Int) {
        val normalized = value.coerceAtLeast(0)
        val editor = prefs.edit()
        val valueKey = valueKey(task.id, date)
        if (normalized > 0) {
            editor.putInt(valueKey, normalized)
        } else {
            editor.remove(valueKey)
        }
        if (isCompletedByValue(task, normalized)) {
            editor.putBoolean(doneKey(task.id, date), true)
        } else {
            editor.remove(doneKey(task.id, date))
        }
        editor.apply()
    }

    fun setDayValue(taskId: String, date: LocalDate, value: Int) {
        val task = getTasks().firstOrNull { it.id == taskId } ?: return
        setDayValue(task, date, value)
    }

    fun addToDayValue(task: HabitTask, date: LocalDate, delta: Int): Int {
        val updated = (getDayValue(task, date) + delta).coerceAtLeast(0)
        setDayValue(task, date, updated)
        return updated
    }

    fun completionPercent(task: HabitTask, date: LocalDate): Int {
        if (!isScheduledOn(task, date)) return 0
        return completionPercentByValue(task, getDayValue(task, date))
    }

    fun progressPercentForWidget(task: HabitTask, date: LocalDate): Int {
        if (!isScheduledOn(task, date)) return 0
        val value = getDayValue(task, date)
        if (task.trackingType == TrackingType.YES_NO) {
            return if (value >= 1) 100 else 0
        }
        if (value <= 0) return 0
        if (isCompletedByValue(task, value)) return 100
        val threshold = when (task.trackingType) {
            TrackingType.COUNT, TrackingType.DURATION -> getMinimumCompletionPercent().coerceAtLeast(1)
            TrackingType.YES_NO -> 100
        }
        val raw = completionPercentByValue(task, value).coerceAtLeast(0)
        return ((raw.toFloat() / threshold.toFloat()) * 100f).roundToInt().coerceIn(0, 100)
    }

    fun isCompletedOn(task: HabitTask, date: LocalDate): Boolean {
        if (!isScheduledOn(task, date)) return false
        return isCompletedByValue(task, getDayValue(task, date))
    }

    fun isPartialOn(task: HabitTask, date: LocalDate): Boolean {
        if (!isScheduledOn(task, date)) return false
        if (task.trackingType == TrackingType.YES_NO) return false
        val value = getDayValue(task, date)
        return value > 0 && !isCompletedByValue(task, value)
    }

    fun isDone(taskId: String, date: LocalDate): Boolean {
        val task = getTasks().firstOrNull { it.id == taskId } ?: return false
        return isCompletedOn(task, date)
    }

    fun setDone(taskId: String, date: LocalDate, done: Boolean) {
        val task = getTasks().firstOrNull { it.id == taskId } ?: return
        setDayValue(task, date, if (done) defaultCompletedValue(task) else 0)
    }

    fun updateTaskStartDate(taskId: String, startDate: LocalDate) {
        val updated = getTasks().map { task ->
            if (task.id == taskId) {
                val adjustedEndDate = task.endDate?.takeIf { !it.isBefore(startDate) }
                task.copy(startDate = startDate, endDate = adjustedEndDate)
            } else task
        }
        clearCompletionPromptMarker(taskId)
        saveTasks(updated)
    }

    fun updateTaskEndDate(taskId: String, endDate: LocalDate?) {
        val updated = getTasks().map { task ->
            if (task.id == taskId) {
                task.copy(endDate = endDate?.takeIf { !it.isBefore(task.startDate) })
            } else task
        }
        clearCompletionPromptMarker(taskId)
        saveTasks(updated)
    }

    fun getTaskNote(taskId: String): String {
        return prefs.getString(noteKey(taskId), "")?.trim().orEmpty()
    }

    fun setTaskNote(taskId: String, note: String) {
        val normalized = note.trim().take(MAX_HABIT_NOTE_LENGTH)
        if (normalized.isBlank()) {
            prefs.edit().remove(noteKey(taskId)).apply()
        } else {
            prefs.edit().putString(noteKey(taskId), normalized).apply()
        }
    }

    fun getStreakSaverCount(taskId: String): Int {
        return prefs.getInt(streakSaverKey(taskId), 0).coerceAtLeast(0)
    }

    fun addStreakSavers(taskId: String, amount: Int) {
        if (amount <= 0) return
        val updated = (getStreakSaverCount(taskId) + amount).coerceAtLeast(0)
        prefs.edit().putInt(streakSaverKey(taskId), updated).apply()
    }

    fun consumeStreakSaver(taskId: String, missedDate: LocalDate): Boolean {
        val current = getStreakSaverCount(taskId)
        if (current <= 0) return false
        val saved = getSavedMissedDates(taskId)
        if (missedDate in saved) return false
        val updated = saved.toMutableSet().apply { add(missedDate) }
        setSavedMissedDates(taskId, updated)
        prefs.edit().putInt(streakSaverKey(taskId), (current - 1).coerceAtLeast(0)).apply()
        return true
    }

    fun isMissedDaySaved(taskId: String, date: LocalDate): Boolean {
        return date in getSavedMissedDates(taskId)
    }

    fun calculateStreak(task: HabitTask, fromDate: LocalDate = LocalDate.now()): Int {
        if (task.frequency == TaskFrequency.TIMES_PER_WEEK) {
            return calculateWeeklyStreak(task, fromDate)
        }
        return calculateDailyLikeStreak(task, fromDate)
    }

    private fun calculateDailyLikeStreak(task: HabitTask, fromDate: LocalDate): Int {
        var streak = 0
        var cursor = fromDate
        val today = LocalDate.now()

        repeat(3650) {
            val scheduled = isScheduledOn(task, cursor)
            if (scheduled) {
                if (isCompletedOn(task, cursor) || isMissedDaySaved(task.id, cursor)) {
                    streak++
                } else if (cursor != today) {
                    return streak
                }
            }
            cursor = cursor.minusDays(1)
            if (cursor.isBefore(task.startDate)) return streak
        }
        return streak
    }

    private fun calculateWeeklyStreak(task: HabitTask, fromDate: LocalDate): Int {
        var streak = 0
        var currentWeekStart = fromDate.minusDays((fromDate.dayOfWeek.value - 1).toLong())
        val today = LocalDate.now()
        val thisWeekStart = today.minusDays((today.dayOfWeek.value - 1).toLong())

        repeat(520) {
            var completionsInWeek = 0
            for (i in 0..6) {
                val day = currentWeekStart.plusDays(i.toLong())
                if (day.isAfter(today)) continue
                if (isCompletedOn(task, day)) completionsInWeek++
            }

            if (completionsInWeek >= task.timesPerWeek) {
                streak++
            } else if (currentWeekStart != thisWeekStart) {
                return streak
            }
            
            currentWeekStart = currentWeekStart.minusWeeks(1)
            if (currentWeekStart.plusDays(6).isBefore(task.startDate)) return streak
        }
        return streak
    }

    fun last7Days(task: HabitTask, anchorDate: LocalDate = LocalDate.now()): List<Int> {
        return (6L downTo 0L).map { offset ->
            val day = anchorDate.minusDays(offset)
            progressPercentForWidget(task, day)
        }
    }

    fun progressForLast30Days(task: HabitTask, anchorDate: LocalDate = LocalDate.now()): Int {
        return completionRate(task, 30, anchorDate)
    }

    fun completionRate(task: HabitTask, days: Int, anchorDate: LocalDate = LocalDate.now()): Int {
        if (days <= 0) return 0
        if (task.frequency == TaskFrequency.TIMES_PER_WEEK) {
            var completed = 0
            var effectiveDays = 0
            for (offset in 0L until days.toLong()) {
                val day = anchorDate.minusDays(offset)
                if (!isScheduledOn(task, day)) continue
                effectiveDays += 1
                if (isCompletedOn(task, day)) completed += 1
            }
            if (effectiveDays == 0) return 0
            val target = ceil((task.timesPerWeek / 7f) * effectiveDays).toInt().coerceAtLeast(1)
            return (completed * 100 / target).coerceIn(0, 100)
        }

        var scheduled = 0
        var completed = 0
        for (offset in 0L until days.toLong()) {
            val day = anchorDate.minusDays(offset)
            if (isScheduledOn(task, day)) {
                scheduled += 1
                if (isCompletedOn(task, day)) completed += 1
            }
        }
        if (scheduled == 0) return 0
        return (completed * 100 / scheduled)
    }

    fun bestStreak(task: HabitTask, upToDate: LocalDate = LocalDate.now()): Int {
        if (task.frequency == TaskFrequency.TIMES_PER_WEEK) {
            return weeklyStreakSegments(task, upToDate)
                .maxOfOrNull { it.length }
                ?: 0
        }
        var best = 0
        var current = 0
        var cursor = upToDate
        val earliest = task.startDate.minusDays(1)

        while (cursor.isAfter(earliest)) {
            if (!isScheduledOn(task, cursor)) {
                cursor = cursor.minusDays(1)
                continue
            }

            if (isCompletedOn(task, cursor)) {
                current += 1
                if (current > best) best = current
            } else if (isMissedDaySaved(task.id, cursor)) {
                current += 1
                if (current > best) best = current
            } else {
                current = 0
            }
            cursor = cursor.minusDays(1)
        }
        return best
    }

    fun streakHistory(task: HabitTask, limit: Int = 4, upToDate: LocalDate = LocalDate.now()): List<Int> {
        if (limit <= 0) return emptyList()
        val runs = if (task.frequency == TaskFrequency.TIMES_PER_WEEK) {
            weeklyStreakSegments(task, upToDate)
        } else {
            dailyLikeStreakSegments(task, upToDate)
        }
        return runs
            .map { it.length }
            .filter { it > 0 }
            .sortedDescending()
            .take(limit)
    }

    fun totalCompletions(task: HabitTask): Int {
        val today = LocalDate.now()
        val lastDate = minOf(today, task.endDate ?: today)
        if (lastDate.isBefore(task.startDate)) return 0
        var completed = 0
        var cursor = task.startDate
        while (!cursor.isAfter(lastDate)) {
            if (isCompletedOn(task, cursor)) {
                completed += 1
            }
            cursor = cursor.plusDays(1)
        }
        return completed
    }

    fun totalTrackedValue(task: HabitTask): Int {
        val prefix = "${KEY_DAY_VALUE_PREFIX}${task.id}_"
        return prefs.all
            .filterKeys { it.startsWith(prefix) }
            .values
            .sumOf { (it as? Number)?.toInt()?.coerceAtLeast(0) ?: 0 }
    }

    fun averageTrackedValue(task: HabitTask, days: Int = 30, anchorDate: LocalDate = LocalDate.now()): Int {
        if (days <= 0) return 0
        var activeDays = 0
        var total = 0
        for (offset in 0L until days.toLong()) {
            val day = anchorDate.minusDays(offset)
            if (isScheduledOn(task, day)) {
                activeDays += 1
                total += getDayValue(task, day)
            }
        }
        if (activeDays == 0) return 0
        return (total.toFloat() / activeDays.toFloat()).roundToInt()
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
                if (isCompletedOn(task, date)) done[weekIndex] += 1
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
                if (isCompletedOn(task, date)) done[index] += 1
            }
        }

        return (0..6).map { idx ->
            val sched = scheduled[idx]
            if (sched == 0) 0 else (done[idx] * 100 / sched).coerceIn(0, 100)
        }
    }

    fun selectedTaskWidgetSummary(): Triple<String, Int, Int> {
        val tasks = getTasks().filter { isHabitActive(it) }
        val selected = tasks.firstOrNull { it.id == getSelectedTaskId() } ?: tasks.firstOrNull()
        if (selected == null) return Triple("Create a habit", 0, 0)

        val streak = calculateStreak(selected)
        val progress = progressForLast30Days(selected)
        return Triple(selected.title, streak, progress)
    }

    fun refreshWidget() {
        HabitWidgetProvider.refreshAll(context)
    }

    private fun parseTaskStartDate(taskId: String, rawValue: String): LocalDate {
        val normalized = rawValue.trim()
        if (normalized.isNotEmpty()) {
            parseLocalDateLenient(normalized)?.let { return it }
        }
        return earliestCompletionDate(taskId) ?: LocalDate.now().minusYears(5)
    }

    private fun parseLocalDateLenient(rawValue: String): LocalDate? {
        return runCatching { LocalDate.parse(rawValue, formatter) }.getOrNull()
            ?: runCatching { LocalDate.parse(rawValue) }.getOrNull()
            ?: runCatching { LocalDateTime.parse(rawValue).toLocalDate() }.getOrNull()
            ?: runCatching { OffsetDateTime.parse(rawValue).toLocalDate() }.getOrNull()
    }

    private fun earliestCompletionDate(taskId: String): LocalDate? {
        val donePrefix = "done_${taskId}_"
        val valuePrefix = "${KEY_DAY_VALUE_PREFIX}${taskId}_"
        return prefs.all.keys.asSequence()
            .filter { key -> key.startsWith(donePrefix) || key.startsWith(valuePrefix) }
            .mapNotNull { key ->
                val rawDate = when {
                    key.startsWith(donePrefix) -> key.removePrefix(donePrefix)
                    key.startsWith(valuePrefix) -> key.removePrefix(valuePrefix)
                    else -> return@mapNotNull null
                }
                runCatching { LocalDate.parse(rawDate, formatter) }.getOrNull()
            }
            .minOrNull()
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
                .put("dailyTarget", task.dailyTarget)
                .put("unitLabel", task.unitLabel)
                .put("frequency", task.frequency.name)
                .put("timesPerWeek", task.timesPerWeek)
                .put("reminderHour", task.reminderHour)
                .put("reminderMinute", task.reminderMinute)
                .put("reminderEnabled", task.reminderEnabled)
                .put("startDate", task.startDate.format(formatter))
                .put("isArchived", task.isArchived)
            task.endDate?.let { obj.put("endDate", it.format(formatter)) }
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

    private fun sanitizeDailyTarget(trackingType: TrackingType, target: Int): Int {
        return when (trackingType) {
            TrackingType.YES_NO -> 1
            TrackingType.COUNT, TrackingType.DURATION -> target.coerceAtLeast(1)
        }
    }

    private fun sanitizeUnitLabel(trackingType: TrackingType, unitLabel: String): String {
        return when (trackingType) {
            TrackingType.YES_NO -> ""
            TrackingType.COUNT -> unitLabel.trim().take(20)
            TrackingType.DURATION -> "мин"
        }
    }

    private fun defaultTargetForType(trackingType: TrackingType): Int {
        return when (trackingType) {
            TrackingType.YES_NO -> 1
            TrackingType.COUNT -> 8
            TrackingType.DURATION -> 20
        }
    }

    private fun completionPercentByValue(task: HabitTask, value: Int): Int {
        val safeValue = value.coerceAtLeast(0)
        return when (task.trackingType) {
            TrackingType.YES_NO -> if (safeValue >= 1) 100 else 0
            TrackingType.COUNT, TrackingType.DURATION -> {
                val target = dailyTarget(task).coerceAtLeast(1)
                ((safeValue.toFloat() / target.toFloat()) * 100f).roundToInt().coerceAtLeast(0)
            }
        }
    }

    private fun isCompletedByValue(task: HabitTask, value: Int): Boolean {
        return when (task.trackingType) {
            TrackingType.YES_NO -> value >= 1
            TrackingType.COUNT, TrackingType.DURATION ->
                completionPercentByValue(task, value) >= getMinimumCompletionPercent()
        }
    }

    private fun defaultCompletedValue(task: HabitTask): Int {
        return when (task.trackingType) {
            TrackingType.YES_NO -> 1
            TrackingType.COUNT, TrackingType.DURATION -> dailyTarget(task)
        }
    }

    private fun sanitizeTitle(title: String): String {
        return title.trim().take(MAX_HABIT_TITLE_LENGTH)
    }

    private fun dailyLikeStreakSegments(task: HabitTask, upToDate: LocalDate): List<StreakSegment> {
        if (upToDate.isBefore(task.startDate)) return emptyList()
        val segments = mutableListOf<StreakSegment>()
        var cursor = task.startDate
        var currentRun = 0
        while (!cursor.isAfter(upToDate)) {
            if (isScheduledOn(task, cursor)) {
                if (isCompletedOn(task, cursor) || isMissedDaySaved(task.id, cursor)) {
                    currentRun += 1
                } else if (currentRun > 0) {
                    segments += StreakSegment(currentRun)
                    currentRun = 0
                }
            }
            cursor = cursor.plusDays(1)
        }
        if (currentRun > 0) {
            segments += StreakSegment(currentRun)
        }
        return segments
    }

    private fun weeklyStreakSegments(task: HabitTask, upToDate: LocalDate): List<StreakSegment> {
        val segments = mutableListOf<StreakSegment>()
        var currentRun = 0
        var weekStart = task.startDate.minusDays((task.startDate.dayOfWeek.value - 1).toLong())
        val lastWeekStart = upToDate.minusDays((upToDate.dayOfWeek.value - 1).toLong())
        while (!weekStart.isAfter(lastWeekStart)) {
            var completionsInWeek = 0
            for (i in 0..6) {
                val day = weekStart.plusDays(i.toLong())
                if (day.isBefore(task.startDate) || day.isAfter(upToDate)) continue
                if (isCompletedOn(task, day)) completionsInWeek += 1
            }
            if (completionsInWeek >= task.timesPerWeek) {
                currentRun += 1
            } else if (currentRun > 0) {
                segments += StreakSegment(currentRun)
                currentRun = 0
            }
            weekStart = weekStart.plusWeeks(1)
        }
        if (currentRun > 0) {
            segments += StreakSegment(currentRun)
        }
        return segments
    }

    private fun doneKey(taskId: String, date: LocalDate): String {
        return "done_${taskId}_${date.format(formatter)}"
    }

    private fun valueKey(taskId: String, date: LocalDate): String {
        return "${KEY_DAY_VALUE_PREFIX}${taskId}_${date.format(formatter)}"
    }

    private fun noteKey(taskId: String): String = "${KEY_NOTE_PREFIX}${taskId}"
    private fun streakSaverKey(taskId: String): String = "${KEY_STREAK_SAVER_PREFIX}${taskId}"
    private fun savedMissedDatesKey(taskId: String): String = "${KEY_SAVED_MISSED_DATES_PREFIX}${taskId}"
    private fun completedPromptKey(taskId: String): String = "${KEY_COMPLETED_PROMPT_PREFIX}${taskId}"

    fun shouldShowCompletedPrompt(task: HabitTask, today: LocalDate = LocalDate.now()): Boolean {
        if (!isHabitCompleted(task, today)) return false
        val endDate = task.endDate ?: return false
        val marker = prefs.getString(completedPromptKey(task.id), null)
        return marker != endDate.format(formatter)
    }

    fun markCompletedPromptShown(taskId: String, endDate: LocalDate) {
        prefs.edit().putString(completedPromptKey(taskId), endDate.format(formatter)).apply()
    }

    fun clearCompletionPromptMarker(taskId: String) {
        prefs.edit().remove(completedPromptKey(taskId)).apply()
    }

    private fun getSavedMissedDates(taskId: String): Set<LocalDate> {
        val raw = prefs.getString(savedMissedDatesKey(taskId), null) ?: return emptySet()
        return runCatching {
            val array = JSONArray(raw)
            (0 until array.length()).mapNotNull { index ->
                val value = array.optString(index)
                runCatching { LocalDate.parse(value, formatter) }.getOrNull()
            }.toSet()
        }.getOrDefault(emptySet())
    }

    private fun setSavedMissedDates(taskId: String, dates: Set<LocalDate>) {
        if (dates.isEmpty()) {
            prefs.edit().remove(savedMissedDatesKey(taskId)).apply()
            return
        }
        val array = JSONArray()
        dates.sorted().forEach { date -> array.put(date.format(formatter)) }
        prefs.edit().putString(savedMissedDatesKey(taskId), array.toString()).apply()
    }

    companion object {
        private const val KEY_TASKS_JSON = "tasks_json"
        private const val KEY_SELECTED_TASK = "selected_task"
        private const val KEY_PLAN = "user_plan"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_LANGUAGE = "app_language"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_DEFAULT_REMINDER_HOUR = "default_reminder_hour"
        private const val KEY_DEFAULT_REMINDER_MINUTE = "default_reminder_minute"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_MIN_COMPLETION_PERCENT = "minimum_completion_percent"
        private const val KEY_DAY_VALUE_PREFIX = "value_"
        private const val KEY_NOTE_PREFIX = "habit_note_"
        private const val KEY_STREAK_SAVER_PREFIX = "streak_saver_"
        private const val KEY_SAVED_MISSED_DATES_PREFIX = "saved_missed_dates_"
        private const val KEY_COMPLETED_PROMPT_PREFIX = "completed_prompt_"
        private const val DEFAULT_MINIMUM_COMPLETION_PERCENT = 100
        private const val MAX_HABIT_NOTE_LENGTH = 180
    }
}
