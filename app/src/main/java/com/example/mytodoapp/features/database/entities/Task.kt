package com.example.mytodoapp.features.database.entities

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.bundleOf
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.mytodoapp.R
import com.example.mytodoapp.components.extensions.isAfterNowMinuteInclude
import com.example.mytodoapp.components.extensions.isAfterToday
import com.example.mytodoapp.components.extensions.isToday
import com.example.mytodoapp.components.extensions.isTomorrow
import com.example.mytodoapp.components.extensions.isYesterday
import com.example.mytodoapp.features.database.converters.DateTimeConverter
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

@Parcelize
@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = TasksGroup::class,
            parentColumns = arrayOf("taskGroupID"), childColumns = arrayOf("groupID"),
            onDelete = ForeignKey.SET_DEFAULT
        )]
)
data class Task @JvmOverloads constructor(
    @PrimaryKey
    @ColumnInfo(index = true)
    var taskID: String = UUID.randomUUID().toString(),
    @ColumnInfo(index = true)
    var groupID: String = "1",
    var title: String? = null,
    var details: String? = null,
    var isStared: Boolean = false,
    @TypeConverters(DateTimeConverter::class)
    var dueDate: LocalDate? = null,
    @TypeConverters(DateTimeConverter::class)
    var dueTime: LocalTime? = null,
    var isFinished: Boolean = false,
    @TypeConverters(DateTimeConverter::class)
    var dateAdded: LocalDateTime? = LocalDateTime.now() // need for sort by last added
) : Parcelable {

    fun hasDetails(): Boolean = details != null

    fun hasDueDate(): Boolean = dueDate != null

    fun hasDueTime(): Boolean = dueTime != null

    fun isDueDateInFuture(): Boolean = dueDate?.isAfterToday() ?: false

    fun isDueToday(): Boolean = dueDate?.isToday() ?: false

    fun isDueTimeInLaterToday(): Boolean = dueTime?.isAfterNowMinuteInclude() ?: false

    fun isDueInFuture(): Boolean =
        isDueDateInFuture() || (isDueToday() && isDueTimeInLaterToday())

    /**
     * Check if the task has due time
     * and @return time in the required format
     */
    fun formatDueTime(context: Context): String {
        return if (dueTime == null) ""
        else dueTime!!.format(DateTimeConverter.getTimeFormatter(context))
    }

    /**
     * Check if the day on the task's due
     * is today/yesterday/tomorrow
     * and @return needed string format
     */
    private fun formatDueDate(context: Context): String {
        if (dueDate == null) return ""

        return if (dueDate!!.isToday())
            String.format(context.getString(R.string.today))
        else if (dueDate!!.isYesterday())
            String.format(
                context.getString(R.string.yesterday)
            )
        else if (dueDate!!.isTomorrow())
            String.format(context.getString(R.string.tomorrow))
        else dueDate!!.format(DateTimeConverter.getDateFormatter())
    }

    /**
     * Checks if the task has a dueDate and dueTime
     * and @return them in the appropriate format
     */
    fun formatDueDateTime(context: Context): String {
        if (dueDate == null) return ""
        val dueDateString: String = formatDueDate(context)

        val dueTimeString: String
        if (dueTime == null) return dueDateString
        else dueTimeString = formatDueTime(context)

        return String.format(
            context.getString(
                if (dueDate!!.isToday() || dueDate!!.isYesterday() || dueDate!!.isTomorrow())
                    R.string.date_at_time
                else
                    R.string.date_and_time
            ),
            dueDateString,
            dueTimeString
        )
    }

    companion object {
        const val EXTRA_TASK = "EXTRA_TASK"
        private const val EXTRA_ID = "EXTRA_ID"
        private const val EXTRA_GROUP_ID = "EXTRA_GROUP_ID"
        private const val EXTRA_TITLE = "EXTRA_TITLE"
        private const val EXTRA_DETAILS = "EXTRA_DETAILS"
        private const val EXTRA_IS_STARED = "EXTRA_IS_STARED"
        private const val EXTRA_DUE_DATE = "EXTRA_DUE_DATE"
        private const val EXTRA_DUE_TIME = "EXTRA_DUE_TIME"
        private const val EXTRA_IS_FINISHED = "EXTRA_IS_FINISHED"
        private const val EXTRA_DATE_ADDED = "EXTRA_ADDED_DATE"

        fun toBundle(task: Task): Bundle {
            return bundleOf(
                EXTRA_ID to task.taskID,
                EXTRA_GROUP_ID to task.groupID,
                EXTRA_TITLE to task.title,
                EXTRA_DETAILS to task.details,
                EXTRA_IS_STARED to task.isStared,
                EXTRA_DUE_DATE to task.dueDate,
                EXTRA_DUE_TIME to task.dueTime,
                EXTRA_IS_FINISHED to task.isFinished,
                EXTRA_DATE_ADDED to task.dateAdded
            )
        }

        fun fromBundle(bundle: Bundle): Task? {
            if (!bundle.containsKey(EXTRA_ID) || !bundle.containsKey(EXTRA_GROUP_ID)) return null

            with(bundle) {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Task(
                        taskID = getString(EXTRA_ID)!!,
                        groupID = getString(EXTRA_GROUP_ID)!!,
                        title = getString(EXTRA_TITLE),
                        details = getString(EXTRA_DETAILS),
                        isStared = getBoolean(EXTRA_IS_STARED),
                        dueDate = getSerializable(EXTRA_DUE_DATE, LocalDate::class.java),
                        dueTime = getSerializable(EXTRA_DUE_TIME, LocalTime::class.java),
                        isFinished = getBoolean(EXTRA_IS_FINISHED),
                        dateAdded = getSerializable(EXTRA_DATE_ADDED, LocalDateTime::class.java),
                    )
                } else {
                    Task(
                        taskID = getString(EXTRA_ID)!!,
                        groupID = getString(EXTRA_GROUP_ID)!!,
                        title = getString(EXTRA_TITLE),
                        details = getString(EXTRA_DETAILS),
                        isStared = getBoolean(EXTRA_IS_STARED),
                        dueDate = getSerializable(EXTRA_DUE_DATE) as? LocalDate,
                        dueTime = getSerializable(EXTRA_DUE_TIME) as? LocalTime,
                        isFinished = getBoolean(EXTRA_IS_FINISHED),
                        dateAdded = getSerializable(EXTRA_DATE_ADDED) as LocalDateTime,
                    )
                }
            }

        }
    }

}
