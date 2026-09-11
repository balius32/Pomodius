package com.example.domain.model

data class Task(
    val id: Long = 0,
    val title: String,
    val notes: String = "",
    val estimatedPomodoros: Int = 1,
    val completedPomodoros: Int = 0,
    val completed: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dueDate: Long? = null,
    val tag: String = "WORK",
    val isSelected: Boolean = false
)
