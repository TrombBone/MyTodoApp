package com.example.mytodoapp.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mytodoapp.database.entities.TasksGroup
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(group: TasksGroup)

    @Delete
    suspend fun delete(group: TasksGroup)

    @Update
    suspend fun update(group: TasksGroup)

    @Query("SELECT * FROM groups")
    fun fetchAllGroups(): Flow<List<TasksGroup>>

    @Query("SELECT COUNT(*) FROM groups")
    fun countAllGroups(): Flow<Int>

    // FIXME: Do I really need it?
    @Query("SELECT taskGroupID FROM groups WHERE groupTitle = :groupTitle COLLATE NOCASE AND taskGroupID != :groupId")
    fun checkTitleUniqueness(groupTitle: String?, groupId: String?): Flow<List<String>>
}