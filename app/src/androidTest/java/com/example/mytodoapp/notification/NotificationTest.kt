package com.example.mytodoapp.notification

import android.Manifest
import android.app.Notification
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.example.mytodoapp.R
import com.example.mytodoapp.features.database.entities.Task
import com.example.mytodoapp.features.database.entities.TasksGroup
import com.example.mytodoapp.features.database.repositories.GroupRepository
import com.example.mytodoapp.features.database.repositories.TaskRepository
import com.example.mytodoapp.features.notifications.NotificationAlarmManager
import com.example.mytodoapp.notification.extentions.getNotificationManager
import com.example.mytodoapp.testhelpers.DisableAnimationsRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltAndroidTest
class NotificationTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var grantPostNotificationPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)

    @get:Rule(order = 2)
    val disableAnimationsRule = DisableAnimationsRule()

    @Inject
    lateinit var notificationAlarmManager: NotificationAlarmManager

    @Inject
    lateinit var taskRepository: TaskRepository

    @Inject
    lateinit var groupRepository: GroupRepository

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun init() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        context.getNotificationManager()?.cancelAll()
    }

    @Test
    fun testRegularNotification() {
        val taskTitle = "Testing notification title"
        val taskDetails = "Testing notification details"
        val task = createTaskAndAlarmAfterTwoSeconds(taskTitle, taskDetails)
        val taskID = task.taskID

        val manager = context.getNotificationManager()
        waitUntil { manager!!.activeNotifications.isNotEmpty() }

        with(manager!!.activeNotifications.first()) {
            assertEquals(UUID.fromString(taskID).mostSignificantBits.toInt(), this.id)
            assertEquals(taskTitle, this.notification.extras.getString(Notification.EXTRA_TITLE))
            assertEquals(taskDetails, this.notification.extras.getString(Notification.EXTRA_TEXT))
            assertFalse(isOngoing)
        }
    }

    @Test
    fun testStaredNotification() {
        val taskTitle = "Testing notification title"
        val taskDetails = "Testing notification details"
        val task = createTaskAndAlarmAfterTwoSeconds(taskTitle, taskDetails, true)
        val taskID = task.taskID

        val manager = context.getNotificationManager()
        waitUntil { manager!!.activeNotifications.isNotEmpty() }

        with(manager!!.activeNotifications.first()) {
            assertEquals(UUID.fromString(taskID).mostSignificantBits.toInt(), this.id)
            assertEquals(taskTitle, notification.extras.getString(Notification.EXTRA_TITLE))
            assertEquals(taskDetails, notification.extras.getString(Notification.EXTRA_TEXT))
            assertTrue(isOngoing)
        }
    }

    @Test
    fun testNotificationContentClick() {
        createTaskAndAlarmAfterTwoSeconds()

        val manager = context.getNotificationManager()
        waitUntil { manager!!.activeNotifications.isNotEmpty() }

        manager!!.activeNotifications.first().notification.contentIntent.send()

        onView(withId(R.id.all_pages_container)).check(matches(isDisplayed()))
    }

    @Test
    fun testNotificationActionSetFinishedClick() {
        runBlocking {
            if (groupRepository.allItems.first().isEmpty())
                groupRepository.insert(TasksGroup("1", "My Tasks"))
        }

        val task = createTaskAndAlarmAfterTwoSeconds()
        val taskID = task.taskID

        var taskInDb = runBlocking {
            if (taskRepository.allItems.first().isEmpty())
                groupRepository.insert(TasksGroup("1", "My Tasks"))
            taskRepository.insert(task)
            taskRepository.allItems.first().find { it.taskID == taskID }
        }
        assertFalse(taskInDb?.isFinished ?: true)

        val manager = context.getNotificationManager()
        waitUntil { manager!!.activeNotifications.isNotEmpty() }
        // Hardcode: setFinished action is in position 0
        manager!!.activeNotifications.first().notification.actions[0].actionIntent.send()

        waitUntil { manager.activeNotifications.isEmpty() }
        Thread.sleep(500)
        taskInDb = runBlocking {
            taskRepository.allItems.first().find { it.taskID == taskID }
        }
        assertTrue(taskInDb?.isFinished ?: false)

        runBlocking {
            taskRepository.delete(task)
        }
    }

    private fun createTaskAndAlarmAfterTwoSeconds(
        title: String = "Testing notification title",
        details: String = "Testing notification details",
        stared: Boolean = false,
    ): Task =
        with(Task(title = title, details = details, isStared = stared)) {
            dueDate = LocalDate.now()
            dueTime = LocalTime.now().plusSeconds(2)
            notificationAlarmManager.setAlarm(this)
            this
        }

    private fun waitUntil(waitTimeMillis: Long = 10_000, expression: () -> Boolean) {
        val endTime = System.currentTimeMillis() + waitTimeMillis
        do if (expression.invoke()) return
        while (System.currentTimeMillis() < endTime)
        throw TimeoutException()
    }
}