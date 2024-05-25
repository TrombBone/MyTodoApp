package com.example.mytodoapp.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mytodoapp.features.database.AppDatabase
import com.example.mytodoapp.features.database.dao.GroupDAO
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
class GroupDAOTest {

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
        groupDAO = db.getGroupDAO()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun groupInsertFetchUpdateFetchDeleteCheck() = runBlocking {
        val group = TasksGroup(groupTitle = "New Group")
        groupDAO.insert(group)
        var groups = groupDAO.fetchAllGroups().first()
        assertEquals(groups[0].taskGroupID, group.taskGroupID)

        assertTrue(groups[0].groupTitle == group.groupTitle)
        groupDAO.update(group.copy(groupTitle = "My New Group"))
        groups = groupDAO.fetchAllGroups().first()
        assertTrue(groups[0].groupTitle == "My New Group")
        assertFalse(groups[0].groupTitle == group.groupTitle)

        groupDAO.delete(group)
        groups = groupDAO.fetchAllGroups().first()
        assertTrue(groups.isEmpty())
    }

}