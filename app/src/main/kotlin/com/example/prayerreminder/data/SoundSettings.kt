package com.example.prayerreminder.data

import android.content.Context

object SoundSettings {

    private const val PREFS_NAME = "prayer_settings"
    private const val SOUND_KEY = "notification_sound"

    const val FULL_ADHAN = "Full Adhan"
    const val SHORT_NOTIFICATION = "Short notification"
    const val SILENT = "Silent"

    fun saveSound(context: Context, sound: String) {
        val prefs = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        prefs.edit()
            .putString(SOUND_KEY, sound)
            .apply()
    }

    fun getSound(context: Context): String {
        val prefs = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        return prefs.getString(
            SOUND_KEY,
            SHORT_NOTIFICATION
        ) ?: SHORT_NOTIFICATION
    }
}