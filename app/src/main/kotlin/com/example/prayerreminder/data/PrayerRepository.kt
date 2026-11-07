package com.example.prayerreminder.data

import com.example.prayerreminder.domain.Prayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.prayerreminder.domain.HijriDate

object PrayerRepository {

    suspend fun getTodayPrayerTimes(): List<Prayer> =
    withContext(Dispatchers.IO) {

        val date = SimpleDateFormat(
            "dd-MM-yyyy",
            Locale.US
        ).format(Date())

        val url = URL(
            "https://api.aladhan.com/v1/timingsByCity/$date?city=Baghdad&country=Iraq&method=0"
        )

        val connection =
        url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 15000
            connection.readTimeout = 15000

            if (connection.responseCode != 200) {
                throw Exception("Server error: ${connection.responseCode}")
            }

            val response = connection.inputStream
            .bufferedReader()
            .use { it.readText() }

            val json = JSONObject(response)

            if (json.getInt("code") != 200) {
                throw Exception("Could not retrieve prayer times")
            }

            val timings = json
            .getJSONObject("data")
            .getJSONObject("timings")

            listOf(
                Prayer("Fajr", timings.getString("Fajr").take(5)),
                Prayer("Dhuhr", timings.getString("Dhuhr").take(5)),
                Prayer("Asr", timings.getString("Asr").take(5)),
                Prayer("Maghrib", timings.getString("Maghrib").take(5)),
                Prayer("Isha", timings.getString("Isha").take(5))
            )

        } finally {
            connection.disconnect()
        }
    }
    suspend fun getTodayHijriDate(): HijriDate =
    withContext(Dispatchers.IO) {

        val date = SimpleDateFormat(
            "dd-MM-yyyy",
            Locale.US
        ).format(Date())

        val url = URL(
            "https://api.aladhan.com/v1/gToH?date=$date"
        )

        val connection =
        url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 15000
            connection.readTimeout = 15000

            if (connection.responseCode != 200) {
                throw Exception("Hijri date server error")
            }

            val response = connection.inputStream
            .bufferedReader()
            .use { it.readText() }

            val json = JSONObject(response)

            if (json.getInt("code") != 200) {
                throw Exception("Could not retrieve Hijri date")
            }

            val hijri = json
            .getJSONObject("data")
            .getJSONObject("hijri")

            val month = hijri.getJSONObject("month")

            HijriDate(
                day = hijri.getString("day"),
                monthArabic = month.getString("ar"),
                monthEnglish = month.getString("en"),
                year = hijri.getString("year")
            )

        } finally {
            connection.disconnect()
        }
}}
