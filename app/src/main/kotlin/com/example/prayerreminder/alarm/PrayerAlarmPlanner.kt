package com.example.prayerreminder.alarm

import android.content.Context
import com.example.prayerreminder.domain.Prayer
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object PrayerAlarmPlanner {

    fun scheduleToday(
        context: Context,
        prayers: List<Prayer>
    ): Boolean {

        val now = System.currentTimeMillis()

        for (prayer in prayers) {

            val parsedTime = SimpleDateFormat(
                "HH:mm",
                Locale.US
            ).parse(prayer.time) ?: continue

            val timeCalendar = Calendar.getInstance().apply {
                time = parsedTime
            }

            val alarmCalendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val triggerTime = alarmCalendar.timeInMillis

            if (triggerTime > now) {

                val scheduled = AlarmScheduler.schedulePrayer(
                    context,
                    prayer.name,
                    triggerTime
                )

                if (!scheduled) {
                    return false
                }
            }
        }

        return true
    }
}