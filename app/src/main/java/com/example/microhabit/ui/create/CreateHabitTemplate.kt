package com.example.microhabit.ui.create

import com.example.microhabit.data.TaskFrequency
import com.example.microhabit.data.TrackingType
import java.time.LocalDate

enum class TemplateCategory {
    ALL,
    HEALTH,
    SPORT,
    MENTAL,
    PRODUCTIVITY
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
        TemplateCategory.PRODUCTIVITY
    )

    val templates: List<CreateHabitTemplate> = listOf(
        CreateHabitTemplate("drink_water", "💧", "tmpl_drink_water", TemplateCategory.HEALTH, TrackingType.COUNT, 8, "unit_cup", TaskFrequency.DAILY, emptySet(), "#1F6F64"),
        CreateHabitTemplate("vitamins", "💊", "tmpl_vitamins", TemplateCategory.HEALTH, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), "#378ADD"),
        CreateHabitTemplate("healthy_food", "🥗", "tmpl_healthy_food", TemplateCategory.HEALTH, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), "#2E8C63"),
        CreateHabitTemplate("no_sugar", "🌿", "tmpl_no_sugar", TemplateCategory.HEALTH, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), "#639922"),
        CreateHabitTemplate("sleep_early", "😴", "tmpl_sleep_early", TemplateCategory.HEALTH, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), "#534AB7"),

        CreateHabitTemplate("running", "🏃", "tmpl_running", TemplateCategory.SPORT, TrackingType.YES_NO, 1, "", TaskFrequency.SELECTED_DAYS, setOf(1, 2, 3, 4, 5), "#D85A30"),
        CreateHabitTemplate("workout", "🏋️", "tmpl_workout", TemplateCategory.SPORT, TrackingType.YES_NO, 1, "", TaskFrequency.SELECTED_DAYS, setOf(1, 2, 3, 4, 5), "#D85A30"),
        CreateHabitTemplate("cycling", "🚴", "tmpl_cycling", TemplateCategory.SPORT, TrackingType.DURATION, 30, "unit_min", TaskFrequency.DAILY, emptySet(), "#D85A30"),
        CreateHabitTemplate("stretching", "🤸", "tmpl_stretching", TemplateCategory.SPORT, TrackingType.DURATION, 10, "unit_min", TaskFrequency.DAILY, emptySet(), "#D85A30"),
        CreateHabitTemplate("steps", "👟", "tmpl_steps", TemplateCategory.SPORT, TrackingType.COUNT, 8000, "unit_steps", TaskFrequency.DAILY, emptySet(), "#D85A30"),

        CreateHabitTemplate("meditation", "🧘", "tmpl_meditation", TemplateCategory.MENTAL, TrackingType.DURATION, 10, "unit_min", TaskFrequency.DAILY, emptySet(), "#7F77DD"),
        CreateHabitTemplate("no_phone", "📵", "tmpl_no_phone", TemplateCategory.MENTAL, TrackingType.DURATION, 60, "unit_min", TaskFrequency.DAILY, emptySet(), "#7F77DD"),
        CreateHabitTemplate("gratitude", "🙏", "tmpl_gratitude", TemplateCategory.MENTAL, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), "#7F77DD"),

        CreateHabitTemplate("reading", "📚", "tmpl_reading", TemplateCategory.PRODUCTIVITY, TrackingType.DURATION, 30, "unit_min", TaskFrequency.DAILY, emptySet(), "#BA7517"),
        CreateHabitTemplate("journal", "✍️", "tmpl_journal", TemplateCategory.PRODUCTIVITY, TrackingType.YES_NO, 1, "", TaskFrequency.DAILY, emptySet(), "#BA7517"),
        CreateHabitTemplate("learning", "🎓", "tmpl_learning", TemplateCategory.PRODUCTIVITY, TrackingType.DURATION, 20, "unit_min", TaskFrequency.DAILY, emptySet(), "#BA7517")
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
    }
}
