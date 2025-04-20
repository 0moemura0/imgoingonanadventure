package com.imgoingonanadventure.di

import android.content.Context
import androidx.room.Room
import com.imgoingonanadventure.CrashLogger
import com.imgoingonanadventure.data.EventDataSource
import com.imgoingonanadventure.data.SettingsDataStore
import com.imgoingonanadventure.data.database.AppDatabase

interface AppModule {
    val repositoryModule: RepositoryModule
    val viewModuleModule: ViewModuleModule
    val crashLogger: CrashLogger
    val dataStore: SettingsDataStore
}

class AppModuleImpl(private val applicationContext: Context) : AppModule {
    private val appDatabase: AppDatabase by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "AppDatabase"
        ).build()
    }
    override val dataStore: SettingsDataStore
        get() = SettingsDataStore(applicationContext)

    private val eventDataSource: EventDataSource = EventDataSource(applicationContext)

    private val mapperModule: MapperModule
        get() = MapperModuleImpl()

    override val repositoryModule: RepositoryModule
        get() = RepositoryModuleImpl(appDatabase, dataStore, eventDataSource, mapperModule)

    override val viewModuleModule: ViewModuleModule
        get() = ViewModuleModuleImpl(repositoryModule, dataStore)

    override val crashLogger: CrashLogger
        get() = CrashLogger(applicationContext)
}
