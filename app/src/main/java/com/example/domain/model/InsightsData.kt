package com.example.domain.model

data class DailyFocusMetric(
    val dayOfWeek: String, // "M", "T", "W", "T", "F", "S", "S"
    val dayName: String, // "Mon", "Tue", etc.
    val focusHours: Float,
    val isCurrentDay: Boolean = false
)

data class InsightsData(
    val todayFocusSeconds: Long = 0,
    val todayCompletedCycles: Int = 0,
    val targetCycles: Int = 5,
    val finishedTasksCount: Int = 0,
    val highPriorityFinishedCount: Int = 0,
    val currentStreakDays: Int = 0,
    val recordStreakDays: Int = 14,
    val weeklyFocusHours: Float = 0f,
    val weeklyTargetHours: Float = 20.0f,
    val weeklyMetrics: List<DailyFocusMetric> = emptyList(),
    val workRatio: Int = 70,
    val reviewRatio: Int = 20,
    val learnRatio: Int = 10,
    val flowAnchorTime: String = "09:00 - 11:30 AM",
    val efficiencyRating: Int = 94
)
