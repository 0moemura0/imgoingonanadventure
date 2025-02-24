package com.imgoingonanadventure.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity
data class Note(
    @PrimaryKey val id: Int,
    @ColumnInfo val stepDayDate: DateTime,
    @ColumnInfo val title: String,
    @ColumnInfo val text: String,
)
