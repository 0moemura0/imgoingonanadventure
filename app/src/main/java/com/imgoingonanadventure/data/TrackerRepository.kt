package com.imgoingonanadventure.data

import android.util.Log
import com.imgoingonanadventure.data.database.AppDatabase
import com.imgoingonanadventure.data.database.EventDao
import com.imgoingonanadventure.data.database.StepsInDayDao
import com.imgoingonanadventure.model.Event
import com.imgoingonanadventure.model.Route
import com.imgoingonanadventure.model.RouteIdToRouteMapper
import com.imgoingonanadventure.model.StepsInDay
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.joda.time.DateTime

class TrackerRepository(
    database: AppDatabase,
    private val dataStore: SettingsDataStore,
    private val eventDataSource: EventDataSource,
    private val routeIdToRouteMapper: RouteIdToRouteMapper,
) {

    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
    private val eventDao: EventDao = database.eventDao()
    private val stepsInDayDao: StepsInDayDao = database.stepsInDayDao()


    suspend fun getStepCount(): Int = withContext(defaultDispatcher) { stepsInDayDao.countSteps() }

    // to usecase?
    fun getDistanceByStepCount(stepCount: Int): Flow<Double> {
        val stepLength = dataStore.getStepLength()
        return stepLength
            .map { length -> (stepCount * length) }
    }

    suspend fun getDistance(): Flow<Double> {
        return withContext(defaultDispatcher) {
            val stepLength = dataStore.getStepLength()
            stepLength.map { length -> (getStepCount() * length) }
        }
    }

    // to usecase?
    suspend fun getDistancesEvent(distance: Double): Flow<Event> {
        return withContext(defaultDispatcher) {
            dataStore.getEventChunkId().map { chunkId ->
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
            }.catch {
                Log.e("TrackerRepository", "getDistancesEvent: ", it)
            }
        }
    }

    // to usecase?
    suspend fun setOrAddStepCount(stepSession: Int) {
        withContext(defaultDispatcher) {
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

    fun getRouteName(): Flow<Route> {
        return dataStore.getEventChunkId().map { routeIdToRouteMapper(it) }
    }
}
