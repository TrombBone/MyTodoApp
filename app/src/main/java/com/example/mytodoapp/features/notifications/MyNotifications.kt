package com.example.mytodoapp.features.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.WorkerThread
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import androidx.core.graphics.drawable.IconCompat
import com.example.mytodoapp.R
import com.example.mytodoapp.core.activity.MainActivity
import com.example.mytodoapp.features.broadcastreceiver.NotificationBroadcastReceiver
import com.example.mytodoapp.features.broadcastreceiver.NotificationBroadcastReceiver.Companion.KEY_EXTRA_NOTIFICATION_ID
import com.example.mytodoapp.features.broadcastreceiver.NotificationBroadcastReceiver.Companion.TAG_ACTION_FINISH
import com.example.mytodoapp.features.database.entities.Task
import java.util.UUID

class MyNotifications(private val context: Context) {

    companion object {
        const val NOTIFICATION_REQUEST_PERMISSION_CODE = 1

        private const val STARED_CHANNEL_ID = "STARED_CHANNEL_ID"
        private const val REGULAR_CHANNEL_ID = "REGULAR_CHANNEL_ID"

        private const val REQUEST_CONTENT_CODE = 1
    }

    // Register the channel with the system.
    private val notificationManager: NotificationManager =
        context.getSystemService() ?: throw IllegalStateException()

    fun setupStaredNotificationChannel() {
        if (notificationManager.getNotificationChannel(STARED_CHANNEL_ID) == null) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    STARED_CHANNEL_ID,
                    "Important Tasks",//context.getString(R.string.channel_new_messages),
                    // The importance must be IMPORTANCE_HIGH to show Bubbles.
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description =
                        "Test marked as important with a star"
                    //context.getString(R.string.channel_new_messages_description)
                }
            )
        }
    }

    fun setupRegularNotificationChannel() {
        if (notificationManager.getNotificationChannel(REGULAR_CHANNEL_ID) == null) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    REGULAR_CHANNEL_ID,
                    "Regular Tasks",//context.getString(R.string.channel_new_messages),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description =
                        "All of the tasks, not marked as important with a star"
                    //context.getString(R.string.channel_new_messages_description)
                }
            )
        }
    }

    private fun taskIDToNotificationID(taskID: String): Int =
        UUID.fromString(taskID).mostSignificantBits.toInt()

    private fun contentClickIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, REQUEST_CONTENT_CODE, intent, flagUpdateCurrent(false)
        )
        return pendingIntent
    }

    private fun actionSetFinished(task: Task) =
        NotificationCompat.Action.Builder(
            IconCompat.createWithResource(context, R.drawable.ic_check_24),
            "Mark task ready"/*getString(context, R.string)*/,
            markTaskReadyIntent(task)
        ).build()

    private fun markTaskReadyIntent(task: Task): PendingIntent {
        val markTaskReadyIntent = Intent(context, NotificationBroadcastReceiver::class.java).apply {
            action = TAG_ACTION_FINISH
            putExtra(KEY_EXTRA_NOTIFICATION_ID, task.taskID)
        }
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CONTENT_CODE,
            markTaskReadyIntent,
            flagUpdateCurrent(false)
        )
    }

    private fun flagUpdateCurrent(mutable: Boolean): Int {
        return if (mutable) {
            if (Build.VERSION.SDK_INT >= 31) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        }
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    MainActivity().requestNotificationPermission()
                }
                return@with
            }

            val builder = createNotification(task)
            // notificationId is a unique int for each notification that you must define.
            notify(taskIDToNotificationID(task.taskID), builder.build())
        }
    }

    fun dismissNotification(taskID: String) {
        notificationManager.cancel(taskIDToNotificationID(taskID))
    }

//    fun updateNotification(task: Task, isShowing: Boolean = true) {
//        if (!isShowing) {
//            showNotification(task)
//        } else {
//            dismissNotification(taskIDToNotificationID(task.taskID))
//        }
//    }
}

