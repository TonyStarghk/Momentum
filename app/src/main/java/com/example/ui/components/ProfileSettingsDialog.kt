package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.repository.UserProfile
import com.example.ui.theme.BentoCardDark
import com.example.ui.theme.BentoCyan
import com.example.ui.theme.BentoViolet
import com.example.ui.theme.BentoVioletLight
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

private val PROFILE_SETTINGS_REGIONS = listOf(
    "North America",
    "Europe",
    "Asia-Pacific",
    "Latin America",
    "Local Squad"
)

private val DAYS_OF_WEEK = listOf(
    1 to "Sun",
    2 to "Mon",
    3 to "Tue",
    4 to "Wed",
    5 to "Thu",
    6 to "Fri",
    7 to "Sat"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileSettingsDialog(
    userProfile: UserProfile,
    onSaveProfile: (name: String, handle: String, avatar: String, dailyTarget: Int, region: String) -> Unit,
    onToggleSound: () -> Unit,
    onToggleHaptics: () -> Unit,
    onUpdateReminders: (enabled: Boolean, morningTime: String, eveningTime: String) -> Unit = { _, _, _ -> },
    onUpdateRestDays: (enabled: Boolean, restDays: Set<Int>) -> Unit = { _, _ -> },
    onSendTestNotification: () -> Unit = {},
    onRerunSetup: () -> Unit,
    onClearAllData: () -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(userProfile.name) }
    var handle by remember { mutableStateOf(userProfile.handle) }
    var selectedAvatar by remember { mutableStateOf(userProfile.avatarEmoji) }
    var selectedRegion by remember { mutableStateOf(userProfile.region) }
    var dailyTarget by remember { mutableIntStateOf(userProfile.dailyTargetHabits) }

    // Reminders state
    var remindersEnabled by remember { mutableStateOf(userProfile.remindersEnabled) }
    var morningTime by remember { mutableStateOf(userProfile.morningReminderTime) }
    var eveningTime by remember { mutableStateOf(userProfile.eveningReminderTime) }

    // Rest days state
    var restDaysEnabled by remember { mutableStateOf(userProfile.restDaysEnabled) }
    var selectedRestDays by remember { mutableStateOf(userProfile.designatedRestDays) }

    var showClearConfirmation by remember { mutableStateOf(false) }

    val avatars = listOf("⚡", "🔥", "🚀", "🧘", "👑", "🎯", "🦁", "💎", "🧠", "🌊", "🌿", "🏆")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .imePadding()
                .navigationBarsPadding()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            backgroundColor = BentoCardDark,
            borderColor = GlassBorder
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PREFERENCES & PROFILE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoVioletLight,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "Settings & Gamification",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate50
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = Slate400,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Avatar Grid
                Text(
                    text = "Avatar & Identity",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate100
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    avatars.forEach { avatar ->
                        val isSelected = avatar == selectedAvatar
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) BentoViolet.copy(alpha = 0.3f) else Slate800)
                                .border(
                                    1.5.dp,
                                    if (isSelected) BentoVioletLight else GlassBorder,
                                    CircleShape
                                )
                                .clickable { selectedAvatar = avatar },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = avatar, fontSize = 18.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Name input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Display Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoVioletLight,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = Slate50,
                        unfocusedTextColor = Slate100,
                        focusedLabelColor = BentoVioletLight,
                        unfocusedLabelColor = Slate400
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Handle input
                OutlinedTextField(
                    value = handle,
                    onValueChange = { handle = it },
                    label = { Text("Username / Handle") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoVioletLight,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = Slate50,
                        unfocusedTextColor = Slate100,
                        focusedLabelColor = BentoVioletLight,
                        unfocusedLabelColor = Slate400
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Region / Squad Selection
                Text(
                    text = "Region / Location",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate100
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PROFILE_SETTINGS_REGIONS.forEach { reg ->
                        val isSelected = selectedRegion.equals(reg, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(if (isSelected) BentoViolet.copy(alpha = 0.35f) else Slate900)
                                .border(
                                    1.dp,
                                    if (isSelected) BentoVioletLight else GlassBorder,
                                    RoundedCornerShape(100.dp)
                                )
                                .clickable { selectedRegion = reg }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = reg,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Slate400
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Smart Habit Reminders & Notifications Section
                Text(
                    text = "Smart Notifications & Reminders",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate100
                )
                Spacer(modifier = Modifier.height(8.dp))

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    backgroundColor = Slate900,
                    borderColor = GlassBorder
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Filled.NotificationsActive,
                                    contentDescription = null,
                                    tint = BentoVioletLight,
                                    modifier = Modifier.size(18.dp)
                                )
                                Column {
                                    Text("Habit Reminders", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate100)
                                    Text("Quick check-in shade actions", fontSize = 11.sp, color = Slate400)
                                }
                            }
                            Switch(
                                checked = remindersEnabled,
                                onCheckedChange = {
                                    remindersEnabled = it
                                    onUpdateReminders(it, morningTime, eveningTime)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = BentoViolet,
                                    uncheckedTrackColor = Slate800
                                )
                            )
                        }

                        if (remindersEnabled) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { onSendTestNotification() },
                                    colors = ButtonDefaults.buttonColors(containerColor = BentoViolet.copy(alpha = 0.25f), contentColor = BentoVioletLight),
                                    shape = RoundedCornerShape(100.dp),
                                    modifier = Modifier.fillMaxWidth().height(36.dp)
                                ) {
                                    Text("Send Test Notification ⚡", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Streak Freezes & Rest Days Section
                Text(
                    text = "Streak Shields & Rest Days",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate100
                )
                Spacer(modifier = Modifier.height(8.dp))

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    backgroundColor = Slate900,
                    borderColor = GlassBorder
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Filled.AcUnit, contentDescription = null, tint = BentoCyan, modifier = Modifier.size(18.dp))
                                Column {
                                    Text("Streak Freezes", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate100)
                                    Text("${userProfile.streakFreezesAvailable} shields remaining in inventory", fontSize = 11.sp, color = BentoCyan)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Filled.Shield, contentDescription = null, tint = BentoVioletLight, modifier = Modifier.size(18.dp))
                                Column {
                                    Text("Designated Rest Days", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate100)
                                    Text("Prevents negative XP on off-days", fontSize = 11.sp, color = Slate400)
                                }
                            }
                            Switch(
                                checked = restDaysEnabled,
                                onCheckedChange = {
                                    restDaysEnabled = it
                                    onUpdateRestDays(it, selectedRestDays)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = BentoViolet,
                                    uncheckedTrackColor = Slate800
                                )
                            )
                        }

                        if (restDaysEnabled) {
                            Spacer(modifier = Modifier.height(8.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                DAYS_OF_WEEK.forEach { (dayInt, dayName) ->
                                    val isSelected = selectedRestDays.contains(dayInt)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) BentoCyan.copy(alpha = 0.25f) else Slate800)
                                            .border(1.dp, if (isSelected) BentoCyan else GlassBorder, RoundedCornerShape(8.dp))
                                            .clickable {
                                                val newSet = selectedRestDays.toMutableSet()
                                                if (isSelected) newSet.remove(dayInt) else newSet.add(dayInt)
                                                selectedRestDays = newSet
                                                onUpdateRestDays(restDaysEnabled, newSet)
                                            }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = dayName,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) BentoCyan else Slate400
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sensory Toggles
                Text(
                    text = "Sensory Feedback",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate100
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Slate900)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.VolumeUp, contentDescription = null, tint = BentoVioletLight, modifier = Modifier.size(18.dp))
                        Text("Sound Chimes", fontSize = 13.sp, color = Slate100)
                    }
                    Switch(
                        checked = userProfile.soundEnabled,
                        onCheckedChange = { onToggleSound() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = BentoViolet,
                            uncheckedTrackColor = Slate800
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Slate900)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.Vibration, contentDescription = null, tint = BentoVioletLight, modifier = Modifier.size(18.dp))
                        Text("Soft Haptic Vibrations", fontSize = 13.sp, color = Slate100)
                    }
                    Switch(
                        checked = userProfile.hapticsEnabled,
                        onCheckedChange = { onToggleHaptics() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = BentoViolet,
                            uncheckedTrackColor = Slate800
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions: Re-run Setup Wizard & Clear All
                Text(
                    text = "App Management",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate100
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Slate900)
                        .clickable { onRerunSetup() }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = null, tint = BentoVioletLight, modifier = Modifier.size(18.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Re-Run Setup Wizard", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate100)
                        Text("Switch focus track or re-seed archetype", fontSize = 11.sp, color = Slate400)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Slate900)
                        .clickable { showClearConfirmation = true }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Filled.DeleteForever, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Clear All Data & Fresh Start", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                        Text("Wipes all history, habits and friends", fontSize = 11.sp, color = Slate400)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Save Profile Button
                Button(
                    onClick = {
                        onSaveProfile(name, handle, selectedAvatar, dailyTarget, selectedRegion)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BentoViolet),
                    shape = RoundedCornerShape(100.dp),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }

    if (showClearConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearConfirmation = false },
            containerColor = Slate900,
            title = {
                Text("Start Completely Fresh?", fontWeight = FontWeight.Bold, color = Slate50)
            },
            text = {
                Text(
                    "This will delete all current habits, check-in history, and friends circle, giving you a completely clean slate.",
                    fontSize = 13.sp,
                    color = Slate400
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showClearConfirmation = false
                        onClearAllData()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Text("Wipe & Start Fresh", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmation = false }) {
                    Text("Cancel", color = Slate400)
                }
            },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
        )
    }
}

