package com.example.microhabit

import android.content.Context
import android.content.SharedPreferences

internal data class StreakMilestoneEvent(
    val habitId: String,
    val days: Int
)

internal object StreakMilestoneQueue {
    private const val PREFS_NAME = "habit_prefs"
    private const val PENDING_QUEUE_KEY = "milestone_pending_queue"
    private const val SHOWN_PREFIX = "milestone_shown_"

    private val milestoneDays = setOf(1, 3, 7, 14, 21, 30, 50, 66, 100, 180, 365, 500, 1000)

    fun milestoneSet(): Set<Int> = milestoneDays

    fun enqueueIfEligible(context: Context, habitId: String, days: Int): Boolean {
        if (habitId.isBlank() || days !in milestoneDays) return false
        val prefs = prefs(context)
        synchronized(this) {
            if (prefs.getBoolean(shownKey(habitId, days), false)) return false
            val queue = parseQueue(prefs.getString(PENDING_QUEUE_KEY, null))
            if (queue.any { it.habitId == habitId && it.days == days }) return false
            queue += StreakMilestoneEvent(habitId = habitId, days = days)
            prefs.edit().putString(PENDING_QUEUE_KEY, serializeQueue(queue)).apply()
            return true
        }
    }

    fun peekPending(context: Context): StreakMilestoneEvent? {
        val prefs = prefs(context)
        synchronized(this) {
            val queue = parseQueue(prefs.getString(PENDING_QUEUE_KEY, null))
            return queue.firstOrNull()
        }
    }

    fun markShownAndRemove(context: Context, event: StreakMilestoneEvent) {
        val prefs = prefs(context)
        synchronized(this) {
            val queue = parseQueue(prefs.getString(PENDING_QUEUE_KEY, null))
            val updated = queue.filterNot { it.habitId == event.habitId && it.days == event.days }
            prefs.edit()
                .putBoolean(shownKey(event.habitId, event.days), true)
                .putString(PENDING_QUEUE_KEY, serializeQueue(updated))
                .apply()
        }
    }

    private fun prefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun shownKey(habitId: String, days: Int): String = "${SHOWN_PREFIX}${habitId}_${days}"

    private fun parseQueue(raw: String?): MutableList<StreakMilestoneEvent> {
        if (raw.isNullOrBlank()) return mutableListOf()
        return raw.split(';')
            .mapNotNull { chunk ->
                val parts = chunk.split('|')
                if (parts.size != 2) return@mapNotNull null
                val days = parts[1].toIntOrNull() ?: return@mapNotNull null
                val habitId = parts[0]
                if (habitId.isBlank()) return@mapNotNull null
                StreakMilestoneEvent(habitId = habitId, days = days)
            }
            .toMutableList()
    }

    private fun serializeQueue(events: List<StreakMilestoneEvent>): String {
        if (events.isEmpty()) return ""
        return events.joinToString(separator = ";") { "${it.habitId}|${it.days}" }
    }
}
