package com.imgoingonanadventure.ui.tracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imgoingonanadventure.data.TrackerRepository
import com.imgoingonanadventure.model.Event
import com.imgoingonanadventure.model.StepsInDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.joda.time.DateTime

class TrackerViewModel(private val trackerRepository: TrackerRepository) : ViewModel() {

    private val _state: MutableStateFlow<StepState> = MutableStateFlow(StepState.Loading)
    val state: StateFlow<StepState> = _state.asStateFlow()

    fun getStepState() {
        //todo exception
        viewModelScope.launch {
            val distance: Flow<Long> = trackerRepository.getTrackedDistance(mockDateTime)
            distance.map {
                val steps: StepsInDay? = trackerRepository.getStepCount(mockDateTime)
                val event: List<Event> = trackerRepository.getDistancesEvent(it)
                _state.emit(StepState.Data(distance = it, steps = steps, event = event))
            }
        }
    }

    companion object{
        val mockDateTime = DateTime(2024,1,1,1,1)
    }
}

sealed class StepState {
    data object Loading : StepState()
    data class Data(val distance: Long,val steps: StepsInDay?,val event: List<Event>): StepState()
    data class Error(val error: Throwable): StepState()
}
