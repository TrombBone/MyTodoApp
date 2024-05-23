package com.example.mytodoapp.features.database.repositories

import androidx.annotation.WorkerThread
import com.example.mytodoapp.features.database.dao.GroupDAO
import com.example.mytodoapp.features.database.entities.TasksGroup
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GroupRepository @Inject constructor(
    private val groups: GroupDAO,
//    private val preferenceManager: MySharedPreferenceManager
) {
    val allGroups: Flow<List<TasksGroup>> = groups.fetchAllGroups()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @WorkerThread
    suspend fun insert(group: TasksGroup) {
        groups.insert(group)
    }

    @WorkerThread
    suspend fun delete(group: TasksGroup) {
        groups.delete(group)
    }

    @WorkerThread
    suspend fun update(group: TasksGroup) {
        groups.update(group)
    }
}