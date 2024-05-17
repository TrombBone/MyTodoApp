package com.example.mytodoapp.features.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.mytodoapp.features.database.repository.TaskRepository
import com.example.mytodoapp.features.notifications.MyNotifications
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var taskRepository: TaskRepository

    override fun onReceive(context: Context, intent: Intent) {
        var extraTaskID: String? = null

        intent.action?.apply {
            takeIf { this == TAG_ACTION_FINISH }?.apply {
                intent.extras?.apply {
                    takeIf { it.containsKey(KEY_EXTRA_NOTIFICATION_ID) }?.apply {
                        extraTaskID = getString(KEY_EXTRA_NOTIFICATION_ID)!!
                    }
                }
            }
        }

        extraTaskID?.let {
            @OptIn(DelicateCoroutinesApi::class)
            GlobalScope.launch {
                taskRepository.setFinished(it, true)
            }
            MyNotifications(context).dismissNotification(it)
        }
        // TODO
    }

    companion object {
        const val TAG_ACTION_FINISH = "TAG_ACTION_FINISH"
        const val KEY_EXTRA_NOTIFICATION_ID = "EXTRA_NOTIFICATION_ID"
    }
}