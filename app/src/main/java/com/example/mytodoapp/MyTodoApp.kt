package com.example.mytodoapp

import android.app.Application
import com.example.mytodoapp.database.AppDatabase
import com.example.mytodoapp.database.repository.TaskRepository
import com.example.mytodoapp.utils.MySharedPreferenceManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class MyTodoApp : Application() {

    // No need to cancel this scope as it'll be torn down with the process
    private val applicationScope = CoroutineScope(SupervisorJob())

    private val preferenceManager by lazy { MySharedPreferenceManager(this) }

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
     val database by lazy { AppDatabase.getInstance(this, applicationScope) }
    val taskRepository by lazy { TaskRepository(this, database.getTaskDAO(), preferenceManager) }

}