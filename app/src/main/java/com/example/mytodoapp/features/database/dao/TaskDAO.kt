package com.example.mytodoapp.features.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mytodoapp.features.database.entities.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Update
    suspend fun update(task: Task)

    @Query("SELECT * FROM tasks WHERE groupID = :groupId")
    fun fetchTasksSelectedGroup(groupId: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks ORDER BY dueDate, dueTime ASC")
    fun fetchAllTasks(): Flow<List<Task>>

    @Query("DELETE FROM tasks")
    suspend fun deleteAll()

//    @Query("SELECT * FROM tasks LEFT JOIN groups ON tasks.groupID == groups.taskGroupID ORDER BY dueDate ASC")
//    fun fetchAllWithGroups(): Flow<List<Task>>

    @Query("UPDATE tasks SET isFinished = :isFinished WHERE taskID = :taskId")
    suspend fun setFinished(taskId: String, isFinished: Boolean)
}