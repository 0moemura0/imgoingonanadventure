package com.imgoingonanadventure.di

import com.imgoingonanadventure.data.SettingsDataStore
import com.imgoingonanadventure.data.TrackerRepository
import com.imgoingonanadventure.data.database.AppDatabase

interface RepositoryModule {

    val trackerRepository: TrackerRepository
}

class RepositoryModuleImpl(
    private val appDatabase: AppDatabase,
    private val dataStore: SettingsDataStore,
): RepositoryModule {

    override val trackerRepository: TrackerRepository
        get() = TrackerRepository(appDatabase, dataStore)
}
