package com.faridnia.mytodoapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.faridnia.mytodoapp.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().taskDao()

            //add sample data
            applicationScope.launch {
                dao.insert(Task(name = "Go to Pool"))
                dao.insert(Task(name = "Go to School", isCompleted = true))
                dao.insert(Task(name = "Go Home", isImportant = true))
                dao.insert(Task(name = "Go to Work"))
                dao.insert(Task(name = "Go to Trip"))
                dao.insert(Task(name = "Go to Anywhere", isImportant = true))
                dao.insert(Task(name = "Go to Sleep"))
                dao.insert(Task(name = "Go to Nowhere", isCompleted = true))
            }
        }
    }
}