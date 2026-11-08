
package com.example.prayerreminder.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prayerreminder.data.PrayerRepository
import com.example.prayerreminder.domain.Prayer
import com.example.prayerreminder.domain.findNextPrayer
import kotlinx.coroutines.delay
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import com.example.prayerreminder.alarm.NotificationHelper
import com.example.prayerreminder.alarm.PrayerAlarmPlanner
import com.example.prayerreminder.alarm.AlarmPermissionHelper
import com.example.prayerreminder.domain.HijriDate
import com.example.prayerreminder.audio.AdhanPlaybackService
import com.example.prayerreminder.data.SoundSettings

@Composable
fun HomeScreen(
    onOpenSettings: () -> Unit
) {

    var prayers by remember {
        mutableStateOf<List<Prayer>>(emptyList())
    }

    var loading by remember {
        mutableStateOf(true)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    var currentTime by remember {
        mutableStateOf(System.currentTimeMillis())
    }
    val refreshScope = rememberCoroutineScope()

    val context = LocalContext.current
    var alarmStatus by remember {
        mutableStateOf("Alarms not scheduled yet")
    }
    var hijriDate by remember {
        mutableStateOf<HijriDate?>(null)
    }

    fun refreshPrayerTimes() {
        refreshScope.launch {
            loading = true
            error = null

            try {
                prayers = PrayerRepository.getTodayPrayerTimes()

                val scheduled = PrayerAlarmPlanner.scheduleToday(
                    context,
                    prayers
                )

                alarmStatus = if (scheduled) {
                    "Prayer alarms scheduled successfully"
                } else {
                    "Please enable exact alarm permission"
                }

                hijriDate = try {
                    PrayerRepository.getTodayHijriDate()
                } catch (e: Exception) {
                    null
                }

            } catch (e: Exception) {
                error = e.message ?: "Something went wrong"
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(prayers) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000)
        }
    }

    val nextPrayer = findNextPrayer(prayers, currentTime)

    val remainingSeconds = nextPrayer?.let {
        ((it.timeMillis - currentTime) / 1000).coerceAtLeast(0)
    } ?: 0

    val hours = remainingSeconds / 3600
    val minutes = (remainingSeconds % 3600) / 60
    val seconds = remainingSeconds % 60

    val countdown = "%02d:%02d:%02d".format(
        hours, minutes, seconds
    )

    LaunchedEffect(Unit) {
        try {
            prayers = PrayerRepository.getTodayPrayerTimes()

            val scheduled = PrayerAlarmPlanner.scheduleToday(
                context,
                prayers
            )

            alarmStatus = if (scheduled) {
                "Prayer alarms scheduled successfully"
            } else {
                "Please enable exact alarm permission"
            }

            hijriDate = try {
                PrayerRepository.getTodayHijriDate()
            } catch (e: Exception) {
                null
            }

        } catch (e: Exception) {
            error = e.message ?: "Something went wrong"
        } finally {
            loading = false
        }
    }

    Column(
        modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onOpenSettings
            ) {
                Text("⚙", style = MaterialTheme.typography.titleLarge)
            }

            Text(
                text = "Prayer Times",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = { refreshPrayerTimes() },
                enabled = !loading
            ) {
                Text("↻", style = MaterialTheme.typography.headlineLarge)
            }
        }
       /* Button(
            onClick = {
                when (SoundSettings.getSound(context)) {
                    SoundSettings.FULL_ADHAN -> {
                        AdhanPlaybackService.start(context)
                    }

                    else -> {
                        NotificationHelper.showTestNotification(context)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Test Notification")
        }*/
        //removing the notify test button
        hijriDate?.let { hijri ->

            Text(
                text = "${hijri.day} ${hijri.monthArabic} ${hijri.year} هـ",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "${hijri.day} ${hijri.monthEnglish} ${hijri.year} AH",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (!AlarmPermissionHelper.canSchedule(context)) {
            Button(
                onClick = {
                    AlarmPermissionHelper.openSettings(context)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enable Exact Alarm Permission")
            }
        }

        if (loading) {
            CircularProgressIndicator()
        } else if (error != null) {
            Text("Error: $error")
        } else {

            nextPrayer?.let { next ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("NEXT PRAYER")

                        Text(
                            next.prayer.name,
                            style = MaterialTheme.typography.headlineMedium
                        )

                        Text(
                            countdown,
                            style = MaterialTheme.typography.headlineLarge
                        )

                        Text("Remaining time")
                    }
                }
            }

            prayers.forEach { prayer ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(prayer.name)
                        Text(prayer.time)
                    }
                }
            }
        }
    }
}
