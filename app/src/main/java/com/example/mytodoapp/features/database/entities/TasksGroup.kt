package com.example.mytodoapp.features.database.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
@Entity(
    tableName = "groups"
)
data class TasksGroup @JvmOverloads constructor(
    @PrimaryKey
    @ColumnInfo(index = true)
    var taskGroupID: String = UUID.randomUUID().toString(),
    var groupTitle: String? = ""
) : Parcelable
