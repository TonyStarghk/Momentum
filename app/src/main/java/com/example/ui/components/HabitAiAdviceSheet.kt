package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.HabitWithWeeklyStatus
import com.example.data.model.TaskAiAdvice
import com.example.ui.theme.BentoCyan
import com.example.ui.theme.BentoViolet
import com.example.ui.theme.BentoVioletDark
import com.example.ui.theme.BentoVioletLight
import com.example.ui.theme.FlameAmber
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@Composable
fun HabitAiAdviceDialog(
    habitStatus: HabitWithWeeklyStatus,
    advice: TaskAiAdvice?,
    isLoading: Boolean,
    onRegenerate: () -> Unit,
    onDismiss: () -> Unit
) {
    val habit = habitStatus.habit

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(28.dp))
                .border(1.dp, GlassBorder, RoundedCornerShape(28.dp))
                .testTag("ai_advice_dialog"),
            color = Slate900
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Bar
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
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(BentoViolet, BentoVioletDark)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = "AI Coach",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "GEMINI AI COACH",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoVioletLight,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = habit.title,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate50
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_ai_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = Slate400
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Habit Status Pill Strip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(Slate800)
                            .border(1.dp, Slate700, RoundedCornerShape(100.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocalFireDepartment,
                                contentDescription = "Streak",
                                tint = FlameAmber,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${habitStatus.currentStreak}d Streak",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Slate50
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(Slate800)
                            .border(1.dp, Slate700, RoundedCornerShape(100.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = habit.timeOfDay.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BentoCyan
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(Slate800)
                            .border(1.dp, Slate700, RoundedCornerShape(100.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "${habitStatus.totalCompletions} Total Check-ins",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate400
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(
                                color = BentoVioletLight,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "Gemini is analyzing habit triggers...",
                                fontSize = 13.sp,
                                color = Slate400
                            )
                        }
                    }
                } else if (advice != null) {
                    // Bento Advice Grid
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                        // Bento Card 1: Habit Stack Cue
                        AdviceBentoCard(
                            title = "HABIT STACKING ANCHOR",
                            content = advice.habitStackCue,
                            icon = Icons.Filled.ElectricBolt,
                            accentColor = BentoViolet
                        )

                        // Bento Card 2: 2-Minute Micro-Action
                        AdviceBentoCard(
                            title = "2-MINUTE RULE (LOW RESISTANCE)",
                            content = advice.twoMinuteRule,
                            icon = Icons.Filled.Lightbulb,
                            accentColor = FlameAmber
                        )

                        // Bento Card 3: Friction Reducer
                        AdviceBentoCard(
                            title = "ENVIRONMENT FRICTION REDUCER",
                            content = advice.frictionReducer,
                            icon = Icons.Filled.Shield,
                            accentColor = BentoCyan
                        )

                        // Bento Card 4: Neuroscience Insight
                        AdviceBentoCard(
                            title = "PSYCHOLOGY OF AUTOMATICITY",
                            content = advice.psychologyInsight,
                            icon = Icons.Filled.Psychology,
                            accentColor = BentoVioletLight
                        )

                        // Recommended Slot
                        AdviceBentoCard(
                            title = "OPTIMAL TIME WINDOW",
                            content = advice.recommendedTimeSlot,
                            icon = Icons.Filled.Schedule,
                            accentColor = Color(0xFF10B981)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onRegenerate,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("regenerate_advice_button"),
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Slate800,
                            contentColor = BentoVioletLight
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Regenerate",
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Refresh Advice",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("apply_advice_button"),
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BentoViolet,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Got It",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdviceBentoCard(
    title: String,
    content: String,
    icon: ImageVector,
    accentColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Slate800.copy(alpha = 0.7f))
            .border(1.dp, GlassBorder, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(accentColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(13.dp)
                    )
                }
                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = content,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Normal,
                color = Slate50
            )
        }
    }
}
