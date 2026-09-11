package com.example.presentation.tasks

import com.example.domain.model.Priority
import com.example.domain.model.Task

enum class TaskFilter {
    ALL,
    ACTIVE,
    DONE
}

data class TasksUiState(
    val tasks: List<Task> = emptyList(),
    val filter: TaskFilter = TaskFilter.ALL,
    val isAddDialogOpen: Boolean = false,
    val editingTask: Task? = null,
    val newTaskTitle: String = "",
    val newTaskNotes: String = "",
    val newTaskPriority: Priority = Priority.HIGH,
    val newTaskTag: String = "SPRINT",
    val newTaskEstimated: Int = 2
) {
    val filteredTasks: List<Task>
        get() = when (filter) {
            TaskFilter.ALL -> tasks
            TaskFilter.ACTIVE -> tasks.filter { !it.completed }
            TaskFilter.DONE -> tasks.filter { it.completed }
        }

    val activeCount: Int
        get() = tasks.count { !it.completed }

    val completedCount: Int
        get() = tasks.count { it.completed }
}
