package com.imgoingonanadventure.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.imgoingonanadventure.model.StepsInDay
import org.joda.time.DateTime

@Dao
interface StepsInDayDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStepsInDay(stepsInDay: StepsInDay)

    @Update
    suspend fun updateStepsInDay(stepsInDay: StepsInDay)

    @Query("SELECT * FROM stepsinday")
    suspend fun getAll(): List<StepsInDay>

    @Query("SELECT * FROM stepsinday WHERE date = :dateTime")
    suspend fun getStepsInDay(dateTime:DateTime): StepsInDay?
}
