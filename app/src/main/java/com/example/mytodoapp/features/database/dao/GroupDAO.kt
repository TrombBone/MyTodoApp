package com.example.mytodoapp.features.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mytodoapp.features.database.entities.TasksGroup
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

 }