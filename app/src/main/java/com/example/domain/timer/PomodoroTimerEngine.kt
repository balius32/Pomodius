package com.example.domain.timer

import com.example.domain.model.PomodoroSession
import com.example.domain.model.PomodoroType
import com.example.domain.model.SessionMode
import com.example.domain.model.Task
import com.example.domain.model.TimerState
import com.example.domain.model.UserPreferences
import com.example.domain.repository.PomodoroRepository
import com.example.domain.repository.SettingsRepository
import com.example.domain.repository.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PomodoroTimerEngine(
    private val scope: CoroutineScope,
    private val taskRepository: TaskRepository,
    private val pomodoroRepository: PomodoroRepository,
    private val settingsRepository: SettingsRepository,
    private val dispatcher: kotlinx.coroutines.CoroutineDispatcher = Dispatchers.Default,
    private val onTimerTick: ((remainingSeconds: Int, taskTitle: String, isRunning: Boolean) -> Unit)? = null
) {
    private val _timerState = MutableStateFlow(TimerState.IDLE)
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private val _sessionMode = MutableStateFlow(SessionMode(PomodoroType.FOCUS, 1))
    val sessionMode: StateFlow<SessionMode> = _sessionMode.asStateFlow()

    private val _currentType = MutableStateFlow(PomodoroType.FOCUS)
    val currentType: StateFlow<PomodoroType> = _currentType.asStateFlow()

    private val _currentCycle = MutableStateFlow(1)
    val currentCycle: StateFlow<Int> = _currentCycle.asStateFlow()

    private val _totalDurationSeconds = MutableStateFlow(25 * 60)
    val totalDurationSeconds: StateFlow<Int> = _totalDurationSeconds.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(25 * 60)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    private fun updateSessionMode(type: PomodoroType, cycle: Int) {
        _sessionMode.value = SessionMode(type, cycle)
        _currentType.value = type
        _currentCycle.value = cycle
    }

    private val _linkedTask = MutableStateFlow<Task?>(null)
    val linkedTask: StateFlow<Task?> = _linkedTask.asStateFlow()

    private val _sessionCompletedEvent = MutableSharedFlow<PomodoroSession>(extraBufferCapacity = 1)
    val sessionCompletedEvent: SharedFlow<PomodoroSession> = _sessionCompletedEvent.asSharedFlow()

    private var targetEndTimeMs: Long = 0L
    private var sessionStartTimeMs: Long = 0L
    private var timerJob: Job? = null

    init {
        // Observe selected task from database
        scope.launch {
            taskRepository.getSelectedTask().collect { task ->
                _linkedTask.value = task
            }
        }
        // Observe user preferences for durations
        scope.launch {
            settingsRepository.preferences.collect { prefs ->
                if (_timerState.value == TimerState.IDLE) {
                    val durationMins = when (_currentType.value) {
                        PomodoroType.FOCUS -> prefs.focusDurationMinutes
                        PomodoroType.SHORT_BREAK -> prefs.shortBreakDurationMinutes
                        PomodoroType.LONG_BREAK -> prefs.longBreakDurationMinutes
                    }
                    _totalDurationSeconds.value = durationMins * 60
                    _remainingSeconds.value = durationMins * 60
                }
            }
        }
    }

    fun setMode(type: PomodoroType, autoStart: Boolean = false) {
        timerJob?.cancel()
        updateSessionMode(type, _currentCycle.value)
        scope.launch {
            val prefs = settingsRepository.preferences.first()
            val durationMins = when (type) {
                PomodoroType.FOCUS -> prefs.focusDurationMinutes
                PomodoroType.SHORT_BREAK -> prefs.shortBreakDurationMinutes
                PomodoroType.LONG_BREAK -> prefs.longBreakDurationMinutes
            }
            _totalDurationSeconds.value = durationMins * 60
            _remainingSeconds.value = durationMins * 60
            if (autoStart) {
                start()
            } else {
                _timerState.value = TimerState.IDLE
                notifyService()
            }
        }
    }

    fun start() {
        if (_timerState.value == TimerState.RUNNING) return

        val now = System.currentTimeMillis()
        if (_timerState.value == TimerState.IDLE || _timerState.value == TimerState.COMPLETED) {
            sessionStartTimeMs = now
            val currentRemaining = _remainingSeconds.value
            targetEndTimeMs = now + (currentRemaining * 1000L)
        } else if (_timerState.value == TimerState.PAUSED) {
            // Resuming from pause
            targetEndTimeMs = now + (_remainingSeconds.value * 1000L)
        }

        _timerState.value = TimerState.RUNNING
        notifyService()

        timerJob?.cancel()
        timerJob = scope.launch(dispatcher) {
            while (isActive && _timerState.value == TimerState.RUNNING) {
                val currentMs = System.currentTimeMillis()
                val remainingMs = targetEndTimeMs - currentMs
                val remainingSec = ((remainingMs + 999) / 1000).toInt().coerceAtLeast(0)
                _remainingSeconds.value = remainingSec
                notifyService()

                if (remainingSec <= 0) {
                    handleTimerCompletion()
                    break
                }
                delay(500)
            }
        }
    }

    fun pause() {
        if (_timerState.value != TimerState.RUNNING) return
        timerJob?.cancel()
        val now = System.currentTimeMillis()
        val remainingMs = targetEndTimeMs - now
        _remainingSeconds.value = ((remainingMs + 999) / 1000).toInt().coerceAtLeast(0)
        _timerState.value = TimerState.PAUSED
        notifyService()
    }

    fun resume() {
        if (_timerState.value == TimerState.PAUSED) {
            start()
        }
    }

    fun toggle() {
        when (_timerState.value) {
            TimerState.IDLE -> start()
            TimerState.RUNNING -> pause()
            TimerState.PAUSED -> resume()
            TimerState.COMPLETED -> restart()
        }
    }

    fun restart() {
        timerJob?.cancel()
        _timerState.value = TimerState.IDLE
        scope.launch(dispatcher) {
            val prefs = settingsRepository.preferences.first()
            val durationMins = when (_currentType.value) {
                PomodoroType.FOCUS -> prefs.focusDurationMinutes
                PomodoroType.SHORT_BREAK -> prefs.shortBreakDurationMinutes
                PomodoroType.LONG_BREAK -> prefs.longBreakDurationMinutes
            }
            _totalDurationSeconds.value = durationMins * 60
            _remainingSeconds.value = durationMins * 60
            notifyService()
        }
    }

    fun skip() {
        timerJob?.cancel()
        advanceToNextSession(autoStart = false)
    }

    private fun handleTimerCompletion() {
        _timerState.value = TimerState.COMPLETED
        notifyService()

        val completedType = _currentType.value
        val completedDuration = _totalDurationSeconds.value.toLong()
        val now = System.currentTimeMillis()
        val currentTask = _linkedTask.value

        val session = PomodoroSession(
            taskId = currentTask?.id,
            taskTitle = currentTask?.title,
            startTime = sessionStartTimeMs,
            endTime = now,
            duration = completedDuration,
            type = completedType,
            completed = true
        )

        scope.launch(dispatcher) {
            // 1. Save session
            pomodoroRepository.saveSession(session)

            // 2. Increment task completed cycle/session count ONLY when a full set finishes after LONG_BREAK
            if (completedType == PomodoroType.LONG_BREAK && currentTask != null) {
                taskRepository.incrementCompletedPomodoro(currentTask.id)
            }

            // 3. Emit completed event for UI celebration dialog
            _sessionCompletedEvent.emit(session)

            // 4. Advance session/cycle
            val prefs = settingsRepository.preferences.first()
            val sessionsBeforeLong = prefs.sessionsBeforeLongBreak
            val cycle = _currentCycle.value

            val (nextType, nextCycle) = when (completedType) {
                PomodoroType.FOCUS -> {
                    val isLong = cycle >= sessionsBeforeLong
                    Pair(if (isLong) PomodoroType.LONG_BREAK else PomodoroType.SHORT_BREAK, cycle)
                }
                PomodoroType.SHORT_BREAK -> {
                    Pair(PomodoroType.FOCUS, cycle + 1)
                }
                PomodoroType.LONG_BREAK -> {
                    Pair(PomodoroType.FOCUS, 1)
                }
            }

            val shouldAutoStart = if (completedType == PomodoroType.FOCUS) prefs.autoStartBreaks else prefs.autoStartFocus

            updateSessionMode(nextType, nextCycle)
            val nextDurationMins = when (nextType) {
                PomodoroType.FOCUS -> prefs.focusDurationMinutes
                PomodoroType.SHORT_BREAK -> prefs.shortBreakDurationMinutes
                PomodoroType.LONG_BREAK -> prefs.longBreakDurationMinutes
            }

            _totalDurationSeconds.value = nextDurationMins * 60
            _remainingSeconds.value = nextDurationMins * 60

            if (shouldAutoStart) {
                delay(1000)
                start()
            } else {
                _timerState.value = TimerState.IDLE
                notifyService()
            }
        }
    }

    private fun advanceToNextSession(autoStart: Boolean) {
        timerJob?.cancel()
        scope.launch(dispatcher) {
            val prefs = settingsRepository.preferences.first()
            val sessionsBeforeLong = prefs.sessionsBeforeLongBreak
            val completedType = _currentType.value
            val cycle = _currentCycle.value
            val currentTask = _linkedTask.value

            if (completedType == PomodoroType.FOCUS && _timerState.value != TimerState.COMPLETED) {
                val durationMins = prefs.focusDurationMinutes
                val session = PomodoroSession(
                    taskId = currentTask?.id,
                    taskTitle = currentTask?.title,
                    startTime = System.currentTimeMillis() - (durationMins * 60 * 1000L),
                    endTime = System.currentTimeMillis(),
                    duration = durationMins * 60L,
                    type = PomodoroType.FOCUS,
                    completed = true
                )
                pomodoroRepository.saveSession(session)
            }

            if (completedType == PomodoroType.LONG_BREAK) {
                if (_timerState.value != TimerState.COMPLETED) {
                    val durationMins = prefs.longBreakDurationMinutes
                    val session = PomodoroSession(
                        taskId = currentTask?.id,
                        taskTitle = currentTask?.title,
                        startTime = System.currentTimeMillis() - (durationMins * 60 * 1000L),
                        endTime = System.currentTimeMillis(),
                        duration = durationMins * 60L,
                        type = PomodoroType.LONG_BREAK,
                        completed = true
                    )
                    pomodoroRepository.saveSession(session)
                }
                if (currentTask != null) {
                    taskRepository.incrementCompletedPomodoro(currentTask.id)
                }
            }

            val (nextType, nextCycle) = when (completedType) {
                PomodoroType.FOCUS -> {
                    val isLong = cycle >= sessionsBeforeLong
                    Pair(if (isLong) PomodoroType.LONG_BREAK else PomodoroType.SHORT_BREAK, cycle)
                }
                PomodoroType.SHORT_BREAK -> {
                    Pair(PomodoroType.FOCUS, cycle + 1)
                }
                PomodoroType.LONG_BREAK -> {
                    Pair(PomodoroType.FOCUS, 1)
                }
            }

            updateSessionMode(nextType, nextCycle)

            val durationMins = when (nextType) {
                PomodoroType.FOCUS -> prefs.focusDurationMinutes
                PomodoroType.SHORT_BREAK -> prefs.shortBreakDurationMinutes
                PomodoroType.LONG_BREAK -> prefs.longBreakDurationMinutes
            }
            _totalDurationSeconds.value = durationMins * 60
            _remainingSeconds.value = durationMins * 60

            if (autoStart) {
                start()
            } else {
                _timerState.value = TimerState.IDLE
                notifyService()
            }
        }
    }

    private fun notifyService() {
        val taskTitle = _linkedTask.value?.title ?: "Focus Session"
        onTimerTick?.invoke(
            _remainingSeconds.value,
            taskTitle,
            _timerState.value == TimerState.RUNNING
        )
    }
}
