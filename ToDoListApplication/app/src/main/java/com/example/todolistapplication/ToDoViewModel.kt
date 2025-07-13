package com.example.todolistapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class ToDoViewModelFactory(private val repository: ToDoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ToDoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ToDoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/**
 * ViewModel that serves as a bridge between the UI and the data repository.
 * It holds the task list state and exposes functions for managing tasks.
 */
class ToDoViewModel(private val repository: ToDoRepository) : ViewModel() {

    // Expose a read-only task list to the UI
    val taskList: List<TaskEntity> get() = repository.taskList

    init {
        viewModelScope.launch {
            repository.loadTasks()
        }
    }

    /**
     * Add a new task to the list.
     * @param newTask The description of the task to be added.
     */
    fun addTask(newTask: String) {
        viewModelScope.launch {
            repository.addTask(newTask)
        }
    }

    /**
     * Remove an existing task from the list.
     * @param task The task object to be removed.
     */
    fun removeTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.removeTask(task)
        }
    }

    /**
     * Update the checked (completed) state of a task.
     * @param task The task to be updated.
     * @param checked New checked state (true if completed).
     */
    fun updateTask(task: TaskEntity, checked: Boolean) {
        viewModelScope.launch {
            repository.updateTask(task, checked)
        }
    }
}
