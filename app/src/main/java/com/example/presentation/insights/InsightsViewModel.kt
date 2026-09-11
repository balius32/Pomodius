package com.example.presentation.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.sound.SoundFeedbackManager
import com.example.domain.repository.PomodoroRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InsightsViewModel(
    private val pomodoroRepository: PomodoroRepository,
    private val soundManager: SoundFeedbackManager
) : ViewModel() {

    private val _toast = MutableStateFlow<String?>(null)

    val uiState: StateFlow<InsightsUiState> = combine(
        pomodoroRepository.getInsightsData(),
        _toast
    ) { insights, toast ->
        InsightsUiState(
            data = insights,
            isLoading = false,
            exportedToastMessage = toast
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InsightsUiState()
    )

    fun exportTelemetry() {
        soundManager.playClickHaptic()
        viewModelScope.launch {
            _toast.value = "TELEMETRY LOGS EXPORTED TO JSON"
            delay(3000)
            _toast.value = null
        }
    }
}
