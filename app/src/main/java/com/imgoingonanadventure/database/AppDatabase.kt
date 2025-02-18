package com.imgoingonanadventure.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.imgoingonanadventure.model.DateTimeConverter
import com.imgoingonanadventure.model.Event
import com.imgoingonanadventure.model.Note
import com.imgoingonanadventure.model.StepsInDay

@Database(entities = [Event::class, Note::class, StepsInDay::class], version = 1)
@TypeConverters(DateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao

    abstract fun noteDao(): NoteDao

    abstract fun stepsInDayDao(): StepsInDayDao
}
