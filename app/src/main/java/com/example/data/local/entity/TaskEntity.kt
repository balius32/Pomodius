package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.Priority
import com.example.domain.model.Task

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val notes: String = "",
    val estimatedPomodoros: Int = 1,
    val completedPomodoros: Int = 0,
    val completed: Boolean = false,
    val priority: String = Priority.MEDIUM.name,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dueDate: Long? = null,
    val tag: String = "WORK",
    val isSelected: Boolean = false
) {
    fun toDomain(): Task = Task(
        id = id,
        title = title,
        notes = notes,
        estimatedPomodoros = estimatedPomodoros,
        completedPomodoros = completedPomodoros,
        completed = completed,
        priority = try {
            Priority.valueOf(priority)
        } catch (e: Exception) {
            Priority.MEDIUM
        },
        createdAt = createdAt,
        updatedAt = updatedAt,
        dueDate = dueDate,
        tag = tag,
        isSelected = isSelected
    )

    companion object {
        fun fromDomain(task: Task): TaskEntity = TaskEntity(
            id = task.id,
            title = task.title,
            notes = task.notes,
            estimatedPomodoros = task.estimatedPomodoros,
            completedPomodoros = task.completedPomodoros,
            completed = task.completed,
            priority = task.priority.name,
            createdAt = task.createdAt,
            updatedAt = task.updatedAt,
            dueDate = task.dueDate,
            tag = task.tag,
            isSelected = task.isSelected
        )
    }
}
