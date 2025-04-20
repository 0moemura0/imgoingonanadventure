package com.imgoingonanadventure.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.imgoingonanadventure.data.SettingsDataStore
import com.imgoingonanadventure.ui.settings.SettingsViewModel
import com.imgoingonanadventure.ui.splash.SplashViewModel
import com.imgoingonanadventure.ui.tracker.TrackerViewModel

interface ViewModuleModule {

    val trackerViewModelFactory: ViewModelProvider.Factory
    val splashViewModelFactory: ViewModelProvider.Factory
    val settingsViewModelFactory: ViewModelProvider.Factory
}

class ViewModuleModuleImpl(
    private val repositoryModule: RepositoryModule,
    private val dataStore: SettingsDataStore,
) : ViewModuleModule {

    override val trackerViewModelFactory: ViewModelProvider.Factory
        get() = viewModelFactory { TrackerViewModel(repositoryModule.trackerRepository) }

    override val splashViewModelFactory: ViewModelProvider.Factory
        get() = viewModelFactory { SplashViewModel(repositoryModule.trackerRepository) }

    override val settingsViewModelFactory: ViewModelProvider.Factory
        get() = viewModelFactory { SettingsViewModel(dataStore) }
}

@Suppress("UNCHECKED_CAST")
fun <VM : ViewModel> viewModelFactory(initializer: () -> VM): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return initializer() as T
        }
    }
}
