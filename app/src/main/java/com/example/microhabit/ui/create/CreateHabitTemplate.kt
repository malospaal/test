package com.example.microhabit.ui.create

import com.example.microhabit.data.TaskFrequency
import com.example.microhabit.data.TrackingType
import java.time.LocalDate

enum class TemplateCategory(
    val id: String,
    val emoji: String
) {
    ALL("all", "✨"),
    HEALTH("health", "💊"),
    SPORT("sport", "🏃"),
    MENTAL("mental", "🧘"),
    PRODUCTIVITY("productivity", "📚"),
    NUTRITION("nutrition", "🥦"),
    FINANCE("finance", "💰"),
    CREATIVITY("creativity", "🎨"),
    RELATIONSHIPS("relationships", "🤝")
}

data class CreateHabitTemplate(
    val id: String,
    val emoji: String,
    val nameKey: String,
    val category: TemplateCategory,
    val trackingType: TrackingType,
    val dailyTarget: Int,
    val unitLabelKey: String,
    val frequency: TaskFrequency,
    val defaultDays: Set<Int>,
    val defaultTimesPerWeek: Int,
    val colorHex: String
)

data class TemplateConfirmDraft(
    val template: CreateHabitTemplate,
    val habitName: String,
    val dailyTarget: Int,
    val frequency: TaskFrequency,
    val customDays: Set<Int>,
    val timesPerWeek: Int,
    val startDate: LocalDate,
    val reminderEnabled: Boolean,
    val reminderHour: Int,
    val reminderMinute: Int
)

object CreateHabitTemplateCatalog {
    val categories: List<TemplateCategory> = listOf(
        TemplateCategory.ALL,
        TemplateCategory.HEALTH,
        TemplateCategory.SPORT,
        TemplateCategory.MENTAL,
        TemplateCategory.PRODUCTIVITY,
        TemplateCategory.NUTRITION,
        TemplateCategory.FINANCE,
        TemplateCategory.CREATIVITY,
        TemplateCategory.RELATIONSHIPS
    )

