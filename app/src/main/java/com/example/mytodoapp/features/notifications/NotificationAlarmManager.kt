package com.example.mytodoapp.features.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import com.example.mytodoapp.features.database.entities.Task
import com.example.mytodoapp.features.notifications.receivers.NotificationAlarmReceiver
import com.example.mytodoapp.features.notifications.receivers.NotificationAlarmReceiver.Companion.TAG_ACTION_SET_ALARM_NOTIFICATION
import com.example.mytodoapp.utils.flagUpdateCurrent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

class NotificationAlarmManager @Inject constructor(
    @ApplicationContext
    private val context: Context
) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private fun alarmReceiverPendingIntent(task: Task): PendingIntent {
        val intent = Intent(context, NotificationAlarmReceiver::class.java).apply {
            action = TAG_ACTION_SET_ALARM_NOTIFICATION
            putExtra(Task.EXTRA_TASK, Task.toBundle(task))
        }
        return PendingIntent.getBroadcast(
            context,
            UUID.fromString(task.taskID).mostSignificantBits.toInt(),
            intent,
            flagUpdateCurrent(false)
        )
    }

    private fun convertDueDateTimeToCalendarMillis(task: Task): Long {
        val date = task.dueDate ?: return 0L
        val time = task.dueTime ?: LocalTime.of(0, 0)
        val calendar = Calendar.getInstance()
        calendar.set(date.year, date.monthValue - 1, date.dayOfMonth, time.hour, time.minute)
        return calendar.timeInMillis
    }

    fun setAlarm(task: Task) {
        if (!task.isFinished && task.hasDueDate()) {
            val taskDueDateTimeMillis = convertDueDateTimeToCalendarMillis(task)

            if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) alarmManager.canScheduleExactAlarms()
                else true
            )
                if (task.isStared) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        taskDueDateTimeMillis,
                        alarmReceiverPendingIntent(task)
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        taskDueDateTimeMillis,
                        alarmReceiverPendingIntent(task)
                    )
                }
        }
    }

    fun cancelAlarm(task: Task) {
        alarmManager.cancel(alarmReceiverPendingIntent(task))
    }

    fun setAlarmInFuture(task: Task) {
        if (task.isDueInFuture()) setAlarm(task)
        else cancelAlarm(task)
    }

}