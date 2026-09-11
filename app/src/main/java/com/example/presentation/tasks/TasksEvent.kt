package com.example.presentation.tasks

import com.example.domain.model.Priority
import com.example.domain.model.Task

sealed interface TasksEvent {
    data class SetFilter(val filter: TaskFilter) : TasksEvent
    data class ToggleTaskComplete(val taskId: Long) : TasksEvent
    data class SelectTaskForTimer(val taskId: Long) : TasksEvent
    data class DeleteTask(val taskId: Long) : TasksEvent
    data class OpenEditTask(val task: Task) : TasksEvent
    data object OpenAddTask : TasksEvent
    data object CloseDialog : TasksEvent
    data class UpdateTitle(val title: String) : TasksEvent
    data class UpdateNotes(val notes: String) : TasksEvent
    data class UpdatePriority(val priority: Priority) : TasksEvent
    data class UpdateTag(val tag: String) : TasksEvent
    data class UpdateEstimated(val delta: Int) : TasksEvent
    data object SaveTask : TasksEvent
}
