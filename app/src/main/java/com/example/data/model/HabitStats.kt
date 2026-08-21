package com.example.data.model

data class HabitWithWeeklyStatus(
    val habit: Habit,
    val isCompletedToday: Boolean,
    val currentStreak: Int,
    val bestStreak: Int,
    val totalCompletions: Int,
    val weeklyHistory: List<Boolean> // 7 days status: [Day-6, Day-5, Day-4, Day-3, Day-2, Day-1, Today]
)

data class HeatmapDay(
    val dateString: String,
    val count: Int,
    val intensity: Int, // 0 to 4
    val isCurrentMonth: Boolean = true,
    val dayOfWeek: Int // 1 (Mon) to 7 (Sun)
)

data class AnalyticsSummary(
    val totalCheckIns: Int = 0,
    val currentBestStreak: Int = 0,
    val overallCompletionRate: Float = 0f,
    val perfectDaysCount: Int = 0,
    val categoryCounts: Map<String, Int> = emptyMap(),
    val timeOfDayCounts: Map<TimeOfDay, Int> = emptyMap(),
    val heatmapDays: List<HeatmapDay> = emptyList()
)
