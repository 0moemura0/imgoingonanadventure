package com.imgoingonanadventure.ui.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.content.PermissionChecker
import androidx.lifecycle.MutableLiveData
import com.imgoingonanadventure.App
import com.imgoingonanadventure.ui.service.StepTrackerNotificationDecorator.Companion.NOTIFICATION_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class StepTrackerService : Service() {
    private lateinit var listener: StepCountEventListener

    private val scope = CoroutineScope(Dispatchers.Main)
    private val trackerRepository = App.appModule.repositoryModule.trackerRepository

    private val sensorManager: SensorManager by lazy {
        applicationContext.getSystemService(SENSOR_SERVICE) as SensorManager
    }
    private val sensor: Sensor? by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) }

    private val notificationDecorator by lazy { StepTrackerNotificationDecorator(this) }

    @SuppressLint("MissingPermission")
    private val observer: (String) -> Unit = {
        if (it.isNotEmpty() && it.isNotBlank()) {
            notificationDecorator.onUpdate(it)
        }
    }

    override fun onCreate() {
        super.onCreate()
        checkPermission()
        notificationDecorator.createChannel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notificationDecorator.createNotification(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
            )
        } else {
            startForeground(NOTIFICATION_ID, notificationDecorator.createNotification())
        }
        observeEventLiveData()
        startStepCounter()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        checkPermission()
        if (intent != null) {
            val action = intent.action
            if (action != null) when (action) {
                ACTION_STOP_FOREGROUND_SERVICE -> {
                    stopStepCounter()
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }

                ACTION_PLAY -> {
                    notificationDecorator.sendTextUpdate("Counting!")
                    startStepCounter()
                }

                ACTION_PAUSE -> {
                    notificationDecorator.sendTextUpdate("Not counting!")
                    stopStepCounter()
                }
            }
        }
        return START_STICKY
    }

    private fun checkPermission() {
        val activityPermission = Manifest.permission.ACTIVITY_RECOGNITION
        val activityPermissionCheck =
            PermissionChecker.checkSelfPermission(this, activityPermission)
        if (activityPermissionCheck != PermissionChecker.PERMISSION_GRANTED) {
            stopSelf()
            return
        }
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onDestroy() {
        stopStepCounter()
        liveEvent.removeObserver(observer)
        super.onDestroy()
    }

    override fun stopService(name: Intent?): Boolean {
        return super.stopService(name)
    }

    private fun startStepCounter() {
        Log.d(TAG, "Registering sensor listener... ")
        if (!this::listener.isInitialized) {
            listener = StepCountEventListener(scope, trackerRepository) { stepCount ->
                sendStepUpdate(stepCount)
            }
        }
        val supportedAndEnabled =
            sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
        Log.d(TAG, "Sensor listener registered: $supportedAndEnabled")
    }

    @SuppressLint("MissingPermission")
    private fun sendStepUpdate(stepCount: Int) {
        notificationDecorator.sendStepUpdate(stepCount)
        liveStepCount.postValue(stepCount)
    }

    @SuppressLint("MissingPermission")
    private fun observeEventLiveData() {
        liveEvent.observeForever(observer)
    }

    private fun stopStepCounter() {
        sensorManager.unregisterListener(listener, sensor)
    }

    companion object {
        val liveStepCount: MutableLiveData<Int> = MutableLiveData()
        val liveEvent: MutableLiveData<String> = MutableLiveData()

        private const val TAG = "StepTrackerService"

        const val ACTION_STOP_FOREGROUND_SERVICE: String = "ACTION_STOP_FOREGROUND_SERVICE"
        const val ACTION_PAUSE: String = "ACTION_PAUSE"
        const val ACTION_PLAY: String = "ACTION_PLAY"
    }
}
