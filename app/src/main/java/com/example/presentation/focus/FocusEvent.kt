package com.example.presentation.focus

import com.example.domain.model.PomodoroType

sealed interface FocusEvent {
    data object ToggleTimer : FocusEvent
    data object ResetTimer : FocusEvent
    data object SkipSession : FocusEvent
    data class SelectMode(val mode: PomodoroType) : FocusEvent
    data object ToggleSoundscape : FocusEvent
    data object DismissCelebration : FocusEvent
    data object OpenStandby : FocusEvent
    data object CloseStandby : FocusEvent
    data class SelectTask(val taskId: Long) : FocusEvent
    data object OpenTaskPicker : FocusEvent
    data object CloseTaskPicker : FocusEvent
}
