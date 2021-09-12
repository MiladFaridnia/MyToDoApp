package com.faridnia.mytodoapp.ui.addedit

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.faridnia.mytodoapp.data.Task
import com.faridnia.mytodoapp.data.TaskDao


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


}