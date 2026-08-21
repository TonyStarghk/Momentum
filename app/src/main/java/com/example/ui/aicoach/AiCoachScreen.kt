package com.example.ui.aicoach

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HabitWithWeeklyStatus
import com.example.data.model.TaskAiAdvice
import com.example.ui.components.GlassCard
import com.example.ui.theme.BentoBackground
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
import com.example.ui.components.HabitIcons
import com.example.ui.theme.Slate900

@Composable
fun AiCoachScreen(
    habits: List<HabitWithWeeklyStatus>,
    adviceMap: Map<Long, TaskAiAdvice>,
    loadingHabitId: Long?,
    onRequestAdvice: (HabitWithWeeklyStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBackground)
            .testTag("ai_coach_screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp)
    ) {
        // Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "BEHAVIORAL AI COACH",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoVioletLight,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Habit Consistency Intelligence",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate50
                )
                Text(
                    text = "Task-specific cues, micro-actions, and friction elimination",
                    fontSize = 13.sp,
                    color = Slate400
                )
                Spacer(modifier = Modifier.height(18.dp))
            }
        }

        // Hero Bento Diagnostic Overview
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                borderColor = BentoViolet.copy(alpha = 0.5f),
                glowColor = BentoViolet.copy(alpha = 0.25f)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
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
                                    .size(36.dp)
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
                                    text = "System Diagnostics",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate50
                                )
                                Text(
                                    text = "Powered by Gemini & Behavioral Science",
                                    fontSize = 11.sp,
                                    color = Slate400
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(BentoViolet.copy(alpha = 0.25f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Active",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoVioletLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Consistent routines thrive on immediate environmental triggers rather than motivation. Tap any habit below to inspect its optimized habit stack and reduce friction.",
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = Slate400
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))
        }

        // Section Title
        item {
            Text(
                text = "TASK ADVICE & OPTIMIZATION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Slate400,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        // Task Cards
        if (habits.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Slate900)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No habits created yet. Create a habit to receive AI consistency guidance.",
                        fontSize = 13.sp,
                        color = Slate500
                    )
                }
            }
        } else {
            items(habits) { habitStatus ->
                val advice = adviceMap[habitStatus.habit.id]
                val isLoading = loadingHabitId == habitStatus.habit.id
                var isExpanded by remember { mutableStateOf(false) }

                TaskCoachBentoCard(
                    habitStatus = habitStatus,
                    advice = advice,
                    isLoading = isLoading,
                    isExpanded = isExpanded,
                    onToggleExpand = { isExpanded = !isExpanded },
                    onRequestAdvice = { onRequestAdvice(habitStatus) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
private fun TaskCoachBentoCard(
    habitStatus: HabitWithWeeklyStatus,
    advice: TaskAiAdvice?,
    isLoading: Boolean,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onRequestAdvice: () -> Unit
) {
    val habit = habitStatus.habit
    val completionRate = if (habitStatus.weeklyHistory.isNotEmpty()) {
        val done = habitStatus.weeklyHistory.count { it }
        (done.toFloat() / habitStatus.weeklyHistory.size * 100).toInt()
    } else 0

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        borderColor = if (isExpanded) BentoViolet.copy(alpha = 0.5f) else GlassBorder
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Slate800),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = HabitIcons.getIcon(habit.iconName),
                            contentDescription = habit.title,
                            tint = BentoVioletLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = habit.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate50
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = habit.category,
                                fontSize = 11.sp,
                                color = BentoCyan
                            )
                            Text(text = "•", fontSize = 10.sp, color = Slate500)
                            Text(
                                text = habit.timeOfDay.label,
                                fontSize = 11.sp,
                                color = Slate400
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(Slate800)
                        .clickable { onToggleExpand() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (isExpanded) "Hide" else "Plan",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoVioletLight
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = null,
                            tint = BentoVioletLight,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Consistency Metric Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Weekly Adherence",
                    fontSize = 11.sp,
                    color = Slate400
                )
                Text(
                    text = "$completionRate% (Streak: ${habitStatus.currentStreak}d)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (completionRate >= 70) BentoVioletLight else FlameAmber
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { completionRate / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(100.dp)),
                color = BentoViolet,
                trackColor = Slate800
            )

            // Expanded AI Advice Details
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = BentoVioletLight,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    } else if (advice != null) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Habit Stack Cue
                            CoachInsightBox(
                                label = "HABIT STACKING TRIGGER",
                                text = advice.habitStackCue,
                                icon = Icons.Filled.ElectricBolt,
                                color = BentoViolet
                            )

                            // 2-Minute Rule
                            CoachInsightBox(
                                label = "2-MINUTE MICRO-VERSION",
                                text = advice.twoMinuteRule,
                                icon = Icons.Filled.Lightbulb,
                                color = FlameAmber
                            )

                            // Friction Reducer
                            CoachInsightBox(
                                label = "ENVIRONMENT FRICTION REDUCER",
                                text = advice.frictionReducer,
                                icon = Icons.Filled.Shield,
                                color = BentoCyan
                            )

                            // Psychology Insight
                            CoachInsightBox(
                                label = "PSYCHOLOGICAL CUE",
                                text = advice.psychologyInsight,
                                icon = Icons.Filled.Psychology,
                                color = BentoVioletLight
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = onRequestAdvice,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp),
                            shape = RoundedCornerShape(100.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Slate800,
                                contentColor = BentoVioletLight
                            )
                        ) {
                            Text(
                                text = "Regenerate AI Analysis",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Button(
                            onClick = onRequestAdvice,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(100.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BentoViolet,
                                contentColor = Color.White
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Generate AI Consistency Plan",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CoachInsightBox(
    label: String,
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Slate800.copy(alpha = 0.6f))
            .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
            .padding(10.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = label,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = Slate50
            )
        }
    }
}
