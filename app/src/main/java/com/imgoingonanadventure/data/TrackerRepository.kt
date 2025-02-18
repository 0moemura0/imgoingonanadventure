package com.imgoingonanadventure.data

import com.imgoingonanadventure.database.AppDatabase
import com.imgoingonanadventure.model.Event
import com.imgoingonanadventure.model.StepsInDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.joda.time.DateTime
import kotlin.math.roundToLong

class TrackerRepository(
    database: AppDatabase,
    private val dataStore: SettingsDataStore,
) {

    private val eventDao = database.eventDao()
    private val stepsInDayDao = database.stepsInDayDao()


    suspend fun getTrackedDistance(dateTime: DateTime) : Flow<Long> {
        val stepLength = dataStore.getStepLength()
        return stepLength.map { step ->
            ((getStepCount(dateTime)?.count ?:0L) * step).roundToLong()
        }// to usecase?
    }

    suspend fun getDistancesEvent(distance: Long): List<Event> =  eventDao.getEventWithDistance(distance)

    suspend fun getStepCount(dateTime: DateTime) : StepsInDay? = stepsInDayDao.getStepsInDay(dateTime)

    suspend fun setStepCount(stepsInDay: StepsInDay) = stepsInDayDao.insertStepsInDay(stepsInDay)

}
