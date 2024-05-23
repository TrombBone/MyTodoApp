package com.example.mytodoapp.features.database.repositories

import androidx.annotation.WorkerThread
import com.example.mytodoapp.features.database.dao.TaskDAO
import com.example.mytodoapp.features.database.entities.Task
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val tasks: TaskDAO,
//    private val preferenceManager: MySharedPreferenceManager
) {

    val allTasks: Flow<List<Task>> = tasks.fetchAllTasks()

    fun fetchTasksSelectedGroup(groupID: String): Flow<List<Task>> =
        tasks.fetchTasksSelectedGroup(groupID)

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

    @WorkerThread
    suspend fun setFinished(taskID: String, isFinished: Boolean) {
        if (isFinished) tasks.setFinished(taskID, 1)
        else tasks.setFinished(taskID, 0)
    }
}