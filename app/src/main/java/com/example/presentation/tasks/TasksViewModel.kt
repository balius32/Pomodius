package com.example.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.sound.SoundFeedbackManager
import com.example.domain.model.Priority
import com.example.domain.model.Task
import com.example.domain.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TasksViewModel(
    private val taskRepository: TaskRepository,
    private val soundManager: SoundFeedbackManager
) : ViewModel() {

    private val _filter = MutableStateFlow(TaskFilter.ALL)
    private val _isAddDialogOpen = MutableStateFlow(false)
    private val _editingTask = MutableStateFlow<Task?>(null)
    private val _newTaskTitle = MutableStateFlow("")
    private val _newTaskNotes = MutableStateFlow("")
    private val _newTaskPriority = MutableStateFlow(Priority.HIGH)
    private val _newTaskTag = MutableStateFlow("SPRINT")
    private val _newTaskEstimated = MutableStateFlow(2)

    @Suppress("UNCHECKED_CAST")
    val uiState: StateFlow<TasksUiState> = combine(
        taskRepository.getAllTasks(),
        _filter,
        _isAddDialogOpen,
        _editingTask,
        _newTaskTitle,
        _newTaskNotes,
        _newTaskPriority,
        _newTaskTag,
        _newTaskEstimated
    ) { args ->
        val tasks = args[0] as List<Task>
        val filter = args[1] as TaskFilter
        val isAddDialogOpen = args[2] as Boolean
        val editingTask = args[3] as? Task
        val title = args[4] as String
        val notes = args[5] as String
        val priority = args[6] as Priority
        val tag = args[7] as String
        val estimated = args[8] as Int

        TasksUiState(
            tasks = tasks,
            filter = filter,
            isAddDialogOpen = isAddDialogOpen,
            editingTask = editingTask,
            newTaskTitle = title,
            newTaskNotes = notes,
            newTaskPriority = priority,
            newTaskTag = tag,
            newTaskEstimated = estimated
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TasksUiState()
    )

    fun onEvent(event: TasksEvent) {
        soundManager.playClickHaptic()
        when (event) {
            is TasksEvent.SetFilter -> _filter.value = event.filter
            is TasksEvent.ToggleTaskComplete -> {
                viewModelScope.launch {
                    taskRepository.toggleTaskCompleted(event.taskId)
                }
            }
            is TasksEvent.SelectTaskForTimer -> {
                viewModelScope.launch {
                    taskRepository.selectTask(event.taskId)
                }
            }
            is TasksEvent.DeleteTask -> {
                viewModelScope.launch {
                    taskRepository.deleteTask(event.taskId)
                }
            }
            TasksEvent.OpenAddTask -> {
                _editingTask.value = null
                _newTaskTitle.value = ""
                _newTaskNotes.value = ""
                _newTaskPriority.value = Priority.HIGH
                _newTaskTag.value = "SPRINT"
                _newTaskEstimated.value = 2
                _isAddDialogOpen.value = true
            }
            is TasksEvent.OpenEditTask -> {
                _editingTask.value = event.task
                _newTaskTitle.value = event.task.title
                _newTaskNotes.value = event.task.notes
                _newTaskPriority.value = event.task.priority
                _newTaskTag.value = event.task.tag
                _newTaskEstimated.value = event.task.estimatedPomodoros
                _isAddDialogOpen.value = true
            }
            TasksEvent.CloseDialog -> _isAddDialogOpen.value = false
            is TasksEvent.UpdateTitle -> _newTaskTitle.value = event.title
            is TasksEvent.UpdateNotes -> _newTaskNotes.value = event.notes
            is TasksEvent.UpdatePriority -> _newTaskPriority.value = event.priority
            is TasksEvent.UpdateTag -> _newTaskTag.value = event.tag
            is TasksEvent.UpdateEstimated -> {
                val current = _newTaskEstimated.value
                _newTaskEstimated.value = (current + event.delta).coerceIn(1, 20)
            }
            TasksEvent.SaveTask -> {
                val title = _newTaskTitle.value.trim()
                if (title.isNotEmpty()) {
                    viewModelScope.launch {
                        val currentEditing = _editingTask.value
                        if (currentEditing != null) {
                            taskRepository.updateTask(
                                currentEditing.copy(
                                    title = title,
                                    notes = _newTaskNotes.value.trim(),
                                    priority = _newTaskPriority.value,
                                    tag = _newTaskTag.value,
                                    estimatedPomodoros = _newTaskEstimated.value
                                )
                            )
                        } else {
                            val newTask = Task(
                                title = title,
                                notes = _newTaskNotes.value.trim(),
                                priority = _newTaskPriority.value,
                                tag = _newTaskTag.value,
                                estimatedPomodoros = _newTaskEstimated.value,
                                isSelected = uiState.value.tasks.none { it.isSelected }
                            )
                            taskRepository.insertTask(newTask)
                        }
                        _isAddDialogOpen.value = false
                    }
                }
            }
        }
    }
}
