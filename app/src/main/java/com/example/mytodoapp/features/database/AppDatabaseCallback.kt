package com.example.mytodoapp.features.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mytodoapp.features.database.dao.GroupDAO
import com.example.mytodoapp.features.database.dao.TaskDAO
import com.example.mytodoapp.features.database.entities.Task
import com.example.mytodoapp.features.database.entities.TasksGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
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
        // TODO: delete after removing all tasks group from groups
        groupDAO.insert(TasksGroup("1", "My Tasks"))

        // Add temp samples

//            taskDAO.deleteAll()

        var task =  Task(
            title = "Example task!",
            details = "Example details!"
        )
        taskDAO.insert(task)
        task = Task(
            title = "Ready task example!",
            isFinished = true
        )
        taskDAO.insert(task)
        task = Task(
            title = "This is stared task example!",
            isStared = true
        )
        taskDAO.insert(task)
        task = Task(
            title = "Today task example!",
            dueDate = LocalDate.now()
        )
        taskDAO.insert(task)
        task = Task(
            title = "Soon task example!",
            dueDate = LocalDate.now(),
            dueTime = LocalTime.now().plusMinutes(5)
        )
        taskDAO.insert(task)
    }
}