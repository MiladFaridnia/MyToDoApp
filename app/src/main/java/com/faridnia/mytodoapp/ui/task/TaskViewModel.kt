package com.faridnia.mytodoapp.ui.task

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.faridnia.mytodoapp.RESULT_ADD_OK
import com.faridnia.mytodoapp.RESULT_EDIT_OK
import com.faridnia.mytodoapp.data.PreferencesManager
import com.faridnia.mytodoapp.data.SortOrder
import com.faridnia.mytodoapp.data.Task
import com.faridnia.mytodoapp.data.TaskDao
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    val preferencesFlow = preferencesManager.preferenceFlow

    private val taskEventChannel = Channel<TaskEvent>()

    val taskEventFlow = taskEventChannel.receiveAsFlow()

    private val taskFlow = combine(
        searchQuery,
        preferencesFlow
    )
    { query, filterPreferences ->

        Pair(query, filterPreferences)

    }.flatMapLatest { (query, filterPreferences) ->

        taskDao.getTasks(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)

    }

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedSelected(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    fun onTaskItemCheckChanged(task: Task, checked: Boolean) {
        viewModelScope.launch {
            taskDao.update(task.copy(isCompleted = checked))
        }
    }

    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        taskEventChannel.send(TaskEvent.ShowUndoDeleteTaskMessage(task))
    }

    fun onUndoDeleteClicked(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    fun onAddNewTaskClicked() = viewModelScope.launch {
        taskEventChannel.send(TaskEvent.ShowAddTaskFragment)
    }

    fun onTaskSelected(task: Task) = viewModelScope.launch {
        taskEventChannel.send(TaskEvent.ShowEditTaskFragment(task))
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            RESULT_ADD_OK -> showTaskConfirmationMessage("Task Added")
            RESULT_EDIT_OK -> showTaskConfirmationMessage("Task Updated")
        }
    }

    private fun showTaskConfirmationMessage(message: String) = viewModelScope.launch {
        taskEventChannel.send(TaskEvent.ShowTaskSavedConfirmationMessage(message))
    }

    fun onDeleteAllCompletedClicked() = viewModelScope.launch {
        taskEventChannel.send(TaskEvent.ShowDeleteAllCompletedScreen)
    }

    val tasks = taskFlow.asLiveData()


    sealed class TaskEvent {
        object ShowAddTaskFragment : TaskEvent()
        object ShowDeleteAllCompletedScreen : TaskEvent()
        data class ShowTaskSavedConfirmationMessage(val message: String) : TaskEvent()
        data class ShowEditTaskFragment(val task: Task) : TaskEvent()
        data class ShowUndoDeleteTaskMessage(val task: Task) : TaskEvent()

    }

}