package com.example.presentation.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.sound.SoundFeedbackManager
import com.example.domain.model.PomodoroType
import com.example.domain.model.TimerState
import com.example.domain.repository.PomodoroRepository
import com.example.domain.repository.SettingsRepository
import com.example.domain.repository.TaskRepository
import com.example.domain.timer.PomodoroTimerEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FocusViewModel(
    private val timerEngine: PomodoroTimerEngine,
    private val taskRepository: TaskRepository,
    private val pomodoroRepository: PomodoroRepository,
    private val settingsRepository: SettingsRepository,
    private val soundManager: SoundFeedbackManager
) : ViewModel() {

    private val soundscapes = listOf("CYBERPUNK RAIN", "LO-FI CAFE", "WHITE NOISE", "DEEP ANALOG")
    private var soundscapeIndex = 0

    private val _isStandbyOpen = MutableStateFlow(false)
    private val _isTaskPickerOpen = MutableStateFlow(false)
    private val _showCelebration = MutableStateFlow(false)
    private val _soundscapeName = MutableStateFlow(soundscapes[0])
    private val _isSoundActive = MutableStateFlow(true)
    private val _lastCompletedDuration = MutableStateFlow(25 * 60L)

    init {
        // Observe timer completion events
        viewModelScope.launch {
            timerEngine.sessionCompletedEvent.collect { session ->
                _lastCompletedDuration.value = session.duration
                _showCelebration.value = true
                soundManager.playSessionCompleteSound()
                soundManager.playCompletionVibration()
            }
        }
    }

    val uiState: StateFlow<FocusUiState> = combine(
        combine(
            timerEngine.timerState,
            timerEngine.sessionMode,
            timerEngine.remainingSeconds,
            timerEngine.totalDurationSeconds,
            timerEngine.linkedTask,
            settingsRepository.preferences
        ) { args -> args },
        combine(
            pomodoroRepository.getInsightsData(),
            _isStandbyOpen,
            _isTaskPickerOpen,
            _showCelebration,
            _soundscapeName,
            _isSoundActive,
            taskRepository.getActiveTasks()
        ) { args -> args }
    ) { group1, group2 ->
        val timerState = group1[0] as TimerState
        val sessionMode = group1[1] as com.example.domain.model.SessionMode
        val remainingSeconds = group1[2] as Int
        val totalDurationSeconds = group1[3] as Int
        val linkedTask = group1[4] as? com.example.domain.model.Task
        val preferences = group1[5] as com.example.domain.model.UserPreferences

        val currentType = sessionMode.type
        val currentCycle = sessionMode.cycle

        val insights = group2[0] as com.example.domain.model.InsightsData
        val isStandbyOpen = group2[1] as Boolean
        val isTaskPickerOpen = group2[2] as Boolean
        val showCelebration = group2[3] as Boolean
        val soundscapeName = group2[4] as String
        val isSoundActive = group2[5] as Boolean
        @Suppress("UNCHECKED_CAST")
        val activeTasks = group2[6] as List<com.example.domain.model.Task>

        val progress = if (totalDurationSeconds > 0) {
            1f - (remainingSeconds.toFloat() / totalDurationSeconds.toFloat())
        } else 0f

        FocusUiState(
            timerState = timerState,
            currentType = currentType,
            remainingSeconds = remainingSeconds,
            totalDurationSeconds = totalDurationSeconds,
            progress = progress,
            currentCycle = currentCycle,
            maxCycles = preferences.sessionsBeforeLongBreak,
            activeTask = linkedTask,
            availableTasks = activeTasks,
            todayFocusSeconds = insights.todayFocusSeconds,
            todayCompletedCycles = insights.todayCompletedCycles,
            currentStreakDays = insights.currentStreakDays,
            soundscapeName = soundscapeName,
            isSoundActive = isSoundActive,
            isStandbyOpen = isStandbyOpen,
            isTaskPickerOpen = isTaskPickerOpen,
            showCelebrationDialog = showCelebration,
            completedSessionDuration = _lastCompletedDuration.value
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FocusUiState()
    )

    fun onEvent(event: FocusEvent) {
        soundManager.playClickHaptic()
        when (event) {
            FocusEvent.ToggleTimer -> timerEngine.toggle()
            FocusEvent.ResetTimer -> timerEngine.restart()
            FocusEvent.SkipSession -> timerEngine.skip()
            is FocusEvent.SelectMode -> timerEngine.setMode(event.mode)
            FocusEvent.ToggleSoundscape -> {
                soundscapeIndex = (soundscapeIndex + 1) % soundscapes.size
                _soundscapeName.value = soundscapes[soundscapeIndex]
            }
            FocusEvent.DismissCelebration -> _showCelebration.value = false
            FocusEvent.OpenStandby -> _isStandbyOpen.value = true
            FocusEvent.CloseStandby -> _isStandbyOpen.value = false
            FocusEvent.OpenTaskPicker -> _isTaskPickerOpen.value = true
            FocusEvent.CloseTaskPicker -> _isTaskPickerOpen.value = false
            is FocusEvent.SelectTask -> {
                viewModelScope.launch {
                    taskRepository.selectTask(event.taskId)
                    _isTaskPickerOpen.value = false
                }
            }
        }
    }
}
