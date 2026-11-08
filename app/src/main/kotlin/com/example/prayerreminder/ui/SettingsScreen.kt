package com.example.prayerreminder.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.prayerreminder.data.SoundSettings
import com.example.prayerreminder.alarm.NotificationHelper
import com.example.prayerreminder.audio.AdhanPlaybackService

@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var selectedSound by remember {
        mutableStateOf(SoundSettings.getSound(context))
    }

    val soundOptions = listOf(
        SoundSettings.FULL_ADHAN,
        SoundSettings.SHORT_NOTIFICATION,
        SoundSettings.SILENT
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("← Back")
            }

            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Prayer Notifications",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Choose how you want to be notified.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        soundOptions.forEach { option ->

            val description = when (option) {
                SoundSettings.FULL_ADHAN ->
                    "Play the complete Adhan"

                SoundSettings.SHORT_NOTIFICATION ->
                    "Play a short notification sound"

                else ->
                    "Receive notifications without sound"
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clickable {
                        selectedSound = option
                        SoundSettings.saveSound(context, option)
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedSound == option) {
                        MaterialTheme.colorScheme.secondaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    RadioButton(
                        selected = selectedSound == option,
                        onClick = {
                            selectedSound = option
                            SoundSettings.saveSound(context, option)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (selectedSound == SoundSettings.FULL_ADHAN) {
    AdhanPlaybackService.start(context)
} else {
    NotificationHelper.showTestNotification(context)
}
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Test Sound")
        }
    }
}