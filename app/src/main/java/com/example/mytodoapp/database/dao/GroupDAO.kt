package com.example.mytodoapp.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mytodoapp.database.entities.TasksGroup

@Dao
interface GroupDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(group: TasksGroup)

    @Delete
    fun remove(group: TasksGroup)

    @Update
    fun update(group: TasksGroup)

    @Query("SELECT taskGroupID FROM groups WHERE groupTitle = :groupTitle COLLATE NOCASE AND taskGroupID != :groupId")
    suspend fun checkTitleUniqueness(groupTitle: String?, groupId: String?): List<String>

    @Query("SELECT * FROM groups")
    suspend fun fetch(): List<TasksGroup>

    @Query("SELECT COUNT(*) FROM groups")
    suspend fun fetchCount(): Int
}