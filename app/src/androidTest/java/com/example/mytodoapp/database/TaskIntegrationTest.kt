package com.example.mytodoapp.database

import android.content.Context
import android.text.format.DateFormat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mytodoapp.features.database.entities.Task
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class TaskIntegrationTest {

    private fun getContext(): Context = ApplicationProvider.getApplicationContext()

    @Test
    @Throws(Exception::class)
    fun taskTimeShowsInCorrectFormat() {
        val task = Task(
            dueTime = LocalTime.of(23, 32)
        )
        val formatTime = task.formatDueTime(getContext())
        if (DateFormat.is24HourFormat(getContext()))
            assertEquals("23:32", formatTime)
        else
            assertEquals("11:32 PM", formatTime)
    }

    @Test
    @Throws(Exception::class)
    fun taskDateShowsInCorrectFormatDefault() {
        val task = Task(
            dueDate = LocalDate.of(2020, 10, 10)
        )
        val formatDate = task.formatDueDateTime(getContext())
        when (Locale.getDefault()) {
            Locale.ENGLISH -> assertEquals("oct. 10", formatDate)
            Locale("ru", "RU") -> assertEquals("окт. 10", formatDate)
        }
    }

    @Test
    @Throws(Exception::class)
    fun taskDateShowsInCorrectFormatToday() {
        val task = Task(
            dueDate = LocalDate.now()
        )
        val formatDate = task.formatDueDateTime(getContext())
        when (Locale.getDefault()) {
            Locale.ENGLISH -> assertEquals("Today", formatDate)
            Locale("ru", "RU") -> assertEquals("Сегодня", formatDate)
        }
    }

    @Test
    @Throws(Exception::class)
    fun taskDateShowsInCorrectFormatTomorrow() {
        val task = Task(
            dueDate = LocalDate.now().plusDays(1)
        )
        val formatDate = task.formatDueDateTime(getContext())
        when (Locale.getDefault()) {
            Locale.ENGLISH -> assertEquals("Tomorrow", formatDate)
            Locale("ru", "RU") -> assertEquals("Завтра", formatDate)
        }
    }

    @Test
    @Throws(Exception::class)
    fun taskDateShowsInCorrectFormatYesterday() {
        val task = Task(
            dueDate = LocalDate.now().minusDays(1)
        )
        val formatDate = task.formatDueDateTime(getContext())
        when (Locale.getDefault()) {
            Locale.ENGLISH -> assertEquals("Yesterday", formatDate)
            Locale("ru", "RU") -> assertEquals("Вчера", formatDate)
        }
    }

    @Test
    @Throws(Exception::class)
    fun taskDateTimeShowsInCorrectFormatDefault() {
        val task = Task(
            dueDate = LocalDate.of(2020, 10, 10),
            dueTime = LocalTime.of(23, 32)
        )
        val formatDateTime = task.formatDueDateTime(getContext())
        if (DateFormat.is24HourFormat(getContext()))
            when (Locale.getDefault()) {
                Locale.ENGLISH -> assertEquals("oct. 10, 23:32", formatDateTime)
                Locale("ru", "RU") -> assertEquals("окт. 10, 23:32", formatDateTime)
            }
        else
            when (Locale.getDefault()) {
                Locale.ENGLISH -> assertEquals("oct. 10, 11:32 PM", formatDateTime)
                Locale("ru", "RU") -> assertEquals("окт. 10, 11:32 PM", formatDateTime)
            }
    }

    @Test
    @Throws(Exception::class)
    fun taskDateTimeShowsInCorrectFormatToday() {
        val task = Task(
            dueDate = LocalDate.now(),
            dueTime = LocalTime.of(23, 32)
        )
        val formatDateTime = task.formatDueDateTime(getContext())
        if (DateFormat.is24HourFormat(getContext()))
            when (Locale.getDefault()) {
                Locale.ENGLISH -> assertEquals("Today at 23:32", formatDateTime)
                Locale("ru", "RU") -> assertEquals("Сегодня в 23:32", formatDateTime)
            }
        else
            when (Locale.getDefault()) {
                Locale.ENGLISH -> assertEquals("Today at 11:32 PM", formatDateTime)
                Locale("ru", "RU") -> assertEquals("Сегодня в 11:32 PM", formatDateTime)
            }
    }

    @Test
    @Throws(Exception::class)
    fun taskDateTimeShowsInCorrectFormatTomorrow() {
        val task = Task(
            dueDate = LocalDate.now().plusDays(1),
            dueTime = LocalTime.of(23, 32)
        )
        val formatDateTime = task.formatDueDateTime(getContext())
        if (DateFormat.is24HourFormat(getContext()))
            when (Locale.getDefault()) {
                Locale.ENGLISH -> assertEquals("Tomorrow at 23:32", formatDateTime)
                Locale("ru", "RU") -> assertEquals("Завтра в 23:32", formatDateTime)
            }
        else
            when (Locale.getDefault()) {
                Locale.ENGLISH -> assertEquals("Tomorrow at 11:32 PM", formatDateTime)
                Locale("ru", "RU") -> assertEquals("Завтра в 11:32 PM", formatDateTime)
            }
    }

    @Test
    @Throws(Exception::class)
    fun taskDateTimeShowsInCorrectFormatYesterday() {
        val task = Task(
            dueDate = LocalDate.now().minusDays(1),
            dueTime = LocalTime.of(23, 32)
        )
        val formatDateTime = task.formatDueDateTime(getContext())
        if (DateFormat.is24HourFormat(getContext()))
            when (Locale.getDefault()) {
                Locale.ENGLISH -> assertEquals("Yesterday at 23:32", formatDateTime)
                Locale("ru", "RU") -> assertEquals("Вчера в 23:32", formatDateTime)
            }
        else
            when (Locale.getDefault()) {
                Locale.ENGLISH -> assertEquals("Yesterday at 11:32 PM", formatDateTime)
                Locale("ru", "RU") -> assertEquals("Вчера в 11:32 PM", formatDateTime)
            }
    }
}