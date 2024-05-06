package com.example.mytodoapp.database.entities

import android.content.Context
import android.os.Parcelable
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

    //TODO: mb need many functions for JSON and Bundle
}
