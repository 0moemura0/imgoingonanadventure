package com.imgoingonanadventure.ui.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.BitmapFactory
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.MutableLiveData
import com.imgoingonanadventure.App
import com.imgoingontheadventure.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class StepTrackerService : Service() {
    private lateinit var listener: StepCountEventListener

    private val scope = CoroutineScope(Dispatchers.Main)
    private val trackerRepository = App.appModule.repositoryModule.trackerRepository

    private val sensorManager: SensorManager by lazy { applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private val sensor: Sensor? by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) }
    private val notificationBuilder by lazy { NotificationCompat.Builder(this, CHANNEL_ID) }
    override fun onCreate() {
        super.onCreate()
        checkPermission()
        createChannel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                createNotification(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
            )
        } else {
            startForeground(NOTIFICATION_ID, createNotification())
        }
        startStepCounter()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action = intent.action
            if (action != null) when (action) {
                ACTION_START_FOREGROUND_SERVICE -> {
                    Log.d(TAG, "Foreground service is started.")
                    checkPermission()
                    createChannel()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        startForeground(
                            NOTIFICATION_ID,
                            createNotification(),
                            ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
                        )
                    } else {
                        startForeground(NOTIFICATION_ID, createNotification())
                    }
                    sendTextUpdate("Counting!")
                    startStepCounter()
                }

                ACTION_STOP_FOREGROUND_SERVICE -> {
                    stopStepCounter()
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }

                ACTION_PLAY -> {
                    sendTextUpdate("Counting!")
                    startStepCounter()
                }

                ACTION_PAUSE -> {
                    sendTextUpdate("Not counting!")
                    stopStepCounter()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
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
        val intent = Intent()
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT.or(PendingIntent.FLAG_IMMUTABLE)
        )
        val bigTextStyle = NotificationCompat.BigTextStyle()
        bigTextStyle.setBigContentTitle("You're going on an adventure!")
        bigTextStyle.bigText("Step counting...")

        val largeIconBitmap =
            BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_background)

        val playIntent = Intent(this, StepTrackerService::class.java)
        playIntent.setAction(ACTION_PLAY)
        val pendingPlayIntent = PendingIntent.getService(
            this,
            0,
            playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT.or(PendingIntent.FLAG_IMMUTABLE)
        )
        val playAction =
            NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", pendingPlayIntent)

        val pauseIntent = Intent(this, StepTrackerService::class.java)
        pauseIntent.setAction(ACTION_PAUSE)
        val pendingPauseIntent =
            PendingIntent.getService(
                this,
                0,
                pauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT.or(PendingIntent.FLAG_IMMUTABLE)
            )
        val pauseAction =
            NotificationCompat.Action(
                android.R.drawable.ic_media_pause,
                "Pause",
                pendingPauseIntent
            )

        val stopIntent = Intent(this, StepTrackerService::class.java)
        stopIntent.setAction(ACTION_STOP_FOREGROUND_SERVICE)
        val pendingStopIntent =
            PendingIntent.getService(
                this,
                0,
                stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT.or(PendingIntent.FLAG_IMMUTABLE)
            )
        val stopAction =
            NotificationCompat.Action(android.R.drawable.star_on, "Stop", pendingStopIntent)

        notificationBuilder.apply {
            setStyle(bigTextStyle)
            setWhen(System.currentTimeMillis())
            setSmallIcon(R.mipmap.ic_launcher)
            setLargeIcon(largeIconBitmap)
            setFullScreenIntent(pendingIntent, true)
            addAction(playAction)
            addAction(pauseAction)
            addAction(stopAction)
        }
        return notificationBuilder.build()
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun stopService(name: Intent?): Boolean {
        stopStepCounter()
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
        val newNotification =
            notificationBuilder
                .setContentText("$stepCount steps")
                .build()

        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, newNotification)
        liveStepCount.postValue(stepCount)
    }

    @SuppressLint("MissingPermission")
    private fun sendTextUpdate(text: String) {
        val newNotification =
            notificationBuilder
                .setSubText(text)
                .build()

        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, newNotification)
    }

    private fun stopStepCounter() {
        listener.stop()
        sensorManager.unregisterListener(listener, sensor)
    }

    companion object {
        val liveStepCount: MutableLiveData<Int> = MutableLiveData()

        private const val TAG = "StepTrackerService"

        private const val NOTIFICATION_ID = 1234567890
        private const val CHANNEL_ID = "StepTrackerServiceChannelId"
        private const val CHANNEL_NAME = "StepTrackerServiceChannel"

        const val ACTION_START_FOREGROUND_SERVICE: String = "ACTION_START_FOREGROUND_SERVICE"
        const val ACTION_STOP_FOREGROUND_SERVICE: String = "ACTION_STOP_FOREGROUND_SERVICE"
        const val ACTION_PAUSE: String = "ACTION_PAUSE"
        const val ACTION_PLAY: String = "ACTION_PLAY"
    }
}
