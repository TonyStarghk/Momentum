package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HabitWithWeeklyStatus
import com.example.ui.theme.BentoCardBg
import com.example.ui.theme.BentoViolet
import com.example.ui.theme.BentoVioletDark
import com.example.ui.theme.BentoVioletLight
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.FlameAmber
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun HabitCard(
    item: HabitWithWeeklyStatus,
    onToggleCheckIn: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAiAdviceClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    isFeatured: Boolean = false
) {
    val habit = item.habit
    val isDone = item.isCompletedToday
    val habitColor = try {
        Color(android.graphics.Color.parseColor(habit.colorHex))
    } catch (_: Exception) {
        BentoViolet
    }

    var menuExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val buttonScale = remember { Animatable(1f) }

    val cardGlow = if (isDone) habitColor.copy(alpha = 0.35f) else null

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("habit_card_${habit.id}"),
        shape = RoundedCornerShape(24.dp),
        glowColor = cardGlow,
        borderColor = if (isDone) habitColor.copy(alpha = 0.4f) else GlassBorder
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Ambient corner glow
            if (isDone) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(100.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(habitColor.copy(alpha = 0.18f), Color.Transparent)
                            )
                        )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Top Row: Habit Icon + Info + Options Menu
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Bento Category Icon Squircle
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(habitColor.copy(alpha = 0.15f))
                            .border(1.dp, habitColor.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = HabitIcons.getIcon(habit.iconName),
                            contentDescription = habit.title,
                            tint = habitColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Title & Bento Tags
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = habit.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Time of Day Pill
                            Text(
                                text = habit.timeOfDay.label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Slate400,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(Slate800.copy(alpha = 0.6f))
                                    .border(1.dp, Slate700.copy(alpha = 0.4f), RoundedCornerShape(100.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            )

                            // Category Pill
                            Text(
                                text = habit.category,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = habitColor,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(habitColor.copy(alpha = 0.12f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Streak Pill
                    if (item.currentStreak > 0) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(Color(0x2BF97316))
                                .border(1.dp, FlameAmber.copy(alpha = 0.35f), RoundedCornerShape(100.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocalFireDepartment,
                                contentDescription = "Streak",
                                tint = FlameAmber,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "${item.currentStreak}d",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FlameAmber
                            )
                        }
                    }

                    // Options Menu
                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "Options",
                                tint = Slate500,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier.background(Slate900).border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                        ) {
                            if (onAiAdviceClick != null) {
                                DropdownMenuItem(
                                    text = { Text("AI Consistency Advice", color = BentoVioletLight, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) },
                                    leadingIcon = {
                                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = BentoVioletLight, modifier = Modifier.size(16.dp))
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onAiAdviceClick()
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("Edit Habit", color = TextPrimary, fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(Icons.Filled.Edit, contentDescription = null, tint = BentoVioletLight, modifier = Modifier.size(16.dp))
                                },
                                onClick = {
                                    menuExpanded = false
                                    onEdit()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete Habit", color = Color(0xFFEF4444), fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(Icons.Filled.DeleteOutline, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                },
                                onClick = {
                                    menuExpanded = false
                                    onDelete()
                                }
                            )
                        }
                    }
                }

                if (habit.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = habit.description,
                        fontSize = 12.sp,
                        color = Slate400,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Section: Bento Activity Strip & Glowing Check Squircle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mini Activity History
                    Column {
                        Text(
                            text = "WEEKLY ACTIVITY",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate500,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            item.weeklyHistory.forEachIndexed { index, done ->
                                val isLast = index == item.weeklyHistory.lastIndex
                                Box(
                                    modifier = Modifier
                                        .size(width = 14.dp, height = 10.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(
                                            when {
                                                done -> habitColor
                                                isLast -> Slate800
                                                else -> Slate900.copy(alpha = 0.8f)
                                            }
                                        )
                                        .then(
                                            if (isLast && !done) {
                                                Modifier.border(1.dp, habitColor.copy(alpha = 0.5f), RoundedCornerShape(3.dp))
                                            } else Modifier
                                        )
                                )
                            }
                        }
                    }

                    // Bento Check Squircle Action Button
                    val buttonBg = if (isDone) habitColor else Slate800
                    val buttonIconTint = if (isDone) Color.Black else habitColor

                    Box(
                        modifier = Modifier
                            .scale(buttonScale.value)
                            .clip(RoundedCornerShape(16.dp))
                            .background(buttonBg)
                            .border(
                                width = 1.dp,
                                color = if (isDone) habitColor else Slate700,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                scope.launch {
                                    buttonScale.animateTo(
                                        targetValue = 0.85f,
                                        animationSpec = spring(stiffness = Spring.StiffnessMedium)
                                    )
                                    buttonScale.animateTo(
                                        targetValue = 1.1f,
                                        animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioMediumBouncy)
                                    )
                                    buttonScale.animateTo(
                                        targetValue = 1.0f,
                                        animationSpec = spring(stiffness = Spring.StiffnessMedium)
                                    )
                                }
                                onToggleCheckIn()
                            }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .testTag("check_in_button_${habit.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = if (isDone) "Completed" else "Check in",
                                tint = buttonIconTint,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isDone) "DONE" else "CHECK IN",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDone) Color.Black else TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

