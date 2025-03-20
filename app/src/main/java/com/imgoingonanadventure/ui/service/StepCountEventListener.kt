package com.imgoingonanadventure.ui.service

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.Log
import com.imgoingonanadventure.data.TrackerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class StepCountEventListener(
    private val scope: CoroutineScope,
    private val trackerRepository: TrackerRepository,
    private val update: (Int) -> Unit,
) : SensorEventListener {

    private var steps: Int = 0

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        val stepsSinceLastReboot: Int = event.values[0].toInt()
        steps += stepsSinceLastReboot
        update(steps)
        Log.d(TAG, "Steps: $steps")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "Accuracy changed to: $accuracy")
    }

    fun stop() {
        scope.launch { trackerRepository.setOrAddStepCount(steps) }
    }

    companion object {
        private const val TAG = "StepCountEventListener"
    }
}