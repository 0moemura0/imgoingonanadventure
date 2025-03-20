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
}

class AppModuleImpl(private val applicationContext: Context) : AppModule {
    private val appDatabase: AppDatabase by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "AppDatabase"
        ).build()
    }
    private val dataStore: SettingsDataStore = SettingsDataStore(applicationContext)

    private val eventDataSource: EventDataSource = EventDataSource(applicationContext)

    override val repositoryModule: RepositoryModule
        get() = RepositoryModuleImpl(appDatabase, dataStore, eventDataSource)

    override val viewModuleModule: ViewModuleModule
        get() = ViewModuleModuleImpl(repositoryModule)

    override val crashLogger: CrashLogger
        get() = CrashLogger(applicationContext)
}
