package com.imgoingonanadventure.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.imgoingonanadventure.model.DateTimeConverter
import com.imgoingonanadventure.model.Note
import com.imgoingonanadventure.model.StepsInDay

@Database(entities = [Note::class, StepsInDay::class], version = 1)
@TypeConverters(value = [DateTimeConverter::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun notesDao(): NotesDao

    abstract fun stepsInDayDao(): StepsInDayDao
}
