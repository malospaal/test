package com.example.microhabit

import android.content.Context
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.example.microhabit.data.HabitLifecycleState
import com.example.microhabit.data.HabitRepository
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
class MainViewModelPolicyTest {

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
    fun freePlan_canCreateTaskDependsOnlyOnActiveCount() {
        val today = LocalDate.now()
        val active = createTask("Active", startDate = today.minusDays(7))
        createTask("Completed", startDate = today.minusDays(20), endDate = today.minusDays(1))

        val vm = createViewModel()
        assertFalse(vm.canCreateTask())

        vm.archiveTask(active.id)
        waitUntil { repository.lifecycleState(repository.getTasks().first { it.id == active.id }) == HabitLifecycleState.ARCHIVED }
        assertTrue(vm.canCreateTask())
    }

    @Test
    fun unarchiveTask_isBlockedOnFree_whenWouldBecomeActiveAtLimit() {
        val today = LocalDate.now()
        createTask("Active", startDate = today.minusDays(7))
        val archived = createTask("Archived candidate", startDate = today.minusDays(7), archived = true)

        val vm = createViewModel()
        val result = vm.unarchiveTask(archived.id)

        assertFalse(result)
        val reloaded = repository.getTasks().first { it.id == archived.id }
        assertEquals(HabitLifecycleState.ARCHIVED, repository.lifecycleState(reloaded))
    }

    @Test
    fun unarchiveTask_isAllowedOnFree_whenTaskRemainsCompleted() {
        val today = LocalDate.now()
        createTask("Active", startDate = today.minusDays(7))
        val archivedCompleted = createTask(
            title = "Archived completed",
            startDate = today.minusDays(30),
            endDate = today.minusDays(3),
            archived = true
        )

        val vm = createViewModel()
        val result = vm.unarchiveTask(archivedCompleted.id)
        waitUntil { repository.lifecycleState(repository.getTasks().first { it.id == archivedCompleted.id }) == HabitLifecycleState.COMPLETED }

        assertTrue(result)
        val updated = repository.getTasks().first { it.id == archivedCompleted.id }
        assertEquals(HabitLifecycleState.COMPLETED, repository.lifecycleState(updated))
    }

    @Test
    fun continueCompletedHabitIndefinite_canReactivateEvenOnFreeLimit() {
        val today = LocalDate.now()
        createTask("Active", startDate = today.minusDays(7))
        val completed = createTask(
            title = "Completed to continue",
            startDate = today.minusDays(20),
            endDate = today.minusDays(1)
        )

        val vm = createViewModel()
        val promptId = vm.state.value.completedPromptTaskId
        assertEquals(completed.id, promptId)

        vm.continueCompletedHabitIndefinite()
        waitUntil { repository.getTasks().first { it.id == completed.id }.endDate == null }

        val updated = repository.getTasks().first { it.id == completed.id }
        assertEquals(null, updated.endDate)
        val activeCount = repository.getTasks().count { repository.lifecycleState(it) == HabitLifecycleState.ACTIVE }
        assertEquals(2, activeCount)
    }

    @Test
    fun continueCompletedHabitWithPastDate_isIgnored() {
        val today = LocalDate.now()
        val completed = createTask(
            title = "Completed to keep",
            startDate = today.minusDays(12),
            endDate = today.minusDays(1)
        )
        val vm = createViewModel()

        vm.continueCompletedHabitWithEndDate(today.minusDays(2))
        waitUntil { repository.getTasks().first { it.id == completed.id }.endDate == today.minusDays(1) }

        val unchanged = repository.getTasks().first { it.id == completed.id }
        assertEquals(today.minusDays(1), unchanged.endDate)
    }

    @Test
    fun continueCompletedHabitWithFutureEndDate_canReactivateOnFreeLimit() {
        val today = LocalDate.now()
        createTask("Active", startDate = today.minusDays(7))
        val completed = createTask(
            title = "Completed to extend",
            startDate = today.minusDays(30),
            endDate = today.minusDays(1)
        )
        val vm = createViewModel()
        val newEndDate = today.plusDays(10)

        vm.continueCompletedHabitWithEndDate(newEndDate)
        waitUntil { repository.getTasks().first { it.id == completed.id }.endDate == newEndDate }

        val updated = repository.getTasks().first { it.id == completed.id }
        assertEquals(newEndDate, updated.endDate)
        val activeCount = repository.getTasks().count { repository.lifecycleState(it) == HabitLifecycleState.ACTIVE }
        assertEquals(2, activeCount)
    }

