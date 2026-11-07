
package com.example.prayerreminder.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.content.Intent

object AlarmScheduler {

    fun schedule(
        context: Context,
        triggerAtMillis: Long,
        pendingIntent: PendingIntent
    ): Boolean {

        val alarmManager = context.getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager

        // Check exact alarm permission on Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                return false
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )

        return true
    }

    fun cancel(
        context: Context,
        pendingIntent: PendingIntent
    ) {
        val alarmManager = context.getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager

        alarmManager.cancel(pendingIntent)
    }
    fun schedulePrayer(
        context: Context,
        prayerName: String,
        triggerAtMillis: Long
    ): Boolean {

        val intent = Intent(
            context,
            PrayerReceiver::class.java
        ).apply {
            putExtra(
                PrayerReceiver.EXTRA_PRAYER_NAME,
                prayerName
            )
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            prayerName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
            PendingIntent.FLAG_IMMUTABLE
        )

        return schedule(
            context,
            triggerAtMillis,
            pendingIntent
        )
    }
}
