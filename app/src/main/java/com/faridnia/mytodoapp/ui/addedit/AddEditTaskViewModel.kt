package com.faridnia.mytodoapp.ui.addedit

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faridnia.mytodoapp.RESULT_ADD_OK
import com.faridnia.mytodoapp.RESULT_EDIT_OK
import com.faridnia.mytodoapp.data.Task
import com.faridnia.mytodoapp.data.TaskDao
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class AddEditTaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    val task = state.get<Task>("task")

    var taskName = state.get<String>("taskName") ?: task?.name ?: ""
        set(value) {
            field = value
            state.set("taskName", value)
        }

    var taskImportance = state.get<Boolean>("taskImportance") ?: task?.isImportant ?: false
        set(value) {
            field = value
            state.set("taskImportance", value)
        }

    private val taskEventChannel = Channel<AddEditTaskEvent>()
    val taskEvent = taskEventChannel.receiveAsFlow()

    fun onSaveClick() {
        if (taskName.isBlank()) {
            showInvalidInputMessage("Name can not be empty")
            return
        }

        if (task != null) {
            val updatedTask = task.copy(name = taskName, isImportant = taskImportance)
            updateTask(updatedTask)
        } else {
            val newTask = Task(name = taskName, isImportant = taskImportance)
            createNewTask(newTask)
        }
    }

    private fun showInvalidInputMessage(message: String) = viewModelScope.launch {
        taskEventChannel.send(AddEditTaskEvent.ShowInvalidInputMessage(message))
    }

    private fun createNewTask(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
        taskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(RESULT_ADD_OK))
    }

    private fun updateTask(task: Task) = viewModelScope.launch {
        taskDao.update(task)
        taskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(RESULT_EDIT_OK))

    }


    sealed class AddEditTaskEvent {
        data class ShowInvalidInputMessage(val message: String) : AddEditTaskEvent()
        data class NavigateBackWithResult(val resultCode: Int) : AddEditTaskEvent()
    }

}