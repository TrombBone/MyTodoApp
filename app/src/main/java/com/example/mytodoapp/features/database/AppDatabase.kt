package com.example.mytodoapp.features.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mytodoapp.features.database.converters.DateTimeConverter
import com.example.mytodoapp.features.database.dao.GroupDAO
import com.example.mytodoapp.features.database.dao.TaskDAO
import com.example.mytodoapp.features.database.entities.Task
import com.example.mytodoapp.features.database.entities.TasksGroup

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
    }

}