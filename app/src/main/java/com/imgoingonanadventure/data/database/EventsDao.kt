package com.imgoingonanadventure.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.imgoingonanadventure.model.Event

@Dao
interface EventsDao {

    @Query("SELECT * FROM event")
    suspend fun getAll(): List<Event>

    @Query("SELECT * FROM event WHERE routeId = :routeId")
    suspend fun getListWithId(routeId: String): List<Event>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addEventList(list: List<Event>)
}