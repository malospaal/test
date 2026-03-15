package com.example.microhabit

import android.content.Context
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.example.microhabit.data.HabitRepository
import com.example.microhabit.data.HabitTask
import com.example.microhabit.data.TaskFrequency
import com.example.microhabit.data.TrackingType
import com.example.microhabit.notifications.HabitReminderScheduler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MainViewModelGlobalCalendarTest {

    private lateinit var context: Context
    private lateinit var repository: HabitRepository
    private lateinit var reminderScheduler: HabitReminderScheduler

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences("habit_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        repository = HabitRepository(context)
        reminderScheduler = HabitReminderScheduler(context, repository)
    }

    @Test
    fun defaultScope_includesActiveAndCompleted_excludesArchived() {
        val targetDate = LocalDate.now().minusDays(1)
        val active = createTask(
            title = "Active",
            startDate = targetDate.minusDays(14)
        )
        val completed = createTask(
            title = "Completed",
            startDate = targetDate.minusDays(30),
            endDate = targetDate
        )
        val archived = createTask(
            title = "Archived",
            startDate = targetDate.minusDays(14),
            archived = true
        )

        repository.setDayValue(active.id, targetDate, 1)
        repository.setDayValue(completed.id, targetDate, 1)
        repository.setDayValue(archived.id, targetDate, 1)

        val vm = createViewModel()
        vm.selectDate(targetDate)

        val state = vm.state.value
        val filterIds = state.calendarFilterOptions.map { it.taskId }.toSet()
        val breakdownIds = state.calendarBreakdownItems.map { it.taskId }.toSet()

        assertTrue(filterIds.contains(active.id))
        assertTrue(filterIds.contains(completed.id))
        assertFalse(filterIds.contains(archived.id))
        assertTrue(breakdownIds.contains(active.id))
        assertTrue(breakdownIds.contains(completed.id))
        assertFalse(breakdownIds.contains(archived.id))
        assertEquals(2, state.calendarScheduledCountByDate[targetDate] ?: 0)
        assertEquals(2, state.calendarCompletedCountByDate[targetDate] ?: 0)
    }

    @Test
    fun heatmapCount_matchesCompletedIncludedHabitsForDay() {
        val targetDate = LocalDate.now().minusDays(2)
        val activeDone = createTask("Active done", startDate = targetDate.minusDays(10))
        val activeMissed = createTask("Active missed", startDate = targetDate.minusDays(10))
        val completedDone = createTask(
            title = "Completed done",
            startDate = targetDate.minusDays(20),
            endDate = targetDate
        )
        val archivedDone = createTask(
            title = "Archived done",
            startDate = targetDate.minusDays(10),
            archived = true
        )

        repository.setDayValue(activeDone.id, targetDate, 1)
        repository.setDayValue(activeMissed.id, targetDate, 0)
        repository.setDayValue(completedDone.id, targetDate, 1)
        repository.setDayValue(archivedDone.id, targetDate, 1)

        val vm = createViewModel()
        vm.selectDate(targetDate)

        val state = vm.state.value
        assertEquals(3, state.calendarScheduledCountByDate[targetDate] ?: 0)
        assertEquals(2, state.calendarCompletedCountByDate[targetDate] ?: 0)
    }

    @Test
    fun scheduleAwareCounting_respectsWindowAndFrequencyRules() {
        val targetDate = LocalDate.now().minusDays(3)
        val matchingDow = targetDate.dayOfWeek.value
        val nonMatchingDow = if (matchingDow == 7) 1 else matchingDow + 1

        val dailyScheduled = createTask(
            title = "Daily scheduled",
            startDate = targetDate.minusDays(20)
        )
        val selectedDaysScheduled = createTask(
            title = "Selected days scheduled",
            trackingType = TrackingType.COUNT,
            dailyTarget = 2,
            frequency = TaskFrequency.SELECTED_DAYS,
            customDays = setOf(matchingDow),
            startDate = targetDate.minusDays(20)
        )
        val weeklyScheduled = createTask(
            title = "Weekly scheduled",
            frequency = TaskFrequency.TIMES_PER_WEEK,
            timesPerWeek = 3,
            startDate = targetDate.minusDays(20)
        )
        val beforeStart = createTask(
            title = "Before start",
            startDate = targetDate.plusDays(1)
        )
        val afterEnd = createTask(
            title = "After end",
            startDate = targetDate.minusDays(20),
            endDate = targetDate.minusDays(1)
        )
        createTask(
            title = "Selected days not scheduled",
            frequency = TaskFrequency.SELECTED_DAYS,
            customDays = setOf(nonMatchingDow),
            startDate = targetDate.minusDays(20)
        )

        repository.setDayValue(dailyScheduled.id, targetDate, 1)
        repository.setDayValue(selectedDaysScheduled.id, targetDate, 1) // partial, still scheduled
        repository.setDayValue(weeklyScheduled.id, targetDate, 1)
        repository.setDayValue(beforeStart.id, targetDate, 1) // must not be counted
        repository.setDayValue(afterEnd.id, targetDate, 1) // must not be counted

        val vm = createViewModel()
        vm.selectDate(targetDate)

        val state = vm.state.value
        assertEquals(3, state.calendarScheduledCountByDate[targetDate] ?: 0)
        assertEquals(2, state.calendarCompletedCountByDate[targetDate] ?: 0)
    }

    @Test
    fun dayBreakdown_hasCorrectSummaryAndStatuses() {
        val targetDate = LocalDate.now().minusDays(4)
        val matchingDow = targetDate.dayOfWeek.value
        val nonMatchingDow = if (matchingDow == 7) 1 else matchingDow + 1

        val completed = createTask(
            title = "Completed",
            startDate = targetDate.minusDays(10)
        )
        val partial = createTask(
            title = "Partial",
            trackingType = TrackingType.COUNT,
            dailyTarget = 2,
            frequency = TaskFrequency.SELECTED_DAYS,
            customDays = setOf(matchingDow),
            startDate = targetDate.minusDays(10)
        )
        val missed = createTask(
            title = "Missed",
            startDate = targetDate.minusDays(10)
        )
        val notScheduled = createTask(
            title = "Not scheduled",
            frequency = TaskFrequency.SELECTED_DAYS,
            customDays = setOf(nonMatchingDow),
            startDate = targetDate.minusDays(10)
        )

        repository.setDayValue(completed.id, targetDate, 1)
        repository.setDayValue(partial.id, targetDate, 1)
        repository.setDayValue(missed.id, targetDate, 0)
        repository.setDayValue(notScheduled.id, targetDate, 1)

        val vm = createViewModel()
        vm.selectDate(targetDate)

        val state = vm.state.value
        val statusByTaskId = state.calendarBreakdownItems.associate { it.taskId to it.status }

        assertEquals(1, state.calendarBreakdownCompletedCount)
        assertEquals(3, state.calendarBreakdownScheduledCount)
        assertEquals(CalendarBreakdownStatus.COMPLETED, statusByTaskId[completed.id])
        assertEquals(CalendarBreakdownStatus.PARTIAL, statusByTaskId[partial.id])
        assertEquals(CalendarBreakdownStatus.MISSED, statusByTaskId[missed.id])
        assertEquals(CalendarBreakdownStatus.NOT_SCHEDULED, statusByTaskId[notScheduled.id])
    }

    @Test
    fun filters_allVsSingleHabit_affectHeatmapAndBreakdown() {
        val targetDate = LocalDate.now().minusDays(1)
        val first = createTask("First", startDate = targetDate.minusDays(8))
        val second = createTask("Second", startDate = targetDate.minusDays(8))

        repository.setDayValue(first.id, targetDate, 1)
        repository.setDayValue(second.id, targetDate, 0)

        val vm = createViewModel()
        vm.selectDate(targetDate)

        val allState = vm.state.value
        assertEquals(2, allState.calendarScheduledCountByDate[targetDate] ?: 0)
        assertEquals(1, allState.calendarCompletedCountByDate[targetDate] ?: 0)
        assertEquals(2, allState.calendarBreakdownItems.size)

        vm.setCalendarFilterTask(second.id)
        val filteredState = vm.state.value
        assertEquals(second.id, filteredState.calendarFilterTaskId)
        assertEquals(1, filteredState.calendarScheduledCountByDate[targetDate] ?: 0)
        assertEquals(0, filteredState.calendarCompletedCountByDate[targetDate] ?: 0)
        assertEquals(1, filteredState.calendarBreakdownItems.size)
        assertEquals(second.id, filteredState.calendarBreakdownItems.single().taskId)
    }

    @Test
    fun zeroScheduledDays_areModeledAsZeroScheduled_notMissed() {
        val targetDate = LocalDate.now().minusDays(20)
        val futureStart = createTask(
            title = "Starts later",
            startDate = LocalDate.now().minusDays(5)
        )
        val nonMatchingSelectedDay = createTask(
            title = "Mismatched day",
            frequency = TaskFrequency.SELECTED_DAYS,
            customDays = setOf(if (targetDate.dayOfWeek.value == 7) 1 else targetDate.dayOfWeek.value + 1),
            startDate = targetDate.minusDays(5)
        )

        repository.setDayValue(futureStart.id, targetDate, 1)
        repository.setDayValue(nonMatchingSelectedDay.id, targetDate, 1)

        val vm = createViewModel()
        vm.selectDate(targetDate)

        val state = vm.state.value
        assertEquals(0, state.calendarScheduledCountByDate[targetDate] ?: 0)
        assertEquals(0, state.calendarCompletedCountByDate[targetDate] ?: 0)
        assertTrue(state.calendarBreakdownItems.isNotEmpty())
        assertTrue(state.calendarBreakdownItems.all { it.status == CalendarBreakdownStatus.NOT_SCHEDULED })
        assertFalse(state.calendarBreakdownItems.any { it.status == CalendarBreakdownStatus.MISSED })
    }

    private fun createViewModel(): MainViewModel {
        val vm = MainViewModel(repository, reminderScheduler)
        waitForLoaded(vm)
        return vm
    }

    private fun waitForLoaded(vm: MainViewModel) {
        val deadline = System.currentTimeMillis() + 2_000
        while (System.currentTimeMillis() < deadline) {
            shadowOf(Looper.getMainLooper()).idle()
            if (vm.state.value.isLoaded) return
            Thread.sleep(10)
        }
        throw AssertionError("MainViewModel did not load within timeout")
    }

    private fun createTask(
        title: String,
        trackingType: TrackingType = TrackingType.YES_NO,
        dailyTarget: Int = 1,
        frequency: TaskFrequency = TaskFrequency.DAILY,
        customDays: Set<Int> = emptySet(),
        timesPerWeek: Int = 3,
        startDate: LocalDate,
        endDate: LocalDate? = null,
        archived: Boolean = false
    ): HabitTask {
        val task = repository.createTask(
            title = title,
            emoji = "✨",
            colorHex = "#1F6F64",
            trackingType = trackingType,
            dailyTarget = dailyTarget,
            unitLabel = if (trackingType == TrackingType.COUNT) "units" else "",
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
        return task
    }
}
