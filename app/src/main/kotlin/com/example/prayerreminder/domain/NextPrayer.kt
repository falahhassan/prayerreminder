package com.example.prayerreminder.domain

import java.util.Calendar

data class NextPrayer(
    val prayer: Prayer,
    val timeMillis: Long
)

fun findNextPrayer(
    prayers: List<Prayer>,
    nowMillis: Long = System.currentTimeMillis()
): NextPrayer? {

    if (prayers.isEmpty()) return null

    val now = Calendar.getInstance().apply {
        timeInMillis = nowMillis
    }

    for (prayer in prayers) {

        val parts = prayer.time.split(":")
        if (parts.size != 2) continue

        val hour = parts[0].toIntOrNull() ?: continue
        val minute = parts[1].toIntOrNull() ?: continue

        val prayerTime = (now.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (prayerTime.after(now)) {
            return NextPrayer(
                prayer = prayer,
                timeMillis = prayerTime.timeInMillis
            )
        }
    }

    // All today's prayers have passed.
    // The next prayer is tomorrow's Fajr.

    val fajr = prayers.firstOrNull {
        it.name == "Fajr"
    } ?: return null

    val parts = fajr.time.split(":")
    if (parts.size != 2) return null

    val hour = parts[0].toIntOrNull() ?: return null
    val minute = parts[1].toIntOrNull() ?: return null

    val tomorrowFajr = (now.clone() as Calendar).apply {
        add(Calendar.DAY_OF_YEAR, 1)
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    return NextPrayer(
        prayer = fajr,
        timeMillis = tomorrowFajr.timeInMillis
    )
}