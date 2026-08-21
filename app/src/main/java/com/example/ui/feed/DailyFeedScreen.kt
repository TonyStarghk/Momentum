package com.example.ui.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Habit
import com.example.ui.DailyFeedUiState
import com.example.ui.components.CalendarStrip
import com.example.ui.components.CircularProgressRing
import com.example.ui.components.GlassCard
import com.example.ui.components.HabitCard
import com.example.ui.components.HabitIcons
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoCardBg
import com.example.ui.theme.BentoCyan
import com.example.ui.theme.BentoViolet
import com.example.ui.theme.BentoVioletDark
import com.example.ui.theme.BentoVioletLight
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.FlameAmber
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
import com.example.util.DateUtils

@Composable
fun DailyFeedScreen(
    uiState: DailyFeedUiState,
    tierProgress: com.example.data.model.TierProgress? = null,
    soundEnabled: Boolean = true,
    hapticsEnabled: Boolean = true,
    onToggleSound: () -> Unit = {},
    onToggleHaptics: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onOpenXpSheet: () -> Unit = {},
    onOpenSetupWizard: () -> Unit = {},
    onUseStreakFreeze: () -> Unit = {},
    onSelectDate: (String) -> Unit,
    onSelectTimeFilter: (String) -> Unit,
    onSelectCategoryFilter: (String) -> Unit,
    onToggleCheckIn: (Long) -> Unit,
    onEditHabit: (Habit) -> Unit,
    onDeleteHabit: (Habit) -> Unit,
    onAddNewHabit: () -> Unit,
    onShareProgressClick: () -> Unit,
    onAiAdviceClick: (com.example.data.model.HabitWithWeeklyStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    val calendarDays = remember(uiState.selectedDate) {
        DateUtils.get7DayWindow(uiState.selectedDate)
    }

    val isToday = uiState.selectedDate == DateUtils.getTodayString()
    val progressPercent = (uiState.progressFraction * 100).toInt()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBackground)
            .testTag("daily_feed_screen"),
        contentPadding = PaddingValues(bottom = 110.dp)
    ) {
        // Bento Header Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Top App Bar: Greeting on left, Action buttons on right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = DateUtils.formatDisplayHeader(uiState.selectedDate).uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoVioletLight,
                            letterSpacing = 1.1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isToday) DateUtils.getGreeting() else "Selected Date",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate50
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Sound FX Toggle Button
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (soundEnabled) BentoViolet.copy(alpha = 0.2f) else Slate900)
                                .border(
                                    1.dp,
                                    if (soundEnabled) BentoVioletLight.copy(alpha = 0.4f) else GlassBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onToggleSound() }
                                .testTag("toggle_sound_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (soundEnabled) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
                                contentDescription = if (soundEnabled) "Sound Effects Enabled" else "Sound Effects Muted",
                                tint = if (soundEnabled) BentoVioletLight else Slate500,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Soft Vibration Toggle Button
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (hapticsEnabled) BentoViolet.copy(alpha = 0.2f) else Slate900)
                                .border(
                                    1.dp,
                                    if (hapticsEnabled) BentoVioletLight.copy(alpha = 0.4f) else GlassBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onToggleHaptics() }
                                .testTag("toggle_haptics_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Vibration,
                                contentDescription = if (hapticsEnabled) "Vibrations Enabled" else "Vibrations Disabled",
                                tint = if (hapticsEnabled) BentoVioletLight else Slate500,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Share Progress Button
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Slate900)
                                .border(1.dp, GlassBorder, RoundedCornerShape(10.dp))
                                .clickable { onShareProgressClick() }
                                .testTag("header_share_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "Share Progress",
                                tint = BentoVioletLight,
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        // Settings & Profile Button
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Slate900)
                                .border(1.dp, GlassBorder, RoundedCornerShape(10.dp))
                                .clickable { onOpenSettings() }
                                .testTag("header_settings_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Settings & Profile",
                                tint = Slate400,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Hero Daily Progress Bento Card
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("feed_progress_hero_card"),
                    shape = RoundedCornerShape(20.dp),
                    backgroundColor = Slate900.copy(alpha = 0.95f),
                    borderColor = BentoViolet.copy(alpha = 0.3f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "TODAY'S TARGET",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoVioletLight,
                                    letterSpacing = 1.sp
                                )

                                if (tierProgress != null) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(100.dp))
                                            .background(BentoViolet.copy(alpha = 0.2f))
                                            .border(1.dp, BentoVioletLight.copy(alpha = 0.4f), RoundedCornerShape(100.dp))
                                            .clickable { onOpenXpSheet() }
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                            .testTag("feed_tier_badge")
                                    ) {
                                        Text(
                                            text = "${tierProgress.currentTier.iconEmoji} ${tierProgress.currentTier.title}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BentoVioletLight
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = if (uiState.totalHabitsCount > 0) {
                                    "${uiState.completedCount} of ${uiState.totalHabitsCount} completed"
                                } else {
                                    "0 habits active"
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate50
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = if (uiState.totalHabitsCount == 0) {
                                    "Start from scratch: tap '+ Add Habit' below"
                                } else if (uiState.completedCount == uiState.totalHabitsCount && uiState.totalHabitsCount > 0) {
                                    "🎉 100% Perfect Day! +150 XP Bonus earned"
                                } else {
                                    "${uiState.totalHabitsCount - uiState.completedCount} habits remaining to lock streak"
                                },
                                fontSize = 12.sp,
                                color = if (uiState.completedCount == uiState.totalHabitsCount && uiState.totalHabitsCount > 0) {
                                    EmeraldGreen
                                } else {
                                    Slate400
                                }
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Large Bento Circular Progress Ring
                        CircularProgressRing(
                            progress = uiState.progressFraction,
                            size = 62.dp,
                            strokeWidth = 5.dp,
                            trackColor = Slate800,
                            gradientColors = listOf(BentoViolet, BentoVioletLight),
                            centerText = "$progressPercent%"
                        )
                    }
                }

                // Streak Freeze & XP Banner
                if (tierProgress != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenXpSheet() },
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = if (tierProgress.isStreakFreezeActiveToday) {
                            BentoCyan.copy(alpha = 0.12f)
                        } else {
                            BentoViolet.copy(alpha = 0.10f)
                        },
                        borderColor = if (tierProgress.isStreakFreezeActiveToday) {
                            BentoCyan.copy(alpha = 0.35f)
                        } else {
                            GlassBorder
                        }
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
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (tierProgress.isStreakFreezeActiveToday) "❄️" else "⚡",
                                    fontSize = 16.sp
                                )
                                Column {
                                    Text(
                                        text = if (tierProgress.isStreakFreezeActiveToday) {
                                            "Streak Shield Active Today"
                                        } else {
                                            "${tierProgress.currentTier.title} League • ${tierProgress.totalXp} XP"
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (tierProgress.isStreakFreezeActiveToday) BentoCyan else Slate50
                                    )
                                    Text(
                                        text = if (tierProgress.isStreakFreezeActiveToday) {
                                            "Consistency protected against negative XP"
                                        } else if (tierProgress.nextTier != null) {
                                            "${tierProgress.xpNeededForNextTier} XP until ${tierProgress.nextTier.title} (${tierProgress.nextTier.badgeLabel})"
                                        } else {
                                            "Apex Champion Tier Reached!"
                                        },
                                        fontSize = 11.sp,
                                        color = Slate400
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(Slate800)
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "View XP",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoVioletLight
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Bento Calendar Strip
                CalendarStrip(
                    days = calendarDays,
                    completedDayMap = uiState.completedDayMap,
                    onSelectDay = onSelectDate
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Bento Quick Filter Bar (All, Morning, Evening)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val timeFilters = listOf(
                        Triple("ALL", "All", null),
                        Triple("MORNING", "Morning", Icons.Filled.WbSunny),
                        Triple("AFTERNOON", "Afternoon", Icons.Filled.WbTwilight),
                        Triple("EVENING", "Evening", Icons.Filled.Bedtime)
                    )

                    timeFilters.forEach { (key, label, icon) ->
                        val isSelected = uiState.selectedTimeFilter == key

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(100.dp))
                                .background(
                                    if (isSelected) BentoViolet.copy(alpha = 0.2f) else Slate800
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) BentoViolet.copy(alpha = 0.4f) else Slate700,
                                    shape = RoundedCornerShape(100.dp)
                                )
                                .clickable { onSelectTimeFilter(key) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (icon != null) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        tint = if (isSelected) BentoVioletLight else Slate400,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) BentoVioletLight else Slate400
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Category Filter Pills Scrollable Row
                val categories = listOf("All") + HabitIcons.availableCategories
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = uiState.selectedCategoryFilter == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(
                                    if (isSelected) BentoViolet.copy(alpha = 0.2f) else Slate900
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) BentoViolet.copy(alpha = 0.5f) else Slate800,
                                    shape = RoundedCornerShape(100.dp)
                                )
                                .clickable { onSelectCategoryFilter(cat) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) BentoVioletLight else Slate500
                            )
                        }
                    }
                }
            }
        }

        // Habit Cards Bento Grid
        if (uiState.habits.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        borderColor = GlassBorder
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(BentoViolet.copy(alpha = 0.15f))
                                    .border(1.dp, BentoViolet.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = BentoVioletLight,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "No habits found",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate50
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Create a new habit or adjust your active filters.",
                                fontSize = 13.sp,
                                color = Slate400
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = onAddNewHabit,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = BentoVioletDark,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(100.dp),
                                    modifier = Modifier.testTag("feed_empty_add_habit_button")
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add Habit", fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = onOpenSetupWizard,
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = BentoVioletLight
                                    ),
                                    border = BorderStroke(1.dp, BentoViolet.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(100.dp),
                                    modifier = Modifier.testTag("feed_empty_setup_wizard_button")
                                ) {
                                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Setup Wizard", fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            itemsIndexed(
                items = uiState.habits,
                key = { _, it -> it.habit.id }
            ) { index, item ->
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    HabitCard(
                        item = item,
                        onToggleCheckIn = { onToggleCheckIn(item.habit.id) },
                        onEdit = { onEditHabit(item.habit) },
                        onDelete = { onDeleteHabit(item.habit) },
                        onAiAdviceClick = { onAiAdviceClick(item) },
                        isFeatured = index == 0
                    )
                }
            }
        }
    }
}

