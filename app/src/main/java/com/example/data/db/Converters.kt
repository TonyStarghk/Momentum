package com.example.data.db

import androidx.room.TypeConverter
import com.example.data.model.TimeOfDay

class Converters {
    @TypeConverter
    fun fromTimeOfDay(value: TimeOfDay?): String {
        return value?.name ?: TimeOfDay.MORNING.name
    }

    @TypeConverter
    fun toTimeOfDay(value: String?): TimeOfDay {
        return try {
            if (value != null) TimeOfDay.valueOf(value) else TimeOfDay.MORNING
        } catch (e: Exception) {
            TimeOfDay.MORNING
        }
    }
}
