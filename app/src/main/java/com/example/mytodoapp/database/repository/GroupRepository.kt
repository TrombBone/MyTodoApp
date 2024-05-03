package com.example.mytodoapp.database.repository

import android.content.Context
import androidx.annotation.WorkerThread
import com.example.mytodoapp.database.dao.GroupDAO
import com.example.mytodoapp.database.entities.TasksGroup
import com.example.mytodoapp.utils.MySharedPreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GroupRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val groups: GroupDAO,
    private val preferenceManager: MySharedPreferenceManager
) {
    val allGroups: Flow<List<TasksGroup>> = groups.fetchAllGroups()

    val countGroups: Flow<Int> = groups.countAllGroups()

    // FIXME: Do I really need it?
    fun checkTitleUniqueness(title: String?, id: String?): Flow<List<String>> = groups.checkTitleUniqueness(title, id)

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