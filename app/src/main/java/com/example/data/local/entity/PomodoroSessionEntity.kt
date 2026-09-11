package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.PomodoroSession
import com.example.domain.model.PomodoroType

@Entity(tableName = "pomodoro_sessions")
data class PomodoroSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskId: Long? = null,
    val taskTitle: String? = null,
    val startTime: Long,
    val endTime: Long,
    val duration: Long, // seconds
    val type: String = PomodoroType.FOCUS.name,
    val completed: Boolean = true
) {
    fun toDomain(): PomodoroSession = PomodoroSession(
        id = id,
        taskId = taskId,
        taskTitle = taskTitle,
        startTime = startTime,
        endTime = endTime,
        duration = duration,
        type = try {
            PomodoroType.valueOf(type)
        } catch (e: Exception) {
            PomodoroType.FOCUS
        },
        completed = completed
    )

    companion object {
        fun fromDomain(session: PomodoroSession): PomodoroSessionEntity = PomodoroSessionEntity(
            id = session.id,
            taskId = session.taskId,
            taskTitle = session.taskTitle,
            startTime = session.startTime,
            endTime = session.endTime,
            duration = session.duration,
            type = session.type.name,
            completed = session.completed
        )
    }
}
