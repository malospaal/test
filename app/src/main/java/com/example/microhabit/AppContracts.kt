package com.example.microhabit

import androidx.compose.animation.core.EaseInOutQuart
import androidx.compose.animation.core.Easing
import androidx.compose.ui.graphics.Color
import com.example.microhabit.data.HabitCategory
import com.example.microhabit.data.HabitTemplate
import com.example.microhabit.data.TaskFrequency
import com.example.microhabit.data.TrackingType
// ─── Tracker habit switch animation constants ────────────────────────────────
// Shared by HeroCard transition, calendar crossfade and page dots spring.
internal const val HABIT_TRANSITION_MS = 220
internal val HABIT_EASING: Easing = EaseInOutQuart
internal const val DOTS_SPRING_DAMPING = 0.7f
internal const val DOTS_SPRING_STIFFNESS = 500f
internal const val DEFAULT_REMINDER_HOUR = 8
internal const val DEFAULT_REMINDER_MINUTE = 0
// ──────────────────────────────────────────────────────────────────────────────

internal enum class AppPage {
    TRACKER,
    HABIT_DETAIL,
    HABITS,
    ANALYTICS,
    CALENDAR,
    PAYWALL,
    ACCOUNT,
    MANAGE_SUBSCRIPTION,
    SETTINGS
}

internal enum class PaywallTrigger {
    DEFAULT,
    HABIT_LIMIT,
    ANALYTICS,
    WIDGETS
}

internal enum class BillingCycle {
    MONTHLY,
    YEARLY,
    LIFETIME
}

internal const val PRODUCT_ID_MONTHLY = "micro_habit_pro_monthly"
internal const val PRODUCT_ID_YEARLY = "micro_habit_pro_yearly"
internal const val PRODUCT_ID_LIFETIME = "micro_habit_pro_lifetime"
const val EXTRA_OPEN_PAYWALL_TRIGGER = "extra_open_paywall_trigger"
const val EXTRA_PAYWALL_TRIGGER_WIDGETS = "WIDGETS"

internal enum class NotificationPermissionAction {
    SAVE_EDITOR_REMINDER,
    PICK_TEMPLATE_REMINDER_TIME
}

internal enum class DurationSheetMode {
    MANUAL,
    TIMER
}

internal enum class TimerUiState {
    IDLE,
    RUNNING,
    PAUSED
}

internal enum class OnboardingStep {
    WELCOME,
    CATEGORY,
    TEMPLATE,
    SETUP,
    READY
}

internal enum class SurfaceTone {
    PRIMARY,
    SECONDARY,
    TERTIARY
}

internal enum class ActionEmphasis {
    PRIMARY,
    SECONDARY,
    TERTIARY,
    DANGER
}


internal data class StreakOverlayModel(
    val streak: Int,
    val milestone: Boolean
)

internal data class StreakMilestoneTier(
    val days: Int,
    val badgeLabel: String,
    val headline: String,
    val message: String,
    val ctaLabel: String,
    val accentColor: Color,
    val backgroundColor: Color,
    val nextMilestoneDays: Int?,
    val nextMilestoneIcon: String
)

internal data class StreakMilestoneTierDefinition(
    val days: Int,
    val accentColor: Color,
    val backgroundColor: Color,
    val nextMilestoneDays: Int?,
    val nextMilestoneIcon: String
)

internal val streakMilestoneTierDefinitions = listOf(
    StreakMilestoneTierDefinition(1, Color(0xFF2DCF96), Color(0xFF0A2318), 3, "🎯"),
    StreakMilestoneTierDefinition(3, Color(0xFF2DCF96), Color(0xFF0A2318), 7, "🔥"),
    StreakMilestoneTierDefinition(7, Color(0xFFF59E42), Color(0xFF1F1008), 14, "⚡"),
    StreakMilestoneTierDefinition(14, Color(0xFFF59E42), Color(0xFF1F1008), 21, "⭐"),
    StreakMilestoneTierDefinition(21, Color(0xFFEAB308), Color(0xFF1A1200), 30, "🏅"),
    StreakMilestoneTierDefinition(30, Color(0xFFEAB308), Color(0xFF1A1200), 50, "💪"),
    StreakMilestoneTierDefinition(50, Color(0xFFFBBF24), Color(0xFF160E00), 66, "⚡"),
    StreakMilestoneTierDefinition(66, Color(0xFFFBBF24), Color(0xFF160E00), 100, "💎"),
    StreakMilestoneTierDefinition(100, Color(0xFFFBBF24), Color(0xFF160E00), 180, "🌟"),
    StreakMilestoneTierDefinition(180, Color(0xFFA78BFA), Color(0xFF0F0820), 365, "👑"),
    StreakMilestoneTierDefinition(365, Color(0xFFA78BFA), Color(0xFF0F0820), 500, "🔮"),
    StreakMilestoneTierDefinition(500, Color(0xFFC084FC), Color(0xFF0C0618), 1000, "∞"),
    StreakMilestoneTierDefinition(1000, Color(0xFFE879F9), Color(0xFF080818), null, "")
)

internal data class OnboardingHabitDraft(
    val name: String,
    val category: HabitCategory,
    val template: HabitTemplate,
    val trackingType: TrackingType = TrackingType.YES_NO,
    val dailyTarget: Int = 1,
    val unitLabel: String = "",
    val frequency: TaskFrequency,
    val customDays: Set<Int>,
    val reminderEnabled: Boolean,
    val reminderHour: Int,
    val reminderMinute: Int
)

