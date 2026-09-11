package com.example.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.AppContainer
import com.example.presentation.focus.FocusViewModel
import com.example.presentation.insights.InsightsViewModel
import com.example.presentation.settings.SettingsViewModel
import com.example.presentation.tasks.TasksViewModel

class AppViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(FocusViewModel::class.java) -> {
                FocusViewModel(
                    timerEngine = container.timerEngine,
                    taskRepository = container.taskRepository,
                    pomodoroRepository = container.pomodoroRepository,
                    settingsRepository = container.settingsRepository,
                    soundManager = container.soundManager
                ) as T
            }
            modelClass.isAssignableFrom(TasksViewModel::class.java) -> {
                TasksViewModel(
                    taskRepository = container.taskRepository,
                    soundManager = container.soundManager
                ) as T
            }
            modelClass.isAssignableFrom(InsightsViewModel::class.java) -> {
                InsightsViewModel(
                    pomodoroRepository = container.pomodoroRepository,
                    soundManager = container.soundManager
                ) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(
                    settingsRepository = container.settingsRepository,
                    soundManager = container.soundManager
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
