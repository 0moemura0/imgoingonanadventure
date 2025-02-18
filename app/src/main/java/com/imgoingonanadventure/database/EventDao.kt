package com.imgoingonanadventure.database

import androidx.room.Dao
import androidx.room.Query
import com.imgoingonanadventure.model.Event

@Dao
interface EventDao {

    @Query("SELECT * FROM event")
    suspend fun getAll(): List<Event>

    @Query("SELECT * FROM event WHERE distance = :distance")
    suspend fun getEventWithDistance(distance:Long) : List<Event>
}
