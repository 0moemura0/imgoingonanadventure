package com.imgoingonanadventure.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity
data class StepsInDay(
    @PrimaryKey val date: DateTime,
    @ColumnInfo(defaultValue = "0") val count: Int,
)
