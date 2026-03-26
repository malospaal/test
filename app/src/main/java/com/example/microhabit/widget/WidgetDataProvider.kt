package com.example.microhabit.widget

import android.content.Context
import com.example.microhabit.data.AppLanguage
import com.example.microhabit.data.HabitLifecycleState
import com.example.microhabit.data.HabitRepository
import com.example.microhabit.data.HabitTask
import com.example.microhabit.data.TrackingType
import com.example.microhabit.data.hasPremiumAccess
import com.example.microhabit.i18n.localeForLanguage
import com.example.microhabit.i18n.translate
import com.example.microhabit.i18n.weekdayLabels
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Locale

enum class DayStatus {
    DONE,
    MISSED,
    NOT_SCHEDULED,
    TODAY_PENDING,
    TODAY_DONE,
    FUTURE
}

data class WidgetHabitData(
    val habitId: String,
    val emoji: String,
    val title: String,
    val trackingType: TrackingType,
    val dailyTarget: Int,
    val todayValue: Int,
    val unitLabel: String,
    val currentStreak: Int,
    val bestStreak: Int,
    val isCompletedToday: Boolean,
    val last7Days: List<DayStatus>,
    val dayLabels: List<String>,
    val weekCompletionPct: Int,
    val isProUser: Boolean
)

object WidgetBindingStore {
    private const val PREFS_NAME = "widget_prefs"
    private const val KEY_PREFIX = "widget_habit_id_"

    fun getHabitId(context: Context, appWidgetId: Int): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString("$KEY_PREFIX$appWidgetId", null)
    }

    fun setHabitId(context: Context, appWidgetId: Int, habitId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString("$KEY_PREFIX$appWidgetId", habitId).commit()
    }

    fun clearHabitId(context: Context, appWidgetId: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove("$KEY_PREFIX$appWidgetId").commit()
    }
}

class WidgetDataProvider(
    private val context: Context,
    private val language: AppLanguage = HabitRepository(context).getLanguage(),
    private val locale: Locale = localeForLanguage(language)
) {
    private val repository = HabitRepository(context)

    fun isProUser(): Boolean = repository.getPlan().hasPremiumAccess()

    fun getActiveHabits(): List<HabitTask> {
        return repository.getTasks().filter { repository.lifecycleState(it) == HabitLifecycleState.ACTIVE }
    }

    fun defaultHabitId(): String? {
        val activeHabits = getActiveHabits()
        if (activeHabits.isEmpty()) return null
        val selectedId = repository.getSelectedTaskId()
        val resolved = activeHabits.firstOrNull { it.id == selectedId }?.id ?: activeHabits.first().id
        WidgetDebugLog.d(
            "defaultHabitId selectedId=$selectedId activeCount=${activeHabits.size} resolved=$resolved"
        )
        return resolved
    }

    fun getWidgetData(habitId: String?): WidgetHabitData {
        val activeHabits = getActiveHabits()
        val resolvedTask = activeHabits.firstOrNull { it.id == habitId }
            ?: activeHabits.firstOrNull()
            ?: return placeholderData()

        val today = LocalDate.now()
        val weekStart = today.with(DayOfWeek.MONDAY)
        val dayLabels = weekdayLabels(language).map { it.take(2) }

        val last7 = (0..6).map { offset ->
            val date = weekStart.plusDays(offset.toLong())
            when {
                date.isAfter(today) -> DayStatus.FUTURE
                !repository.isScheduledOn(resolvedTask, date) -> DayStatus.NOT_SCHEDULED
                repository.isCompletedOn(resolvedTask, date) -> {
                    if (date.isEqual(today)) DayStatus.TODAY_DONE else DayStatus.DONE
                }
                date.isEqual(today) -> DayStatus.TODAY_PENDING
                else -> DayStatus.MISSED
            }
        }

        val isScheduledToday = repository.isScheduledOn(resolvedTask, today)
        val completedToday = if (isScheduledToday) {
            repository.isCompletedOn(resolvedTask, today)
        } else {
            repository.getDayValue(resolvedTask, today) > 0
        }
        val scheduled = last7.count { status ->
            status != DayStatus.FUTURE && status != DayStatus.NOT_SCHEDULED
        }
        val completed = last7.count { status ->
            status == DayStatus.DONE || status == DayStatus.TODAY_DONE
        }
        val weekPct = if (scheduled > 0) (completed * 100 / scheduled).coerceIn(0, 100) else 0
        val resolvedUnitLabel = when (resolvedTask.trackingType) {
            TrackingType.DURATION -> translate(language, "min")
            TrackingType.COUNT -> repository.unitLabel(resolvedTask)
            TrackingType.YES_NO -> ""
        }
        WidgetDebugLog.d(
            "getWidgetData requestedHabitId=$habitId resolvedHabitId=${resolvedTask.id} " +
                "completedToday=$completedToday dayValueToday=${repository.getDayValue(resolvedTask, today)} " +
                "scheduledToday=$isScheduledToday weekPct=$weekPct"
        )

        return WidgetHabitData(
            habitId = resolvedTask.id,
            emoji = resolvedTask.emoji,
            title = resolvedTask.title,
            trackingType = resolvedTask.trackingType,
            dailyTarget = repository.dailyTarget(resolvedTask),
            todayValue = repository.getDayValue(resolvedTask, today),
            unitLabel = resolvedUnitLabel,
            currentStreak = repository.calculateStreak(resolvedTask, today),
            bestStreak = repository.bestStreak(resolvedTask, today),
            isCompletedToday = completedToday,
            last7Days = last7,
            dayLabels = dayLabels,
            weekCompletionPct = weekPct,
            isProUser = isProUser()
        )
    }

    private fun placeholderData(): WidgetHabitData {
        return WidgetHabitData(
            habitId = "",
            emoji = "✨",
            title = translate(language, "Micro-habit"),
            trackingType = TrackingType.YES_NO,
            dailyTarget = 1,
            todayValue = 0,
            unitLabel = "",
            currentStreak = 0,
            bestStreak = 0,
            isCompletedToday = false,
            last7Days = List(7) { DayStatus.NOT_SCHEDULED },
            dayLabels = weekdayLabels(language).map { it.take(2) },
            weekCompletionPct = 0,
            isProUser = isProUser()
        )
    }
}
