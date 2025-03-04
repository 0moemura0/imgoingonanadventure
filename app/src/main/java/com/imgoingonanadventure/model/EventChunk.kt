package com.imgoingonanadventure.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity
class EventChunk(
    @PrimaryKey val routeId: String,
    @ColumnInfo val list: List<Event>,
)
