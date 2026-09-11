package com.example.domain.model

data class SessionMode(
    val type: PomodoroType = PomodoroType.FOCUS,
    val cycle: Int = 1
)
