package com.example.mytodoapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.mytodoapp.features.task.Task

@Dao
interface TaskDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(task: Task)

    @Delete
    fun remove(task: Task)

    @Update
    fun update(task: Task)

    @Query("SELECT taskID FROM tasks WHERE name = :task COLLATE NOCASE AND taskID != :taskId")
    suspend fun checkNameUniqueness(task: String?, taskId: String?): List<String>

    @Query("SELECT * FROM tasks WHERE isFinished = 0")
    suspend fun fetch(): List<Task>

    @Query("SELECT COUNT(*) FROM tasks WHERE isFinished = 0")
    suspend fun fetchCount(): Int

    @Query("SELECT * FROM tasks WHERE groupID = :group")
    suspend fun fetchSelectGroup(group: String): List<Task>

    @Query("UPDATE tasks SET groupID = :group WHERE taskID = :taskID")
    suspend fun setGroup(taskID: String, group: String)

    @Query("UPDATE tasks SET isFinished = :status WHERE taskID = :taskID")
    suspend fun setFinished(taskID: String, status: Int)

}