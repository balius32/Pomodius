package com.example.data.repository

import com.example.data.local.dao.TaskDao
import com.example.data.local.entity.TaskEntity
import com.example.domain.model.Task
import com.example.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { list -> list.map { it.toDomain() } }
    }

    override fun getActiveTasks(): Flow<List<Task>> {
        return taskDao.getActiveTasks().map { list -> list.map { it.toDomain() } }
    }

    override fun getCompletedTasks(): Flow<List<Task>> {
        return taskDao.getCompletedTasks().map { list -> list.map { it.toDomain() } }
    }

    override fun getSelectedTask(): Flow<Task?> {
        return taskDao.getSelectedTask().map { it?.toDomain() }
    }

    override suspend fun getTaskById(id: Long): Task? {
        return taskDao.getTaskById(id)?.toDomain()
    }

    override suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(TaskEntity.fromDomain(task))
    }

    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(TaskEntity.fromDomain(task.copy(updatedAt = System.currentTimeMillis())))
    }

    override suspend fun deleteTask(id: Long) {
        taskDao.deleteTaskById(id)
    }

    override suspend fun selectTask(id: Long) {
        taskDao.clearSelectedTasks()
        taskDao.setSelectedTask(id)
    }

    override suspend fun toggleTaskCompleted(id: Long) {
        val task = taskDao.getTaskById(id) ?: return
        val newStatus = !task.completed
        taskDao.setTaskCompleted(id, newStatus, System.currentTimeMillis())
    }

    override suspend fun incrementCompletedPomodoro(id: Long) {
        val now = System.currentTimeMillis()
        taskDao.incrementCompletedPomodoro(id, now)
        val updatedTask = taskDao.getTaskById(id)
        if (updatedTask != null && updatedTask.completedPomodoros >= updatedTask.estimatedPomodoros) {
            taskDao.setTaskCompleted(id, true, now)
            taskDao.clearSelectedTasks()
            val nextTask = taskDao.getFirstActiveTask()
            if (nextTask != null) {
                taskDao.setSelectedTask(nextTask.id)
            }
        }
    }
}
