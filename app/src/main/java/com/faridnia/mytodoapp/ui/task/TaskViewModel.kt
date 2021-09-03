package com.faridnia.mytodoapp.ui.task

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.faridnia.mytodoapp.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

class TaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    val sortOrder = MutableStateFlow(SortOrder.BY_NAME)
    val hideCompleted = MutableStateFlow(false)

    private val taskFlow = combine(
        searchQuery,
        sortOrder,
        hideCompleted
    )
    { query, sortOrder, hideCompleted ->

        Triple(query, sortOrder, hideCompleted)

    }.flatMapLatest { (query, sortOrder, hideCompleted) ->

        taskDao.getTasks(query, sortOrder, hideCompleted)

    }

    val tasks = taskFlow.asLiveData()

    enum class SortOrder {
        BY_NAME,
        BY_DATE
    }

}