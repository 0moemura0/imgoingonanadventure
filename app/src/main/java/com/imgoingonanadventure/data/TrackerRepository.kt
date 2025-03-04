package com.imgoingonanadventure.data

import com.imgoingonanadventure.data.database.AppDatabase
import com.imgoingonanadventure.model.Event
import com.imgoingonanadventure.model.StepsInDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.joda.time.DateTime
import kotlin.math.roundToInt

class TrackerRepository(
    database: AppDatabase,
    private val dataStore: SettingsDataStore,
) {

    private val eventChunkDao = database.eventChunk()
    private val stepsInDayDao = database.stepsInDayDao()

    // to usecase?
    suspend fun getTrackedDistanceByDate(dateTime: DateTime): Flow<Int> {
        val stepLength = dataStore.getStepLength()
        return stepLength.map { length ->
            ((getStepCount(dateTime)?.count ?: 0) * length).roundToInt()
        }
    }

    fun getTrackedDistanceBySteps(steps: Int): Flow<Int> {
        val stepLength = dataStore.getStepLength()
        return stepLength.map { length -> (steps * length).roundToInt() }
    }

    // to usecase?
    suspend fun getDistancesEvent(distance: Int): Flow<Event> =
        dataStore.getEventChunkId().map { chunkId ->
            eventChunkDao
                .getChunkWithId(chunkId)
                .list
                .findLast { event -> event.distance <= distance }!! //todo
        }

    suspend fun getStepCount(dateTime: DateTime): StepsInDay? =
        stepsInDayDao.getStepsInDay(dateTime)

    // to usecase?
    suspend fun setOrAddStepCount(stepSession: Int) {
        val today: DateTime = DateTime.now()
        val stepsBefore: StepsInDay? = stepsInDayDao.getStepsInDay(today)
        if (stepsBefore != null) {
            val stepsNow = stepsBefore.count + stepSession
            stepsInDayDao.updateStepsInDay(StepsInDay(today, stepsNow))
        } else {
            stepsInDayDao.insertStepsInDay(StepsInDay(today, stepSession))
        }
    }
}
