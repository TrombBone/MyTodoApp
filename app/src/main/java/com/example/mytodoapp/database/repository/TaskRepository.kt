package com.example.mytodoapp.database.repository

import android.app.NotificationManager
import android.content.Context
import androidx.work.WorkManager
import com.example.mytodoapp.database.dao.TaskDAO
import com.example.mytodoapp.features.task.Task
import com.example.mytodoapp.utils.MySharedPreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TaskRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val tasks: TaskDAO,
    private val preferenceManager: MySharedPreferenceManager,
    private val workManager: WorkManager,
    private val notificationManager: NotificationManager
) {
    suspend fun fetch(): List<Task> = tasks.fetch()

    suspend fun fetchCount(): Int = tasks.fetchCount()

    suspend fun checkNameUniqueness(name: String?, id: String?): List<String> = tasks.checkNameUniqueness(name, id)

    suspend fun fetchSelectedGroup(group: String): List<Task> = tasks.fetchSelectGroup(group)

    // TODO in all functions:
    // TODO: use preference manager like as shared preferences
    // TODO: use Worker
    // TODO: use Widgets

    suspend fun insert(task: Task) {
        tasks.insert(task)

    }

    suspend fun remove(task: Task) {
        tasks.remove(task)

    }

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