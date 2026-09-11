package com.example.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.sound.SoundFeedbackManager
import com.example.domain.model.UserPreferences
import com.example.domain.repository.SettingsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val soundManager: SoundFeedbackManager
) : ViewModel() {

    private val _toast = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.preferences,
        _toast
    ) { prefs, toast ->
        SettingsUiState(
            preferences = prefs,
            toastMessage = toast
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun onEvent(event: SettingsEvent) {
        soundManager.playClickHaptic()
        val current = uiState.value.preferences
        viewModelScope.launch {
            when (event) {
                is SettingsEvent.ChangeFocusDuration -> {
                    val newVal = (current.focusDurationMinutes + event.delta).coerceIn(1, 90)
                    settingsRepository.updateFocusDuration(newVal)
                }
                is SettingsEvent.ChangeShortBreakDuration -> {
                    val newVal = (current.shortBreakDurationMinutes + event.delta).coerceIn(1, 30)
                    settingsRepository.updateShortBreakDuration(newVal)
                }
                is SettingsEvent.ChangeLongBreakDuration -> {
                    val newVal = (current.longBreakDurationMinutes + event.delta).coerceIn(1, 60)
                    settingsRepository.updateLongBreakDuration(newVal)
                }
                is SettingsEvent.ChangeSessionsBeforeLongBreak -> {
                    val newVal = (current.sessionsBeforeLongBreak + event.delta).coerceIn(1, 10)
                    settingsRepository.updateSessionsBeforeLongBreak(newVal)
                }
                is SettingsEvent.ToggleAutoStartBreaks -> settingsRepository.updateAutoStartBreaks(event.enabled)
                is SettingsEvent.ToggleAutoStartFocus -> settingsRepository.updateAutoStartFocus(event.enabled)
                is SettingsEvent.ToggleSound -> settingsRepository.updateSoundEnabled(event.enabled)
                is SettingsEvent.ToggleVibration -> settingsRepository.updateVibrationEnabled(event.enabled)
                is SettingsEvent.ToggleStandbyOnLandscape -> settingsRepository.updateStandbyOnLandscape(event.enabled)
                is SettingsEvent.ToggleKeepScreenAwake -> settingsRepository.updateKeepScreenAwake(event.enabled)
                is SettingsEvent.ToggleOledPureBlack -> settingsRepository.updateOledPureBlack(event.enabled)
                SettingsEvent.ResetDefaults -> {
                    settingsRepository.updateFocusDuration(25)
                    settingsRepository.updateShortBreakDuration(5)
                    settingsRepository.updateLongBreakDuration(15)
                    settingsRepository.updateSessionsBeforeLongBreak(4)
                    settingsRepository.updateAutoStartBreaks(true)
                    settingsRepository.updateAutoStartFocus(false)
                    settingsRepository.updateSoundEnabled(true)
                    settingsRepository.updateVibrationEnabled(true)
                    settingsRepository.updateStandbyOnLandscape(true)
                    settingsRepository.updateKeepScreenAwake(true)
                    settingsRepository.updateOledPureBlack(false)
                    _toast.value = "SETTINGS RESET TO FACTORY DEFAULTS"
                    delay(3000)
                    _toast.value = null
                }
            }
        }
    }
}
