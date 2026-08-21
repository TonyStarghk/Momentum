package com.example.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnalyticsSummary
import com.example.data.model.TimeOfDay
import com.example.ui.components.ConsistencyHeatmap
import com.example.ui.components.GlassCard
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoCardBg
import com.example.ui.theme.BentoCyan
import com.example.ui.theme.BentoViolet
import com.example.ui.theme.BentoVioletDark
import com.example.ui.theme.BentoVioletLight
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.FlameAmber
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AnalyticsScreen(
    summary: AnalyticsSummary,
    tierProgress: com.example.data.model.TierProgress? = null,
    onOpenXpSheet: () -> Unit = {},
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBackground)
            .testTag("analytics_screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp)
    ) {
        // Bento Title Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ANALYTICS & INSIGHTS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoVioletLight,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Habit Momentum",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate50
                        )
                        Text(
                            text = "Consistency and performance metrics",
                            fontSize = 13.sp,
                            color = Slate400
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(BentoViolet.copy(alpha = 0.2f))
                            .border(1.dp, BentoViolet.copy(alpha = 0.5f), RoundedCornerShape(100.dp))
                            .clickable { onShareClick() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("analytics_share_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "Share",
                                tint = BentoVioletLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Share",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoVioletLight
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }
        }

        // Hero Tier & XP Progression Card
        if (tierProgress != null) {
            item {
                val currentTier = tierProgress.currentTier
                val tierColor = try {
                    androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(currentTier.primaryColorHex))
                } catch (_: Exception) {
                    BentoVioletLight
                }

                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenXpSheet() }
                        .testTag("analytics_tier_card"),
                    shape = RoundedCornerShape(20.dp),
                    backgroundColor = Slate900.copy(alpha = 0.9f),
                    borderColor = tierColor.copy(alpha = 0.4f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(tierColor.copy(alpha = 0.2f))
                                        .border(1.dp, tierColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = currentTier.iconEmoji, fontSize = 24.sp)
                                }

                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = currentTier.title,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Slate50
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(100.dp))
                                                .background(tierColor.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = currentTier.badgeLabel,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = tierColor
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${tierProgress.totalXp} Total XP",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = tierColor
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(BentoViolet.copy(alpha = 0.2f))
                                    .border(1.dp, BentoVioletLight.copy(alpha = 0.4f), RoundedCornerShape(100.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "XP Breakdown ⚡",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoVioletLight
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Tier Progress Bar
                        androidx.compose.material3.LinearProgressIndicator(
                            progress = { tierProgress.tierProgressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(100.dp)),
                            color = tierColor,
                            trackColor = Slate800
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (tierProgress.negativePenaltyXp > 0) "⚠️ -${tierProgress.negativePenaltyXp} XP Streak Penalties Applied" else "🛡️ Clean Record: 0 Negative XP",
                                fontSize = 11.sp,
                                color = if (tierProgress.negativePenaltyXp > 0) CrimsonRed else EmeraldGreen
                            )

                            if (tierProgress.nextTier != null) {
                                Text(
                                    text = "${tierProgress.xpNeededForNextTier} XP to ${tierProgress.nextTier.title}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoCyan
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        // 4 Key Stats Bento Grid (2x2)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Total Check-Ins
                    StatCard(
                        title = "TOTAL CHECK-INS",
                        value = "${summary.totalCheckIns}",
                        subtitle = "Lifetime entries",
                        icon = Icons.Filled.CheckCircleOutline,
                        glowColor = BentoViolet,
                        modifier = Modifier.weight(1f)
                    )

                    // Best Streak
                    StatCard(
                        title = "BEST STREAK",
                        value = "${summary.currentBestStreak} days",
                        subtitle = "Highest run",
                        icon = Icons.Filled.LocalFireDepartment,
                        glowColor = FlameAmber,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Completion Rate
                    StatCard(
                        title = "30-DAY RATE",
                        value = "${summary.overallCompletionRate.toInt()}%",
                        subtitle = "Adherence score",
                        icon = Icons.Filled.TrendingUp,
                        glowColor = EmeraldGreen,
                        modifier = Modifier.weight(1f)
                    )

                    // Perfect Days
                    StatCard(
                        title = "PERFECT DAYS",
                        value = "${summary.perfectDaysCount}",
                        subtitle = "100% completed",
                        icon = Icons.Filled.Stars,
                        glowColor = NeonCyan,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Bento Consistency Heatmap Section
        item {
            ConsistencyHeatmap(days = summary.heatmapDays)
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Time of Day Breakdown Card
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                borderColor = GlassBorder
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Time of Day Distribution",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate50
                    )
                    Text(
                        text = "When you complete habits most often",
                        fontSize = 12.sp,
                        color = Slate400
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val timeMap = summary.timeOfDayCounts
                    val totalTimeCount = timeMap.values.sum().coerceAtLeast(1)

                    listOf(
                        Triple(TimeOfDay.MORNING, BentoViolet, "Morning Routine"),
                        Triple(TimeOfDay.AFTERNOON, FlameAmber, "Afternoon Habits"),
                        Triple(TimeOfDay.EVENING, BentoCyan, "Evening Wind-down")
                    ).forEach { (tod, color, label) ->
                        val count = timeMap[tod] ?: 0
                        val fraction = count.toFloat() / totalTimeCount

                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Slate50
                                )
                                Text(
                                    text = "$count check-ins (${(fraction * 100).toInt()}%)",
                                    fontSize = 12.sp,
                                    color = Slate400
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { fraction },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(100.dp)),
                                color = color,
                                trackColor = Slate800
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Category Adherence Breakdown Bento Card
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                borderColor = GlassBorder
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Category Breakdown",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate50
                    )
                    Text(
                        text = "Check-ins grouped by lifestyle domains",
                        fontSize = 12.sp,
                        color = Slate400
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val catMap = summary.categoryCounts
                    val totalCatCount = catMap.values.sum().coerceAtLeast(1)

                    val categoryColors = mapOf(
                        "Health" to BentoCyan,
                        "Fitness" to FlameAmber,
                        "Mind" to BentoViolet,
                        "Productivity" to BentoVioletLight,
                        "Learning" to ElectricBlue
                    )

                    catMap.entries.sortedByDescending { it.value }.forEach { (cat, count) ->
                        val fraction = count.toFloat() / totalCatCount
                        val color = categoryColors[cat] ?: BentoViolet

                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Slate50
                                )
                                Text(
                                    text = "$count check-ins",
                                    fontSize = 12.sp,
                                    color = Slate400
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { fraction },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(100.dp)),
                                color = color,
                                trackColor = Slate800
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    glowColor: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        glowColor = glowColor.copy(alpha = 0.25f),
        borderColor = GlassBorder
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400,
                    letterSpacing = 0.5.sp
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(glowColor.copy(alpha = 0.15f))
                        .border(1.dp, glowColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = glowColor,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Slate50
            )

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Slate500
            )
        }
    }
}

