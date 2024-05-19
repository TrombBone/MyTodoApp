package com.example.mytodoapp.features.notifications.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.mytodoapp.features.database.repository.TaskRepository
import com.example.mytodoapp.features.notifications.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationActionsReceiver : BroadcastReceiver() {

    @Inject
    lateinit var taskRepository: TaskRepository

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onReceive(context: Context, intent: Intent) {
        var extraTaskID: String? = null

        intent.action?.apply {
            takeIf { this == TAG_ACTION_FINISH }?.apply {
                intent.extras?.apply {
                    takeIf { it.containsKey(KEY_EXTRA_TASK_ID) }?.apply {
                        extraTaskID = getString(KEY_EXTRA_TASK_ID)!!
                    }
                }
            }
        }

        extraTaskID?.let {
            CoroutineScope(Dispatchers.IO).launch {
                taskRepository.setFinished(it, true)
            }
            notificationHelper.dismissNotification(it)
        }
    }

    companion object {
        const val TAG_ACTION_FINISH = "TAG_ACTION_FINISH"
        const val KEY_EXTRA_TASK_ID = "EXTRA_NOTIFICATION_ID"
    }
}