
package com.example.prayerreminder.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.app.Notification

object NotificationHelper {

    const val CHANNEL_ID = "prayer_reminders"

    fun createNotificationChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Prayer Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications at prayer times"
            }

            val manager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            manager.createNotificationChannel(channel)
            android.util.Log.d(
                "PrayerReminder",
                "Channel created: ${manager.getNotificationChannel(CHANNEL_ID)?.name}"
            )
        }
    }
    fun showTestNotification(context: Context) {

        val manager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        val builder = Notification.Builder(context, CHANNEL_ID)

        val notification = builder
        .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
        .setContentTitle("Prayer Reminder")
        .setContentText("This is a test notification!")
        .setAutoCancel(true)
        .build()

        manager.notify(1001, notification)
    }
    fun showPrayerNotification(
        context: Context,
        prayerName: String
    ) {
        val manager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        val notification = Notification.Builder(context, CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
        .setContentTitle("$prayerName Prayer Time")
        .setContentText("It's time for $prayerName prayer.")
        .setAutoCancel(true)
        .build()

        manager.notify(prayerName.hashCode(), notification)
    }
}
