package com.imgoingonanadventure.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.imgoingonanadventure.model.EventChunk

@Dao
interface EventChunkDao {

    @Query("SELECT * FROM eventChunk")
    suspend fun getAll(): List<EventChunk>

    @Query("SELECT * FROM eventChunk WHERE routeId = :routeId")
    suspend fun getChunkWithId(routeId: String): EventChunk

    @Insert
    suspend fun addChunk(eventChunk: EventChunk)
}