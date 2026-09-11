package com.example.domain.model

data class UserPreferences(
    val focusDurationMinutes: Int = 25,
    val shortBreakDurationMinutes: Int = 5,
    val longBreakDurationMinutes: Int = 15,
    val sessionsBeforeLongBreak: Int = 4,
    val autoStartBreaks: Boolean = true,
    val autoStartFocus: Boolean = false,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val themeMode: String = "LIGHT", // LIGHT, DARK, BRUTAL
    val standbyOnLandscape: Boolean = true,
    val keepScreenAwake: Boolean = true,
    val oledPureBlack: Boolean = false
)
