package com.example

import android.content.Context
import com.example.core.service.TimerForegroundService
import com.example.core.sound.SoundFeedbackManager
import com.example.data.local.AppDatabase
import com.example.data.local.preferences.UserPreferencesDataStore
import com.example.data.repository.PomodoroRepositoryImpl
import com.example.data.repository.SettingsRepositoryImpl
import com.example.data.repository.TaskRepositoryImpl
import com.example.domain.repository.PomodoroRepository
import com.example.domain.repository.SettingsRepository
import com.example.domain.repository.TaskRepository
import com.example.domain.timer.PomodoroTimerEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class AppContainer(private val context: Context) {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context, applicationScope)
    }

    val taskRepository: TaskRepository by lazy {
        TaskRepositoryImpl(database.taskDao())
    }

    val pomodoroRepository: PomodoroRepository by lazy {
        PomodoroRepositoryImpl(database.pomodoroSessionDao(), database.taskDao())
    }

    val preferencesDataStore: UserPreferencesDataStore by lazy {
        UserPreferencesDataStore(context)
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(preferencesDataStore)
    }

    val soundManager: SoundFeedbackManager by lazy {
        SoundFeedbackManager(context)
    }

    val timerEngine: PomodoroTimerEngine by lazy {
        PomodoroTimerEngine(
            scope = applicationScope,
            taskRepository = taskRepository,
            pomodoroRepository = pomodoroRepository,
            settingsRepository = settingsRepository,
            onTimerTick = { remainingSec, taskTitle, isRunning ->
                if (isRunning) {
                    TimerForegroundService.startOrUpdate(context, remainingSec, taskTitle, true)
                } else if (remainingSec > 0) {
                    TimerForegroundService.startOrUpdate(context, remainingSec, taskTitle, false)
                } else {
                    TimerForegroundService.stop(context)
                }
            }
        )
    }
}
