package com.faridnia.mytodoapp.ui.task

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
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

    val tasks = taskFlow.asLiveData()


    sealed class TaskEvent {
        data class ShowUndoDeleteTaskMessage(val task: Task) : TaskEvent()
    }

}