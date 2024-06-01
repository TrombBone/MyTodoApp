package com.example.mytodoapp.features.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.annotation.WorkerThread
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getString
import androidx.core.content.getSystemService
import androidx.core.graphics.drawable.IconCompat
import com.example.mytodoapp.R
import com.example.mytodoapp.core.activity.MainActivity
import com.example.mytodoapp.features.database.entities.Task
import com.example.mytodoapp.features.notifications.receivers.NotificationActionsReceiver
import com.example.mytodoapp.features.notifications.receivers.NotificationActionsReceiver.Companion.KEY_EXTRA_TASK_ID
import com.example.mytodoapp.features.notifications.receivers.NotificationActionsReceiver.Companion.TAG_ACTION_FINISH
import com.example.mytodoapp.utils.flagUpdateCurrent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject

class NotificationHelper @Inject constructor(
    @ApplicationContext
    private val context: Context,
) {

    private var notificationManager: NotificationManager =
        context.getSystemService() ?: throw IllegalStateException()

    init {
        setupChannels()
    }

    private fun setupChannels() {
        setupRegularNotificationChannel()
        setupStaredNotificationChannel()
    }

    private fun setupStaredNotificationChannel() {
        if (notificationManager.getNotificationChannel(STARED_CHANNEL_ID) == null) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    STARED_CHANNEL_ID,
                    context.getString(R.string.notification_channel_title_important),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description =
                        context.getString(R.string.notification_channel_description_important)
                }
            )
        }
    }

    private fun setupRegularNotificationChannel() {
        if (notificationManager.getNotificationChannel(REGULAR_CHANNEL_ID) == null) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    REGULAR_CHANNEL_ID,
                    context.getString(R.string.notification_channel_title_regular),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description =
                        context.getString(R.string.notification_channel_description_regular)
                }
            )
        }
    }

    private fun taskIDtoInt(taskID: String): Int =
        UUID.fromString(taskID).mostSignificantBits.toInt()

    private fun contentClickIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
        }
        return PendingIntent.getActivity(
            context, REQUEST_CONTENT_CODE, intent, flagUpdateCurrent(false)
        )
    }

    private fun actionSetFinished(task: Task) =
        NotificationCompat.Action.Builder(
            IconCompat.createWithResource(context, R.drawable.ic_check_24),
            getString(context, R.string.mark_task_ready),
            markTaskReadyIntent(task)
        ).build()

    private fun markTaskReadyIntent(task: Task): PendingIntent {
        val markTaskReadyIntent = Intent(context, NotificationActionsReceiver::class.java).apply {
            action = TAG_ACTION_FINISH
            putExtra(KEY_EXTRA_TASK_ID, task.taskID)
        }
        return PendingIntent.getBroadcast(
            context,
            taskIDtoInt(task.taskID),
            markTaskReadyIntent,
            flagUpdateCurrent(false)
        )
    }

    private fun createNotification(task: Task): NotificationCompat.Builder {
        return NotificationCompat.Builder(
            context,
            if (task.isStared) STARED_CHANNEL_ID else REGULAR_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_star_fill_24)
            .setContentTitle(task.title)
            .setContentText(task.details)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(task.isStared)
            .setContentIntent(contentClickIntent())
            .addAction(actionSetFinished(task))
    }

    @WorkerThread
    fun showNotification(task: Task) {
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                // ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                // public fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                //                                        grantResults: IntArray)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return@with
            }

            val builder = createNotification(task)
            // notificationId is a unique int for each notification that you must define.
            notify(taskIDtoInt(task.taskID), builder.build())
        }
    }

    fun dismissNotification(taskID: String) {
        notificationManager.cancel(taskIDtoInt(taskID))
    }

  /*fun updateNotification(task: Task, isShowing: Boolean = true) {
        if (!isShowing) {
            showNotification(task)
        } else {
            dismissNotification(taskIDtoInt(task.taskID))
        }
    }*/

    companion object {
        private const val STARED_CHANNEL_ID = "STARED_CHANNEL_ID"
        private const val REGULAR_CHANNEL_ID = "REGULAR_CHANNEL_ID"

        private const val REQUEST_CONTENT_CODE = 1
    }
}

