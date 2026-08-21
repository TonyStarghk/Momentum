package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TierLevel
import com.example.data.model.TierProgress
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoCardBg
import com.example.ui.theme.BentoCyan
import com.example.ui.theme.BentoViolet
import com.example.ui.theme.BentoVioletDark
import com.example.ui.theme.BentoVioletLight
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.FlameAmber
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XpBreakdownSheet(
    tierProgress: TierProgress,
    onDismiss: () -> Unit,
    onUseStreakFreeze: () -> Unit,
    onSendTestReminder: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showRoadmap by remember { mutableStateOf(false) }

    val currentTier = tierProgress.currentTier
    val nextTier = tierProgress.nextTier

    val tierColor = try {
        Color(android.graphics.Color.parseColor(currentTier.primaryColorHex))
    } catch (_: Exception) {
        BentoVioletLight
    }

    val animatedProgress by animateFloatAsState(
        targetValue = tierProgress.tierProgressFraction,
        label = "xp_progress"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BentoCardBg,
        dragHandle = null,
        modifier = modifier.testTag("xp_breakdown_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(tierColor.copy(alpha = 0.2f))
                            .border(1.dp, tierColor.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = currentTier.iconEmoji, fontSize = 20.sp)
                    }
                    Column {
                        Text(
                            text = "TIER & XP PROGRESSION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate400,
                            letterSpacing = 1.1.sp
                        )
                        Text(
                            text = "${currentTier.title} (${currentTier.badgeLabel})",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate50
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp).testTag("close_xp_sheet")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = Slate400,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Main Tier Status Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                backgroundColor = Slate900.copy(alpha = 0.8f),
                borderColor = tierColor.copy(alpha = 0.35f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "Current Experience",
                                fontSize = 12.sp,
                                color = Slate400
                            )
                            Text(
                                text = "${tierProgress.totalXp} XP",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = tierColor
                            )
                        }

                        if (nextTier != null) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Next: ${nextTier.title}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Slate400
                                )
                                Text(
                                    text = "${tierProgress.xpNeededForNextTier} XP to go",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoCyan
                                )
                            }
                        } else {
                            Text(
                                text = "MAX TIER REACHED 👑",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = FlameAmber
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress Bar
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(100.dp)),
                        color = tierColor,
                        trackColor = Slate800,
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = currentTier.perkDescription,
                        fontSize = 12.sp,
                        color = Slate400,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Streak Freeze & Rest Day Protection Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                backgroundColor = BentoViolet.copy(alpha = 0.10f),
                borderColor = BentoVioletLight.copy(alpha = 0.25f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(BentoCyan.copy(alpha = 0.15f))
                                .border(1.dp, BentoCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AcUnit,
                                contentDescription = "Streak Freeze",
                                tint = BentoCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Streak Freeze Shield",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate50
                            )
                            Text(
                                text = if (tierProgress.isStreakFreezeActiveToday) {
                                    "❄️ Active Today — Streak & XP Protected!"
                                } else {
                                    "${tierProgress.streakFreezesAvailable} shields left • Protects from negative XP"
                                },
                                fontSize = 12.sp,
                                color = if (tierProgress.isStreakFreezeActiveToday) BentoCyan else Slate400
                            )
                        }
                    }

                    if (!tierProgress.isStreakFreezeActiveToday && tierProgress.streakFreezesAvailable > 0) {
                        Button(
                            onClick = onUseStreakFreeze,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BentoCyan.copy(alpha = 0.2f),
                                contentColor = BentoCyan
                            ),
                            shape = RoundedCornerShape(100.dp),
                            modifier = Modifier.testTag("use_streak_freeze_button")
                        ) {
                            Text("Freeze", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // XP Calculation Formula Breakdown
            Text(
                text = "XP EARNINGS & PENALTIES BREAKDOWN",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Slate400,
                letterSpacing = 1.1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                XpFormulaItem(
                    icon = Icons.Filled.CheckCircle,
                    iconColor = BentoCyan,
                    title = "Habit Check-Ins",
                    subtitle = "+50 XP per completed routine",
                    valueText = "+${tierProgress.baseCheckInXp} XP"
                )

                XpFormulaItem(
                    icon = Icons.Filled.LocalFireDepartment,
                    iconColor = FlameOrange,
                    title = "Active Streak Multiplier",
                    subtitle = "+25 XP per consecutive day",
                    valueText = "+${tierProgress.streakBonusXp} XP"
                )

                XpFormulaItem(
                    icon = Icons.Filled.EmojiEvents,
                    iconColor = FlameAmber,
                    title = "100% Perfect Days",
                    subtitle = "+150 XP for completing all scheduled habits",
                    valueText = "+${tierProgress.perfectDayBonusXp} XP"
                )

                XpFormulaItem(
                    icon = Icons.Filled.TrendingUp,
                    iconColor = EmeraldGreen,
                    title = "Consistency Rate Bonus",
                    subtitle = "30-day consistency score multiplier",
                    valueText = "+${tierProgress.consistencyBonusXp} XP"
                )

                // Negative XP Penalty Item
                XpFormulaItem(
                    icon = Icons.Filled.Warning,
                    iconColor = CrimsonRed,
                    title = "Broken Streak & Missed Consistency Penalties",
                    subtitle = "-120 XP on broken streaks • -60 XP missed routine (without freeze)",
                    valueText = if (tierProgress.negativePenaltyXp > 0) "-${tierProgress.negativePenaltyXp} XP" else "0 XP (Clean)",
                    isPenalty = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Row: Test Smart Reminder & View Full Tier Roadmap
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Test Smart Reminder Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Slate800)
                        .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
                        .clickable { onSendTestReminder() }
                        .padding(vertical = 12.dp, horizontal = 10.dp)
                        .testTag("test_reminder_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.NotificationsActive,
                            contentDescription = null,
                            tint = BentoVioletLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Test Reminder",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate50
                        )
                    }
                }

                // View Roadmap Toggle
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (showRoadmap) BentoVioletDark else Slate800)
                        .border(
                            1.dp,
                            if (showRoadmap) BentoVioletLight else GlassBorder,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { showRoadmap = !showRoadmap }
                        .padding(vertical = 12.dp, horizontal = 10.dp)
                        .testTag("toggle_roadmap_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Stars,
                            contentDescription = null,
                            tint = if (showRoadmap) Color.White else BentoCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (showRoadmap) "Hide Roadmap" else "Tier Roadmap",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (showRoadmap) Color.White else Slate50
                        )
                    }
                }
            }

            // Expandable Tier Roadmap
            if (showRoadmap) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ALL 8 TIER RANKS & PERKS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400,
                    letterSpacing = 1.1.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TierLevel.entries.forEach { level ->
                        val isCurrent = level == currentTier
                        val isUnlocked = tierProgress.totalXp >= level.minXp
                        val itemColor = try {
                            Color(android.graphics.Color.parseColor(level.primaryColorHex))
                        } catch (_: Exception) {
                            BentoVioletLight
                        }

                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            backgroundColor = if (isCurrent) BentoViolet.copy(alpha = 0.2f) else Slate900,
                            borderColor = if (isCurrent) itemColor else if (isUnlocked) Slate700 else Slate800
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = level.iconEmoji, fontSize = 22.sp)

                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = level.title,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isUnlocked) Slate50 else Slate500
                                            )
                                            Text(
                                                text = level.badgeLabel,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = itemColor
                                            )
                                            if (isCurrent) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(100.dp))
                                                        .background(itemColor.copy(alpha = 0.2f))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "CURRENT",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = itemColor
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = level.perkDescription,
                                            fontSize = 11.sp,
                                            color = if (isUnlocked) Slate400 else Slate500,
                                            lineHeight = 14.sp
                                        )
                                    }
                                }

                                Text(
                                    text = "${level.minXp} XP",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUnlocked) itemColor else Slate500
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun XpFormulaItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    valueText: String,
    isPenalty: Boolean = false
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        backgroundColor = if (isPenalty) CrimsonRed.copy(alpha = 0.08f) else Slate900,
        borderColor = if (isPenalty) CrimsonRed.copy(alpha = 0.25f) else GlassBorder
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(17.dp)
                    )
                }

                Column {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isPenalty) CrimsonRed else Slate50
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = Slate400
                    )
                }
            }

            Text(
                text = valueText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPenalty) CrimsonRed else BentoCyan
            )
        }
    }
}
