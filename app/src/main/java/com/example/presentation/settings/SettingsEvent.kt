package com.example.presentation.settings

sealed interface SettingsEvent {
    data class ChangeFocusDuration(val delta: Int) : SettingsEvent
    data class ChangeShortBreakDuration(val delta: Int) : SettingsEvent
    data class ChangeLongBreakDuration(val delta: Int) : SettingsEvent
    data class ChangeSessionsBeforeLongBreak(val delta: Int) : SettingsEvent
    data class ToggleAutoStartBreaks(val enabled: Boolean) : SettingsEvent
    data class ToggleAutoStartFocus(val enabled: Boolean) : SettingsEvent
    data class ToggleSound(val enabled: Boolean) : SettingsEvent
    data class ToggleVibration(val enabled: Boolean) : SettingsEvent
    data class ToggleStandbyOnLandscape(val enabled: Boolean) : SettingsEvent
    data class ToggleKeepScreenAwake(val enabled: Boolean) : SettingsEvent
    data class ToggleOledPureBlack(val enabled: Boolean) : SettingsEvent
    data object ResetDefaults : SettingsEvent
}
