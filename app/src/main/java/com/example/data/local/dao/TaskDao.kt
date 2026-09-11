package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY completed ASC, priority DESC, createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE completed = 0 ORDER BY priority DESC, createdAt DESC")
    fun getActiveTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE completed = 1 ORDER BY updatedAt DESC")
    fun getCompletedTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isSelected = 1 LIMIT 1")
    fun getSelectedTask(): Flow<TaskEntity?>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Query("UPDATE tasks SET isSelected = 0")
    suspend fun clearSelectedTasks()

    @Query("UPDATE tasks SET isSelected = 1 WHERE id = :id")
    suspend fun setSelectedTask(id: Long)

    @Query("UPDATE tasks SET completed = :completed, updatedAt = :timestamp WHERE id = :id")
    suspend fun setTaskCompleted(id: Long, completed: Boolean, timestamp: Long)

    @Query("UPDATE tasks SET completedPomodoros = completedPomodoros + 1, updatedAt = :timestamp WHERE id = :id")
    suspend fun incrementCompletedPomodoro(id: Long, timestamp: Long)

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun getTaskCount(): Int

    @Query("SELECT * FROM tasks WHERE completed = 0 ORDER BY priority DESC, createdAt DESC LIMIT 1")
    suspend fun getFirstActiveTask(): TaskEntity?
}
