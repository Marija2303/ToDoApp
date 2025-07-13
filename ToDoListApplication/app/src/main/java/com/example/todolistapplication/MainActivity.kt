package com.example.todolistapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolistapplication.ui.theme.ToDoListApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = ToDoDatabase.getDatabase(applicationContext)
        val dao = database.taskDao()
        val repository = ToDoRepository(dao)
        val viewModelFactory = ToDoViewModelFactory(repository)
        val viewModel: ToDoViewModel = ViewModelProvider(this, viewModelFactory)[ToDoViewModel::class.java]

        enableEdgeToEdge()

        setContent {
            ToDoListApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ToDoList(modifier = Modifier.padding(innerPadding), viewModel)
                }
            }
        }
    }
}

@Composable
fun ToDoList(modifier: Modifier = Modifier, viewModel: ToDoViewModel) {
    val tasks = viewModel.taskList                       // State-backed list of tasks
    val newTask = remember { mutableStateOf("") }        // Input field state
    val focusManager = LocalFocusManager.current         // Used to dismiss keyboard on background tap

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .pointerInput(Unit) {
                // Detect background tap to dismiss the keyboard
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        // Header title
        Text(
            text = "To-do list:",
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 8.dp),
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.padding(8.dp))

        // Input row: TextField + Add Button
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = newTask.value,
                onValueChange = { newTask.value = it },
                label = { Text("Add new task") },
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = {
                    viewModel.addTask(newTask.value)
                    newTask.value = ""
                }
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add task")
            }
        }

        Spacer(modifier = Modifier.padding(8.dp))

        // Task list
        LazyColumn {
            items(tasks) { task ->
                ToDoTask(
                    task = task,
                    onCheckedChange = { isChecked ->
                        viewModel.updateTask(task, isChecked)
                    },
                    onDeleteClick = {
                        viewModel.removeTask(task)
                    }
                )
            }
        }
    }
}

@Composable
fun ToDoTask(
    task: TaskEntity,
    onCheckedChange: (Boolean) -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Checkbox(
            checked = task.isChecked,
            onCheckedChange = onCheckedChange
        )

        Text(
            text = task.task,
            style = if (task.isChecked) {
                TextStyle(textDecoration = TextDecoration.LineThrough)
            } else {
                TextStyle.Default
            }
        )

        Spacer(modifier = Modifier.weight(1f)) // Pushes the delete icon to the end

        IconButton(onClick = onDeleteClick) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete task")
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun TaskPreview() {
//    ToDoListApplicationTheme {
//        val task = TaskEntity("Sample task", isChecked = true)
//        ToDoTask(task, {}, {})
//    }
//}
