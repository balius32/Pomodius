package com.example.domain.model

data class PomodoroSession(
    val id: Long = 0,
    val taskId: Long? = null,
    val taskTitle: String? = null,
    val startTime: Long,
    val endTime: Long,
    val duration: Long, // in seconds
    val type: PomodoroType,
    val completed: Boolean
)
