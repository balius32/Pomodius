package com.example.data.repository

import com.example.data.local.dao.PomodoroSessionDao
import com.example.data.local.dao.TaskDao
import com.example.data.local.entity.PomodoroSessionEntity
import com.example.domain.model.DailyFocusMetric
import com.example.domain.model.InsightsData
import com.example.domain.model.PomodoroSession
import com.example.domain.model.PomodoroType
import com.example.domain.repository.PomodoroRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.Calendar

class PomodoroRepositoryImpl(
    private val sessionDao: PomodoroSessionDao,
    private val taskDao: TaskDao
) : PomodoroRepository {

    override fun getAllSessions(): Flow<List<PomodoroSession>> {
        return sessionDao.getAllSessions().map { list -> list.map { it.toDomain() } }
    }

    override fun getSessionsForDateRange(startTime: Long, endTime: Long): Flow<List<PomodoroSession>> {
        return sessionDao.getSessionsForDateRange(startTime, endTime).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun saveSession(session: PomodoroSession): Long {
        return sessionDao.insertSession(PomodoroSessionEntity.fromDomain(session))
    }

    override fun getInsightsData(): Flow<InsightsData> {
        return combine(
            sessionDao.getAllSessions(),
            taskDao.getAllTasks()
        ) { sessionEntities, taskEntities ->
            val sessions = sessionEntities.map { it.toDomain() }
            val tasks = taskEntities.map { it.toDomain() }

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfToday = calendar.timeInMillis
            val endOfToday = startOfToday + 86400000L

            val todayFocusSessions = sessions.filter {
                it.type == PomodoroType.FOCUS &&
                it.completed &&
                it.startTime >= startOfToday &&
                it.startTime < endOfToday
            }

            val todayLongBreakSessions = sessions.filter {
                it.type == PomodoroType.LONG_BREAK &&
                it.completed &&
                it.startTime >= startOfToday &&
                it.startTime < endOfToday
            }

            val finishedTasks = tasks.filter { it.completed }
            val finishedTasksCount = finishedTasks.size
            val highPriorityFinishedCount = finishedTasks.count {
                it.priority.name == "HIGH" || it.priority.name == "CRITICAL"
            }

            val todayFocusSeconds = todayFocusSessions.sumOf { it.duration }
            val todayCompletedCycles = maxOf(todayLongBreakSessions.size, finishedTasksCount)

            // Calculate weekly metrics (Monday to Sunday)
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val daysFromMonday = (dayOfWeek + 5) % 7 // 0 = Monday, 6 = Sunday
            val mondayCalendar = Calendar.getInstance().apply {
                timeInMillis = startOfToday - (daysFromMonday * 86400000L)
            }
            val startOfWeek = mondayCalendar.timeInMillis

            val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")
            val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            val weeklyMetrics = mutableListOf<DailyFocusMetric>()
            var weeklyTotalSeconds = 0L

            for (i in 0..6) {
                val dayStart = startOfWeek + (i * 86400000L)
                val dayEnd = dayStart + 86400000L
                val isToday = dayStart == startOfToday

                val daySessions = sessions.filter {
                    it.type == PomodoroType.FOCUS &&
                    it.completed &&
                    it.startTime >= dayStart &&
                    it.startTime < dayEnd
                }
                val seconds = daySessions.sumOf { it.duration }
                weeklyTotalSeconds += seconds
                val hours = (seconds / 3600f)

                weeklyMetrics.add(
                    DailyFocusMetric(
                        dayOfWeek = dayLabels[i],
                        dayName = dayNames[i],
                        focusHours = hours,
                        isCurrentDay = isToday
                    )
                )
            }

            // Streak calculation (consecutive days backward from today/yesterday)
            var streak = 0
            var checkDay = Calendar.getInstance().apply {
                timeInMillis = startOfToday
            }
            val todayHasSessions = todayFocusSessions.isNotEmpty()
            if (!todayHasSessions) {
                checkDay.add(Calendar.DAY_OF_YEAR, -1)
            }

            while (true) {
                val dStart = checkDay.timeInMillis
                val dEnd = dStart + 86400000L
                val count = sessions.count {
                    it.type == PomodoroType.FOCUS &&
                    it.completed &&
                    it.startTime >= dStart &&
                    it.startTime < dEnd
                }
                if (count > 0) {
                    streak++
                    checkDay.add(Calendar.DAY_OF_YEAR, -1)
                } else {
                    break
                }
            }
            val finalStreak = streak

            // Calculate record streak from all historical sessions
            val allFocusSessions = sessions.filter { it.type == PomodoroType.FOCUS && it.completed }
            val sessionDates = allFocusSessions.map { s ->
                val cal = Calendar.getInstance().apply {
                    timeInMillis = s.startTime
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                cal.timeInMillis
            }.distinct().sorted()

            var maxStreak = finalStreak
            var tempStreak = 0
            var prevDate = 0L
            for (date in sessionDates) {
                if (prevDate == 0L || date == prevDate + 86400000L) {
                    tempStreak++
                } else {
                    tempStreak = 1
                }
                prevDate = date
                if (tempStreak > maxStreak) {
                    maxStreak = tempStreak
                }
            }

            // Flow anchor calculation (peak 2-hour window based on completed sessions)
            val flowAnchorStr = if (allFocusSessions.isNotEmpty()) {
                val hourCounts = IntArray(24)
                for (s in allFocusSessions) {
                    val cal = Calendar.getInstance().apply { timeInMillis = s.startTime }
                    val hour = cal.get(Calendar.HOUR_OF_DAY)
                    hourCounts[hour]++
                }
                var peakHour = 9
                var maxCount = -1
                for (h in 0..23) {
                    if (hourCounts[h] > maxCount) {
                        maxCount = hourCounts[h]
                        peakHour = h
                    }
                }
                val startAmPm = if (peakHour < 12) "AM" else "PM"
                val displayHour = if (peakHour % 12 == 0) 12 else peakHour % 12
                val endHour = (peakHour + 2) % 24
                val endDisplayHour = if (endHour % 12 == 0) 12 else endHour % 12
                String.format(java.util.Locale.US, "%02d:00 - %02d:30 %s", displayHour, endDisplayHour, startAmPm)
            } else {
                "NO SESSIONS YET"
            }

            // Distribution based on task tags or defaults
            var workSec = 0L
            var reviewSec = 0L
            var learnSec = 0L
            for (s in sessions) {
                if (s.type == PomodoroType.FOCUS && s.completed) {
                    when {
                        s.taskTitle?.contains("Review", ignoreCase = true) == true -> reviewSec += s.duration
                        s.taskTitle?.contains("Learn", ignoreCase = true) == true -> learnSec += s.duration
                        else -> workSec += s.duration
                    }
                }
            }
            val hasSessionData = (workSec + reviewSec + learnSec) > 0
            val totalSec = (workSec + reviewSec + learnSec).coerceAtLeast(1L)
            val workPct = if (hasSessionData) ((workSec * 100) / totalSec).toInt() else 0
            val reviewPct = if (hasSessionData) ((reviewSec * 100) / totalSec).toInt() else 0
            val learnPct = if (hasSessionData) (100 - workPct - reviewPct).coerceAtLeast(0) else 0

            val efficiency = if (todayCompletedCycles > 0) ((todayCompletedCycles * 100) / 5).coerceAtMost(100) else 0

            InsightsData(
                todayFocusSeconds = todayFocusSeconds,
                todayCompletedCycles = todayCompletedCycles,
                targetCycles = 5,
                finishedTasksCount = finishedTasksCount,
                highPriorityFinishedCount = highPriorityFinishedCount,
                currentStreakDays = finalStreak,
                recordStreakDays = maxStreak,
                weeklyFocusHours = (weeklyTotalSeconds / 3600f),
                weeklyTargetHours = 20.0f,
                weeklyMetrics = weeklyMetrics,
                workRatio = workPct,
                reviewRatio = reviewPct,
                learnRatio = learnPct,
                flowAnchorTime = flowAnchorStr,
                efficiencyRating = efficiency
            )
        }
    }
}
