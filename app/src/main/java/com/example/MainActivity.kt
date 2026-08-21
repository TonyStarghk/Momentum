package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MomentumViewModel
import com.example.ui.aicoach.AiCoachScreen
import com.example.ui.analytics.AnalyticsScreen
import com.example.ui.components.AddEditFriendDialog
import com.example.ui.components.AddEditHabitDialog
import com.example.ui.components.GlassCard
import com.example.ui.components.HabitAiAdviceDialog
import com.example.ui.components.InviteFriendsDialog
import com.example.ui.components.ProfileSettingsDialog
import com.example.ui.components.ShareProgressDialog
import com.example.ui.components.XpBreakdownSheet
import com.example.ui.feed.DailyFeedScreen
import com.example.ui.leaderboard.LeaderboardScreen
import com.example.ui.setup.SetupOnboardingScreen
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoViolet
import com.example.ui.theme.BentoVioletDark
import com.example.ui.theme.BentoVioletLight
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MomentumTheme
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

class MainActivity : ComponentActivity() {
    private val viewModel: MomentumViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        intent?.data?.toString()?.let { uriString ->
            viewModel.handleIncomingInviteUri(uriString)
        }

        setContent {
            MomentumTheme {
                MomentumApp(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent.data?.toString()?.let { uriString ->
            viewModel.handleIncomingInviteUri(uriString)
        }
    }
}

@Composable
fun MomentumApp(viewModel: MomentumViewModel) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val feedState by viewModel.dailyFeedState.collectAsStateWithLifecycle()
    val analyticsSummary by viewModel.analyticsSummary.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val showAddEditDialog by viewModel.showAddEditDialog.collectAsStateWithLifecycle()
    val editingHabit by viewModel.editingHabit.collectAsStateWithLifecycle()
    val habitToDelete by viewModel.habitToDelete.collectAsStateWithLifecycle()

    // New Features State
    val leaderboardUsers by viewModel.leaderboardUsers.collectAsStateWithLifecycle()
    val leaderboardScope by viewModel.leaderboardScope.collectAsStateWithLifecycle()
    val selectedRegion by viewModel.selectedRegion.collectAsStateWithLifecycle()
    val leaderboardTimeframe by viewModel.leaderboardTimeframe.collectAsStateWithLifecycle()
    val showAddEditFriendDialog by viewModel.showAddEditFriendDialog.collectAsStateWithLifecycle()
    val editingFriend by viewModel.editingFriend.collectAsStateWithLifecycle()
    val taskAdviceMap by viewModel.taskAdviceMap.collectAsStateWithLifecycle()
    val selectedHabitForAdvice by viewModel.selectedHabitForAdvice.collectAsStateWithLifecycle()
    val isLoadingAdvice by viewModel.isLoadingAdvice.collectAsStateWithLifecycle()
    val loadingHabitId by viewModel.loadingHabitId.collectAsStateWithLifecycle()
    val showShareDialog by viewModel.showShareDialog.collectAsStateWithLifecycle()
    val showInviteFriendsDialog by viewModel.showInviteFriendsDialog.collectAsStateWithLifecycle()
    val showProfileSettingsDialog by viewModel.showProfileSettingsDialog.collectAsStateWithLifecycle()

    // Gamification & Tiers State
    val tierProgress by viewModel.tierProgress.collectAsStateWithLifecycle()
    val showXpSheet by viewModel.showXpBreakdownSheet.collectAsStateWithLifecycle()

    // Sound and Haptics State
    val soundEnabled by viewModel.soundEnabled.collectAsStateWithLifecycle()
    val hapticsEnabled by viewModel.hapticsEnabled.collectAsStateWithLifecycle()

    if (!userProfile.isOnboardingCompleted) {
        SetupOnboardingScreen(
            feedbackManager = viewModel.feedbackManager,
            onCompleteSetup = { name, handle, avatar, focusTrack, habits, generateHistory, dailyTarget, sound, haptics ->
                viewModel.completeOnboarding(
                    name = name,
                    handle = handle,
                    avatarEmoji = avatar,
                    focusTrack = focusTrack,
                    selectedHabits = habits,
                    generateHistory = generateHistory,
                    dailyTarget = dailyTarget,
                    soundEnabled = sound,
                    hapticsEnabled = haptics
                )
            }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = BentoBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                BottomNavigationGlassBar(
                    currentTab = currentTab,
                    onSelectTab = { viewModel.selectTab(it) },
                    onAddClick = { viewModel.openAddHabitDialog() }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tab_transition"
                ) { tab ->
                    when (tab) {
                        0 -> DailyFeedScreen(
                            uiState = feedState,
                            tierProgress = tierProgress,
                            soundEnabled = soundEnabled,
                            hapticsEnabled = hapticsEnabled,
                            onToggleSound = { viewModel.toggleSound() },
                            onToggleHaptics = { viewModel.toggleHaptics() },
                            onOpenSettings = { viewModel.openProfileSettingsDialog() },
                            onOpenXpSheet = { viewModel.openXpBreakdownSheet() },
                            onOpenSetupWizard = { viewModel.rerunSetup() },
                            onUseStreakFreeze = { viewModel.useStreakFreezeForToday() },
                            onSelectDate = { viewModel.selectDate(it) },
                            onSelectTimeFilter = { viewModel.selectTimeFilter(it) },
                            onSelectCategoryFilter = { viewModel.selectCategoryFilter(it) },
                            onToggleCheckIn = { viewModel.toggleHabitCheckIn(it) },
                            onEditHabit = { viewModel.openEditHabitDialog(it) },
                            onDeleteHabit = { viewModel.promptDeleteHabit(it) },
                            onAddNewHabit = { viewModel.openAddHabitDialog() },
                            onShareProgressClick = { viewModel.openShareDialog() },
                            onAiAdviceClick = { viewModel.openAiAdviceDialog(it) }
                        )
                        1 -> AnalyticsScreen(
                            summary = analyticsSummary,
                            tierProgress = tierProgress,
                            onOpenXpSheet = { viewModel.openXpBreakdownSheet() },
                            onShareClick = { viewModel.openShareDialog() }
                        )
                        2 -> LeaderboardScreen(
                            users = leaderboardUsers,
                            scope = leaderboardScope,
                            timeframe = leaderboardTimeframe,
                            selectedRegion = selectedRegion,
                            onScopeChange = { viewModel.setLeaderboardScope(it) },
                            onTimeframeChange = { viewModel.setLeaderboardTimeframe(it) },
                            onRegionChange = { viewModel.setSelectedRegion(it) },
                            onAddFriendClick = { viewModel.openAddFriendDialog() },
                            onEditFriend = { viewModel.openEditFriendDialog(it) },
                            onDeleteFriend = { viewModel.deleteFriend(it) },
                            onShareClick = { viewModel.openShareDialog() },
                            onInviteFriendsClick = { viewModel.openInviteFriendsDialog() },
                            onCheer = { viewModel.cheerUser(it) }
                        )
                        3 -> AiCoachScreen(
                            habits = feedState.habits,
                            adviceMap = taskAdviceMap,
                            loadingHabitId = loadingHabitId,
                            onRequestAdvice = { viewModel.fetchAiAdvice(it) }
                        )
                    }
                }

                // Gamification XP Breakdown & Streak Freeze Sheet
                if (showXpSheet && tierProgress != null) {
                    XpBreakdownSheet(
                        tierProgress = tierProgress!!,
                        onDismiss = { viewModel.dismissXpBreakdownSheet() },
                        onUseStreakFreeze = { viewModel.useStreakFreezeForToday() },
                        onSendTestReminder = { viewModel.sendTestReminder() }
                    )
                }

                // AI Coach Advice Sheet
                if (selectedHabitForAdvice != null) {
                    val currentHabitStatus = selectedHabitForAdvice!!
                    val advice = taskAdviceMap[currentHabitStatus.habit.id]
                    HabitAiAdviceDialog(
                        habitStatus = currentHabitStatus,
                        advice = advice,
                        isLoading = isLoadingAdvice,
                        onRegenerate = { viewModel.fetchAiAdvice(currentHabitStatus) },
                        onDismiss = { viewModel.dismissAiAdviceDialog() }
                    )
                }

                // Share Progress Dialog
                if (showShareDialog) {
                    ShareProgressDialog(
                        streakDays = analyticsSummary.currentBestStreak,
                        totalCompletions = analyticsSummary.totalCheckIns,
                        completionRate = analyticsSummary.overallCompletionRate.toInt(),
                        topHabit = feedState.habits.maxByOrNull { it.currentStreak }?.habit?.title ?: "Daily Routine",
                        perfectDays = analyticsSummary.perfectDaysCount,
                        onShareTriggered = { viewModel.openShareDialog() },
                        onDismiss = { viewModel.dismissShareDialog() }
                    )
                }

                // Profile & App Settings Dialog
                if (showProfileSettingsDialog) {
                    ProfileSettingsDialog(
                        userProfile = userProfile,
                        onSaveProfile = { name, handle, avatar, target, region ->
                            viewModel.updateUserProfile(name, handle, avatar, target, region)
                        },
                        onToggleSound = { viewModel.toggleSound() },
                        onToggleHaptics = { viewModel.toggleHaptics() },
                        onUpdateReminders = { enabled, morning, evening ->
                            viewModel.updateReminderSettings(enabled, morning, evening)
                        },
                        onUpdateRestDays = { enabled, restDays ->
                            viewModel.updateRestDaysConfig(enabled, restDays)
                        },
                        onSendTestNotification = {
                            viewModel.sendTestReminder()
                        },
                        onRerunSetup = {
                            viewModel.dismissProfileSettingsDialog()
                            viewModel.rerunSetup()
                        },
                        onClearAllData = {
                            viewModel.clearAllDataAndStartFresh()
                        },
                        onDismiss = { viewModel.dismissProfileSettingsDialog() }
                    )
                }

                // Add / Edit Friend Dialog
                if (showAddEditFriendDialog) {
                    AddEditFriendDialog(
                        initialFriend = editingFriend,
                        defaultRegion = userProfile.region,
                        onDismiss = { viewModel.dismissAddEditFriendDialog() },
                        onSave = { friend -> viewModel.saveFriend(friend) },
                        onDelete = { friendId -> viewModel.deleteFriend(friendId) }
                    )
                }

                // Invite Friends & Community Dialog
                if (showInviteFriendsDialog) {
                    InviteFriendsDialog(
                        userProfile = userProfile,
                        feedbackManager = viewModel.feedbackManager,
                        onImportFriend = { friend -> viewModel.saveFriend(friend) },
                        onDismiss = { viewModel.dismissInviteFriendsDialog() }
                    )
                }

                // Add / Edit Habit Dialog
                if (showAddEditDialog) {
                    AddEditHabitDialog(
                        initialHabit = editingHabit,
                        onDismiss = { viewModel.dismissAddEditDialog() },
                        onSave = { habit -> viewModel.saveHabit(habit) }
                    )
                }

                // Delete Habit Confirmation Dialog
                if (habitToDelete != null) {
                    AlertDialog(
                        onDismissRequest = { viewModel.dismissDeleteDialog() },
                        containerColor = Slate900,
                        title = {
                            Text(
                                text = "Delete Habit?",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate50
                            )
                        },
                        text = {
                            Text(
                                text = "Are you sure you want to delete \"${habitToDelete?.title}\"? All historical records will be removed.",
                                fontSize = 14.sp,
                                color = Slate400
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = { viewModel.confirmDeleteHabit() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Text("Delete", fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { viewModel.dismissDeleteDialog() }) {
                                Text("Cancel", color = Slate400)
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationGlassBar(
    currentTab: Int,
    onSelectTab: (Int) -> Unit,
    onAddClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .testTag("bottom_nav_bar"),
            shape = RoundedCornerShape(33.dp),
            backgroundColor = Color(0xF2121216),
            borderColor = GlassBorder
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 0: Habits
                NavTabItem(
                    title = "Habits",
                    icon = Icons.Filled.GridView,
                    isSelected = currentTab == 0,
                    onClick = { onSelectTab(0) },
                    testTag = "nav_feed_button"
                )

                // Tab 1: Analytics
                NavTabItem(
                    title = "Stats",
                    icon = Icons.Filled.Insights,
                    isSelected = currentTab == 1,
                    onClick = { onSelectTab(1) },
                    testTag = "nav_analytics_button"
                )

                // Center Floating Create Button (Bento Violet Glow Squircle)
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(BentoViolet, BentoVioletDark)
                            )
                        )
                        .border(1.dp, BentoVioletLight.copy(alpha = 0.4f), CircleShape)
                        .clickable { onAddClick() }
                        .testTag("fab_add_habit"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Create Habit",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Tab 2: Leaderboard Arena
                NavTabItem(
                    title = "Arena",
                    icon = Icons.Filled.EmojiEvents,
                    isSelected = currentTab == 2,
                    onClick = { onSelectTab(2) },
                    testTag = "nav_leaderboard_button"
                )

                // Tab 3: AI Coach
                NavTabItem(
                    title = "AI Coach",
                    icon = Icons.Filled.AutoAwesome,
                    isSelected = currentTab == 3,
                    onClick = { onSelectTab(3) },
                    testTag = "nav_ai_coach_button"
                )
            }
        }
    }
}

@Composable
private fun NavTabItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(if (isSelected) BentoViolet.copy(alpha = 0.2f) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 7.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) BentoVioletLight else Slate500,
            modifier = Modifier.size(18.dp)
        )
        if (isSelected) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BentoVioletLight
            )
        }
    }
}
