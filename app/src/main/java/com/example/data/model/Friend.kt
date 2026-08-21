package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friends")
data class Friend(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val username: String,
    val avatarEmoji: String = "⚡",
    val region: String = "North America",
    val topHabitName: String = "Daily Focus",
    val streakDays: Int = 0,
    val weeklyCompletions: Int = 0,
    val cheerCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
