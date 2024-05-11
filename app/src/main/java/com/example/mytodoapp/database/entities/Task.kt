package com.example.mytodoapp.database.entities

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
import com.example.mytodoapp.database.converters.DateTimeConverter
import com.example.mytodoapp.extensions.isAfterNow
import com.example.mytodoapp.extensions.isToday
import com.example.mytodoapp.extensions.isTomorrow
import com.example.mytodoapp.extensions.isYesterday
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime
import java.util.UUID

@Parcelize
@JsonClass(generateAdapter = true)
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
    var dueDate: ZonedDateTime? = null,
    var isFinished: Boolean = false,
    @TypeConverters(DateTimeConverter::class)
    var dateAdded: ZonedDateTime? = ZonedDateTime.now() // need for sort by last added
) : Parcelable {

    fun hasDetails(): Boolean = details != null

    fun hasDueDate(): Boolean = dueDate != null

    fun isDueDateInFuture(): Boolean = dueDate?.isAfterNow() == true

    fun isDueToday(): Boolean = dueDate?.isToday() == true

    /**
     * Check if the day on the task's due
     * is today/yesterday/tomorrow
     * and @return needed string format
     */
    fun formatDueDate(context: Context): String? {
        if (dueDate == null) return ""

        return if (dueDate?.isToday() == true)
            String.format(
                context.getString(R.string.today_at),
                dueDate?.format(DateTimeConverter.getTimeFormatter(context))
            )
        else if (dueDate?.isYesterday() == true)
            String.format(
                context.getString(R.string.yesterday_at),
                dueDate?.format(DateTimeConverter.getTimeFormatter(context))
            )
        else if (dueDate?.isTomorrow() == true)
            String.format(
                context.getString(R.string.tomorrow_at),
                dueDate?.format(DateTimeConverter.getTimeFormatter(context))
            )
        else dueDate?.format(DateTimeConverter.getDateTimeFormatter(context))
    }

    companion object {
        const val EXTRA_TASK = "EXTRA_TASK"
        private const val EXTRA_ID = "EXTRA_ID"
        private const val EXTRA_GROUP_ID = "EXTRA_GROUP_ID"
        private const val EXTRA_TITLE = "EXTRA_TITLE"
        private const val EXTRA_DETAILS = "EXTRA_DETAILS"
        private const val EXTRA_IS_STARED = "EXTRA_IS_STARED"
        private const val EXTRA_DUE_DATE = "EXTRA_DUE_DATE"
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
                        dueDate = getSerializable(EXTRA_DUE_DATE, ZonedDateTime::class.java),
                        isFinished = getBoolean(EXTRA_IS_FINISHED),
                        dateAdded = getSerializable(EXTRA_DATE_ADDED, ZonedDateTime::class.java),
                    )
                } else {
                    Task(
                        taskID = getString(EXTRA_ID)!!,
                        groupID = getString(EXTRA_GROUP_ID)!!,
                        title = getString(EXTRA_TITLE),
                        details = getString(EXTRA_DETAILS),
                        isStared = getBoolean(EXTRA_IS_STARED),
                        dueDate = getSerializable(EXTRA_DUE_DATE) as? ZonedDateTime,
                        isFinished = getBoolean(EXTRA_IS_FINISHED),
                        dateAdded = getSerializable(EXTRA_DATE_ADDED) as ZonedDateTime,
                    )
                }
            }

        }
    }

    //TODO: mb need the same functions for JSON
}
