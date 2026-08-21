package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TimeOfDay(val label: String) {
    MORNING("Morning"),
    AFTERNOON("Afternoon"),
    EVENING("Evening"),
    ANYTIME("Anytime")
}

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val timeOfDay: TimeOfDay = TimeOfDay.MORNING,
    val category: String = "Health", // Health, Mind, Fitness, Productivity, Creativity, Learning
    val iconName: String = "sparkles", // water, book, dumbbell, meditation, code, moon, sun, runner, heart, sparkles, coffee, smile
    val colorHex: String = "#00F0FF", // Accent glow hex
    val targetPerDay: Int = 1,
    val createdAt: Long = System.currentTimeMillis(),
    val isArchived: Boolean = false
)
