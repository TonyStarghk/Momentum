package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val displayHeaderFormat = SimpleDateFormat("EEEE, MMMM d", Locale.US)
    private val shortMonthDayFormat = SimpleDateFormat("MMM d", Locale.US)
    private val dayOfWeekFormat = SimpleDateFormat("EEE", Locale.US)

    fun getTodayString(): String {
        return dateFormat.format(Date())
    }

    fun formatDate(calendar: Calendar): String {
        return dateFormat.format(calendar.time)
    }

    fun formatDisplayHeader(dateString: String): String {
        return try {
            val date = dateFormat.parse(dateString) ?: Date()
            displayHeaderFormat.format(date)
        } catch (e: Exception) {
            "Today"
        }
    }

    fun formatShortMonthDay(dateString: String): String {
        return try {
            val date = dateFormat.parse(dateString) ?: Date()
            shortMonthDayFormat.format(date)
        } catch (e: Exception) {
            dateString
        }
    }

    fun formatDayOfWeek(dateString: String): String {
        return try {
            val date = dateFormat.parse(dateString) ?: Date()
            dayOfWeekFormat.format(date)
        } catch (e: Exception) {
            "Day"
        }
    }

    fun parseDate(dateString: String): Calendar {
        val cal = Calendar.getInstance()
        try {
            val date = dateFormat.parse(dateString)
            if (date != null) cal.time = date
        } catch (_: Exception) {}
        return cal
    }

    fun getPastNDays(days: Int): List<String> {
        val list = mutableListOf<String>()
        val cal = Calendar.getInstance()
        for (i in (days - 1) downTo 0) {
            val current = Calendar.getInstance()
            current.add(Calendar.DAY_OF_YEAR, -i)
            list.add(dateFormat.format(current.time))
        }
        return list
    }

    fun get7DayWindow(selectedDateString: String): List<DayItem> {
        val selectedCal = parseDate(selectedDateString)
        val list = mutableListOf<DayItem>()
        // Show 3 days before, selected day, and 3 days after (or current week centered)
        val todayStr = getTodayString()
        val startCal = Calendar.getInstance().apply {
            time = selectedCal.time
            add(Calendar.DAY_OF_YEAR, -3)
        }

        for (i in 0 until 7) {
            val dCal = Calendar.getInstance().apply {
                time = startCal.time
                add(Calendar.DAY_OF_YEAR, i)
            }
            val dateStr = dateFormat.format(dCal.time)
            val dayName = dayOfWeekFormat.format(dCal.time).uppercase()
            val dayNumber = dCal.get(Calendar.DAY_OF_MONTH).toString()
            val isToday = dateStr == todayStr
            val isSelected = dateStr == selectedDateString

            list.add(
                DayItem(
                    dateString = dateStr,
                    dayOfWeek = dayName,
                    dayOfMonth = dayNumber,
                    isToday = isToday,
                    isSelected = isSelected
                )
            )
        }
        return list
    }

    fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 4..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..22 -> "Good Evening"
            else -> "Night Owl"
        }
    }
}

data class DayItem(
    val dateString: String,
    val dayOfWeek: String,
    val dayOfMonth: String,
    val isToday: Boolean,
    val isSelected: Boolean
)
