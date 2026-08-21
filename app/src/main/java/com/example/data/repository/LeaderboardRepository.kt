package com.example.data.repository

import com.example.data.model.Friend
import com.example.data.model.LeaderboardScope
import com.example.data.model.LeaderboardTimeframe
import com.example.data.model.LeaderboardUser
import com.example.data.model.TierLevel
import com.example.data.model.TierProgress
import com.example.data.model.XpPenaltyRecord

class LeaderboardRepository {

    fun calculateUserTierProgress(
        totalCompletions: Int,
        currentStreak: Int,
        bestStreak: Int,
        perfectDaysCount: Int,
        completionRate: Float,
        brokenStreakCount: Int,
        missedDaysCount: Int,
        streakFreezesAvailable: Int = 2,
        isStreakFreezeActiveToday: Boolean = false,
        recentPenalties: List<XpPenaltyRecord> = emptyList()
    ): TierProgress {
        val baseCheckInXp = totalCompletions * 50
        val streakBonusXp = currentStreak * 25
        val consistencyBonusXp = (completionRate * 10f).toInt()
        val perfectDayBonusXp = perfectDaysCount * 150

        // Negative XP penalty: Deduct XP for broken streaks and missed consistency
        val streakBreakPenalty = brokenStreakCount * 120
        val missedDayPenalty = missedDaysCount * 60
        val totalNegativePenalty = streakBreakPenalty + missedDayPenalty

        val grossXp = baseCheckInXp + streakBonusXp + consistencyBonusXp + perfectDayBonusXp
        val totalXp = (grossXp - totalNegativePenalty).coerceAtLeast(0)

        val currentTier = TierLevel.fromXp(totalXp)
        val nextTier = TierLevel.entries.getOrNull(currentTier.ordinal + 1)

        val xpInTier = totalXp - currentTier.minXp
        val tierSpan = if (nextTier != null) (nextTier.minXp - currentTier.minXp) else 1
        val fraction = if (nextTier != null) {
            (xpInTier.toFloat() / tierSpan.toFloat()).coerceIn(0f, 1f)
        } else {
            1f
        }
        val neededForNext = if (nextTier != null) (nextTier.minXp - totalXp).coerceAtLeast(0) else 0

        return TierProgress(
            currentTier = currentTier,
            nextTier = nextTier,
            totalXp = totalXp,
            tierProgressFraction = fraction,
            xpInCurrentTier = xpInTier.coerceAtLeast(0),
            xpNeededForNextTier = neededForNext,
            baseCheckInXp = baseCheckInXp,
            streakBonusXp = streakBonusXp,
            consistencyBonusXp = consistencyBonusXp,
            perfectDayBonusXp = perfectDayBonusXp,
            negativePenaltyXp = totalNegativePenalty,
            recentPenalties = recentPenalties,
            streakFreezesAvailable = streakFreezesAvailable,
            isStreakFreezeActiveToday = isStreakFreezeActiveToday
        )
    }

    fun getLeaderboard(
        friends: List<Friend>,
        userStreak: Int,
        userWeeklyCompletions: Int,
        userTotalCompletions: Int,
        userTopHabit: String,
        userTotalXp: Int,
        userTierTitle: String,
        scope: LeaderboardScope = LeaderboardScope.FRIENDS,
        selectedRegion: String = "All Regions",
        timeframe: LeaderboardTimeframe = LeaderboardTimeframe.WEEKLY,
        userName: String = "New Challenger",
        userHandle: String = "@challenger",
        userAvatarEmoji: String = "🌱",
        userRegion: String = "North America"
    ): List<LeaderboardUser> {
        val currentUser = LeaderboardUser(
            id = "current_user",
            friendId = null,
            name = userName.ifBlank { "New Challenger" },
            username = userHandle.ifBlank { "@challenger" },
            avatarEmoji = userAvatarEmoji.ifBlank { "🌱" },
            rank = 1,
            streakDays = userStreak,
            weeklyCompletions = userWeeklyCompletions,
            totalXp = userTotalXp,
            tierTitle = userTierTitle,
            isCurrentUser = true,
            topHabitName = userTopHabit.ifBlank { "Daily Routine" },
            cheerCount = (userStreak * 2) + 1,
            region = userRegion.ifBlank { "North America" }
        )

        // Map real friends into LeaderboardUser list
        val filteredFriends = when (scope) {
            LeaderboardScope.FRIENDS -> friends
            LeaderboardScope.REGION -> {
                if (selectedRegion.equals("All Regions", ignoreCase = true)) {
                    friends
                } else {
                    friends.filter { it.region.equals(selectedRegion, ignoreCase = true) }
                }
            }
        }

        val friendUsers = filteredFriends.map { friend ->
            // Calculate friend XP using consistent tier formulas
            val friendXp = ((friend.weeklyCompletions * 4) * 50) + (friend.streakDays * 25) + 300
            val friendTier = TierLevel.fromXp(friendXp).title

            LeaderboardUser(
                id = "friend_${friend.id}",
                friendId = friend.id,
                name = friend.name,
                username = if (friend.username.startsWith("@")) friend.username else "@${friend.username}",
                avatarEmoji = friend.avatarEmoji.ifBlank { "⚡" },
                rank = 0,
                streakDays = friend.streakDays,
                weeklyCompletions = friend.weeklyCompletions,
                totalXp = friendXp,
                tierTitle = friendTier,
                isCurrentUser = false,
                topHabitName = friend.topHabitName.ifBlank { "Daily Routine" },
                cheerCount = friend.cheerCount,
                region = friend.region.ifBlank { "North America" }
            )
        }

        // Include current user unless strictly filtered out by specific regional mismatch
        val shouldIncludeCurrentUser = when (scope) {
            LeaderboardScope.FRIENDS -> true
            LeaderboardScope.REGION -> {
                selectedRegion.equals("All Regions", ignoreCase = true) ||
                    userRegion.equals(selectedRegion, ignoreCase = true)
            }
        }

        val combinedList = if (shouldIncludeCurrentUser) {
            (friendUsers + currentUser).toMutableList()
        } else {
            friendUsers.toMutableList()
        }

        // Sort based on timeframe
        if (timeframe == LeaderboardTimeframe.WEEKLY) {
            combinedList.sortByDescending { it.weeklyCompletions * 100 + it.streakDays * 50 }
        } else {
            combinedList.sortByDescending { it.totalXp }
        }

        return combinedList.mapIndexed { index, user ->
            user.copy(rank = index + 1)
        }
    }
}

