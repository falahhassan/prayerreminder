
package com.example.prayerreminder.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.app.Notification
import android.media.AudioAttributes
import android.provider.Settings
import com.example.prayerreminder.data.SoundSettings

object NotificationHelper {

    const val CHANNEL_ID = "prayer_reminders_sound_vibration"
    const val SILENT_CHANNEL_ID = "prayer_reminders_silent"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val manager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            // Sound channel
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Prayer Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications at prayer times"

                setSound(
                    Settings.System.DEFAULT_NOTIFICATION_URI,
                    AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
                )
                enableVibration(true)

                vibrationPattern = longArrayOf(
                    0,
                    400,
                    200,
                    400
                )
            }

            // Silent channel
            val silentChannel = NotificationChannel(
                SILENT_CHANNEL_ID,
                "Silent Prayer Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Prayer notifications without sound"
                setSound(null, null)
                enableVibration(false)
            }

            manager.createNotificationChannel(channel)
            manager.createNotificationChannel(silentChannel)
        }
    }
    fun showTestNotification(context: Context) {

        val manager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        val channelId = when (SoundSettings.getSound(context)) {
            SoundSettings.SILENT -> SILENT_CHANNEL_ID
            else -> CHANNEL_ID
        }

        val notification = Notification.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
        .setContentTitle("Prayer Reminder")
        .setContentText("This is a test notification!")
        .setAutoCancel(true)
        .build()

        manager.notify(channelId.hashCode(), notification)
    }
    fun showPrayerNotification(
        context: Context,
        prayerName: String
    ) {
        val manager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        val channelId = when (SoundSettings.getSound(context)) {
            SoundSettings.SILENT -> SILENT_CHANNEL_ID
            else -> CHANNEL_ID
        }

        val notification = Notification.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
        .setContentTitle("$prayerName Prayer Time")
        .setContentText("It's time for $prayerName prayer.")
        .setAutoCancel(true)
        .build()

        manager.notify(prayerName.hashCode(), notification)
    }
}
