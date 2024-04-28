package com.example.mytodoapp.database.repository

import android.content.Context
import androidx.annotation.WorkerThread
import com.example.mytodoapp.database.dao.TaskDAO
import com.example.mytodoapp.database.entities.Task
import com.example.mytodoapp.utils.MySharedPreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow


class TaskRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val tasks: TaskDAO,
    private val preferenceManager: MySharedPreferenceManager,
//    private val workManager: WorkManager,
//    private val notificationManager: NotificationManager
) {

    val allTasksJoinGroups: Flow<List<Task>> = tasks.fetchAllWithGroups()

    val allTasks: Flow<List<Task>> = tasks.fetchAllTasks()

    fun fetchSelectedGroup(group: String): Flow<List<Task>> = tasks.fetchAllOnSelectedGroup(group)

    suspend fun checkTitleUniqueness(title: String?, id: String?): List<String> = tasks.checkTitleUniqueness(title, id)

    @WorkerThread
    suspend fun fetchCountAllTasks(): Int = tasks.fetchCountAllTasks()

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

    suspend fun setFinished(taskID: String, status: Boolean) {
        if (status)
            tasks.setFinished(taskID, 1)
        else tasks.setFinished(taskID, 0)
    }

    suspend fun setGroup(taskID: String, group: String) {
        tasks.setGroup(taskID, group)
    }
}