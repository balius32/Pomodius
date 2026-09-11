package com.example.data.repository

import com.example.data.local.preferences.UserPreferencesDataStore
import com.example.domain.model.UserPreferences
import com.example.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val dataStore: UserPreferencesDataStore
) : SettingsRepository {

    override val preferences: Flow<UserPreferences> = dataStore.userPreferences

    override suspend fun updateFocusDuration(minutes: Int) {
        dataStore.updateFocusDuration(minutes)
    }

    override suspend fun updateShortBreakDuration(minutes: Int) {
        dataStore.updateShortBreakDuration(minutes)
    }

    override suspend fun updateLongBreakDuration(minutes: Int) {
        dataStore.updateLongBreakDuration(minutes)
    }

    override suspend fun updateSessionsBeforeLongBreak(count: Int) {
        dataStore.updateSessionsBeforeLongBreak(count)
    }

    override suspend fun updateAutoStartBreaks(enabled: Boolean) {
        dataStore.updateAutoStartBreaks(enabled)
    }

    override suspend fun updateAutoStartFocus(enabled: Boolean) {
        dataStore.updateAutoStartFocus(enabled)
    }

    override suspend fun updateSoundEnabled(enabled: Boolean) {
        dataStore.updateSoundEnabled(enabled)
    }

    override suspend fun updateVibrationEnabled(enabled: Boolean) {
        dataStore.updateVibrationEnabled(enabled)
    }

    override suspend fun updateThemeMode(theme: String) {
        dataStore.updateThemeMode(theme)
    }

    override suspend fun updateStandbyOnLandscape(enabled: Boolean) {
        dataStore.updateStandbyOnLandscape(enabled)
    }

    override suspend fun updateKeepScreenAwake(enabled: Boolean) {
        dataStore.updateKeepScreenAwake(enabled)
    }

    override suspend fun updateOledPureBlack(enabled: Boolean) {
        dataStore.updateOledPureBlack(enabled)
    }
}
