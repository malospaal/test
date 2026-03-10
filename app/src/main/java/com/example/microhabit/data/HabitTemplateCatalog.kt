package com.example.microhabit.data

enum class HabitCategory {
    HEALTH,
    LEARNING,
    MINDFULNESS,
    PRODUCTIVITY,
    PERSONAL_GROWTH,
    CUSTOM
}

data class HabitTemplate(
    val id: String,
    val titleKey: String,
    val emoji: String,
    val frequency: TaskFrequency = TaskFrequency.DAILY,
    val customDays: Set<Int> = emptySet()
)

object HabitTemplateCatalog {
    val categories: List<HabitCategory> = listOf(
        HabitCategory.HEALTH,
        HabitCategory.LEARNING,
        HabitCategory.MINDFULNESS,
        HabitCategory.PRODUCTIVITY,
        HabitCategory.PERSONAL_GROWTH,
        HabitCategory.CUSTOM
    )

    fun categoryTitleKey(category: HabitCategory): String = when (category) {
        HabitCategory.HEALTH -> "Health"
        HabitCategory.LEARNING -> "Learning"
        HabitCategory.MINDFULNESS -> "Mindfulness"
        HabitCategory.PRODUCTIVITY -> "Productivity"
        HabitCategory.PERSONAL_GROWTH -> "Personal Growth"
        HabitCategory.CUSTOM -> "Custom habit"
    }

    fun defaultColorHex(category: HabitCategory): String = when (category) {
        HabitCategory.HEALTH -> "#1F6F64"
        HabitCategory.LEARNING -> "#3B7EA1"
        HabitCategory.MINDFULNESS -> "#7B6BC9"
        HabitCategory.PRODUCTIVITY -> "#3E8E5F"
        HabitCategory.PERSONAL_GROWTH -> "#B36A3C"
        HabitCategory.CUSTOM -> "#1F6F64"
    }

    fun templatesFor(category: HabitCategory): List<HabitTemplate> {
        val base = templatesByCategory[category].orEmpty()
        return if (base.any { it.id == CUSTOM_TEMPLATE.id }) base else base + CUSTOM_TEMPLATE
    }

    val CUSTOM_TEMPLATE: HabitTemplate = HabitTemplate(
        id = "custom",
        titleKey = "Custom habit",
        emoji = "✨"
    )

    private val templatesByCategory: Map<HabitCategory, List<HabitTemplate>> = mapOf(
        HabitCategory.HEALTH to listOf(
            HabitTemplate("health_water", "Drink water", "💧"),
            HabitTemplate("health_workout", "Workout", "🏋️"),
            HabitTemplate("health_stretch", "Stretch", "🧘"),
            HabitTemplate("health_walk", "Walk 10 minutes", "🚶"),
            HabitTemplate("health_vitamins", "Take vitamins", "💊")
        ),
        HabitCategory.LEARNING to listOf(
            HabitTemplate("learning_read", "Read 10 minutes", "📚"),
            HabitTemplate("learning_language", "Practice language", "🗣️"),
            HabitTemplate("learning_video", "Watch educational video", "🎓")
        ),
        HabitCategory.MINDFULNESS to listOf(
            HabitTemplate("mindfulness_meditate", "Meditate", "🧘"),
            HabitTemplate("mindfulness_journal", "Journal", "📓"),
            HabitTemplate("mindfulness_gratitude", "Gratitude", "🙏")
        ),
        HabitCategory.PRODUCTIVITY to listOf(
            HabitTemplate("productivity_plan", "Plan the day", "🗓️"),
            HabitTemplate("productivity_deep_work", "Deep work", "🎯"),
            HabitTemplate("productivity_cleanup", "Clean workspace", "🧹")
        ),
        HabitCategory.PERSONAL_GROWTH to listOf(
            HabitTemplate("growth_ideas", "Write ideas", "💡"),
            HabitTemplate("growth_goals", "Review goals", "🎯"),
            HabitTemplate("growth_reflect", "Reflect on day", "🌙")
        ),
        HabitCategory.CUSTOM to listOf(CUSTOM_TEMPLATE)
    )
}

