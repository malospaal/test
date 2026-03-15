package com.example.microhabit.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
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
class HabitRepositoryTrackingLogicTest {

    private lateinit var context: Context
    private lateinit var repository: HabitRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences("habit_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        repository = HabitRepository(context)
    }

    @Test
    fun threshold_defaultsTo100_andClampsToRange_1_to_100() {
        assertEquals(100, repository.getMinimumCompletionPercent())

        repository.setMinimumCompletionPercent(1)
        assertEquals(1, repository.getMinimumCompletionPercent())

        repository.setMinimumCompletionPercent(100)
        assertEquals(100, repository.getMinimumCompletionPercent())

        repository.setMinimumCompletionPercent(0)
        assertEquals(1, repository.getMinimumCompletionPercent())

        repository.setMinimumCompletionPercent(101)
        assertEquals(100, repository.getMinimumCompletionPercent())
    }

    @Test
    fun yesNo_isBinary_andHasNoPartial() {
        val task = createTask(trackingType = TrackingType.YES_NO, target = 1)
        val day = LocalDate.now().minusDays(1)

        repository.setDayValue(task, day, 0)
        assertFalse(repository.isCompletedOn(task, day))
        assertFalse(repository.isPartialOn(task, day))

        repository.setDayValue(task, day, 1)
        assertTrue(repository.isCompletedOn(task, day))
        assertFalse(repository.isPartialOn(task, day))
    }

    @Test
    fun count_usesThreshold_supportsPartial_andAllowsValueAboveTarget() {
        repository.setMinimumCompletionPercent(75)
        val task = createTask(
            trackingType = TrackingType.COUNT,
            target = 8,
            unitLabel = "glasses"
        )
        val day = LocalDate.now().minusDays(2)

        repository.setDayValue(task, day, 5)
        assertFalse(repository.isCompletedOn(task, day))
        assertTrue(repository.isPartialOn(task, day))

        repository.setDayValue(task, day, 6)
        assertTrue(repository.isCompletedOn(task, day))
        assertFalse(repository.isPartialOn(task, day))

        repository.setDayValue(task, day, 10)
        assertEquals(10, repository.getDayValue(task, day))
        assertTrue(repository.isCompletedOn(task, day))
        assertFalse(repository.isPartialOn(task, day))
    }

    @Test
    fun duration_usesThreshold_supportsPartial_andAllowsValueAboveTarget() {
        repository.setMinimumCompletionPercent(80)
        val task = createTask(trackingType = TrackingType.DURATION, target = 20)
        val day = LocalDate.now().minusDays(3)

        repository.setDayValue(task, day, 15)
        assertFalse(repository.isCompletedOn(task, day))
        assertTrue(repository.isPartialOn(task, day))

        repository.setDayValue(task, day, 16)
        assertTrue(repository.isCompletedOn(task, day))
        assertFalse(repository.isPartialOn(task, day))

        repository.setDayValue(task, day, 25)
        assertEquals(25, repository.getDayValue(task, day))
        assertTrue(repository.isCompletedOn(task, day))
    }

    @Test
    fun threshold_isSharedForCountAndDuration() {
        repository.setMinimumCompletionPercent(60)
        val countTask = createTask(trackingType = TrackingType.COUNT, target = 10, unitLabel = "pages")
        val durationTask = createTask(trackingType = TrackingType.DURATION, target = 20)
        val day = LocalDate.now().minusDays(4)

        repository.setDayValue(countTask, day, 6)
        repository.setDayValue(durationTask, day, 12)

        assertTrue(repository.isCompletedOn(countTask, day))
        assertTrue(repository.isCompletedOn(durationTask, day))
    }

    @Test
    fun pastDateValueEditing_updatesCompletedAndPartialStates() {
        repository.setMinimumCompletionPercent(80)
        val task = createTask(trackingType = TrackingType.COUNT, target = 10, unitLabel = "reps")
        val pastDay = LocalDate.now().minusDays(10)

        repository.setDayValue(task, pastDay, 7)
        assertFalse(repository.isCompletedOn(task, pastDay))
        assertTrue(repository.isPartialOn(task, pastDay))

        repository.setDayValue(task, pastDay, 8)
        assertTrue(repository.isCompletedOn(task, pastDay))
        assertFalse(repository.isPartialOn(task, pastDay))
    }

