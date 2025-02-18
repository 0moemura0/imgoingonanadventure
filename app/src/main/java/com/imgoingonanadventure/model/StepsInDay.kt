package com.imgoingonanadventure.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity(primaryKeys = ["id", "date"])
data class StepsInDay(
    val id: Int,
    val date: DateTime,
    @ColumnInfo(defaultValue = "0") val count: Long,
)
