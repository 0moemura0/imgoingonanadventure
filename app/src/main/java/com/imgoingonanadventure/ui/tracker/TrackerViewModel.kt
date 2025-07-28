package com.imgoingonanadventure.ui.tracker

import android.util.Log
import androidx.annotation.DrawableRes
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrackerViewModel(private val trackerRepository: TrackerRepository) : ViewModel() {

    private val _stateDistance: MutableStateFlow<Double> = MutableStateFlow(0.0)
    val stateDistance: StateFlow<Double>
        get() = _stateDistance.asStateFlow()

    private val _stateEvent: MutableStateFlow<String> = MutableStateFlow("")
    val stateEvent: StateFlow<String>
        get() = _stateEvent.asStateFlow()

    private val _stateImage: MutableStateFlow<ImageState> = MutableStateFlow(ImageState())
    val stateImage: StateFlow<ImageState>
        get() = _stateImage.asStateFlow()

    fun getStepState() {
        viewModelScope.launch {
            updateStateWith(trackerRepository.getDistance())
        }
    }

    fun setStepCount(newSteps: Int) {
        viewModelScope.launch {
            val stepSum = trackerRepository.getDistance()
                .combine(trackerRepository.getDistanceByStepCount(newSteps)) { old, new -> old + new }
            updateStateWith(stepSum)
        }
    }

    private fun updateStateWith(distance: Flow<Double>) {
        viewModelScope.launch {
            distance.collect { newDistance ->
                val distanceInKilometers = newDistance / 1000
                _stateDistance.update { distanceInKilometers }
            }
        }
        @OptIn(ExperimentalCoroutinesApi::class)
        viewModelScope.launch {
            distance
                .map { trackerRepository.getDistancesEvent(it) }
                .flattenConcat()
                .map { it.event }
                .catch { Log.e(TAG, "updateStateWith event: ", it) }
                .collect { newEvent: String ->
                    _stateEvent.update { newEvent }
                }
        }

        viewModelScope.launch {
            trackerRepository.getRouteName()
                .map { getRouteSetting(it) }
                .catch { Log.e(TAG, "updateStateWith: image", it) }
                .collect { newSetting ->
                    _stateImage.update { newSetting }
                }
        }
    }

    private fun getRouteSetting(route: Route): ImageState {
        return when (route) {
            Route.BAG_END_TO_RIVENDELL -> ImageState(
                R.drawable.bag_end_to_rivendell,
                R.drawable.grass_bag
            )

            Route.RIVENDELL_TO_LOTHLORIEN -> ImageState(
                R.drawable.rivendell_to_lothlorien,
                R.drawable.grass_rivendell,
            )

            Route.LOTHLORIEN_TO_RAUROS -> ImageState(
                R.drawable.lothlorien_to_rauros,
                R.drawable.grass_lothlorie,
            )

            Route.RAUROS_TO_DOOM -> ImageState(
                R.drawable.rauros_to_doom,
                R.drawable.grass_rauros,
            )
        }
    }

    private companion object {
        const val TAG = "TrackerViewModel"
    }
}

class ImageState(
    @DrawableRes val imageId: Int = R.drawable.bag_end_to_rivendell,
    @DrawableRes val grassId: Int = R.drawable.grass
)