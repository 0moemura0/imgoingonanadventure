package com.imgoingonanadventure.ui

import android.Manifest.permission.ACTIVITY_RECOGNITION
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.PermissionChecker
import com.imgoingontheadventure.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


class StepTrackerService : Service() {

    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val stepsState: MutableStateFlow<Long> = MutableStateFlow(0)

    override fun onCreate() {
        super.onCreate()
        checkPermission()
        createChannel()
        startForeground(NOTIFICATION_ID, createNotification(), foregroundServiceType)
        uiScope.launch {
            steps(this@StepTrackerService)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action = intent.action
            if (action != null) when (action) {
                ACTION_STOP_FOREGROUND_SERVICE -> {
                    //stopForegroundService()
                    Toast.makeText(
                        applicationContext,
                        "Foreground service is stopped.",
                        Toast.LENGTH_LONG
                    ).show()
                }

                ACTION_PLAY -> {}

                ACTION_PAUSE -> {}
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH,
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {

        // Create notification default intent.
        val intent = Intent()
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)


        // Create notification builder.
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)


        // Make notification show big text.
        val bigTextStyle = NotificationCompat.BigTextStyle()
        bigTextStyle.setBigContentTitle("Music player implemented by foreground service.")
        bigTextStyle.bigText("Android foreground service is a android service which can run in foreground always, it can be controlled by user via notification.")

        // Set big text style.
        builder.setStyle(bigTextStyle)

        builder.setWhen(System.currentTimeMillis())
        builder.setSmallIcon(R.mipmap.ic_launcher)
        val largeIconBitmap =
            BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_background)
        builder.setLargeIcon(largeIconBitmap)

        // Make head-up notification.
        builder.setFullScreenIntent(pendingIntent, true)

        // Add Play button intent in notification.
        val playIntent = Intent(this, StepTrackerService::class.java)
        playIntent.setAction(ACTION_PLAY)
        val pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val playAction = NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", pendingPlayIntent)
        builder.addAction(playAction)

        // Add Pause button intent in notification.
        val pauseIntent = Intent(this, StepTrackerService::class.java)
        pauseIntent.setAction(ACTION_PAUSE)
        val pendingPrevIntent = PendingIntent.getService(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val prevAction =
            NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", pendingPrevIntent)
        builder.addAction(prevAction)

        return builder.build()
    }
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun stopService(name: Intent?): Boolean {

        return super.stopService(name)
    }

    private suspend fun steps(context: Context): Long {
         val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)


        return suspendCancellableCoroutine { continuation ->
        Log.d(TAG, "Registering sensor listener... ")

        val listener: SensorEventListener by lazy {
            object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event == null) return

                    val stepsSinceLastReboot = event.values[0].toLong()
                    stepsState.update { stepsSinceLastReboot }
                    Log.d(TAG, "Steps since last reboot: $stepsSinceLastReboot")

                    if (continuation.isActive) {
                        continuation.resume(stepsSinceLastReboot)
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    Log.d(TAG, "Accuracy changed to: $accuracy")

                }
            }
        }
            val supportedAndEnabled =
                sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
            Log.d(TAG, "Sensor listener registered: $supportedAndEnabled")
        }
    }

    private fun checkPermission() {
        val activityPermission = ACTIVITY_RECOGNITION
        val activityRecognitionPermission = PermissionChecker.checkSelfPermission(this, activityPermission)
        if (activityRecognitionPermission != PermissionChecker.PERMISSION_GRANTED) {
            stopSelf()
            return
        }
    }

    companion object {
        private const val TAG = "StepTrackerService"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID ="StepTrackerServiceChannelId"
        private const val CHANNEL_NAME ="StepTrackerServiceChannel"

        const val ACTION_START_FOREGROUND_SERVICE: String = "ACTION_START_FOREGROUND_SERVICE"
        const val ACTION_STOP_FOREGROUND_SERVICE: String = "ACTION_STOP_FOREGROUND_SERVICE"
        const val ACTION_PAUSE: String = "ACTION_PAUSE"
        const val ACTION_PLAY: String = "ACTION_PLAY"
    }
}
