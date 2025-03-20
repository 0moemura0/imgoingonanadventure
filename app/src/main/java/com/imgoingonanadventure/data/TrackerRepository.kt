package com.imgoingonanadventure.data

import com.imgoingonanadventure.data.database.AppDatabase
import com.imgoingonanadventure.data.database.EventDao
import com.imgoingonanadventure.data.database.StepsInDayDao
import com.imgoingonanadventure.model.Event
import com.imgoingonanadventure.model.StepsInDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.joda.time.DateTime
import kotlin.math.roundToInt

class TrackerRepository(
    database: AppDatabase,
    private val dataStore: SettingsDataStore,
    private val eventDataSource: EventDataSource,
) {

    private val eventDao: EventDao = database.eventDao()
    private val stepsInDayDao: StepsInDayDao = database.stepsInDayDao()


    suspend fun getStepCount(): Int = stepsInDayDao.countSteps()

    // to usecase?
    suspend fun getDistance(): Flow<Int> {
        val stepLength = dataStore.getStepLength()
        return stepLength
            .map { length -> (getStepCount() * length).roundToInt() }
    }

    // to usecase?
    suspend fun getDistancesEvent(distance: Int): Flow<Event> {
        return dataStore.getEventChunkId().map { chunkId ->
            val local = eventDao.getListWithId(chunkId)
            val source: List<Event> = local.ifEmpty {
                val notReallyLocal = eventDataSource.getEventList(chunkId)
                eventDao.addEventList(notReallyLocal)
                notReallyLocal
            }
            val event: Event = source.findLast { it.distance <= distance }
                ?: throw NullPointerException("no element in events list somehow")
            // to usecase?
            if (event.event == source.last().event) {
                dataStore.setNextEventChunkId()
            }
            event
        }
    }

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
