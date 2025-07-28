package com.imgoingonanadventure.ui.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.imgoingonanadventure.ui.service.StepTrackerService.Companion.ACTION_PAUSE
import com.imgoingonanadventure.ui.service.StepTrackerService.Companion.ACTION_PLAY
import com.imgoingonanadventure.ui.service.StepTrackerService.Companion.ACTION_STOP_FOREGROUND_SERVICE
import com.imgoingontheadventure.R

class StepTrackerNotificationDecorator(private val serviceContext: Context) {

    private val notificationBuilder: NotificationCompat.Builder by lazy {
        NotificationCompat.Builder(
            serviceContext,
            CHANNEL_ID
        )
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun onUpdate(event: String) {
        val bigTextStyle = NotificationCompat.BigTextStyle()
        bigTextStyle.setBigContentTitle("You're going on an adventure!")
        bigTextStyle.bigText("new event! : $event")

        val newNotification =
            notificationBuilder
                .setSilent(false)
                .setStyle(bigTextStyle)
                .build()
        NotificationManagerCompat.from(serviceContext).notify(NOTIFICATION_ID, newNotification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun sendTextUpdate(text: String) {
        val newNotification =
            notificationBuilder
                .setSilent(true)
                .setSubText(text)
                .build()

        NotificationManagerCompat.from(serviceContext).notify(NOTIFICATION_ID, newNotification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun sendStepUpdate(stepCount: Int) {
        val bigTextStyle = NotificationCompat.BigTextStyle()
        bigTextStyle.setBigContentTitle("You're going on an adventure!")
        bigTextStyle.bigText("$stepCount steps")

        val newNotification =
            notificationBuilder
                .setStyle(bigTextStyle)
                .setSilent(true)
                .setContentText("$stepCount steps")
                .build()

        NotificationManagerCompat.from(serviceContext).notify(NOTIFICATION_ID, newNotification)
    }

    fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH,
        )
        val notificationManager = serviceContext.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }


    fun createNotification(): Notification {
        val intent = Intent()
        val pendingIntent = PendingIntent.getActivity(
            serviceContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT.or(PendingIntent.FLAG_IMMUTABLE)
        )
        val bigTextStyle = NotificationCompat.BigTextStyle()
        bigTextStyle.setBigContentTitle("You're going on an adventure!")
        bigTextStyle.bigText("Step counting...")

        val largeIconBitmap =
            BitmapFactory.decodeResource(
                serviceContext.resources,
                R.drawable.ic_launcher_background
            )

        val playIntent = Intent(serviceContext, StepTrackerService::class.java)
        playIntent.setAction(ACTION_PLAY)
        val pendingPlayIntent = PendingIntent.getService(
            serviceContext,
            0,
            playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT.or(PendingIntent.FLAG_IMMUTABLE)
        )
        val playAction =
            NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", pendingPlayIntent)

        val pauseIntent = Intent(serviceContext, StepTrackerService::class.java)
        pauseIntent.setAction(ACTION_PAUSE)
        val pendingPauseIntent =
            PendingIntent.getService(
                serviceContext,
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

        val stopIntent = Intent(serviceContext, StepTrackerService::class.java)
        stopIntent.setAction(ACTION_STOP_FOREGROUND_SERVICE)
        val pendingStopIntent =
            PendingIntent.getService(
                serviceContext,
                0,
                stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT.or(PendingIntent.FLAG_IMMUTABLE)
            )
        val stopAction =
            NotificationCompat.Action(android.R.drawable.star_on, "Stop", pendingStopIntent)

        notificationBuilder.apply {
            setStyle(bigTextStyle)
            setOngoing(true)
            setOnlyAlertOnce(true)
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

    companion object {
        const val NOTIFICATION_ID = 1234567890

        private const val CHANNEL_ID = "StepTrackerServiceChannelId"
        private const val CHANNEL_NAME = "Step Tracker Channel"
    }
}