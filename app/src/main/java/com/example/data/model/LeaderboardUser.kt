package com.example.data.model

data class LeaderboardUser(
    val id: String,
    val friendId: Long? = null,
    val name: String,
    val username: String,
    val avatarEmoji: String,
    val rank: Int,
    val streakDays: Int,
    val weeklyCompletions: Int,
    val totalXp: Int,
    val tierTitle: String, // e.g. "Diamond Titan", "Platinum Elite", "Gold Pioneer"
    val isCurrentUser: Boolean = false,
    val topHabitName: String = "Morning Focus",
    val cheerCount: Int = 0,
    val region: String = "North America"
)

enum class LeaderboardScope {
    FRIENDS,
    REGION
}

enum class LeaderboardTimeframe {
    WEEKLY,
    ALL_TIME
}
