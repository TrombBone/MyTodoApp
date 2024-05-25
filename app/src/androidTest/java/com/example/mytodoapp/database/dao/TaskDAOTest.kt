package com.example.mytodoapp.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mytodoapp.features.database.AppDatabase
import com.example.mytodoapp.features.database.dao.GroupDAO
import com.example.mytodoapp.features.database.dao.TaskDAO
import com.example.mytodoapp.features.database.entities.Task
import com.example.mytodoapp.features.database.entities.TasksGroup
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TaskDAOTest {

    private lateinit var taskDAO: TaskDAO
    private lateinit var groupDAO: GroupDAO
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        taskDAO = db.getTaskDAO()
        groupDAO = db.getGroupDAO()
    }

    @Before
    fun createDefaultGroup() = runBlocking {
        groupDAO.insert(TasksGroup("1", "My Tasks"))
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun taskInsertFetchUpdateFetchDeleteCheck() = runBlocking {
        val task = Task()
        taskDAO.insert(task)
        var tasks = taskDAO.fetchAllTasks().first()
        assertEquals(tasks[0].taskID, task.taskID)

        assertFalse(tasks[0].hasDetails())
        taskDAO.update(task.copy(details = "details"))
        tasks = taskDAO.fetchAllTasks().first()
        assertTrue(tasks[0].hasDetails())

        taskDAO.delete(task)
        tasks = taskDAO.fetchAllTasks().first()
        assertTrue(tasks.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun taskInsertAndThanFetchSelectedGroupAndThanDeleteAll() = runBlocking {
        groupDAO.insert(TasksGroup("2"))

        val task1Group1 = Task(groupID = "1")
        taskDAO.insert(task1Group1)
        val task2Group1 = Task(groupID = "1")
        taskDAO.insert(task2Group1)
        val task3Group1 = Task(groupID = "1")
        taskDAO.insert(task3Group1)
        val task1Group2 = Task(groupID = "2")
        taskDAO.insert(task1Group2)
        val task2Group2 = Task(groupID = "2")
        taskDAO.insert(task2Group2)
        val task3Group2 = Task(groupID = "2")
        taskDAO.insert(task3Group2)

        var tasksAll = taskDAO.fetchAllTasks().first()
        assertEquals(tasksAll.size, 6)

        val tasks1 = taskDAO.fetchTasksSelectedGroup("1").first()
        assertEquals(tasks1.size, 3)

        val tasks2 = taskDAO.fetchTasksSelectedGroup("2").first()
        assertEquals(tasks2.size, 3)

        taskDAO.deleteAll()
        tasksAll = taskDAO.fetchAllTasks().first()
        assertTrue(tasksAll.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun taskInsertAndThanSetFinished() = runBlocking {
        val task = Task()
        taskDAO.insert(task)
        var tasks = taskDAO.fetchAllTasks().first()
        assertEquals(tasks[0].taskID, task.taskID)

        assertFalse(tasks[0].isFinished)
        taskDAO.setFinished(tasks[0].taskID, true)
        tasks = taskDAO.fetchAllTasks().first()
        assertTrue(tasks[0].isFinished)

        assertTrue(tasks[0].isFinished)
        taskDAO.setFinished(tasks[0].taskID, false)
        tasks = taskDAO.fetchAllTasks().first()
        assertFalse(tasks[0].isFinished)
    }
}