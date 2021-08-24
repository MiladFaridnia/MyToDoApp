package com.faridnia.mytodoapp.data

import androidx.room.Database

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase {

    abstract fun taskDao(): TaskDao

}