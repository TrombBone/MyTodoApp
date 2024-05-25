package com.example.mytodoapp.database

import com.example.mytodoapp.features.database.entities.Task
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class TaskUnitTest {

    @Test
    fun `should return true when a task has details`() {
        val task = Task()
        task.details = "details"

        assertTrue(task.hasDetails())
    }

    @Test
    fun `should return true when a task has due date`() {
        val task = Task()
        task.dueDate = LocalDate.now()

        assertTrue(task.hasDueDate())
    }

    @Test
    fun `should return true when a task has due time`() {
        val task = Task()
        task.dueTime = LocalTime.now()

        assertTrue(task.hasDueTime())
    }

    @Test
    fun `should return true when task has due date is today`() {
        val task = Task()
        task.dueDate = LocalDate.now()

        assertTrue(task.isDueToday())
    }

    @Test
    fun `should return true when the task has due date in the future`() {
        val task = Task()
        task.dueDate = LocalDate.now().plusDays(1)

        assertTrue(task.isDueDateInFuture())
    }

    @Test
    fun `should return true when the task has due time in later today`() {
        val task = Task()
        task.dueTime = LocalTime.now().plusHours(1)

        assertTrue(task.isDueTimeInLaterToday())
    }

    @Test
    fun `should return true when the task has due date-time in future`() {
        val task = Task()
        task.dueDate = LocalDate.now().plusDays(1)
        task.dueTime = LocalTime.now().plusHours(1)
        assertTrue(task.isDueInFuture())

        task.dueDate = LocalDate.now()
        task.dueTime = LocalTime.now().plusHours(1)
        assertTrue(task.isDueInFuture())

        task.dueDate = LocalDate.now().plusDays(1)
        task.dueTime = null
        assertTrue(task.isDueInFuture())
    }
}