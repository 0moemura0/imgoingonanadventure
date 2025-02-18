package com.imgoingonanadventure.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity
data class Note(
    @PrimaryKey val id: Long,
    @ColumnInfo val stepDayId: Long,
    @ColumnInfo val title: String,
    @ColumnInfo val text: String,
)
