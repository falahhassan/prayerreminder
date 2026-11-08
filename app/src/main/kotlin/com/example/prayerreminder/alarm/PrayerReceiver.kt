package com.example.prayerreminder.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.prayerreminder.audio.AdhanPlaybackService
import com.example.prayerreminder.data.SoundSettings

class PrayerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val prayerName = intent.getStringExtra(EXTRA_PRAYER_NAME)
            ?: return

        when (SoundSettings.getSound(context)) {
    SoundSettings.FULL_ADHAN -> {
        AdhanPlaybackService.start(context)
    }

    else -> {
        NotificationHelper.showPrayerNotification(
            context,
            prayerName
        )
    }
}
    }

    companion object {
        const val EXTRA_PRAYER_NAME =
            "com.example.prayerreminder.PRAYER_NAME"
    }
}