package com.imgoingonanadventure.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.imgoingonanadventure.model.Note
import com.imgoingonanadventure.model.StepsInDay
import org.joda.time.DateTime

@Dao
interface NoteDao {

    @Insert
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM note")
    suspend fun getAll(): List<Note>

    @Query(
        "SELECT * FROM stepsinday" +
                " JOIN note ON stepsinday.date = note.stepDayDate" +
                " WHERE stepsinday.date = :dateTime"
    )
    suspend fun getAllInfoForNote(dateTime: DateTime): Map<StepsInDay, List<Note>>
}
