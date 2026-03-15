package com.example.microhabit.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class HabitRepositoryCoreBehaviorTest {

    private lateinit var context: Context
    private lateinit var repository: HabitRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences("habit_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        repository = HabitRepository(context)
    }

    @Test
    fun scheduling_windowAndFrequency_rulesAreApplied() {
        val monday = LocalDate.now().minusDays((LocalDate.now().dayOfWeek.value - 1).toLong()).minusWeeks(1)
        val tuesday = monday.plusDays(1)
        val sunday = monday.plusDays(6)

        val daily = createTask(
            title = "Daily",
            frequency = TaskFrequency.DAILY,
            startDate = monday,
            endDate = sunday
        )
        val selectedDays = createTask(
            title = "Selected",
            frequency = TaskFrequency.SELECTED_DAYS,
            customDays = setOf(monday.dayOfWeek.value),
            startDate = monday,
            endDate = sunday
        )
        val weekly = createTask(
            title = "Weekly",
            frequency = TaskFrequency.TIMES_PER_WEEK,
            timesPerWeek = 3,
            startDate = monday,
            endDate = sunday
        )

        assertFalse(repository.isScheduledOn(daily, monday.minusDays(1)))
        assertTrue(repository.isScheduledOn(daily, monday))
        assertTrue(repository.isScheduledOn(daily, sunday))
        assertFalse(repository.isScheduledOn(daily, sunday.plusDays(1)))

        assertTrue(repository.isScheduledOn(selectedDays, monday))
        assertFalse(repository.isScheduledOn(selectedDays, tuesday))

        assertTrue(repository.isScheduledOn(weekly, tuesday))
        assertTrue(repository.isScheduledOn(weekly, sunday))
    }

    @Test
    fun lifecycle_activeCompletedArchived_areDistinct() {
        val today = LocalDate.now()
        val active = createTask("Active", startDate = today.minusDays(10), endDate = today.plusDays(2))
        val completed = createTask("Completed", startDate = today.minusDays(20), endDate = today.minusDays(1))
        val archived = createTask("Archived", startDate = today.minusDays(10), archived = true)

        assertEquals(HabitLifecycleState.ACTIVE, repository.lifecycleState(active, today))
        assertEquals(HabitLifecycleState.COMPLETED, repository.lifecycleState(completed, today))
        assertEquals(HabitLifecycleState.ARCHIVED, repository.lifecycleState(archived, today))
        assertFalse(repository.isHabitCompleted(archived, today))
    }

    @Test
    fun analytics_respectScheduleAndThreshold() {
        repository.setMinimumCompletionPercent(80)
        val anchor = LocalDate.now().minusDays(1)
        val countTask = createTask(
            title = "Count",
            trackingType = TrackingType.COUNT,
            dailyTarget = 10,
            unitLabel = "pages",
            startDate = anchor.minusDays(3),
            endDate = anchor
        )

        repository.setDayValue(countTask, anchor.minusDays(3), 10) // completed
        repository.setDayValue(countTask, anchor.minusDays(2), 8) // completed by threshold
        repository.setDayValue(countTask, anchor.minusDays(1), 7) // partial
        repository.setDayValue(countTask, anchor, 0) // missed
        repository.setDayValue(countTask, anchor.plusDays(1), 50) // outside endDate, must not affect schedule metrics

        assertEquals(50, repository.completionRate(countTask, 5, anchor))
        assertEquals(2, repository.totalCompletions(countTask))
        assertEquals(6, repository.averageTrackedValue(countTask, 4, anchor))
        assertEquals(88, repository.progressPercentForWidget(countTask, anchor.minusDays(1)))
        assertEquals(0, repository.progressPercentForWidget(countTask, anchor.plusDays(1)))
    }

    @Test
    fun completedPromptMarker_followsEndDateAndClearsOnEndDateUpdate() {
        val today = LocalDate.now()
        val endDate = today.minusDays(2)
        val task = createTask(
            title = "Prompt",
            startDate = today.minusDays(15),
            endDate = endDate
        )

        assertTrue(repository.shouldShowCompletedPrompt(task, today))
        repository.markCompletedPromptShown(task.id, endDate)

        val afterMark = repository.getTasks().first { it.id == task.id }
        assertFalse(repository.shouldShowCompletedPrompt(afterMark, today))

        val newEndDate = today.plusDays(5)
        repository.updateTaskEndDate(task.id, newEndDate)
        val updated = repository.getTasks().first { it.id == task.id }
        assertFalse(repository.shouldShowCompletedPrompt(updated, today))

        repository.updateTaskEndDate(task.id, today.minusDays(1))
        val completedAgain = repository.getTasks().first { it.id == task.id }
        assertTrue(repository.shouldShowCompletedPrompt(completedAgain, today))
    }

    @Test
    fun parsingCompatibility_supportsLegacyFrequencyAndLenientDate() {
        val prefs = context.getSharedPreferences("habit_prefs", Context.MODE_PRIVATE)
        val legacy = JSONArray()

        val weekdays = JSONObject()
            .put("id", "legacy_weekdays")
            .put("title", "Legacy weekdays")
            .put("trackingType", "YES_NO")
            .put("dailyTarget", 1)
            .put("frequency", "WEEKDAYS")
            .put("startDate", "2026-02-03T10:15:30")
            .put("isArchived", false)

        val custom = JSONObject()
            .put("id", "legacy_custom")
            .put("title", "Legacy custom")
            .put("trackingType", "YES_NO")
            .put("dailyTarget", 1)
            .put("frequency", "CUSTOM")
            .put("customDays", JSONArray().put(2).put(4))
            .put("startDate", "2026-01-05")
            .put("isArchived", false)

        legacy.put(weekdays)
        legacy.put(custom)
        prefs.edit().putString("tasks_json", legacy.toString()).commit()

        val tasks = repository.getTasks()
        val weekdaysTask = tasks.first { it.id == "legacy_weekdays" }
        val customTask = tasks.first { it.id == "legacy_custom" }

        assertEquals(TaskFrequency.SELECTED_DAYS, weekdaysTask.frequency)
        assertEquals(setOf(1, 2, 3, 4, 5), weekdaysTask.customDays)
        assertEquals(LocalDate.of(2026, 2, 3), weekdaysTask.startDate)

        assertEquals(TaskFrequency.SELECTED_DAYS, customTask.frequency)
        assertEquals(setOf(2, 4), customTask.customDays)
    }

    @Test
    fun parsingCompatibility_malformedTasksJson_returnsEmptyListSafely() {
        val prefs = context.getSharedPreferences("habit_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("tasks_json", "{not-valid-json").commit()

        val tasks = repository.getTasks()
        assertTrue(tasks.isEmpty())
    }

    @Test
    fun parsingCompatibility_missingOptionalAndUnknownFields_useSafeDefaults() {
        val prefs = context.getSharedPreferences("habit_prefs", Context.MODE_PRIVATE)
        val payload = JSONArray().put(
            JSONObject()
                .put("id", "minimal")
                .put("title", "Minimal task")
                .put("frequency", "DAILY")
                .put("startDate", "2026-03-01")
                .put("unknownField", "ignored")
        )
        prefs.edit().putString("tasks_json", payload.toString()).commit()

        val parsed = repository.getTasks().single()
        assertEquals("minimal", parsed.id)
        assertEquals("Minimal task", parsed.title)
        assertEquals("✨", parsed.emoji)
        assertEquals("#1F6F64", parsed.colorHex)
        assertEquals(TrackingType.YES_NO, parsed.trackingType)
        assertEquals(1, parsed.dailyTarget)
        assertEquals("", parsed.unitLabel)
        assertEquals(true, parsed.reminderEnabled)
        assertEquals(LocalDate.of(2026, 3, 1), parsed.startDate)
    }

    @Test
    fun parsingCompatibility_blankStartDate_fallsBackToEarliestProgressDate() {
        val prefs = context.getSharedPreferences("habit_prefs", Context.MODE_PRIVATE)
        val taskId = "legacy_without_start"
        val earliest = LocalDate.of(2025, 12, 31)
        val later = earliest.plusDays(2)
        val payload = JSONArray().put(
            JSONObject()
                .put("id", taskId)
                .put("title", "Legacy no start")
                .put("trackingType", "COUNT")
                .put("dailyTarget", 3)
                .put("frequency", "DAILY")
                .put("startDate", "")
                .put("endDate", "2026-01-10T05:00:00+02:00")
        )
        prefs.edit()
            .putString("tasks_json", payload.toString())
            .putBoolean("done_${taskId}_${later}", true)
            .putInt("value_${taskId}_${earliest}", 2)
            .commit()

        val parsed = repository.getTasks().single()
        assertEquals(earliest, parsed.startDate)
        assertEquals(LocalDate.of(2026, 1, 10), parsed.endDate)
    }

    private fun createTask(
        title: String,
        trackingType: TrackingType = TrackingType.YES_NO,
        dailyTarget: Int = 1,
        unitLabel: String = "",
        frequency: TaskFrequency = TaskFrequency.DAILY,
        customDays: Set<Int> = emptySet(),
        timesPerWeek: Int = 3,
        startDate: LocalDate = LocalDate.now().minusDays(20),
        endDate: LocalDate? = null,
        archived: Boolean = false
    ): HabitTask {
        val task = repository.createTask(
            title = title,
            emoji = "✨",
            colorHex = "#1F6F64",
            trackingType = trackingType,
            dailyTarget = dailyTarget,
            unitLabel = unitLabel,
            frequency = frequency,
            customDays = customDays,
            timesPerWeek = timesPerWeek,
            reminderHour = 8,
            reminderMinute = 0,
            reminderEnabled = false,
            startDate = startDate,
            endDate = endDate
        )
        if (archived) repository.archiveTask(task.id, true)
        return repository.getTasks().first { it.id == task.id }
    }
}
