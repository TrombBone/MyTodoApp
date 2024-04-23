package com.example.mytodoapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mytodoapp.database.converters.DateTimeConverter
import com.example.mytodoapp.database.dao.TaskDAO
import com.example.mytodoapp.features.task.Task

@Database(
    entities = [Task::class], // , Schedule::class
    version = AppDatabase.DATABASE_VERSION,
    exportSchema = true
)
@TypeConverters(DateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getTaskDAO(): TaskDAO

    companion object {
        const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "TodoAppDatabase"

        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .build()
            instance!!
        }
    }

}