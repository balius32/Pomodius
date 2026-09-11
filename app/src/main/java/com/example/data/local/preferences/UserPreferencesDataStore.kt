package com.example.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "aura_user_preferences")

class UserPreferencesDataStore(private val context: Context) {

    private object PreferencesKeys {
        val FOCUS_DURATION = intPreferencesKey("focus_duration_minutes")
        val SHORT_BREAK_DURATION = intPreferencesKey("short_break_duration_minutes")
        val LONG_BREAK_DURATION = intPreferencesKey("long_break_duration_minutes")
        val SESSIONS_BEFORE_LONG = intPreferencesKey("sessions_before_long_break")
        val AUTO_START_BREAKS = booleanPreferencesKey("auto_start_breaks")
        val AUTO_START_FOCUS = booleanPreferencesKey("auto_start_focus")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val STANDBY_ON_LANDSCAPE = booleanPreferencesKey("standby_on_landscape")
        val KEEP_SCREEN_AWAKE = booleanPreferencesKey("keep_screen_awake")
        val OLED_PURE_BLACK = booleanPreferencesKey("oled_pure_black")
    }

    val userPreferences: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        UserPreferences(
            focusDurationMinutes = preferences[PreferencesKeys.FOCUS_DURATION] ?: 25,
            shortBreakDurationMinutes = preferences[PreferencesKeys.SHORT_BREAK_DURATION] ?: 5,
            longBreakDurationMinutes = preferences[PreferencesKeys.LONG_BREAK_DURATION] ?: 15,
            sessionsBeforeLongBreak = preferences[PreferencesKeys.SESSIONS_BEFORE_LONG] ?: 4,
            autoStartBreaks = preferences[PreferencesKeys.AUTO_START_BREAKS] ?: true,
            autoStartFocus = preferences[PreferencesKeys.AUTO_START_FOCUS] ?: false,
            soundEnabled = preferences[PreferencesKeys.SOUND_ENABLED] ?: true,
            vibrationEnabled = preferences[PreferencesKeys.VIBRATION_ENABLED] ?: true,
            themeMode = preferences[PreferencesKeys.THEME_MODE] ?: "LIGHT",
            standbyOnLandscape = preferences[PreferencesKeys.STANDBY_ON_LANDSCAPE] ?: true,
            keepScreenAwake = preferences[PreferencesKeys.KEEP_SCREEN_AWAKE] ?: true,
            oledPureBlack = preferences[PreferencesKeys.OLED_PURE_BLACK] ?: false
        )
    }

    suspend fun updateFocusDuration(minutes: Int) {
        context.dataStore.edit { it[PreferencesKeys.FOCUS_DURATION] = minutes }
    }

    suspend fun updateShortBreakDuration(minutes: Int) {
        context.dataStore.edit { it[PreferencesKeys.SHORT_BREAK_DURATION] = minutes }
    }

    suspend fun updateLongBreakDuration(minutes: Int) {
        context.dataStore.edit { it[PreferencesKeys.LONG_BREAK_DURATION] = minutes }
    }

    suspend fun updateSessionsBeforeLongBreak(count: Int) {
        context.dataStore.edit { it[PreferencesKeys.SESSIONS_BEFORE_LONG] = count }
    }

    suspend fun updateAutoStartBreaks(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.AUTO_START_BREAKS] = enabled }
    }

    suspend fun updateAutoStartFocus(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.AUTO_START_FOCUS] = enabled }
    }

    suspend fun updateSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SOUND_ENABLED] = enabled }
    }

    suspend fun updateVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.VIBRATION_ENABLED] = enabled }
    }

    suspend fun updateThemeMode(theme: String) {
        context.dataStore.edit { it[PreferencesKeys.THEME_MODE] = theme }
    }

    suspend fun updateStandbyOnLandscape(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.STANDBY_ON_LANDSCAPE] = enabled }
    }

    suspend fun updateKeepScreenAwake(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.KEEP_SCREEN_AWAKE] = enabled }
    }

    suspend fun updateOledPureBlack(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.OLED_PURE_BLACK] = enabled }
    }
}
