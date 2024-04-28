package com.example.mytodoapp.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.mytodoapp.database.entities.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Update
    suspend fun update(task: Task)

    @Query("SELECT taskID FROM tasks WHERE title = :taskTitle COLLATE NOCASE AND taskID != :taskId")
    fun checkTitleUniqueness(taskTitle: String?, taskId: String?): List<String>

    @Query("SELECT * FROM tasks WHERE groupID = :groupId")
    fun fetchAllOnSelectedGroup(groupId: String): Flow<List<Task>>

    @Transaction
    @Query("SELECT * FROM tasks LEFT JOIN groups ON tasks.groupID == groups.taskGroupID ORDER BY dueDate ASC")
    fun fetchAllWithGroups(): Flow<List<Task>>

    @Query("UPDATE tasks SET groupID = :groupId WHERE taskID = :taskId")
    suspend fun setGroup(taskId: String, groupId: String)

    @Query("UPDATE tasks SET isFinished = :status WHERE taskID = :taskId")
    suspend fun setFinished(taskId: String, status: Int)

    @Query("SELECT * FROM tasks ORDER BY taskID ASC")
    fun fetchAllTasks(): Flow<List<Task>>

    @Query("SELECT COUNT(*) FROM tasks")
    fun fetchCountAllTasks(): Int

    @Query("DELETE FROM tasks")
    suspend fun deleteAll()
}