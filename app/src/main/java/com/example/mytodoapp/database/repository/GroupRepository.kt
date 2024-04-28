package com.example.mytodoapp.database.repository

import android.content.Context
import com.example.mytodoapp.database.dao.GroupDAO
import com.example.mytodoapp.database.entities.TasksGroup
import com.example.mytodoapp.utils.MySharedPreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GroupRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val groups: GroupDAO,
    private val preferenceManager: MySharedPreferenceManager
) {
    suspend fun fetch(): List<TasksGroup> = groups.fetch()

    suspend fun fetchCount(): Int = groups.fetchCount()

    suspend fun checkTitleUniqueness(title: String?, id: String?): List<String> = groups.checkTitleUniqueness(title, id)

    suspend fun insert(group: TasksGroup) {
        groups.insert(group)
    }

    suspend fun remove(group: TasksGroup) {
        groups.remove(group)
    }

    suspend fun update(group: TasksGroup) {
        groups.update(group)
    }
}