package com.imgoingonanadventure.di

import com.imgoingonanadventure.data.EventDataSource
import com.imgoingonanadventure.data.SettingsDataStore
import com.imgoingonanadventure.data.TrackerRepository
import com.imgoingonanadventure.data.database.AppDatabase
import com.imgoingonanadventure.data.database.EventsDatabase

interface RepositoryModule {

    val trackerRepository: TrackerRepository
}

class RepositoryModuleImpl(
    private val appDatabase: AppDatabase,
    private val eventsDatabase: EventsDatabase,
    private val dataStore: SettingsDataStore,
    private val eventDataSource: EventDataSource,
    private val mapperModule: MapperModule,
) : RepositoryModule {

    override val trackerRepository: TrackerRepository
        get() = TrackerRepository(
            appDatabase = appDatabase,
            eventsDatabase = eventsDatabase,
            dataStore = dataStore,
            eventDataSource = eventDataSource,
            routeIdToRouteMapper = mapperModule.routeIdToRouteMapper
        )
}
