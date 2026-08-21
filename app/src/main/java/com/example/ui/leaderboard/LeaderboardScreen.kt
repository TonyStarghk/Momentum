package com.example.ui.leaderboard

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Friend
import com.example.data.model.LeaderboardScope
import com.example.data.model.LeaderboardTimeframe
import com.example.data.model.LeaderboardUser
import com.example.ui.components.GlassCard
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoCardDark
import com.example.ui.theme.BentoViolet
import com.example.ui.theme.BentoVioletDark
import com.example.ui.theme.BentoVioletLight
import com.example.ui.theme.FlameAmber
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

val REGION_OPTIONS = listOf(
    "All Regions",
    "North America",
    "Europe",
    "Asia-Pacific",
    "Latin America",
    "Local Squad"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LeaderboardScreen(
    users: List<LeaderboardUser>,
    scope: LeaderboardScope,
    timeframe: LeaderboardTimeframe,
    selectedRegion: String,
    onScopeChange: (LeaderboardScope) -> Unit,
    onTimeframeChange: (LeaderboardTimeframe) -> Unit,
    onRegionChange: (String) -> Unit,
    onAddFriendClick: () -> Unit,
    onEditFriend: (Long) -> Unit,
    onDeleteFriend: (Long) -> Unit,
    onShareClick: () -> Unit,
    onInviteFriendsClick: () -> Unit = onShareClick,
    onCheer: (LeaderboardUser) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser = users.firstOrNull { it.isCurrentUser }
    val friendCount = users.count { !it.isCurrentUser }

    var friendToDelete by remember { mutableStateOf<LeaderboardUser?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBackground)
            .testTag("leaderboard_screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp)
    ) {
        // Header Section
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (scope == LeaderboardScope.FRIENDS) "FRIENDS ARENA" else "REGIONAL ARENA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoVioletLight,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = if (scope == LeaderboardScope.FRIENDS) "Friends Leaderboard" else "Regional Rankings",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate50
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Add Friend Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(BentoViolet)
                                .clickable { onAddFriendClick() }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("add_friend_header_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PersonAdd,
                                    contentDescription = "Add Friend",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Add",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Invite / Share Button
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Slate800)
                                .border(1.dp, GlassBorder, CircleShape)
                                .clickable { onInviteFriendsClick() }
                                .testTag("invite_friend_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "Invite Friends",
                                tint = Slate400,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Primary Scope Switcher: Friends Circle vs Regional Arena
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(100.dp))
                        .background(Slate900)
                        .border(1.dp, GlassBorder, RoundedCornerShape(100.dp))
                        .padding(4.dp)
                ) {
                    val isFriends = scope == LeaderboardScope.FRIENDS
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(100.dp))
                            .background(if (isFriends) BentoViolet else Color.Transparent)
                            .clickable { onScopeChange(LeaderboardScope.FRIENDS) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Groups,
                                contentDescription = null,
                                tint = if (isFriends) Color.White else Slate400,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "Friends Circle",
                                fontSize = 13.sp,
                                fontWeight = if (isFriends) FontWeight.Bold else FontWeight.Medium,
                                color = if (isFriends) Color.White else Slate400
                            )
                        }
                    }

                    val isRegion = scope == LeaderboardScope.REGION
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(100.dp))
                            .background(if (isRegion) BentoViolet else Color.Transparent)
                            .clickable { onScopeChange(LeaderboardScope.REGION) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Public,
                                contentDescription = null,
                                tint = if (isRegion) Color.White else Slate400,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "Regional / Squad",
                                fontSize = 13.sp,
                                fontWeight = if (isRegion) FontWeight.Bold else FontWeight.Medium,
                                color = if (isRegion) Color.White else Slate400
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Timeframe Selector: Weekly League vs All-Time XP
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(100.dp))
                        .background(Slate900.copy(alpha = 0.6f))
                        .border(1.dp, GlassBorder, RoundedCornerShape(100.dp))
                        .padding(3.dp)
                ) {
                    val isWeekly = timeframe == LeaderboardTimeframe.WEEKLY
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(100.dp))
                            .background(if (isWeekly) BentoViolet.copy(alpha = 0.25f) else Color.Transparent)
                            .clickable { onTimeframeChange(LeaderboardTimeframe.WEEKLY) }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Weekly League",
                            fontSize = 12.sp,
                            fontWeight = if (isWeekly) FontWeight.Bold else FontWeight.Medium,
                            color = if (isWeekly) BentoVioletLight else Slate400
                        )
                    }

                    val isAllTime = timeframe == LeaderboardTimeframe.ALL_TIME
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(100.dp))
                            .background(if (isAllTime) BentoViolet.copy(alpha = 0.25f) else Color.Transparent)
                            .clickable { onTimeframeChange(LeaderboardTimeframe.ALL_TIME) }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "All-Time XP",
                            fontSize = 12.sp,
                            fontWeight = if (isAllTime) FontWeight.Bold else FontWeight.Medium,
                            color = if (isAllTime) BentoVioletLight else Slate400
                        )
                    }
                }

                // Region Filter Chips (Visible when in Regional mode)
                if (scope == LeaderboardScope.REGION) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "FILTER BY REGION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate500,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        REGION_OPTIONS.forEach { reg ->
                            val isSelected = selectedRegion.equals(reg, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(if (isSelected) BentoViolet.copy(alpha = 0.35f) else BentoCardDark)
                                    .border(
                                        1.dp,
                                        if (isSelected) BentoVioletLight else GlassBorder,
                                        RoundedCornerShape(100.dp)
                                    )
                                    .clickable { onRegionChange(reg) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = reg,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else Slate400
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Current User Bento Highlight Card
        if (currentUser != null) {
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    borderColor = BentoViolet.copy(alpha = 0.5f),
                    glowColor = BentoViolet.copy(alpha = 0.3f)
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
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(BentoViolet, BentoVioletDark)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "#${currentUser.rank}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }

                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = currentUser.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate50
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(100.dp))
                                            .background(BentoViolet.copy(alpha = 0.25f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = currentUser.tierTitle,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BentoVioletLight
                                        )
                                    }
                                }
                                Text(
                                    text = "Active Habit: ${currentUser.topHabitName}",
                                    fontSize = 12.sp,
                                    color = Slate400
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${currentUser.totalXp} XP",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = BentoVioletLight
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = FlameAmber,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "${currentUser.streakDays}d streak",
                                    fontSize = 11.sp,
                                    color = FlameAmber,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
            }
        }

        // Top Podium Bento Card (Shown when there are 2 or more contenders)
        if (users.size >= 2) {
            item {
                PodiumBentoCard(topContenders = users.take(3))
                Spacer(modifier = Modifier.height(18.dp))
            }
        }

        // Section Title
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (scope == LeaderboardScope.FRIENDS) "CIRCLE STANDINGS (${users.size})" else "REGION STANDINGS (${users.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400,
                    letterSpacing = 1.sp
                )

                Text(
                    text = if (scope == LeaderboardScope.FRIENDS) "$friendCount Friends Added" else selectedRegion,
                    fontSize = 11.sp,
                    color = BentoVioletLight,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Empty state when user has not added any friends yet
        if (friendCount == 0 && scope == LeaderboardScope.FRIENDS) {
            item {
                EmptyCircleBentoCard(
                    onAddFriend = onAddFriendClick,
                    onInviteClick = onInviteFriendsClick
                )
            }
        } else if (users.isEmpty()) {
            item {
                EmptyRegionBentoCard(
                    region = selectedRegion,
                    onAddMember = onAddFriendClick
                )
            }
        } else {
            // Competitors List (Only actual friends and current user)
            items(users, key = { it.id }) { user ->
                LeaderboardRowItem(
                    user = user,
                    cheerCount = user.cheerCount,
                    onCheer = { onCheer(user) },
                    onEdit = {
                        user.friendId?.let { onEditFriend(it) }
                    },
                    onDelete = {
                        friendToDelete = user
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }

    // Delete Confirmation Dialog
    if (friendToDelete != null) {
        val target = friendToDelete!!
        AlertDialog(
            onDismissRequest = { friendToDelete = null },
            containerColor = Slate900,
            title = {
                Text(
                    text = "Remove from Circle?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate50
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to remove ${target.name} (${target.username}) from your leaderboard?",
                    fontSize = 14.sp,
                    color = Slate400
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        target.friendId?.let { onDeleteFriend(it) }
                        friendToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Text("Remove", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { friendToDelete = null }) {
                    Text("Cancel", color = Slate400)
                }
            },
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
        )
    }
}

@Composable
private fun EmptyCircleBentoCard(
    onAddFriend: () -> Unit,
    onInviteClick: () -> Unit
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
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(BentoViolet.copy(alpha = 0.15f))
                    .border(1.dp, BentoViolet.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.GroupAdd,
                    contentDescription = null,
                    tint = BentoVioletLight,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Build Your Circle",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Slate50
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Default dummy contenders have been removed. Add your real workout buddies, colleagues, or study partners to compare streaks and cheer each other on!",
                fontSize = 13.sp,
                color = Slate400,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onAddFriend,
                    colors = ButtonDefaults.buttonColors(containerColor = BentoViolet),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("add_first_friend_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.PersonAdd,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Add Friend",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }

                Button(
                    onClick = onInviteClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
                        .testTag("invite_friends_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = null,
                        tint = Slate200,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Invite",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Slate200
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyRegionBentoCard(
    region: String,
    onAddMember: () -> Unit
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
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Slate800)
                    .border(1.dp, GlassBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Public,
                    contentDescription = null,
                    tint = BentoVioletLight,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "No Members in $region",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Slate50
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Be the pioneer in your region! Add members to this regional squad to compete together.",
                fontSize = 13.sp,
                color = Slate400,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAddMember,
                colors = ButtonDefaults.buttonColors(containerColor = BentoViolet),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Add Member to $region",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun PodiumBentoCard(topContenders: List<LeaderboardUser>) {
    val rank1 = topContenders.getOrNull(0)
    val rank2 = topContenders.getOrNull(1)
    val rank3 = topContenders.getOrNull(2)

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        borderColor = GlassBorder
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "PODIUM CHAMPIONS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BentoVioletLight,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // Rank 2 (Silver)
                if (rank2 != null) {
                    PodiumPillar(
                        user = rank2,
                        rank = 2,
                        height = 95.dp,
                        crownColor = Color(0xFF94A3B8),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Rank 1 (Gold)
                if (rank1 != null) {
                    PodiumPillar(
                        user = rank1,
                        rank = 1,
                        height = 120.dp,
                        crownColor = Color(0xFFFBBF24),
                        modifier = Modifier.weight(1.15f)
                    )
                }

                // Rank 3 (Bronze)
                if (rank3 != null) {
                    PodiumPillar(
                        user = rank3,
                        rank = 3,
                        height = 80.dp,
                        crownColor = Color(0xFFD97706),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PodiumPillar(
    user: LeaderboardUser,
    rank: Int,
    height: androidx.compose.ui.unit.Dp,
    crownColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Emoji & Crown
        Box(
            modifier = Modifier
                .size(if (rank == 1) 46.dp else 38.dp)
                .clip(CircleShape)
                .background(Slate800)
                .border(2.dp, crownColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.avatarEmoji,
                fontSize = if (rank == 1) 20.sp else 16.sp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = user.name.split(" ").firstOrNull() ?: user.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Slate50,
            maxLines = 1
        )

        Text(
            text = "${user.totalXp} XP",
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = crownColor
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Pillar Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            crownColor.copy(alpha = 0.35f),
                            Slate900
                        )
                    )
                )
                .border(
                    1.dp,
                    crownColor.copy(alpha = 0.4f),
                    RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "#$rank",
                fontSize = if (rank == 1) 20.sp else 16.sp,
                fontWeight = FontWeight.Black,
                color = crownColor
            )
        }
    }
}

@Composable
private fun LeaderboardRowItem(
    user: LeaderboardUser,
    cheerCount: Int,
    onCheer: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(if (user.isCurrentUser) BentoViolet.copy(alpha = 0.14f) else Slate900)
            .border(
                1.dp,
                if (user.isCurrentUser) BentoViolet.copy(alpha = 0.55f) else GlassBorder,
                RoundedCornerShape(18.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "#${user.rank}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (user.rank <= 3) BentoVioletLight else Slate500,
                    modifier = Modifier.width(24.dp)
                )

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Slate800),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.avatarEmoji,
                        fontSize = 18.sp
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = user.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (user.isCurrentUser) BentoVioletLight else Slate50
                        )
                        if (user.isCurrentUser) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(BentoViolet.copy(alpha = 0.3f))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text("You", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BentoVioletLight)
                            }
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${user.streakDays}d streak",
                            fontSize = 11.sp,
                            color = FlameAmber
                        )
                        Text(text = "•", fontSize = 10.sp, color = Slate500)
                        Text(
                            text = "${user.weeklyCompletions}w",
                            fontSize = 11.sp,
                            color = Slate400
                        )
                        Text(text = "•", fontSize = 10.sp, color = Slate500)
                        Text(
                            text = user.region,
                            fontSize = 10.sp,
                            color = Slate500
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${user.totalXp}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate50
                    )
                    Text(
                        text = "XP",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate500
                    )
                }

                // Cheer Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(FlameOrange.copy(alpha = 0.15f))
                        .clickable { onCheer() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("cheer_button_${user.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Cheer",
                            tint = FlameOrange,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "$cheerCount",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = FlameOrange
                        )
                    }
                }

                // Options Menu for friends
                if (!user.isCurrentUser && user.friendId != null) {
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "Options",
                                tint = Slate500,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(Slate900)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit Friend", color = Slate50, fontSize = 13.sp) },
                                onClick = {
                                    showMenu = false
                                    onEdit()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Remove Friend", color = Color(0xFFEF4444), fontSize = 13.sp) },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