    @Test
    fun weeklyBestStreak_isHistoricalMaximum_notJustCurrentStreak() {
        val today = LocalDate.now()
        val currentWeekStart = today.minusDays((today.dayOfWeek.value - 1).toLong())
        val task = createTask(
            trackingType = TrackingType.YES_NO,
            target = 1,
            frequency = TaskFrequency.TIMES_PER_WEEK,
            timesPerWeek = 2,
            startDate = currentWeekStart.minusWeeks(8)
        )

        markWeekAsCompleted(task, currentWeekStart) // current run = 1
        markWeekAsCompleted(task, currentWeekStart.minusWeeks(2))
        markWeekAsCompleted(task, currentWeekStart.minusWeeks(3))
        markWeekAsCompleted(task, currentWeekStart.minusWeeks(4)) // historical run = 3

        assertEquals(1, repository.calculateStreak(task))
        assertEquals(3, repository.bestStreak(task))
        assertTrue(repository.streakHistory(task, limit = 4).contains(3))
    }

    @Test
    fun datesAfterEndDate_areNotScheduled_evenIfDateIsInFuture() {
        val today = LocalDate.now()
        val endDate = today.plusDays(2)
        val task = createTask(
            trackingType = TrackingType.YES_NO,
            target = 1,
            startDate = today.minusDays(14),
            endDate = endDate
        )
        val futureDateAfterEnd = endDate.plusDays(1)

        assertTrue(futureDateAfterEnd.isAfter(today))
        assertFalse(repository.isScheduledOn(task, futureDateAfterEnd))
    }

    @Test
    fun dailyStreak_todayIncomplete_doesNotResetCurrentStreakImmediately() {
        val today = LocalDate.now()
        val task = createTask(
            trackingType = TrackingType.YES_NO,
            target = 1,
            startDate = today.minusDays(7)
        )
        val yesterday = today.minusDays(1)

        repository.setDayValue(task, yesterday, 1)
        repository.setDayValue(task, today, 0)

        assertTrue(repository.isScheduledOn(task, today))
        assertFalse(repository.isCompletedOn(task, today))
        assertEquals(1, repository.calculateStreak(task))
    }

    @Test
    fun weeklyCompletionRate_handlesExactExceededAndNotReachedGoals() {
        val anchor = LocalDate.now().minusDays(1)
        val exact = createTask(
            trackingType = TrackingType.YES_NO,
            target = 1,
            frequency = TaskFrequency.TIMES_PER_WEEK,
            timesPerWeek = 2,
            startDate = anchor.minusDays(30)
        )
        val exceeded = createTask(
            trackingType = TrackingType.YES_NO,
            target = 1,
            frequency = TaskFrequency.TIMES_PER_WEEK,
            timesPerWeek = 2,
            startDate = anchor.minusDays(30)
        )
        val notReached = createTask(
            trackingType = TrackingType.YES_NO,
            target = 1,
            frequency = TaskFrequency.TIMES_PER_WEEK,
            timesPerWeek = 2,
            startDate = anchor.minusDays(30)
        )

        listOf(0L, 1L, 7L, 8L).forEach { offset ->
            repository.setDayValue(exact, anchor.minusDays(offset), 1)
        }
        listOf(0L, 1L, 2L, 7L, 8L, 9L).forEach { offset ->
            repository.setDayValue(exceeded, anchor.minusDays(offset), 1)
        }
        listOf(0L, 7L, 8L).forEach { offset ->
            repository.setDayValue(notReached, anchor.minusDays(offset), 1)
        }

        assertEquals(100, repository.completionRate(exact, 14, anchor))
        assertEquals(100, repository.completionRate(exceeded, 14, anchor))
        assertEquals(75, repository.completionRate(notReached, 14, anchor))
    }

