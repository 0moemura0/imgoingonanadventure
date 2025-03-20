package com.imgoingonanadventure.ui.tracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imgoingonanadventure.data.TrackerRepository
import com.imgoingonanadventure.model.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class TrackerViewModel(private val trackerRepository: TrackerRepository) : ViewModel() {

    private val _state: MutableStateFlow<StepState> = MutableStateFlow(StepState.Loading)
    val state: StateFlow<StepState> = _state.asStateFlow()

    fun getStepState() {
        //todo exception + how to manage flows
        viewModelScope.launch {
            val distance: Flow<Int> = trackerRepository.getDistance()
            val steps: Int = trackerRepository.getStepCount()
            distance.map { dstne ->
                val event: Flow<Event> = trackerRepository.getDistancesEvent(dstne)
                event.map { vnt ->
                    _state.emit(StepState.Data(distance = dstne, steps = steps, event = vnt))
                }
            }
        }
    }

    //todo distance event
    fun setStepCount(newSteps: Int) {
        viewModelScope.launch {
            val steps: Int = trackerRepository.getStepCount()
            //_state.emit()
        }
    }
}

sealed class StepState {
    data object Loading : StepState()
    data class Data(val distance: Int, val steps: Int?, val event: Event) : StepState()
    data class Error(val error: Throwable) : StepState()
}
