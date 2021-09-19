package com.faridnia.mytodoapp.ui.deletallcompeleted

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.faridnia.mytodoapp.data.TaskDao
import com.faridnia.mytodoapp.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DeleteAllCompletedTaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    @ApplicationScope private val coroutineScope: CoroutineScope
) : ViewModel() {

    fun onConfirmDialog()  = coroutineScope.launch {
        taskDao.deleteAllCompletedTasks()
    }

}