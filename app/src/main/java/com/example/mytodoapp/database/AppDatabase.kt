package com.example.mytodoapp.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mytodoapp.database.converters.DateTimeConverter
import com.example.mytodoapp.database.dao.GroupDAO
import com.example.mytodoapp.database.dao.TaskDAO
import com.example.mytodoapp.database.entities.Task
import com.example.mytodoapp.database.entities.TasksGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Task::class, TasksGroup::class], // , Schedule::class
    version = AppDatabase.DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters(DateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getTaskDAO(): TaskDAO
    abstract fun getGroupDAO(): GroupDAO

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "TodoAppDatabase"

        @Volatile
        var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context, scope: CoroutineScope): AppDatabase =
            INSTANCE ?: synchronized(this) {
                // if the INSTANCE is not null, then return it,
                // if it is, then create the database
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(TasksDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }

        private class TasksDatabaseCallback(private val scope: CoroutineScope) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.getTaskDAO(), database.getGroupDAO())
                    }
                }
            }
        }

        /**
         * Populate the database in a new coroutine.
         * If you want to start with more words, just add them.
         */
        suspend fun populateDatabase(taskDAO: TaskDAO, groupDAO: GroupDAO) {
            // TODO: get group name from res
            groupDAO.insert(TasksGroup("0", "My Tasks"))


            // Add temp samples

            //taskDAO.deleteAll()

            var task = Task(
                "0",
                title = "This is empty task example!"
            )
            taskDAO.insert(task)
            task = Task(
                "1",
                title = "This is task with details example!",
                details = "Example details"
            )
            taskDAO.insert(task)
            task = Task(
                "3",
                title = "This is ready task example!",
                isFinished = true
            )
            taskDAO.insert(task)
            task = Task(
                "4",
                title = "This is stared task example!",
                isStared = true
            )
            taskDAO.insert(task)
        }
    }

}