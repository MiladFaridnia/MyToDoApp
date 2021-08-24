package com.faridnia.mytodoapp.di

import android.app.Application
import androidx.room.Room
import com.faridnia.mytodoapp.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(application: Application) =
        Room.databaseBuilder(application, TaskDatabase::class.java, "task_database").build()

    fun provideDao(taskDatabase: TaskDatabase) = taskDatabase.taskDao()
}