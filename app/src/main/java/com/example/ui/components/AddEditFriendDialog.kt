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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Friend
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoCardDark
import com.example.ui.theme.BentoViolet
import com.example.ui.theme.BentoVioletDark
import com.example.ui.theme.BentoVioletLight
import com.example.ui.theme.FlameAmber
import com.example.util.InviteUtils
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

private val POPULAR_EMOJIS = listOf(
    "⚡", "🔥", "🏃", "🧘", "💻", "📚", "🎨", "🚀", "🥑", "🥋", "🌊", "🎯"
)

val POPULAR_REGIONS = listOf(
    "North America",
    "Europe",
    "Asia-Pacific",
    "Latin America",
    "Local Squad"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditFriendDialog(
    initialFriend: Friend? = null,
    defaultRegion: String = "North America",
    onDismiss: () -> Unit,
    onSave: (Friend) -> Unit,
    onDelete: ((Long) -> Unit)? = null
) {
    var name by remember { mutableStateOf(initialFriend?.name ?: "") }
    var username by remember { mutableStateOf(initialFriend?.username?.removePrefix("@") ?: "") }
    var avatarEmoji by remember { mutableStateOf(initialFriend?.avatarEmoji ?: "⚡") }
    var topHabit by remember { mutableStateOf(initialFriend?.topHabitName ?: "") }
    var region by remember { mutableStateOf(initialFriend?.region ?: defaultRegion) }
    var streakDays by remember { mutableIntStateOf(initialFriend?.streakDays ?: 1) }
    var weeklyCompletions by remember { mutableIntStateOf(initialFriend?.weeklyCompletions ?: 5) }

    var nameError by remember { mutableStateOf(false) }
    var quickInviteInput by remember { mutableStateOf("") }
    var quickInviteError by remember { mutableStateOf<String?>(null) }
    var quickInviteSuccess by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .imePadding()
                .navigationBarsPadding()
                .clip(RoundedCornerShape(28.dp))
                .border(1.dp, GlassBorder, RoundedCornerShape(28.dp))
                .testTag("add_edit_friend_dialog"),
            color = BentoBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (initialFriend == null) "NEW CONNECTION" else "EDIT CONNECTION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoVioletLight,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = if (initialFriend == null) "Add Friend / Member" else "Edit Profile",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate50
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Slate800)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = Slate400,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                if (initialFriend == null) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Quick Paste Invite Link / Handle
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(BentoViolet.copy(alpha = 0.1f))
                            .border(1.dp, BentoViolet.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Link,
                                    contentDescription = null,
                                    tint = BentoVioletLight,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "PASTE INVITE LINK OR CODE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoVioletLight,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = quickInviteInput,
                                    onValueChange = {
                                        quickInviteInput = it
                                        quickInviteError = null
                                        quickInviteSuccess = null
                                    },
                                    placeholder = { Text("https://... or @username", color = Slate500, fontSize = 12.sp) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BentoVioletLight,
                                        unfocusedBorderColor = GlassBorder,
                                        focusedTextColor = Slate50,
                                        unfocusedTextColor = Slate200,
                                        focusedContainerColor = BentoCardDark,
                                        unfocusedContainerColor = BentoCardDark
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("quick_import_input")
                                )

                                Button(
                                    onClick = {
                                        if (quickInviteInput.isBlank()) {
                                            quickInviteError = "Enter a link or handle"
                                            return@Button
                                        }
                                        val parsed = InviteUtils.parseInviteInput(quickInviteInput)
                                        if (parsed == null) {
                                            quickInviteError = "Could not parse invite link"
                                            return@Button
                                        }
                                        username = parsed.handle.removePrefix("@")
                                        if (!parsed.name.isNullOrBlank()) name = parsed.name
                                        if (!parsed.region.isNullOrBlank()) region = parsed.region
                                        if (!parsed.avatarEmoji.isNullOrBlank()) avatarEmoji = parsed.avatarEmoji
                                        quickInviteSuccess = "Auto-filled from invite!"
                                        nameError = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BentoViolet),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.height(48.dp)
                                ) {
                                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Auto-Fill", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            if (quickInviteError != null) {
                                Text(
                                    text = quickInviteError!!,
                                    color = Color(0xFFEF4444),
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            if (quickInviteSuccess != null) {
                                Text(
                                    text = quickInviteSuccess!!,
                                    color = Color(0xFF10B981),
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Avatar Selector
                Text(
                    text = "Select Avatar Icon",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    POPULAR_EMOJIS.forEach { emoji ->
                        val isSelected = avatarEmoji == emoji
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) BentoViolet else Slate900)
                                .border(
                                    1.dp,
                                    if (isSelected) BentoVioletLight else GlassBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { avatarEmoji = emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 20.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Name Input
                Text(
                    text = "Friend's Full Name",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (it.isNotBlank()) nameError = false
                    },
                    placeholder = { Text("e.g. Maya Lin", color = Slate500) },
                    isError = nameError,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoVioletLight,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = Slate50,
                        unfocusedTextColor = Slate200,
                        focusedContainerColor = BentoCardDark,
                        unfocusedContainerColor = BentoCardDark
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("friend_name_input")
                )
                if (nameError) {
                    Text(
                        text = "Name cannot be empty",
                        color = Color(0xFFEF4444),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Handle Input
                Text(
                    text = "Username Handle",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    placeholder = { Text("e.g. maya_builds", color = Slate500) },
                    prefix = { Text("@", color = BentoVioletLight, fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoVioletLight,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = Slate50,
                        unfocusedTextColor = Slate200,
                        focusedContainerColor = BentoCardDark,
                        unfocusedContainerColor = BentoCardDark
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("friend_handle_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Top Habit Input
                Text(
                    text = "Top Habit / Goal",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = topHabit,
                    onValueChange = { topHabit = it },
                    placeholder = { Text("e.g. 5km Trail Run / Deep Focus", color = Slate500) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoVioletLight,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = Slate50,
                        unfocusedTextColor = Slate200,
                        focusedContainerColor = BentoCardDark,
                        unfocusedContainerColor = BentoCardDark
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("friend_top_habit_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Region / Squad Selection
                Text(
                    text = "Region / Squad",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    POPULAR_REGIONS.forEach { reg ->
                        val isSelected = region.equals(reg, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(if (isSelected) BentoViolet.copy(alpha = 0.35f) else Slate900)
                                .border(
                                    1.dp,
                                    if (isSelected) BentoVioletLight else GlassBorder,
                                    RoundedCornerShape(100.dp)
                                )
                                .clickable { region = reg }
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

                // Stats: Streak & Weekly Completions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Current Streak
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Streak (Days)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate400
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(BentoCardDark)
                                .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
                                .padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = { if (streakDays > 0) streakDays-- },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("-", fontSize = 18.sp, color = Slate400, fontWeight = FontWeight.Bold)
                            }
                            Text(
                                text = "$streakDays",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = FlameAmber
                            )
                            IconButton(
                                onClick = { streakDays++ },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("+", fontSize = 18.sp, color = BentoVioletLight, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Weekly Completions
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Weekly Check-Ins",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate400
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(BentoCardDark)
                                .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
                                .padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = { if (weeklyCompletions > 0) weeklyCompletions-- },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("-", fontSize = 18.sp, color = Slate400, fontWeight = FontWeight.Bold)
                            }
                            Text(
                                text = "$weeklyCompletions",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoVioletLight
                            )
                            IconButton(
                                onClick = { weeklyCompletions++ },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("+", fontSize = 18.sp, color = BentoVioletLight, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (initialFriend != null && onDelete != null) {
                        IconButton(
                            onClick = { onDelete(initialFriend.id) },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFEF4444).copy(alpha = 0.15f))
                                .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                .testTag("delete_friend_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DeleteOutline,
                                contentDescription = "Delete Friend",
                                tint = Color(0xFFEF4444)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                nameError = true
                                return@Button
                            }

                            val cleanUsername = if (username.isNotBlank()) {
                                if (username.startsWith("@")) username else "@$username"
                            } else {
                                "@${name.lowercase().replace(" ", "_")}"
                            }

                            val friend = Friend(
                                id = initialFriend?.id ?: 0,
                                name = name.trim(),
                                username = cleanUsername,
                                avatarEmoji = avatarEmoji,
                                region = region,
                                topHabitName = topHabit.ifBlank { "Daily Routine" },
                                streakDays = streakDays,
                                weeklyCompletions = weeklyCompletions,
                                cheerCount = initialFriend?.cheerCount ?: 0,
                                createdAt = initialFriend?.createdAt ?: System.currentTimeMillis()
                            )

                            onSave(friend)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BentoViolet),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("save_friend_button")
                    ) {
                        Text(
                            text = if (initialFriend == null) "Add to Circle" else "Save Changes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
