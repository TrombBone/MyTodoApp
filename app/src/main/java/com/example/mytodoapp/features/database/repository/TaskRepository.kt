package com.example.mytodoapp.features.database.repository

import android.content.Context
import androidx.annotation.WorkerThread
import com.example.mytodoapp.features.database.dao.TaskDAO
import com.example.mytodoapp.features.database.entities.Task
import com.example.mytodoapp.utils.MySharedPreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val tasks: TaskDAO,
    private val preferenceManager: MySharedPreferenceManager,
//    private val workManager: WorkManager,
//    private val notificationManager: NotificationManager
) {

    val allTasks: Flow<List<Task>> = tasks.fetchAllTasks()

    val allTasksCount: Flow<Int> = tasks.countAllTasks()

    fun fetchTasksSelectedGroup(groupID: String): Flow<List<Task>> =
        tasks.fetchTasksSelectedGroup(groupID)

    // TODO in all functions:
    // TODO: use preference manager like as shared preferences
    // TODO: use Worker
    // TODO: use Widgets

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @WorkerThread
    suspend fun insert(task: Task) {
        tasks.insert(task)
    }

    @WorkerThread
    suspend fun delete(task: Task) {
        tasks.delete(task)
    }

    @WorkerThread
    suspend fun update(task: Task) {
        tasks.update(task)
    }

}