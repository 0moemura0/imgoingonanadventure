package com.imgoingonanadventure.ui.tracker

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imgoingonanadventure.data.TrackerRepository
import com.imgoingonanadventure.model.Route
import com.imgoingontheadventure.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrackerViewModel(private val trackerRepository: TrackerRepository) : ViewModel() {

    private val _stateDistance: MutableStateFlow<Double> = MutableStateFlow(0.0)
    val stateDistance: StateFlow<Double>
        get() = _stateDistance.asStateFlow()

    private val _stateStep: MutableStateFlow<Int> = MutableStateFlow(0)
    private val stateStep: StateFlow<Int>
        get() = _stateStep.asStateFlow()

    private val _stateEvent: MutableStateFlow<String> = MutableStateFlow("")
    val stateEvent: StateFlow<String>
        get() = _stateEvent.asStateFlow()

    private val _stateImage: MutableStateFlow<Int> = MutableStateFlow(0)
    val stateImage: StateFlow<Int>
        get() = _stateImage.asStateFlow()

    fun getStepState() {
        viewModelScope.launch {
            Log.e(TAG, "getStepState: ")
            updateStateWith(
                distance = trackerRepository.getDistance()
            )
        }
    }

    fun setStepCount(newSteps: Int) {
        val stepStateData = stateStep.value
        val newCount = stepStateData + newSteps
        val distance = trackerRepository.getDistanceByStepCount(newCount)
        viewModelScope.launch { updateStateWith(distance = distance) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun updateStateWith(distance: Flow<Double>) {
        viewModelScope.launch {
            distance.collect { newDistance ->
                _stateDistance.update { newDistance }
            }
        }

        viewModelScope.launch {
            distance
                .map { trackerRepository.getDistancesEvent(it) }
                .flattenConcat()
                .map { it.event }
                .catch { Log.e(TAG, "updateStateWith event: ", it) }
                .collect { newEvent ->
                    _stateEvent.update { newEvent }
                }
        }

        viewModelScope.launch {
            trackerRepository.getRouteName()
                .map { getRouteImage(it) }
                .catch { Log.e(TAG, "updateStateWith: image", it) }
                .collect { newImage -> _stateImage.update { newImage } }
        }
    }

    private fun getRouteImage(route: Route): Int {
        return when (route) {
            Route.BAG_END_TO_RIVENDELL -> R.drawable.bag_end_to_rivendell
            Route.RIVENDELL_TO_LOTHLORIEN -> R.drawable.rivendell_to_lothlorien
            Route.LOTHLORIEN_TO_RAUROS -> R.drawable.lothlorien_to_rauros
            Route.RAUROS_TO_DOOM -> R.drawable.rauros_to_doom
        }
    }

    private companion object {
        const val TAG = "TrackerViewModel"
    }
}
