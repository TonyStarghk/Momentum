package com.example.ui.setup

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.example.data.model.Habit
import com.example.data.model.TimeOfDay
import com.example.ui.components.GlassCard
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoCardDark
import com.example.ui.theme.BentoViolet
import com.example.ui.theme.BentoVioletDark
import com.example.ui.theme.BentoVioletLight
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.util.FeedbackManager

data class StarterHabitTemplate(
    val title: String,
    val description: String,
    val category: String,
    val timeOfDay: TimeOfDay,
    val iconName: String,
    val colorHex: String
)

data class FocusTrackOption(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accentColor: Color,
    val starterHabits: List<StarterHabitTemplate>
)

@Composable
fun SetupOnboardingScreen(
    feedbackManager: FeedbackManager,
    onCompleteSetup: (
        name: String,
        handle: String,
        avatarEmoji: String,
        focusTrack: String,
        selectedHabits: List<Habit>,
        generateHistory: Boolean,
        dailyTarget: Int,
        soundEnabled: Boolean,
        hapticsEnabled: Boolean
    ) -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }

    // Step 0: Profile State
    var userName by remember { mutableStateOf("Alex Rivera") }
    var userHandle by remember { mutableStateOf("@alex") }
    var selectedAvatar by remember { mutableStateOf("⚡") }

    // Step 1: Focus Track State
    val focusTracks = remember {
        listOf(
            FocusTrackOption(
                id = "all_round",
                title = "Peak All-Rounder",
                subtitle = "Balanced morning focus, fitness, reading & calm",
                icon = Icons.Filled.AutoAwesome,
                accentColor = BentoVioletLight,
                starterHabits = listOf(
                    StarterHabitTemplate("Morning Hydration & Electrolytes", "500ml cold lemon water upon waking", "Health", TimeOfDay.MORNING, "water", "#00F0FF"),
                    StarterHabitTemplate("Deep Focus Flow Block", "45 mins single-task deep work", "Productivity", TimeOfDay.MORNING, "code", "#8B5CF6"),
                    StarterHabitTemplate("Daily Movement & Walk", "20-minute sunlight brisk walk", "Fitness", TimeOfDay.AFTERNOON, "runner", "#F59E0B"),
                    StarterHabitTemplate("Strength & Mobility Training", "Resistance circuit or stretching", "Fitness", TimeOfDay.EVENING, "dumbbell", "#EF4444"),
                    StarterHabitTemplate("Read 20 Pages Non-Fiction", "Nighttime reading without screens", "Learning", TimeOfDay.EVENING, "book", "#3B82F6")
                )
            ),
            FocusTrackOption(
                id = "deep_work",
                title = "Deep Work & Productivity",
                subtitle = "Hyper-focus, engineering, learning & task mastery",
                icon = Icons.Filled.Psychology,
                accentColor = Color(0xFF38BDF8),
                starterHabits = listOf(
                    StarterHabitTemplate("Morning Priority Sprint Planning", "Identify top 3 non-negotiables", "Productivity", TimeOfDay.MORNING, "sparkles", "#38BDF8"),
                    StarterHabitTemplate("90-Minute Deep Focus Flow", "Zero distractions, notifications muted", "Productivity", TimeOfDay.MORNING, "code", "#8B5CF6"),
                    StarterHabitTemplate("Inbox & Communication Zero", "Batch process messages in 25 mins", "Productivity", TimeOfDay.AFTERNOON, "smile", "#34D399"),
                    StarterHabitTemplate("Evening Skill Reading", "Study architecture & industry essays", "Learning", TimeOfDay.EVENING, "book", "#F59E0B")
                )
            ),
            FocusTrackOption(
                id = "fitness",
                title = "Fitness & Physical Longevity",
                subtitle = "Strength, energy, hydration & daily movement",
                icon = Icons.Filled.FitnessCenter,
                accentColor = Color(0xFFEF4444),
                starterHabits = listOf(
                    StarterHabitTemplate("1L Morning Hydration", "Electrolytes & large glass of water", "Health", TimeOfDay.MORNING, "water", "#00F0FF"),
                    StarterHabitTemplate("10,000 Daily Steps", "Active walks throughout the day", "Fitness", TimeOfDay.ANYTIME, "runner", "#F59E0B"),
                    StarterHabitTemplate("Daily Workout / Lift", "Heavy training or metabolic conditioning", "Fitness", TimeOfDay.AFTERNOON, "dumbbell", "#EF4444"),
                    StarterHabitTemplate("150g Protein Target", "Clean whole food nutrition", "Health", TimeOfDay.EVENING, "heart", "#34D399"),
                    StarterHabitTemplate("Full Body Mobility & Stretch", "10 mins hip & shoulder opening", "Health", TimeOfDay.EVENING, "meditation", "#A855F7")
                )
            ),
            FocusTrackOption(
                id = "mindfulness",
                title = "Mindfulness & Zen Balance",
                subtitle = "Breathwork, mental clarity, gratitude & restorative sleep",
                icon = Icons.Filled.SelfImprovement,
                accentColor = Color(0xFF34D399),
                starterHabits = listOf(
                    StarterHabitTemplate("Box Breathing & Meditation", "10 mins conscious presence", "Mind", TimeOfDay.MORNING, "meditation", "#34D399"),
                    StarterHabitTemplate("Morning Sunlight Exposure", "15 mins natural circadian light", "Health", TimeOfDay.MORNING, "sun", "#F59E0B"),
                    StarterHabitTemplate("Gratitude & Evening Journal", "Write 3 wins and reflections", "Mind", TimeOfDay.EVENING, "sparkles", "#8B5CF6"),
                    StarterHabitTemplate("Digital Sunset (No Screens)", "Airplane mode 45 mins before sleep", "Health", TimeOfDay.EVENING, "moon", "#38BDF8")
                )
            ),
            FocusTrackOption(
                id = "clean_slate",
                title = "Fresh Slate (Zero Habits)",
                subtitle = "Start 100% clean with an empty dashboard",
                icon = Icons.Filled.AutoAwesome,
                accentColor = Color(0xFFFCD34D),
                starterHabits = emptyList()
            )
        )
    }

    var selectedTrackId by remember { mutableStateOf("all_round") }
    val selectedTrack = focusTracks.firstOrNull { it.id == selectedTrackId } ?: focusTracks[0]

    // Step 2: Selected Habits Checklist
    val selectedHabitTemplates = remember { mutableStateListOf<StarterHabitTemplate>() }

    // Update habits when track changes
    var initialSyncDone by remember { mutableStateOf(false) }
    if (!initialSyncDone) {
        selectedHabitTemplates.clear()
        selectedHabitTemplates.addAll(selectedTrack.starterHabits)
        initialSyncDone = true
    }

    var generateStarterHistory by remember { mutableStateOf(false) }

    // Step 3: Sensory Calibration State
    var soundEnabled by remember { mutableStateOf(true) }
    var hapticsEnabled by remember { mutableStateOf(true) }
    var dailyTarget by remember { mutableIntStateOf(5) }

    val avatars = listOf("⚡", "🔥", "🚀", "🧘", "👑", "🎯", "🦁", "💎", "🧠", "🌊", "🌿", "🏆")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BentoBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MOMENTUM SETUP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoVioletLight,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = when (currentStep) {
                            0 -> "Create Your Profile"
                            1 -> "Choose Focus Path"
                            2 -> "Starter Routine"
                            else -> "Sensory & Targets"
                        },
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate50
                    )
                }

                // Step Indicators (4 dots)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (i in 0..3) {
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(if (i == currentStep) 24.dp else 6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    if (i == currentStep) BentoVioletLight
                                    else if (i < currentStep) BentoViolet.copy(alpha = 0.5f)
                                    else Slate800
                                )
                        )
                    }
                }
            }

            // Wizard Step Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
                        } else {
                            (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
                        }
                    },
                    label = "setup_step_transition"
                ) { step ->
                    when (step) {
                        0 -> StepProfileIdentity(
                            name = userName,
                            onNameChange = { userName = it },
                            handle = userHandle,
                            onHandleChange = { userHandle = it },
                            selectedAvatar = selectedAvatar,
                            avatars = avatars,
                            onSelectAvatar = {
                                selectedAvatar = it
                                feedbackManager.onHabitCreatedOrSaved()
                            }
                        )

                        1 -> StepFocusPath(
                            tracks = focusTracks,
                            selectedTrackId = selectedTrackId,
                            onSelectTrack = { track ->
                                selectedTrackId = track.id
                                selectedHabitTemplates.clear()
                                selectedHabitTemplates.addAll(track.starterHabits)
                                if (track.id == "clean_slate") {
                                    generateStarterHistory = false
                                }
                                feedbackManager.onHabitCreatedOrSaved()
                            }
                        )

                        2 -> StepCustomizeHabits(
                            selectedTrack = selectedTrack,
                            selectedHabitTemplates = selectedHabitTemplates,
                            generateHistory = generateStarterHistory,
                            onToggleHabit = { habit ->
                                if (selectedHabitTemplates.contains(habit)) {
                                    selectedHabitTemplates.remove(habit)
                                } else {
                                    selectedHabitTemplates.add(habit)
                                }
                                feedbackManager.onCheerTriggered()
                            },
                            onToggleGenerateHistory = { generateStarterHistory = it }
                        )

                        3 -> StepSensoryAndGoals(
                            soundEnabled = soundEnabled,
                            onToggleSound = {
                                soundEnabled = it
                                feedbackManager.soundEnabled = it
                                if (it) feedbackManager.onCheerTriggered()
                            },
                            hapticsEnabled = hapticsEnabled,
                            onToggleHaptics = {
                                hapticsEnabled = it
                                feedbackManager.hapticsEnabled = it
                                if (it) feedbackManager.onHabitCreatedOrSaved()
                            },
                            dailyTarget = dailyTarget,
                            onSelectDailyTarget = {
                                dailyTarget = it
                                feedbackManager.onCheerTriggered()
                            },
                            onTestChime = { feedbackManager.onStreakMilestoneAchieved() },
                            onTestCelebration = { feedbackManager.onPerfectDayAchieved() }
                        )
                    }
                }
            }

            // Bottom Navigation Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep > 0) {
                    TextButton(
                        onClick = { currentStep-- },
                        modifier = Modifier.testTag("setup_back_button")
                    ) {
                        Text("Back", color = Slate400, fontSize = 14.sp)
                    }
                } else {
                    TextButton(
                        onClick = {
                            // Quick start with clean slate
                            val habits = selectedHabitTemplates.mapIndexed { idx, template ->
                                Habit(
                                    id = (idx + 1).toLong(),
                                    title = template.title,
                                    description = template.description,
                                    category = template.category,
                                    timeOfDay = template.timeOfDay,
                                    iconName = template.iconName,
                                    colorHex = template.colorHex
                                )
                            }
                            onCompleteSetup(
                                userName,
                                userHandle,
                                selectedAvatar,
                                selectedTrack.title,
                                habits,
                                generateStarterHistory,
                                dailyTarget,
                                soundEnabled,
                                hapticsEnabled
                            )
                        }
                    ) {
                        Text("Skip Setup", color = Slate500, fontSize = 13.sp)
                    }
                }

                Button(
                    onClick = {
                        if (currentStep < 3) {
                            feedbackManager.onHabitCreatedOrSaved()
                            currentStep++
                        } else {
                            // Complete setup!
                            feedbackManager.onPerfectDayAchieved()
                            val habits = selectedHabitTemplates.mapIndexed { idx, template ->
                                Habit(
                                    id = (idx + 1).toLong(),
                                    title = template.title,
                                    description = template.description,
                                    category = template.category,
                                    timeOfDay = template.timeOfDay,
                                    iconName = template.iconName,
                                    colorHex = template.colorHex
                                )
                            }
                            onCompleteSetup(
                                userName,
                                userHandle,
                                selectedAvatar,
                                selectedTrack.title,
                                habits,
                                generateStarterHistory,
                                dailyTarget,
                                soundEnabled,
                                hapticsEnabled
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BentoViolet),
                    shape = RoundedCornerShape(100.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("setup_continue_button")
                ) {
                    Text(
                        text = if (currentStep == 3) "Launch Momentum 🚀" else "Continue →",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// Step 0: Profile Identity
// ----------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StepProfileIdentity(
    name: String,
    onNameChange: (String) -> Unit,
    handle: String,
    onHandleChange: (String) -> Unit,
    selectedAvatar: String,
    avatars: List<String>,
    onSelectAvatar: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = BentoCardDark,
                borderColor = GlassBorder
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar Showcase Circle
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(BentoViolet.copy(alpha = 0.4f), Color.Transparent)
                                )
                            )
                            .border(2.dp, BentoVioletLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = selectedAvatar, fontSize = 40.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Choose Your Avatar",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate200
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Avatar selector grid
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        avatars.forEach { avatar ->
                            val isSelected = avatar == selectedAvatar
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) BentoViolet.copy(alpha = 0.3f) else Slate800)
                                    .border(
                                        1.5.dp,
                                        if (isSelected) BentoVioletLight else GlassBorder,
                                        CircleShape
                                    )
                                    .clickable { onSelectAvatar(avatar) }
                                    .testTag("avatar_option_$avatar"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = avatar, fontSize = 20.sp)
                            }
                        }
                    }
                }
            }
        }

        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = BentoCardDark,
                borderColor = GlassBorder
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Profile & Arena Handle",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate200
                    )
                    Text(
                        text = "Displayed in the Leaderboard Arena and share progress cards",
                        fontSize = 12.sp,
                        color = Slate400
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = onNameChange,
                        label = { Text("Display Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BentoVioletLight,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = Slate50,
                            unfocusedTextColor = Slate100,
                            focusedLabelColor = BentoVioletLight,
                            unfocusedLabelColor = Slate400
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("setup_name_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = handle,
                        onValueChange = onHandleChange,
                        label = { Text("Handle / Username") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BentoVioletLight,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = Slate50,
                            unfocusedTextColor = Slate100,
                            focusedLabelColor = BentoVioletLight,
                            unfocusedLabelColor = Slate400
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("setup_handle_input")
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// Step 1: Focus Path Selection
// ----------------------------------------------------
@Composable
private fun StepFocusPath(
    tracks: List<FocusTrackOption>,
    selectedTrackId: String,
    onSelectTrack: (FocusTrackOption) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Select your daily habit archetype. You can fully customize and add your own habits next.",
                fontSize = 13.sp,
                color = Slate400,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        items(tracks) { track ->
            val isSelected = track.id == selectedTrackId
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectTrack(track) }
                    .testTag("track_option_${track.id}"),
                backgroundColor = if (isSelected) track.accentColor.copy(alpha = 0.12f) else BentoCardDark,
                borderColor = if (isSelected) track.accentColor.copy(alpha = 0.8f) else GlassBorder
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(track.accentColor.copy(alpha = 0.2f))
                            .border(1.dp, track.accentColor.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = track.icon,
                            contentDescription = track.title,
                            tint = track.accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = track.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate50
                        )
                        Text(
                            text = track.subtitle,
                            fontSize = 12.sp,
                            color = Slate400,
                            lineHeight = 16.sp
                        )
                        if (track.starterHabits.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${track.starterHabits.size} curated starter habits",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = track.accentColor
                            )
                        }
                    }

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(track.accentColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Selected",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// Step 2: Customize Starter Habits
// ----------------------------------------------------
@Composable
private fun StepCustomizeHabits(
    selectedTrack: FocusTrackOption,
    selectedHabitTemplates: List<StarterHabitTemplate>,
    generateHistory: Boolean,
    onToggleHabit: (StarterHabitTemplate) -> Unit,
    onToggleGenerateHistory: (Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Habit Blueprint: ${selectedTrack.title}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoVioletLight
                )
                Text(
                    text = "Check the habits you want to include in your routine:",
                    fontSize = 12.sp,
                    color = Slate400
                )
            }
        }

        if (selectedTrack.starterHabits.isEmpty()) {
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = BentoCardDark,
                    borderColor = GlassBorder
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "✨", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Clean Blank Canvas",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate50
                        )
                        Text(
                            text = "You will start with zero pre-loaded habits. You can create your first habit from the dashboard anytime.",
                            fontSize = 12.sp,
                            color = Slate400,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            items(selectedTrack.starterHabits) { habit ->
                val isChecked = selectedHabitTemplates.contains(habit)
                val color = try {
                    Color(android.graphics.Color.parseColor(habit.colorHex))
                } catch (_: Exception) {
                    BentoVioletLight
                }

                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleHabit(habit) }
                        .testTag("habit_toggle_${habit.title}"),
                    backgroundColor = if (isChecked) BentoCardDark else Slate900.copy(alpha = 0.5f),
                    borderColor = if (isChecked) color.copy(alpha = 0.5f) else GlassBorder
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { onToggleHabit(habit) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = color,
                                uncheckedColor = Slate500,
                                checkmarkColor = Color.Black
                            )
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = habit.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isChecked) Slate50 else Slate500
                                )
                            }
                            Text(
                                text = habit.description,
                                fontSize = 11.sp,
                                color = if (isChecked) Slate400 else Slate700
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(color.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = habit.timeOfDay.label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = color
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Slate800)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = habit.category,
                                        fontSize = 10.sp,
                                        color = Slate400
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(6.dp))
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = BentoCardDark,
                    borderColor = GlassBorder
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Simulate Demo Past History (Optional)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate200
                            )
                            Text(
                                text = if (generateHistory) "Will inject 14 days of demo data." else "OFF (Recommended): Starts 100% from scratch on Day 1.",
                                fontSize = 11.sp,
                                color = if (generateHistory) BentoVioletLight else EmeraldGreen
                            )
                        }
                        Switch(
                            checked = generateHistory,
                            onCheckedChange = onToggleGenerateHistory,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = BentoViolet,
                                uncheckedTrackColor = Slate800
                            )
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// Step 3: Sensory & Goals
// ----------------------------------------------------
@Composable
private fun StepSensoryAndGoals(
    soundEnabled: Boolean,
    onToggleSound: (Boolean) -> Unit,
    hapticsEnabled: Boolean,
    onToggleHaptics: (Boolean) -> Unit,
    dailyTarget: Int,
    onSelectDailyTarget: (Int) -> Unit,
    onTestChime: () -> Unit,
    onTestCelebration: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = BentoCardDark,
                borderColor = GlassBorder
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Sensory Achievement Feedback",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate200
                    )
                    Text(
                        text = "Procedural audio chime synthesizers and tactile vibrations",
                        fontSize = 12.sp,
                        color = Slate400
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sound switch
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
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(BentoViolet.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.VolumeUp,
                                    contentDescription = "Sound",
                                    tint = BentoVioletLight,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text("Acoustic Sound Chimes", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate100)
                                Text("Ascending crystal bell chords", fontSize = 11.sp, color = Slate400)
                            }
                        }
                        Switch(
                            checked = soundEnabled,
                            onCheckedChange = onToggleSound,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = BentoViolet,
                                uncheckedTrackColor = Slate800
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Haptics switch
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
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(BentoViolet.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Vibration,
                                    contentDescription = "Haptics",
                                    tint = BentoVioletLight,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text("Soft Haptic Vibrations", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate100)
                                Text("Tactile pulses on check-in", fontSize = 11.sp, color = Slate400)
                            }
                        }
                        Switch(
                            checked = hapticsEnabled,
                            onCheckedChange = onToggleHaptics,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = BentoViolet,
                                uncheckedTrackColor = Slate800
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Test Sound Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onTestChime,
                            colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                            shape = RoundedCornerShape(100.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Test Chime 🔔", fontSize = 12.sp, color = BentoVioletLight)
                        }

                        Button(
                            onClick = onTestCelebration,
                            colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                            shape = RoundedCornerShape(100.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Test Victory 🎉", fontSize = 12.sp, color = BentoVioletLight)
                        }
                    }
                }
            }
        }

        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = BentoCardDark,
                borderColor = GlassBorder
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Daily Habit Target",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate200
                    )
                    Text(
                        text = "Target completed habits per day for a 100% Perfect Day ring",
                        fontSize = 12.sp,
                        color = Slate400
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf(3, 5, 7).forEach { target ->
                            val isSelected = target == dailyTarget
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSelected) BentoViolet.copy(alpha = 0.25f) else Slate800)
                                    .border(
                                        1.5.dp,
                                        if (isSelected) BentoVioletLight else GlassBorder,
                                        RoundedCornerShape(14.dp)
                                    )
                                    .clickable { onSelectDailyTarget(target) }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$target Habits",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) BentoVioletLight else Slate100
                                    )
                                    Text(
                                        text = when (target) {
                                            3 -> "Light"
                                            5 -> "Balanced"
                                            else -> "Intense"
                                        },
                                        fontSize = 11.sp,
                                        color = Slate400
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
