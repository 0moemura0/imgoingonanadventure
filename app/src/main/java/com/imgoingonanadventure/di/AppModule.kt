package com.imgoingonanadventure.di

import android.content.Context
import androidx.room.Room
import com.imgoingonanadventure.data.EventDataSource
import com.imgoingonanadventure.data.SettingsDataStore
import com.imgoingonanadventure.data.database.AppDatabase
import com.imgoingonanadventure.data.database.EventsDatabase

interface AppModule {
    val repositoryModule: RepositoryModule
    val viewModuleModule: ViewModuleModule
    val dataStore: SettingsDataStore
}

class AppModuleImpl(private val applicationContext: Context) : AppModule {
    private val appDatabase: AppDatabase by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "AppDatabase")
            .build()
    }
    private val eventDatabase: EventsDatabase by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        Room.databaseBuilder(applicationContext, EventsDatabase::class.java, "EventsDatabase")
            .createFromAsset("EventsDatabase.db")
            .build()
    }
    override val dataStore: SettingsDataStore
        get() = SettingsDataStore(applicationContext)

    private val eventDataSource: EventDataSource = EventDataSource(applicationContext)

    private val mapperModule: MapperModule
        get() = MapperModuleImpl()

    override val repositoryModule: RepositoryModule
        get() = RepositoryModuleImpl(
            appDatabase,
            eventDatabase,
            dataStore,
            eventDataSource,
            mapperModule
        )

    override val viewModuleModule: ViewModuleModule
        get() = ViewModuleModuleImpl(repositoryModule, dataStore)
}
