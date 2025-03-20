package com.imgoingonanadventure.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class Event(
    @PrimaryKey val distance: Double,
    @ColumnInfo val routeId: String,
    @ColumnInfo val event: String,
)
