package com.example.mytodoapp.features.notifications.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.mytodoapp.features.database.repositories.TaskRepository
import com.example.mytodoapp.features.notifications.NotificationAlarmManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import javax.inject.Inject

class SystemTimeChangedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var taskRepository: TaskRepository

    @Inject
    lateinit var notificationAlarmManager: NotificationAlarmManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.TIME_SET") {
            CoroutineScope(Dispatchers.IO + NonCancellable).launch {
                taskRepository.allItems.collect { tasks ->
                    tasks.forEach { task -> notificationAlarmManager.setAlarmInFuture(task) }
                }
            }
        }
    }

}