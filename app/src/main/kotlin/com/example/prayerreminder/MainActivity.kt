
package com.example.prayerreminder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.prayerreminder.ui.HomeScreen
import com.example.prayerreminder.alarm.NotificationHelper
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.*
import com.example.prayerreminder.ui.SettingsScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.createNotificationChannel(this)
        if (Build.VERSION.SDK_INT >= 33) {
    if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
        != PackageManager.PERMISSION_GRANTED
    ) {
        requestPermissions(
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            1001
        )
    }
}

        setContent {
    MaterialTheme {
        var showSettings by remember { mutableStateOf(false) }

        if (showSettings) {
            SettingsScreen(
                onBack = { showSettings = false }
            )
        } else {
            HomeScreen(
                onOpenSettings = { showSettings = true }
            )
        }
    }
}
    }
}
