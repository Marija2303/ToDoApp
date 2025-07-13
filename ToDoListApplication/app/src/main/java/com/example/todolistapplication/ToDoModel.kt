package com.example.todolistapplication

import androidx.compose.runtime.mutableStateListOf
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class representing a single task in the to-do list.
 * @param task The task description.
 * @param isChecked Indicates whether the task is completed.
 */

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val task: String,
    var isChecked: Boolean = false
)

/**
 * Repository class that handles all data operations related to tasks.
 * Uses a mutableStateListOf to provide observable state for Jetpack Compose.
 */
class ToDoRepository(private val dao: TaskDao) {

    // Internal mutable list of tasks. Exposed as a read-only list.
    private val _taskList = mutableStateListOf<TaskEntity>()
    val taskList: List<TaskEntity> get() = _taskList

    suspend fun loadTasks() {
        val tasksFromDatabase = dao.getAllTasks()
        _taskList.clear()
        _taskList.addAll(tasksFromDatabase)
    }

    /**
     * Adds a new task to the list if it's not blank and doesn't already exist (case-insensitive).
     * @param newTask The task text to be added.
     */
    suspend fun addTask(newTask: String) {
        val entry = newTask.trim()
        if (entry.isNotBlank() && _taskList.none { it.task.equals(entry, ignoreCase = true) }) {
            val newEntity = TaskEntity(task = entry)
            dao.addTask(newEntity)
            loadTasks()
        }
    }

    /**
     * Removes a task from the list.
     * @param task The task object to remove.
     */
    suspend fun removeTask(task: TaskEntity) {
        dao.removeTask(task)
        _taskList.remove(task)
    }

    /**
     * Updates the checked (completed) state of a task.
     * Uses copy-and-replace strategy to ensure Compose detects the state change.
     * @param task The task to update.
     * @param checked The new checked state.
     */
    suspend fun updateTask(task: TaskEntity, checked: Boolean) {
        val updated = task.copy(isChecked = checked)
        dao.updateTask(updated)

        val index = _taskList.indexOf(task)
        if (index != -1) {
            _taskList[index] = updated
        }
    }
}
