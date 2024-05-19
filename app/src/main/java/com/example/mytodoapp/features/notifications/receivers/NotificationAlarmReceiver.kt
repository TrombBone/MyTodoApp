package com.example.mytodoapp.features.notifications.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.mytodoapp.features.database.entities.Task
import com.example.mytodoapp.features.notifications.NotificationAlarmManager
import com.example.mytodoapp.features.notifications.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Showing notification when alarm
 */
@AndroidEntryPoint
class NotificationAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var notificationAlarmManager: NotificationAlarmManager

    override fun onReceive(context: Context, intent: Intent) {
        var currentTask: Task? = null

        intent.action?.apply {
            takeIf { this == TAG_ACTION_SET_ALARM_NOTIFICATION }?.apply {
                intent.extras?.apply {
                    takeIf { it.containsKey(Task.EXTRA_TASK) }?.apply {
                        Task.fromBundle(getBundle(Task.EXTRA_TASK)!!)?.also { task ->
                            currentTask = task
                        }
                    }
                }
            }
        }

        currentTask?.let { task ->
            notificationHelper.showNotification(task)
        }
    }

    companion object {
        const val TAG_ACTION_SET_ALARM_NOTIFICATION = "TAG_ACTION_SET_ALARM_NOTIFICATION"
    }
}