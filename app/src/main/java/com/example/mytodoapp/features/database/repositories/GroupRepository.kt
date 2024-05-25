package com.example.mytodoapp.features.database.repositories

import androidx.annotation.WorkerThread
import com.example.mytodoapp.components.interfaces.BaseRepository
import com.example.mytodoapp.features.database.dao.GroupDAO
import com.example.mytodoapp.features.database.entities.TasksGroup
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GroupRepository @Inject constructor(
    private val groups: GroupDAO,
//    private val preferenceManager: MySharedPreferenceManager
) : BaseRepository<TasksGroup> {

    override val allItems: Flow<List<TasksGroup>> = groups.fetchAllGroups()

    @WorkerThread
    override suspend fun insert(item: TasksGroup) {
        groups.insert(item)
    }

    @WorkerThread
    override suspend fun delete(item: TasksGroup) {
        groups.delete(item)
    }

    @WorkerThread
    override suspend fun update(item: TasksGroup) {
        groups.update(item)
    }

}