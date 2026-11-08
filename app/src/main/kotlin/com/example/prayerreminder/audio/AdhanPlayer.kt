package com.example.prayerreminder.audio

import android.content.Context
import android.media.MediaPlayer
import com.example.prayerreminder.R

object AdhanPlayer {

    private var mediaPlayer: MediaPlayer? = null

    fun play(context: Context) {
        stop()

        mediaPlayer = MediaPlayer.create(
            context.applicationContext,
            R.raw.adhan
        )

        mediaPlayer?.setOnCompletionListener {
            stop()
        }

        mediaPlayer?.start()
    }

    fun stop() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}