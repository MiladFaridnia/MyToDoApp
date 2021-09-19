package com.faridnia.mytodoapp.data

import androidx.room.*
import com.faridnia.mytodoapp.ui.task.TaskViewModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * From task_table")
    fun getAllTasks(): Flow<List<Task>>

    fun getTasks(
        queryString: String,
        sortOrder: SortOrder,
        hideCompleted: Boolean
    ): Flow<List<Task>> = when (sortOrder) {
        SortOrder.BY_NAME -> getTasksSortedByName(queryString, hideCompleted)
        SortOrder.BY_DATE -> getTasksSortedByDate(queryString, hideCompleted)
    }

    @Query("SELECT * FROM task_table WHERE (isCompleted != :hideCompleted OR isCompleted = 0) AND name LIKE  '%' || :queryString || '%' ORDER BY isImportant,name DESC ")
    fun getTasksSortedByName(queryString: String, hideCompleted: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM task_table WHERE (isCompleted != :hideCompleted OR isCompleted = 0) AND name LIKE  '%' || :queryString || '%' ORDER BY isImportant,createDate DESC ")
    fun getTasksSortedByDate(queryString: String, hideCompleted: Boolean): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Update
    suspend fun update(task: Task)

    @Query("DELETE FROM task_table WHERE isCompleted = 1")
    fun deleteAllCompletedTasks()

}