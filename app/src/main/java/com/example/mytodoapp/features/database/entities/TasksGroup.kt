package com.example.mytodoapp.features.database.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
@Entity(
    tableName = "groups"
)
data class TasksGroup @JvmOverloads constructor(
    @PrimaryKey
    @ColumnInfo(index = true)
    var taskGroupID: String = "2",
    var groupTitle: String? = ""
) : Parcelable
