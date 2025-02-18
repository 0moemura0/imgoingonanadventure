package com.imgoingonanadventure.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.imgoingonanadventure.ui.tracker.TrackerViewModel

interface ViewModuleModule {

    val trackerViewModelFactory: ViewModelProvider.Factory
}

class ViewModuleModuleImpl(private val repositoryModule: RepositoryModule): ViewModuleModule {

    override val trackerViewModelFactory: ViewModelProvider.Factory
        get() = viewModelFactory { TrackerViewModel(repositoryModule.trackerRepository) }
}

@Suppress("UNCHECKED_CAST")
fun <VM: ViewModel> viewModelFactory(initializer: () -> VM): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return initializer() as T
        }
    }
}
