
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

@Composable
fun HomeScreen() {

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

    fun refreshPrayerTimes() {
        refreshScope.launch {
            loading = true
            error = null

            try {
                prayers = PrayerRepository.getTodayPrayerTimes()
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Prayer Times",
                style = MaterialTheme.typography.headlineMedium
            )

            IconButton(
                onClick = { refreshPrayerTimes() },
                enabled = !loading
            ) {
                Text(
                    text = "↻",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }
        Button(
            onClick = {
                NotificationHelper.showTestNotification(context)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Test Notification")
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
