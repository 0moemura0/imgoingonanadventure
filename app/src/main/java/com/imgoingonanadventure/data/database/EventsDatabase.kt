package com.imgoingonanadventure.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.imgoingonanadventure.model.Event

@Database(entities = [Event::class], version = 1)
abstract class EventsDatabase : RoomDatabase() {

    abstract fun eventsDao(): EventsDao
}