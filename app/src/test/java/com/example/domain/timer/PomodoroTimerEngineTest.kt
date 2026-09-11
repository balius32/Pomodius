package com.example.domain.timer

import com.example.domain.model.InsightsData
import com.example.domain.model.PomodoroSession
import com.example.domain.model.PomodoroType
import com.example.domain.model.Task
import com.example.domain.model.TimerState
import com.example.domain.model.UserPreferences
import com.example.domain.repository.PomodoroRepository
import com.example.domain.repository.SettingsRepository
import com.example.domain.repository.TaskRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PomodoroTimerEngineTest {

    private lateinit var fakeTaskRepo: FakeTaskRepository
    private lateinit var fakePomodoroRepo: FakePomodoroRepository
    private lateinit var fakeSettingsRepo: FakeSettingsRepository

    @Before
    fun setup() {
        fakeTaskRepo = FakeTaskRepository()
        fakePomodoroRepo = FakePomodoroRepository()
        fakeSettingsRepo = FakeSettingsRepository()
    }

    private fun createTimerEngine(
        scope: kotlinx.coroutines.CoroutineScope,
        dispatcher: kotlinx.coroutines.CoroutineDispatcher
    ): PomodoroTimerEngine {
        return PomodoroTimerEngine(
            scope = scope,
            taskRepository = fakeTaskRepo,
            pomodoroRepository = fakePomodoroRepo,
            settingsRepository = fakeSettingsRepo,
            dispatcher = dispatcher
        )
    }

    @Test
    fun timer_initialState_isIdleWith25Minutes() = runTest {
        val timerEngine = createTimerEngine(backgroundScope, StandardTestDispatcher(testScheduler))
        testScheduler.advanceUntilIdle()
        assertEquals(TimerState.IDLE, timerEngine.timerState.value)
        assertEquals(PomodoroType.FOCUS, timerEngine.currentType.value)
        assertEquals(25 * 60, timerEngine.remainingSeconds.value)
        assertEquals(25 * 60, timerEngine.totalDurationSeconds.value)
    }

    @Test
    fun timer_start_transitionsToRunning() = runTest {
        val timerEngine = createTimerEngine(backgroundScope, StandardTestDispatcher(testScheduler))
        testScheduler.advanceUntilIdle()
        timerEngine.start()
        assertEquals(TimerState.RUNNING, timerEngine.timerState.value)
        timerEngine.pause()
    }

    @Test
    fun timer_pause_transitionsToPaused() = runTest {
        val timerEngine = createTimerEngine(backgroundScope, StandardTestDispatcher(testScheduler))
        testScheduler.advanceUntilIdle()
        timerEngine.start()
        advanceTimeBy(1000)
        timerEngine.pause()
        assertEquals(TimerState.PAUSED, timerEngine.timerState.value)
    }

    @Test
    fun timer_resume_transitionsBackToRunning() = runTest {
        val timerEngine = createTimerEngine(backgroundScope, StandardTestDispatcher(testScheduler))
        testScheduler.advanceUntilIdle()
        timerEngine.start()
        timerEngine.pause()
        timerEngine.resume()
        assertEquals(TimerState.RUNNING, timerEngine.timerState.value)
        timerEngine.pause()
    }

    @Test
    fun timer_restart_resetsToFullDuration() = runTest {
        val timerEngine = createTimerEngine(backgroundScope, StandardTestDispatcher(testScheduler))
        testScheduler.advanceUntilIdle()
        timerEngine.start()
        advanceTimeBy(3000)
        timerEngine.restart()
        testScheduler.advanceUntilIdle()
        assertEquals(TimerState.IDLE, timerEngine.timerState.value)
        assertEquals(25 * 60, timerEngine.remainingSeconds.value)
    }

    @Test
    fun timer_skip_advancesToShortBreak() = runTest {
        val timerEngine = createTimerEngine(backgroundScope, StandardTestDispatcher(testScheduler))
        testScheduler.advanceUntilIdle()
        timerEngine.skip()
        testScheduler.advanceUntilIdle()
        assertEquals(PomodoroType.SHORT_BREAK, timerEngine.currentType.value)
        assertEquals(5 * 60, timerEngine.remainingSeconds.value)
    }
}

// Fakes for testing
private class FakeTaskRepository : TaskRepository {
    val selectedTaskFlow = MutableStateFlow<Task?>(null)
    override fun getAllTasks(): Flow<List<Task>> = flowOf(emptyList())
    override fun getActiveTasks(): Flow<List<Task>> = flowOf(emptyList())
    override fun getCompletedTasks(): Flow<List<Task>> = flowOf(emptyList())
    override fun getSelectedTask(): Flow<Task?> = selectedTaskFlow
    override suspend fun getTaskById(id: Long): Task? = null
    override suspend fun insertTask(task: Task): Long = 1L
    override suspend fun updateTask(task: Task) {}
    override suspend fun deleteTask(id: Long) {}
    override suspend fun selectTask(id: Long) {}
    override suspend fun toggleTaskCompleted(id: Long) {}
    override suspend fun incrementCompletedPomodoro(id: Long) {}
}

private class FakePomodoroRepository : PomodoroRepository {
    override fun getAllSessions(): Flow<List<PomodoroSession>> = flowOf(emptyList())
    override fun getSessionsForDateRange(startTime: Long, endTime: Long): Flow<List<PomodoroSession>> = flowOf(emptyList())
    override suspend fun saveSession(session: PomodoroSession): Long = 1L
    override fun getInsightsData(): Flow<InsightsData> = flowOf(InsightsData())
}

private class FakeSettingsRepository : SettingsRepository {
    val prefsFlow = MutableStateFlow(UserPreferences())
    override val preferences: Flow<UserPreferences> = prefsFlow
    override suspend fun updateFocusDuration(minutes: Int) { prefsFlow.value = prefsFlow.value.copy(focusDurationMinutes = minutes) }
    override suspend fun updateShortBreakDuration(minutes: Int) { prefsFlow.value = prefsFlow.value.copy(shortBreakDurationMinutes = minutes) }
    override suspend fun updateLongBreakDuration(minutes: Int) { prefsFlow.value = prefsFlow.value.copy(longBreakDurationMinutes = minutes) }
    override suspend fun updateSessionsBeforeLongBreak(count: Int) { prefsFlow.value = prefsFlow.value.copy(sessionsBeforeLongBreak = count) }
    override suspend fun updateAutoStartBreaks(enabled: Boolean) { prefsFlow.value = prefsFlow.value.copy(autoStartBreaks = enabled) }
    override suspend fun updateAutoStartFocus(enabled: Boolean) { prefsFlow.value = prefsFlow.value.copy(autoStartFocus = enabled) }
    override suspend fun updateSoundEnabled(enabled: Boolean) { prefsFlow.value = prefsFlow.value.copy(soundEnabled = enabled) }
    override suspend fun updateVibrationEnabled(enabled: Boolean) { prefsFlow.value = prefsFlow.value.copy(vibrationEnabled = enabled) }
    override suspend fun updateThemeMode(theme: String) { prefsFlow.value = prefsFlow.value.copy(themeMode = theme) }
    override suspend fun updateStandbyOnLandscape(enabled: Boolean) { prefsFlow.value = prefsFlow.value.copy(standbyOnLandscape = enabled) }
    override suspend fun updateKeepScreenAwake(enabled: Boolean) { prefsFlow.value = prefsFlow.value.copy(keepScreenAwake = enabled) }
    override suspend fun updateOledPureBlack(enabled: Boolean) { prefsFlow.value = prefsFlow.value.copy(oledPureBlack = enabled) }
}
