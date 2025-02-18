package com.imgoingonanadventure.di

import android.content.Context
import androidx.room.Room
import com.imgoingonanadventure.data.SettingsDataStore
import com.imgoingonanadventure.database.AppDatabase

interface AppModule {
    val repositoryModule: RepositoryModule
    val viewModuleModule: ViewModuleModule
}

class AppModuleImpl(private val applicationContext: Context): AppModule {
    private val appDatabase: AppDatabase by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "AppDatabase"
        ).build()
    }
    private val dataStore: SettingsDataStore = SettingsDataStore(applicationContext)

    override val repositoryModule: RepositoryModule
        get() = RepositoryModuleImpl(appDatabase, dataStore)

    override val viewModuleModule: ViewModuleModule
        get() = ViewModuleModuleImpl(repositoryModule)
}
