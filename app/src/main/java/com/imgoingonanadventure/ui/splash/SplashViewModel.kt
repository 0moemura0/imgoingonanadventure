package com.imgoingonanadventure.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imgoingonanadventure.data.TrackerRepository
import com.imgoingonanadventure.model.Event
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SplashViewModel(private val trackerRepository: TrackerRepository) : ViewModel() {
    val liveData: MutableLiveData<Event> = MutableLiveData()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun checkEventChunk() {
        viewModelScope.launch {
            trackerRepository.getDistance()
                .map {
                    delay(2000L)
                    trackerRepository.getDistancesEvent(it)
                }
                .flattenConcat()
                .collect { liveData.postValue(it) }
        }
    }
}