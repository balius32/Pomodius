package com.example.domain.repository

import com.example.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val preferences: Flow<UserPreferences>
    suspend fun updateFocusDuration(minutes: Int)
    suspend fun updateShortBreakDuration(minutes: Int)
    suspend fun updateLongBreakDuration(minutes: Int)
    suspend fun updateSessionsBeforeLongBreak(count: Int)
    suspend fun updateAutoStartBreaks(enabled: Boolean)
    suspend fun updateAutoStartFocus(enabled: Boolean)
    suspend fun updateSoundEnabled(enabled: Boolean)
    suspend fun updateVibrationEnabled(enabled: Boolean)
    suspend fun updateThemeMode(theme: String)
    suspend fun updateStandbyOnLandscape(enabled: Boolean)
    suspend fun updateKeepScreenAwake(enabled: Boolean)
    suspend fun updateOledPureBlack(enabled: Boolean)
}