    val templates: List<CreateHabitTemplate> = listOf(
        CreateHabitTemplate("drink_water", "💧", "tmpl_drink_water", TemplateCategory.HEALTH, TrackingType.COUNT, 8, "unit_cup", TaskFrequency.DAILY, emptySet(), 1, "#1F6F64"),
        CreateHabitTemplate("vitamins", "💊", "tmpl_vitamins", TemplateCategory.HEALTH, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), 1, "#378ADD"),
        CreateHabitTemplate("healthy_food", "🥗", "tmpl_healthy_food", TemplateCategory.HEALTH, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), 1, "#2E8C63"),
        CreateHabitTemplate("no_sugar", "🌿", "tmpl_no_sugar", TemplateCategory.HEALTH, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), 1, "#639922"),
        CreateHabitTemplate("sleep_early", "😴", "tmpl_sleep_early", TemplateCategory.HEALTH, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), 1, "#534AB7"),
        CreateHabitTemplate("tmpl_floss", "🦷", "tmpl_floss", TemplateCategory.HEALTH, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), 1, "#2E8C63"),
        CreateHabitTemplate("tmpl_sunscreen", "🌞", "tmpl_sunscreen", TemplateCategory.HEALTH, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), 1, "#2E8C63"),
        CreateHabitTemplate("tmpl_posture", "🪑", "tmpl_posture", TemplateCategory.HEALTH, TrackingType.COUNT, 5, "", TaskFrequency.DAILY, emptySet(), 1, "#2E8C63"),
        CreateHabitTemplate("tmpl_cold_shower", "🚿", "tmpl_cold_shower", TemplateCategory.HEALTH, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), 1, "#2E8C63"),
        CreateHabitTemplate("tmpl_walk", "🚶", "tmpl_walk", TemplateCategory.HEALTH, TrackingType.DURATION, 30, "unit_min", TaskFrequency.DAILY, emptySet(), 1, "#2E8C63"),
        CreateHabitTemplate("tmpl_log_weight", "⚖️", "tmpl_log_weight", TemplateCategory.HEALTH, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), 1, "#2E8C63"),

        CreateHabitTemplate("running", "🏃", "tmpl_running", TemplateCategory.SPORT, TrackingType.YES_NO, 1, "", TaskFrequency.SELECTED_DAYS, setOf(1, 2, 3, 4, 5), 1, "#D85A30"),
        CreateHabitTemplate("workout", "🏋️", "tmpl_workout", TemplateCategory.SPORT, TrackingType.YES_NO, 1, "", TaskFrequency.SELECTED_DAYS, setOf(1, 2, 3, 4, 5), 1, "#D85A30"),
        CreateHabitTemplate("cycling", "🚴", "tmpl_cycling", TemplateCategory.SPORT, TrackingType.DURATION, 30, "unit_min", TaskFrequency.DAILY, emptySet(), 1, "#D85A30"),
        CreateHabitTemplate("stretching", "🤸", "tmpl_stretching", TemplateCategory.SPORT, TrackingType.DURATION, 10, "unit_min", TaskFrequency.DAILY, emptySet(), 1, "#D85A30"),
        CreateHabitTemplate("steps", "👟", "tmpl_steps", TemplateCategory.SPORT, TrackingType.COUNT, 8000, "unit_steps", TaskFrequency.DAILY, emptySet(), 1, "#D85A30"),
        CreateHabitTemplate("tmpl_yoga", "🧘‍♀️", "tmpl_yoga", TemplateCategory.SPORT, TrackingType.DURATION, 20, "unit_min", TaskFrequency.DAILY, emptySet(), 1, "#D85A30"),
        CreateHabitTemplate("tmpl_swimming", "🏊", "tmpl_swimming", TemplateCategory.SPORT, TrackingType.DURATION, 30, "unit_min", TaskFrequency.TIMES_PER_WEEK, emptySet(), 3, "#D85A30"),
        CreateHabitTemplate("tmpl_pushups", "💪", "tmpl_pushups", TemplateCategory.SPORT, TrackingType.COUNT, 20, "", TaskFrequency.DAILY, emptySet(), 1, "#D85A30"),
        CreateHabitTemplate("tmpl_pullups", "🏋️‍♂️", "tmpl_pullups", TemplateCategory.SPORT, TrackingType.COUNT, 10, "", TaskFrequency.TIMES_PER_WEEK, emptySet(), 3, "#D85A30"),

        CreateHabitTemplate("meditation", "🧘", "tmpl_meditation", TemplateCategory.MENTAL, TrackingType.DURATION, 10, "unit_min", TaskFrequency.DAILY, emptySet(), 1, "#7F77DD"),
        CreateHabitTemplate("no_phone", "📵", "tmpl_no_phone", TemplateCategory.MENTAL, TrackingType.DURATION, 60, "unit_min", TaskFrequency.DAILY, emptySet(), 1, "#7F77DD"),
        CreateHabitTemplate("gratitude", "🙏", "tmpl_gratitude", TemplateCategory.MENTAL, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), 1, "#7F77DD"),
        CreateHabitTemplate("tmpl_affirmations", "✨", "tmpl_affirmations", TemplateCategory.MENTAL, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), 1, "#7F77DD"),
        CreateHabitTemplate("tmpl_breathwork", "💨", "tmpl_breathwork", TemplateCategory.MENTAL, TrackingType.DURATION, 5, "unit_min", TaskFrequency.DAILY, emptySet(), 1, "#7F77DD"),
        CreateHabitTemplate("tmpl_mood_log", "🌈", "tmpl_mood_log", TemplateCategory.MENTAL, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), 1, "#7F77DD"),
        CreateHabitTemplate("tmpl_digital_detox", "🔕", "tmpl_digital_detox", TemplateCategory.MENTAL, TrackingType.DURATION, 60, "unit_min", TaskFrequency.DAILY, emptySet(), 1, "#7F77DD"),

        CreateHabitTemplate("reading", "📚", "tmpl_reading", TemplateCategory.PRODUCTIVITY, TrackingType.DURATION, 30, "unit_min", TaskFrequency.DAILY, emptySet(), 1, "#BA7517"),
        CreateHabitTemplate("journal", "✍️", "tmpl_journal", TemplateCategory.PRODUCTIVITY, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), 1, "#BA7517"),
        CreateHabitTemplate("learning", "🎓", "tmpl_learning", TemplateCategory.PRODUCTIVITY, TrackingType.DURATION, 20, "unit_min", TaskFrequency.DAILY, emptySet(), 1, "#BA7517"),
        CreateHabitTemplate("tmpl_deep_work", "🎯", "tmpl_deep_work", TemplateCategory.PRODUCTIVITY, TrackingType.DURATION, 90, "unit_min", TaskFrequency.DAILY, emptySet(), 1, "#BA7517"),
        CreateHabitTemplate("tmpl_language", "🗣️", "tmpl_language", TemplateCategory.PRODUCTIVITY, TrackingType.DURATION, 15, "unit_min", TaskFrequency.DAILY, emptySet(), 1, "#BA7517"),
        CreateHabitTemplate("tmpl_plan_day", "📅", "tmpl_plan_day", TemplateCategory.PRODUCTIVITY, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), 1, "#BA7517"),
        CreateHabitTemplate("tmpl_inbox_zero", "📬", "tmpl_inbox_zero", TemplateCategory.PRODUCTIVITY, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), 1, "#BA7517"),
        CreateHabitTemplate("tmpl_eat_frog", "⏰", "tmpl_eat_frog", TemplateCategory.PRODUCTIVITY, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), 1, "#BA7517"),
        CreateHabitTemplate("tmpl_online_course", "💻", "tmpl_online_course", TemplateCategory.PRODUCTIVITY, TrackingType.DURATION, 20, "unit_min", TaskFrequency.DAILY, emptySet(), 1, "#BA7517"),

        CreateHabitTemplate("tmpl_no_alcohol", "🍷", "tmpl_no_alcohol", TemplateCategory.NUTRITION, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), 1, "#2E8C63"),
        CreateHabitTemplate("tmpl_no_junk_food", "🍔", "tmpl_no_junk_food", TemplateCategory.NUTRITION, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), 1, "#2E8C63"),
        CreateHabitTemplate("tmpl_fruits_veggies", "🥦", "tmpl_fruits_veggies", TemplateCategory.NUTRITION, TrackingType.COUNT, 5, "", TaskFrequency.DAILY, emptySet(), 1, "#2E8C63"),
        CreateHabitTemplate("tmpl_no_caffeine", "☕", "tmpl_no_caffeine", TemplateCategory.NUTRITION, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), 1, "#2E8C63"),
        CreateHabitTemplate("tmpl_intermittent_fasting", "⏱️", "tmpl_intermittent_fasting", TemplateCategory.NUTRITION, TrackingType.DURATION, 960, "unit_min", TaskFrequency.DAILY, emptySet(), 1, "#2E8C63"),
        CreateHabitTemplate("tmpl_no_snacking", "🙅", "tmpl_no_snacking", TemplateCategory.NUTRITION, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), 1, "#2E8C63"),

        CreateHabitTemplate("tmpl_save_money", "🪙", "tmpl_save_money", TemplateCategory.FINANCE, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), 1, "#3E8E5F"),
        CreateHabitTemplate("tmpl_no_impulse_buy", "🛍️", "tmpl_no_impulse_buy", TemplateCategory.FINANCE, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), 1, "#3E8E5F"),
        CreateHabitTemplate("tmpl_track_expenses", "📊", "tmpl_track_expenses", TemplateCategory.FINANCE, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), 1, "#3E8E5F"),
        CreateHabitTemplate("tmpl_invest", "📈", "tmpl_invest", TemplateCategory.FINANCE, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), 1, "#3E8E5F"),
        CreateHabitTemplate("tmpl_budget_review", "💳", "tmpl_budget_review", TemplateCategory.FINANCE, TrackingType.YES_NO, 1, "", TaskFrequency.TIMES_PER_WEEK, emptySet(), 1, "#3E8E5F"),

        CreateHabitTemplate("tmpl_draw", "🎨", "tmpl_draw", TemplateCategory.CREATIVITY, TrackingType.DURATION, 20, "unit_min", TaskFrequency.DAILY, emptySet(), 1, "#C65C74"),
        CreateHabitTemplate("tmpl_music", "🎸", "tmpl_music", TemplateCategory.CREATIVITY, TrackingType.DURATION, 30, "unit_min", TaskFrequency.DAILY, emptySet(), 1, "#C65C74"),
        CreateHabitTemplate("tmpl_creative_writing", "📝", "tmpl_creative_writing", TemplateCategory.CREATIVITY, TrackingType.DURATION, 20, "unit_min", TaskFrequency.DAILY, emptySet(), 1, "#C65C74"),
        CreateHabitTemplate("tmpl_photography", "📷", "tmpl_photography", TemplateCategory.CREATIVITY, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), 1, "#C65C74"),
        CreateHabitTemplate("tmpl_diy_craft", "🔨", "tmpl_diy_craft", TemplateCategory.CREATIVITY, TrackingType.YES_NO, 1, "", TaskFrequency.TIMES_PER_WEEK, emptySet(), 3, "#C65C74"),

        CreateHabitTemplate("tmpl_call_family", "📞", "tmpl_call_family", TemplateCategory.RELATIONSHIPS, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), 1, "#B36A3C"),
        CreateHabitTemplate("tmpl_quality_time", "❤️", "tmpl_quality_time", TemplateCategory.RELATIONSHIPS, TrackingType.DURATION, 30, "unit_min", TaskFrequency.DAILY, emptySet(), 1, "#B36A3C"),
        CreateHabitTemplate("tmpl_no_social_media", "📵", "tmpl_no_social_media", TemplateCategory.RELATIONSHIPS, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), 1, "#B36A3C"),
        CreateHabitTemplate("tmpl_compliment", "😊", "tmpl_compliment", TemplateCategory.RELATIONSHIPS, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), 1, "#B36A3C"),
        CreateHabitTemplate("tmpl_acts_of_kindness", "🌟", "tmpl_acts_of_kindness", TemplateCategory.RELATIONSHIPS, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), 1, "#B36A3C")
    )

    fun templatesFor(category: TemplateCategory): List<CreateHabitTemplate> {
        return if (category == TemplateCategory.ALL) templates else templates.filter { it.category == category }
    }

    fun categoryLabelKey(category: TemplateCategory): String = when (category) {
        TemplateCategory.ALL -> "cat_all"
        TemplateCategory.HEALTH -> "cat_health"
        TemplateCategory.SPORT -> "cat_sport"
        TemplateCategory.MENTAL -> "cat_mental"
        TemplateCategory.PRODUCTIVITY -> "cat_productivity"
        TemplateCategory.NUTRITION -> "category_nutrition"
        TemplateCategory.FINANCE -> "category_finance"
        TemplateCategory.CREATIVITY -> "category_creativity"
        TemplateCategory.RELATIONSHIPS -> "category_relationships"
    }
}
