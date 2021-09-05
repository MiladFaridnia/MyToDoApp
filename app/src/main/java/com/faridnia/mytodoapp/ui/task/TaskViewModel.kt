package com.faridnia.mytodoapp.ui.task

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.faridnia.mytodoapp.data.PreferencesManager
import com.faridnia.mytodoapp.data.SortOrder
import com.faridnia.mytodoapp.data.Task
import com.faridnia.mytodoapp.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class TaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    val preferencesFlow = preferencesManager.preferenceFlow

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
    }

    val tasks = taskFlow.asLiveData()


}