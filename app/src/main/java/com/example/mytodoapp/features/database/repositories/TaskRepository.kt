package com.example.mytodoapp.features.database.repositories

import androidx.annotation.WorkerThread
import com.example.mytodoapp.components.interfaces.BaseRepository
import com.example.mytodoapp.features.database.dao.TaskDAO
import com.example.mytodoapp.features.database.entities.Task
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

open class TaskRepository @Inject constructor(
    private val tasks: TaskDAO,
//    private val preferenceManager: MySharedPreferenceManager
) : BaseRepository<Task> {

    override val allItems: Flow<List<Task>> = tasks.fetchAllTasks()

    fun fetchTasksSelectedGroup(groupID: String): Flow<List<Task>> =
        tasks.fetchTasksSelectedGroup(groupID)

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @WorkerThread
    override suspend fun insert(item: Task) {
        tasks.insert(item)
    }

    @WorkerThread
    override suspend fun delete(item: Task) {
        tasks.delete(item)
    }

    @WorkerThread
    override suspend fun update(item: Task) {
        tasks.update(item)
    }

    @WorkerThread
    suspend fun setFinished(taskID: String, isFinished: Boolean) {
        tasks.setFinished(taskID, isFinished)
    }
}