    @Test
    fun streakSaver_isNotOfferedForWeeklyHabits() {
        val today = LocalDate.now()
        val weekly = createTask(
            title = "Weekly",
            frequency = TaskFrequency.TIMES_PER_WEEK,
            startDate = today.minusDays(20)
        )
        repository.addStreakSavers(weekly.id, 1)

        val vm = createViewModel()
        waitUntil { vm.state.value.selectedTaskId == weekly.id }

        assertEquals(null, vm.state.value.streakSaverMissedDate)
        assertFalse(vm.state.value.showStreakSaverDialog)
    }

    @Test
    fun streakSaver_onlyYesterdayMissCanBeAppliedFromViewModel() {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val olderMiss = today.minusDays(2)
        val daily = createTask("Daily", startDate = today.minusDays(15))
        repository.addStreakSavers(daily.id, 1)
        repository.setDayValue(daily.id, olderMiss, 0)
        repository.setDayValue(daily.id, yesterday, 1)

        val vm = createViewModel()
        vm.useStreakSaverForYesterday()
        waitUntil { repository.getStreakSaverCount(daily.id) == 1 }

        assertFalse(repository.isMissedDaySaved(daily.id, olderMiss))
        assertEquals(1, repository.getStreakSaverCount(daily.id))
    }

    @Test
    fun streakSaver_cannotBeConsumedTwiceThroughViewModelFlow() {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val dayBeforeYesterday = today.minusDays(2)
        val daily = createTask("Daily saver", startDate = today.minusDays(15))
        repository.setDayValue(daily.id, dayBeforeYesterday, 1)
        repository.setDayValue(daily.id, yesterday, 0)
        repository.addStreakSavers(daily.id, 1)

        val vm = createViewModel()
        waitUntil { vm.state.value.streakSaverMissedDate == yesterday }

        vm.useStreakSaverForYesterday()
        waitUntil { repository.getStreakSaverCount(daily.id) == 0 }
        assertTrue(repository.isMissedDaySaved(daily.id, yesterday))

        vm.useStreakSaverForYesterday()
        waitUntil { repository.getStreakSaverCount(daily.id) == 0 }
        assertEquals(0, repository.getStreakSaverCount(daily.id))
    }

    private fun createViewModel(): MainViewModel {
        val vm = MainViewModel(repository, reminderScheduler)
        waitUntil { vm.state.value.isLoaded }
        return vm
    }

    private fun waitUntil(predicate: () -> Boolean) {
        val deadline = System.currentTimeMillis() + 2_000
        while (System.currentTimeMillis() < deadline) {
            shadowOf(Looper.getMainLooper()).idle()
            if (predicate()) return
            Thread.sleep(10)
        }
        throw AssertionError("Condition was not met in time")
    }

    private fun createTask(
        title: String,
        trackingType: TrackingType = TrackingType.YES_NO,
        frequency: TaskFrequency = TaskFrequency.DAILY,
        startDate: LocalDate,
        endDate: LocalDate? = null,
        archived: Boolean = false
    ): com.example.microhabit.data.HabitTask {
        val task = repository.createTask(
            title = title,
            emoji = "✨",
            colorHex = "#1F6F64",
            trackingType = trackingType,
            dailyTarget = if (trackingType == TrackingType.YES_NO) 1 else 5,
            unitLabel = if (trackingType == TrackingType.COUNT) "times" else "",
            frequency = frequency,
            customDays = emptySet(),
            timesPerWeek = 3,
            reminderHour = 8,
            reminderMinute = 0,
            reminderEnabled = false,
            startDate = startDate,
            endDate = endDate
        )
        if (archived) {
            repository.archiveTask(task.id, true)
        }
        return repository.getTasks().first { it.id == task.id }
    }
}
