package com.imgoingonanadventure.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Event(
    @PrimaryKey val id: Int,
    @ColumnInfo val distance: Int,
    @ColumnInfo val event:String,
)
