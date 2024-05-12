package com.example.mytodoapp.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mytodoapp.database.dao.GroupDAO
import com.example.mytodoapp.database.dao.TaskDAO
import com.example.mytodoapp.database.entities.Task
import com.example.mytodoapp.database.entities.TasksGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Provider

class AppDatabaseCallback(
    private val providerTaskDAO: Provider<TaskDAO>,
    private val providerGroupDAO: Provider<GroupDAO>
) : RoomDatabase.Callback() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        applicationScope.launch(Dispatchers.IO) {
            populateDatabase(providerTaskDAO.get(), providerGroupDAO.get())
        }
    }

    /**
     * Populate the database in a new coroutine.
     */
    private suspend fun populateDatabase(taskDAO: TaskDAO, groupDAO: GroupDAO) {
        // TODO: get this group name from res
        groupDAO.insert(TasksGroup("1", "My Tasks"))

        // Add temp samples

//            taskDAO.deleteAll()

        var task = Task(
            title = "This is empty task example!"
        )
        taskDAO.insert(task)
        task = Task(
            title = "This is task with details example!",
            details = "Example details"
        )
        taskDAO.insert(task)
        task = Task(
            title = "This is ready task example!",
            isFinished = true
        )
        taskDAO.insert(task)
        task = Task(
            title = "This is stared task example!",
            isStared = true
        )
        taskDAO.insert(task)
    }
}