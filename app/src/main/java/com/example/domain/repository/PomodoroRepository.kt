package com.example.domain.repository

import com.example.domain.model.InsightsData
import com.example.domain.model.PomodoroSession
import kotlinx.coroutines.flow.Flow

interface PomodoroRepository {
    fun getAllSessions(): Flow<List<PomodoroSession>>
    fun getSessionsForDateRange(startTime: Long, endTime: Long): Flow<List<PomodoroSession>>
    suspend fun saveSession(session: PomodoroSession): Long
    fun getInsightsData(): Flow<InsightsData>
}
