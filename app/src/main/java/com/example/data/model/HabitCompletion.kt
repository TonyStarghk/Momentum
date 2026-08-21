package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habit_completions",
    indices = [
        Index(value = ["habitId", "dateString"], unique = true),
        Index(value = ["dateString"])
    ]
)
data class HabitCompletion(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitId: Long,
    val dateString: String, // "YYYY-MM-DD"
    val completedAt: Long = System.currentTimeMillis(),
    val count: Int = 1
)
