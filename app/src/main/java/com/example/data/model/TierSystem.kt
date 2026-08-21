package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.BentoCyan
import com.example.ui.theme.BentoViolet
import com.example.ui.theme.BentoVioletLight
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.FlameAmber
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.Slate400

enum class TierLevel(
    val title: String,
    val minXp: Int,
    val maxXp: Int,
    val badgeLabel: String,
    val iconEmoji: String,
    val primaryColorHex: String,
    val rankIndex: Int,
    val perkDescription: String
) {
    AMATEUR(
        title = "Amateur",
        minXp = 0,
        maxXp = 499,
        badgeLabel = "Tier I",
        iconEmoji = "🌱",
        primaryColorHex = "#94A3B8",
        rankIndex = 1,
        perkDescription = "Starting the journey. Build your first 3-day streak to unlock Rookie!"
    ),
    ROOKIE(
        title = "Rookie",
        minXp = 500,
        maxXp = 999,
        badgeLabel = "Tier II",
        iconEmoji = "⚡",
        primaryColorHex = "#38BDF8",
        rankIndex = 2,
        perkDescription = "+10% Streak Multiplier Bonus. 1 Streak Freeze unlocked."
    ),
    CHALLENGER(
        title = "Challenger",
        minXp = 1000,
        maxXp = 1999,
        badgeLabel = "Tier III",
        iconEmoji = "⚔️",
        primaryColorHex = "#34D399",
        rankIndex = 3,
        perkDescription = "+15% Streak Multiplier. Access to Regional Arena Leaderboards."
    ),
    VETERAN(
        title = "Veteran",
        minXp = 2000,
        maxXp = 3499,
        badgeLabel = "Tier IV",
        iconEmoji = "🛡️",
        primaryColorHex = "#60A5FA",
        rankIndex = 4,
        perkDescription = "+20% Streak Multiplier. Extra weekly Streak Freeze shield."
    ),
    MASTER(
        title = "Master",
        minXp = 3500,
        maxXp = 5499,
        badgeLabel = "Tier V",
        iconEmoji = "🔮",
        primaryColorHex = "#A78BFA",
        rankIndex = 5,
        perkDescription = "+25% Streak Multiplier. Master Badge displayed on Leaderboards."
    ),
    GRANDMASTER(
        title = "Grandmaster",
        minXp = 5500,
        maxXp = 7999,
        badgeLabel = "Tier VI",
        iconEmoji = "👑",
        primaryColorHex = "#F59E0B",
        rankIndex = 6,
        perkDescription = "+30% Streak Multiplier. Radiant Gold Podium presence."
    ),
    LEGENDARY(
        title = "Legendary",
        minXp = 8000,
        maxXp = 11999,
        badgeLabel = "Tier VII",
        iconEmoji = "🔥",
        primaryColorHex = "#EF4444",
        rankIndex = 7,
        perkDescription = "+40% Streak Multiplier. Flame Aura & Elite Status in Leaderboard Arena."
    ),
    CHAMPION(
        title = "Champion",
        minXp = 12000,
        maxXp = Int.MAX_VALUE,
        badgeLabel = "Apex Tier",
        iconEmoji = "🏆",
        primaryColorHex = "#EC4899",
        rankIndex = 8,
        perkDescription = "Maximum +50% XP Multiplier. Apex Champion Crown & Infinite Glory!"
    );

    companion object {
        fun fromXp(xp: Int): TierLevel {
            val safeXp = xp.coerceAtLeast(0)
            return entries.lastOrNull { safeXp >= it.minXp } ?: AMATEUR
        }
    }
}

data class XpPenaltyRecord(
    val reason: String,
    val penaltyXp: Int,
    val dateString: String
)

data class TierProgress(
    val currentTier: TierLevel,
    val nextTier: TierLevel?,
    val totalXp: Int,
    val tierProgressFraction: Float, // 0f to 1f
    val xpInCurrentTier: Int,
    val xpNeededForNextTier: Int,
    val baseCheckInXp: Int,
    val streakBonusXp: Int,
    val consistencyBonusXp: Int,
    val perfectDayBonusXp: Int,
    val negativePenaltyXp: Int,
    val recentPenalties: List<XpPenaltyRecord> = emptyList(),
    val streakFreezesAvailable: Int = 2,
    val isStreakFreezeActiveToday: Boolean = false
)
