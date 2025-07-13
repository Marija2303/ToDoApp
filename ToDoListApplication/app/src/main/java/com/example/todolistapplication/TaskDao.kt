package com.example.todolistapplication

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {
    @Query("Select * from 'tasks'")
    suspend fun getAllTasks(): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTask(task: TaskEntity)

    @Delete
    suspend fun removeTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)
}