    @Test
    fun weeklyStreak_respectsWeekBoundaries_andHistorySegments() {
        val today = LocalDate.now()
        val currentWeekStart = today.minusDays((today.dayOfWeek.value - 1).toLong())
        val task = createTask(
            trackingType = TrackingType.YES_NO,
            target = 1,
            frequency = TaskFrequency.TIMES_PER_WEEK,
            timesPerWeek = 2,
            startDate = currentWeekStart.minusWeeks(6)
        )

        repository.setDayValue(task, currentWeekStart, 1)
        repository.setDayValue(task, currentWeekStart.plusDays(1), 1)
        repository.setDayValue(task, currentWeekStart.minusWeeks(1), 1)
        repository.setDayValue(task, currentWeekStart.minusWeeks(1).plusDays(6), 1)
        repository.setDayValue(task, currentWeekStart.minusWeeks(2), 1) // break week
        repository.setDayValue(task, currentWeekStart.minusWeeks(3), 1)
        repository.setDayValue(task, currentWeekStart.minusWeeks(3).plusDays(1), 1)
        repository.setDayValue(task, currentWeekStart.minusWeeks(4), 1)
        repository.setDayValue(task, currentWeekStart.minusWeeks(4).plusDays(1), 1)

        val history = repository.streakHistory(task, limit = 5)
        assertEquals(2, repository.calculateStreak(task))
        assertEquals(2, repository.bestStreak(task))
        assertTrue(history.count { it == 2 } >= 2)
    }

    @Test
    fun streakSaver_yesterdayMiss_withoutSaverBreaks_andWithSaverKeepsRun() {
        val today = LocalDate.now()
        val task = createTask(
            trackingType = TrackingType.YES_NO,
            target = 1,
            frequency = TaskFrequency.DAILY,
            startDate = today.minusDays(7)
        )
        val yesterday = today.minusDays(1)
        val dayBeforeYesterday = today.minusDays(2)

        repository.setDayValue(task, dayBeforeYesterday, 1)
        repository.setDayValue(task, yesterday, 0)
        repository.setDayValue(task, today, 0)

        assertEquals(0, repository.calculateStreak(task))

        repository.addStreakSavers(task.id, 1)
        assertTrue(repository.consumeStreakSaver(task.id, yesterday))
        assertEquals(2, repository.calculateStreak(task))
    }

    @Test
    fun streakSaver_sameDateCannotBeAppliedTwice() {
        val today = LocalDate.now()
        val missedDate = today.minusDays(1)
        val task = createTask(
            trackingType = TrackingType.YES_NO,
            target = 1,
            frequency = TaskFrequency.DAILY,
            startDate = today.minusDays(10)
        )
        repository.addStreakSavers(task.id, 2)

        assertTrue(repository.consumeStreakSaver(task.id, missedDate))
        assertEquals(1, repository.getStreakSaverCount(task.id))
        assertFalse(repository.consumeStreakSaver(task.id, missedDate))
        assertEquals(1, repository.getStreakSaverCount(task.id))
    }

    private fun createTask(
        trackingType: TrackingType,
        target: Int,
        unitLabel: String = "",
        frequency: TaskFrequency = TaskFrequency.DAILY,
        timesPerWeek: Int = 3,
        startDate: LocalDate = LocalDate.now().minusDays(30),
        endDate: LocalDate? = null
    ): HabitTask {
        return repository.createTask(
            title = "Test ${trackingType.name}",
            emoji = "✨",
            colorHex = "#1F6F64",
            trackingType = trackingType,
            dailyTarget = target,
            unitLabel = unitLabel,
            frequency = frequency,
            customDays = emptySet(),
            timesPerWeek = timesPerWeek,
            reminderHour = 8,
            reminderMinute = 0,
            reminderEnabled = false,
            startDate = startDate,
            endDate = endDate
        )
    }

    private fun markWeekAsCompleted(task: HabitTask, weekStart: LocalDate) {
        repository.setDayValue(task, weekStart, 1)
        repository.setDayValue(task, weekStart.plusDays(1), 1)
    }
}
