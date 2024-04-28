package com.example.mytodoapp.di

import android.content.Context
import androidx.room.Room
import com.example.mytodoapp.database.AppDatabase
import com.example.mytodoapp.database.dao.GroupDAO
import com.example.mytodoapp.database.dao.TaskDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    fun provideTaskDao(database: AppDatabase): TaskDAO {
        return database.getTaskDAO()
    }

    @Provides
    fun provideGroupDao(database: AppDatabase): GroupDAO {
        return database.getGroupDAO()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .build()
    }
}