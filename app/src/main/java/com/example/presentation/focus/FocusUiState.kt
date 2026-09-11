package com.example.presentation.focus

import com.example.domain.model.PomodoroType
import com.example.domain.model.Task
import com.example.domain.model.TimerState

data class FocusUiState(
    val timerState: TimerState = TimerState.IDLE,
    val currentType: PomodoroType = PomodoroType.FOCUS,
    val remainingSeconds: Int = 25 * 60,
    val totalDurationSeconds: Int = 25 * 60,
    val progress: Float = 0f,
    val currentCycle: Int = 1,
    val maxCycles: Int = 4,
    val activeTask: Task? = null,
    val todayFocusSeconds: Long = 0L,
    val todayCompletedCycles: Int = 0,
    val currentStreakDays: Int = 0,
    val soundscapeName: String = "CYBERPUNK RAIN",
    val isSoundActive: Boolean = true,
    val isStandbyOpen: Boolean = false,
    val isTaskPickerOpen: Boolean = false,
    val availableTasks: List<Task> = emptyList(),
    val showCelebrationDialog: Boolean = false,
    val completedSessionDuration: Long = 25 * 60
) {
    val formattedTime: String
        get() {
            val mins = remainingSeconds / 60
            val secs = remainingSeconds % 60
            return String.format("%02d:%02d", mins, secs)
        }

    val phaseLabel: String
        get() = when (currentType) {
            PomodoroType.FOCUS -> "PHASE: DEEP STATE"
            PomodoroType.SHORT_BREAK -> "PHASE: SHORT REST"
            PomodoroType.LONG_BREAK -> "PHASE: NEURAL RESET"
        }

    val badgeLabel: String
        get() = when (currentType) {
            PomodoroType.FOCUS -> "DEEP WORK // ${totalDurationSeconds / 60} MIN"
            PomodoroType.SHORT_BREAK -> "RAPID RESET // ${totalDurationSeconds / 60} MIN"
            PomodoroType.LONG_BREAK -> "LONG RECALIBRATION // ${totalDurationSeconds / 60} MIN"
        }

    val formattedTodayFocusTime: String
        get() {
            val hours = todayFocusSeconds / 3600
            val mins = (todayFocusSeconds % 3600) / 60
            return if (hours > 0) "${hours}H ${mins}M FOCUSED" else "${mins}M FOCUSED"
        }
}
