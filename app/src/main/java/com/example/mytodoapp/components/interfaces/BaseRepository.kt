package com.example.mytodoapp.components.interfaces

import androidx.annotation.WorkerThread
import androidx.room.Dao
import kotlinx.coroutines.flow.Flow

interface BaseRepository<T> {

    val allItems: Flow<List<T>>

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @WorkerThread
    suspend fun insert(item: T)

    @WorkerThread
    suspend fun update(item: T)

    @WorkerThread
    suspend fun delete(item: T)
}