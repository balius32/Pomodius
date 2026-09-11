package com.example.domain.model

enum class PomodoroType(val defaultMinutes: Int, val label: String) {
    FOCUS(25, "FOCUS"),
    SHORT_BREAK(5, "SHORT"),
    LONG_BREAK(15, "LONG")
}
