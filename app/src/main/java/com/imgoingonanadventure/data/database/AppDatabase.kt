package com.imgoingonanadventure.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.imgoingonanadventure.model.DateTimeConverter
import com.imgoingonanadventure.model.Event
import com.imgoingonanadventure.model.Note
import com.imgoingonanadventure.model.StepsInDay

@Database(entities = [Note::class, StepsInDay::class, Event::class], version = 1)
@TypeConverters(value = [DateTimeConverter::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    abstract fun stepsInDayDao(): StepsInDayDao

    abstract fun eventDao(): EventDao
}
