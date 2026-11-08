package com.example.prayerreminder.audio

import android.app.Service
import android.app.Notification
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import com.example.prayerreminder.R
import com.example.prayerreminder.alarm.NotificationHelper

class AdhanPlaybackService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        if (intent?.action == ACTION_STOP) {
            stopAdhan()
            return START_NOT_STICKY
        }

        startForeground(
            NOTIFICATION_ID,
            createNotification()
        )

        releasePlayer()

        mediaPlayer = MediaPlayer.create(
            this,
            R.raw.adhan
        )

        if (mediaPlayer == null) {
            stopSelf()
            return START_NOT_STICKY
        }

        mediaPlayer?.setOnCompletionListener {
            stopAdhan()
        }

        mediaPlayer?.start()

        return START_NOT_STICKY
    }

    private fun createNotification(): Notification {
        return Notification.Builder(
            this,
            NotificationHelper.SILENT_CHANNEL_ID
        )
        .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
        .setContentTitle("Adhan")
        .setContentText("Adhan is playing")
        .setOngoing(true)
        .build()
    }
    private fun releasePlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun stopAdhan() {
        releasePlayer()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        
            releasePlayer()
            super.onDestroy()
        }
        

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
    const val ACTION_STOP =
        "com.example.prayerreminder.STOP_ADHAN"

    private const val NOTIFICATION_ID = 2001

    fun start(context: android.content.Context) {
        val intent = Intent(
            context,
            AdhanPlaybackService::class.java
        )

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }
}

}
