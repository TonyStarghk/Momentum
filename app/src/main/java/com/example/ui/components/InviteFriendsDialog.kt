package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Friend
import com.example.data.repository.UserProfile
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoCardDark
import com.example.ui.theme.BentoCyan
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.BentoViolet
import com.example.ui.theme.BentoVioletDark
import com.example.ui.theme.BentoVioletLight
import com.example.ui.theme.FlameAmber
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.util.FeedbackManager
import com.example.util.InviteUtils

data class RecommendedPeer(
    val name: String,
    val handle: String,
    val emoji: String,
    val habit: String,
    val streak: Int,
    val region: String
)

@Composable
fun InviteFriendsDialog(
    userProfile: UserProfile,
    feedbackManager: FeedbackManager,
    onImportFriend: (Friend) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var pastedInviteInput by remember { mutableStateOf("") }
    var importErrorMessage by remember { mutableStateOf<String?>(null) }
    var importSuccessMessage by remember { mutableStateOf<String?>(null) }
    var copiedConfirmationBanner by remember { mutableStateOf<String?>(null) }

    val recommendedPeers = remember {
        listOf(
            RecommendedPeer("Maya Lin", "@maya_zen", "🧘", "Morning Meditation", 14, "Global"),
            RecommendedPeer("Kai Tanaka", "@kai_run", "⚡", "5km Daily Run", 9, userProfile.region),
            RecommendedPeer("Elena Gomez", "@elena_flow", "📚", "Read 30 Mins", 21, "Global"),
            RecommendedPeer("Marcus Vance", "@marcus_fit", "🔥", "Strength Routine", 7, userProfile.region)
        )
    }

    val inviteUrl = remember(userProfile.handle, userProfile.name, userProfile.region, userProfile.avatarEmoji) {
        InviteUtils.generateWebInviteUrl(
            username = userProfile.handle,
            name = userProfile.name,
            region = userProfile.region,
            avatarEmoji = userProfile.avatarEmoji
        )
    }

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
                .imePadding()
                .navigationBarsPadding()
                .clip(RoundedCornerShape(28.dp))
                .border(1.dp, BentoViolet.copy(alpha = 0.5f), RoundedCornerShape(28.dp))
                .testTag("invite_friends_dialog"),
            color = BentoBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(22.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "EXPAND YOUR CIRCLE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoVioletLight,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "Invite Friends & Squad",
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

                // In-dialog feedback banner
                AnimatedVisibility(
                    visible = copiedConfirmationBanner != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(EmeraldGreen.copy(alpha = 0.15f))
                            .border(1.dp, EmeraldGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = copiedConfirmationBanner ?: "",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Profile Identity Invite Preview Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    borderColor = BentoViolet.copy(alpha = 0.4f),
                    glowColor = BentoViolet.copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(BentoViolet, BentoVioletDark)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = userProfile.avatarEmoji, fontSize = 28.sp)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = userProfile.name.ifBlank { "Habit Challenger" },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate50
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = userProfile.handle.ifBlank { "@challenger" },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BentoVioletLight
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(100.dp))
                                        .background(BentoViolet.copy(alpha = 0.25f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = userProfile.region,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate200
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Invite Link Display & Copy
                Text(
                    text = "YOUR SHAREABLE INVITE LINK",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(BentoCardDark)
                        .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = inviteUrl,
                            fontSize = 11.sp,
                            color = Slate400,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(BentoViolet)
                                .clickable {
                                    feedbackManager.onHabitCreatedOrSaved()
                                    InviteUtils.copyInviteLink(context, inviteUrl)
                                    copiedConfirmationBanner = "Invite link copied to clipboard!"
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("copy_invite_link_chip")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "Copy Link",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons Row (Share Sheet / Copy Handle)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            feedbackManager.onHabitCreatedOrSaved()
                            InviteUtils.shareInviteIntent(
                                context = context,
                                username = userProfile.handle,
                                name = userProfile.name
                            )
                            copiedConfirmationBanner = "Invite text prepared & copied!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BentoViolet),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("share_invite_intent_button")
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share via Apps", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            feedbackManager.onHabitCreatedOrSaved()
                            InviteUtils.copyFriendCode(context, userProfile.handle)
                            copiedConfirmationBanner = "Friend handle ${userProfile.handle} copied!"
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BentoVioletLight),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BentoViolet.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("copy_friend_code_box")
                    ) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Handle", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Quick Import from Received Link / Handle
                Text(
                    text = "JOIN FRIEND'S CIRCLE / PASTE LINK",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoVioletLight,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = pastedInviteInput,
                    onValueChange = {
                        pastedInviteInput = it
                        importErrorMessage = null
                        importSuccessMessage = null
                    },
                    placeholder = { Text("Paste invite URL or friend @handle", color = Slate500, fontSize = 12.sp) },
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
                        .testTag("paste_invite_input")
                )

                if (importErrorMessage != null) {
                    Text(
                        text = importErrorMessage!!,
                        color = Color(0xFFEF4444),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }

                if (importSuccessMessage != null) {
                    Text(
                        text = importSuccessMessage!!,
                        color = EmeraldGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (pastedInviteInput.isBlank()) {
                                importErrorMessage = "Please paste an invite link or enter a handle"
                                return@Button
                            }
                            val parsed = InviteUtils.parseInviteInput(pastedInviteInput)
                            if (parsed == null) {
                                importErrorMessage = "Invalid invite link or username format"
                                return@Button
                            }

                            val newFriend = Friend(
                                id = 0L,
                                name = parsed.name ?: parsed.handle.removePrefix("@"),
                                username = parsed.handle,
                                avatarEmoji = parsed.avatarEmoji ?: "⚡",
                                region = parsed.region ?: userProfile.region,
                                topHabitName = "Daily Consistency",
                                streakDays = (1..6).random(),
                                weeklyCompletions = (3..7).random(),
                                cheerCount = 0,
                                createdAt = System.currentTimeMillis()
                            )

                            onImportFriend(newFriend)
                            feedbackManager.onHabitCreatedOrSaved()
                            importSuccessMessage = "✅ Joined circle with ${newFriend.name} (${newFriend.username})!"
                            pastedInviteInput = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BentoViolet),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("quick_import_friend_button")
                    ) {
                        Icon(Icons.Filled.GroupAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add to Circle", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    // Interactive Simulator Button: Test receiving an invite right away!
                    OutlinedButton(
                        onClick = {
                            val sampleNames = listOf("Sam Rivera", "Taylor Brooks", "Jordan Lee", "Avery Chen", "Morgan Reed")
                            val sampleEmojis = listOf("🚀", "🏃", "🧘", "🔥", "⚡", "⭐")
                            val randomName = sampleNames.random()
                            val randomHandle = "@" + randomName.lowercase().replace(" ", "_")
                            val simulatedFriend = Friend(
                                id = 0L,
                                name = randomName,
                                username = randomHandle,
                                avatarEmoji = sampleEmojis.random(),
                                region = userProfile.region,
                                topHabitName = "Atomic Habit Flow",
                                streakDays = (3..12).random(),
                                weeklyCompletions = (4..7).random(),
                                cheerCount = 0,
                                createdAt = System.currentTimeMillis()
                            )
                            onImportFriend(simulatedFriend)
                            feedbackManager.onHabitCreatedOrSaved()
                            importSuccessMessage = "🎉 Simulated invite accepted! Added ${simulatedFriend.name}."
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BentoCyan),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BentoCyan.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("simulate_invite_test_button")
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simulate Invite", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Recommended Community Champions Quick-Add
                Text(
                    text = "POPULAR HABIT CHAMPIONS TO ADD",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    recommendedPeers.forEach { peer ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(BentoCardDark)
                                .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp),
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
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Slate800),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = peer.emoji, fontSize = 20.sp)
                                }
                                Column {
                                    Text(
                                        text = peer.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate50
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = peer.handle,
                                            fontSize = 11.sp,
                                            color = BentoVioletLight
                                        )
                                        Text(
                                            text = "• ${peer.streak}d streak",
                                            fontSize = 11.sp,
                                            color = FlameAmber,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    val newFriend = Friend(
                                        id = 0L,
                                        name = peer.name,
                                        username = peer.handle,
                                        avatarEmoji = peer.emoji,
                                        region = peer.region,
                                        topHabitName = peer.habit,
                                        streakDays = peer.streak,
                                        weeklyCompletions = 6,
                                        cheerCount = 0,
                                        createdAt = System.currentTimeMillis()
                                    )
                                    onImportFriend(newFriend)
                                    feedbackManager.onHabitCreatedOrSaved()
                                    importSuccessMessage = "Added ${peer.name} to your Circle!"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BentoViolet.copy(alpha = 0.25f)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "+ Add",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoVioletLight
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
