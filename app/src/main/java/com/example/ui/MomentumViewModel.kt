package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.AnalyticsSummary
import com.example.data.model.Friend
import com.example.data.model.Habit
import com.example.data.model.HabitCompletion
import com.example.data.model.HabitWithWeeklyStatus
import com.example.data.model.HeatmapDay
import com.example.data.model.LeaderboardScope
import com.example.data.model.LeaderboardTimeframe
import com.example.data.model.LeaderboardUser
import com.example.data.model.TaskAiAdvice
import com.example.data.model.TierLevel
import com.example.data.model.TierProgress
import com.example.data.model.TimeOfDay
import com.example.data.model.XpPenaltyRecord
import com.example.data.repository.HabitRepository
import com.example.data.repository.LeaderboardRepository
import com.example.data.repository.UserPreferencesRepository
import com.example.data.repository.UserProfile
import com.example.data.service.GeminiCoachService
import com.example.util.DateUtils
import com.example.util.FeedbackManager
import com.example.util.InviteUtils
import com.example.util.NotificationHelper
import com.example.widget.MomentumWidgetProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DailyFeedUiState(
    val selectedDate: String = DateUtils.getTodayString(),
    val habits: List<HabitWithWeeklyStatus> = emptyList(),
    val totalHabitsCount: Int = 0,
    val completedCount: Int = 0,
    val progressFraction: Float = 0f,
    val selectedTimeFilter: String = "ALL",
    val selectedCategoryFilter: String = "All",
    val completedDayMap: Map<String, Int> = emptyMap()
)

class MomentumViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: HabitRepository
    private val friendDao = AppDatabase.getDatabase(application).friendDao()
    private val leaderboardRepository = LeaderboardRepository()
    private val geminiCoachService = GeminiCoachService()
    private val userPreferencesRepository = UserPreferencesRepository(application)
    val feedbackManager = FeedbackManager(application)

    val userProfile: StateFlow<UserProfile> = userPreferencesRepository.userProfile

    private val _soundEnabled = MutableStateFlow(true)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _hapticsEnabled = MutableStateFlow(true)
    val hapticsEnabled: StateFlow<Boolean> = _hapticsEnabled.asStateFlow()

    private val _showProfileSettingsDialog = MutableStateFlow(false)
    val showProfileSettingsDialog: StateFlow<Boolean> = _showProfileSettingsDialog.asStateFlow()

    private val _showXpBreakdownSheet = MutableStateFlow(false)
    val showXpBreakdownSheet: StateFlow<Boolean> = _showXpBreakdownSheet.asStateFlow()

    private val _selectedDate = MutableStateFlow(DateUtils.getTodayString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _selectedTimeFilter = MutableStateFlow("ALL")
    val selectedTimeFilter: StateFlow<String> = _selectedTimeFilter.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow("All")
    val selectedCategoryFilter: StateFlow<String> = _selectedCategoryFilter.asStateFlow()

    private val _currentTab = MutableStateFlow(0) // 0 = Feed, 1 = Analytics, 2 = Leaderboard, 3 = AI Coach
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    private val _showAddEditDialog = MutableStateFlow(false)
    val showAddEditDialog: StateFlow<Boolean> = _showAddEditDialog.asStateFlow()

    private val _editingHabit = MutableStateFlow<Habit?>(null)
    val editingHabit: StateFlow<Habit?> = _editingHabit.asStateFlow()

    private val _habitToDelete = MutableStateFlow<Habit?>(null)
    val habitToDelete: StateFlow<Habit?> = _habitToDelete.asStateFlow()

    // Share Progress State
    private val _showShareDialog = MutableStateFlow(false)
    val showShareDialog: StateFlow<Boolean> = _showShareDialog.asStateFlow()

    // Leaderboard State
    private val _leaderboardScope = MutableStateFlow(LeaderboardScope.FRIENDS)
    val leaderboardScope: StateFlow<LeaderboardScope> = _leaderboardScope.asStateFlow()

    private val _selectedRegion = MutableStateFlow("All Regions")
    val selectedRegion: StateFlow<String> = _selectedRegion.asStateFlow()

    private val _leaderboardTimeframe = MutableStateFlow(LeaderboardTimeframe.WEEKLY)
    val leaderboardTimeframe: StateFlow<LeaderboardTimeframe> = _leaderboardTimeframe.asStateFlow()

    private val _showAddEditFriendDialog = MutableStateFlow(false)
    val showAddEditFriendDialog: StateFlow<Boolean> = _showAddEditFriendDialog.asStateFlow()

    private val _showInviteFriendsDialog = MutableStateFlow(false)
    val showInviteFriendsDialog: StateFlow<Boolean> = _showInviteFriendsDialog.asStateFlow()

    private val _editingFriend = MutableStateFlow<Friend?>(null)
    val editingFriend: StateFlow<Friend?> = _editingFriend.asStateFlow()

    // AI Coach Advice State
    private val _taskAdviceMap = MutableStateFlow<Map<Long, TaskAiAdvice>>(emptyMap())
    val taskAdviceMap: StateFlow<Map<Long, TaskAiAdvice>> = _taskAdviceMap.asStateFlow()

    private val _selectedHabitForAdvice = MutableStateFlow<HabitWithWeeklyStatus?>(null)
    val selectedHabitForAdvice: StateFlow<HabitWithWeeklyStatus?> = _selectedHabitForAdvice.asStateFlow()

    private val _isLoadingAdvice = MutableStateFlow(false)
    val isLoadingAdvice: StateFlow<Boolean> = _isLoadingAdvice.asStateFlow()

    private val _loadingHabitId = MutableStateFlow<Long?>(null)
    val loadingHabitId: StateFlow<Long?> = _loadingHabitId.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = HabitRepository(db.habitDao())
        
        val initialProfile = userPreferencesRepository.userProfile.value
        _soundEnabled.value = initialProfile.soundEnabled
        _hapticsEnabled.value = initialProfile.hapticsEnabled
        feedbackManager.soundEnabled = initialProfile.soundEnabled
        feedbackManager.hapticsEnabled = initialProfile.hapticsEnabled

        // Initialize Notification Channels
        NotificationHelper.createNotificationChannels(application)

        if (!initialProfile.isOnboardingCompleted) {
            viewModelScope.launch {
                repository.clearAllData()
                friendDao.deleteAllFriends()
            }
        }
    }

    private val allHabits = repository.allHabits
    private val allCompletions = repository.allCompletions

    val dailyFeedState: StateFlow<DailyFeedUiState> = combine(
        allHabits,
        allCompletions,
        _selectedDate,
        _selectedTimeFilter,
        _selectedCategoryFilter
    ) { habits, completions, selectedDateStr, timeFilter, catFilter ->
        val completionMap = completions.groupBy { it.habitId }
        val past7Days = DateUtils.getPastNDays(7).reversed()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val todayCal = Calendar.getInstance()
        val todayStr = sdf.format(todayCal.time)

        val habitsWithStatus = habits.map { habit ->
            val habitCompletions = completionMap[habit.id] ?: emptyList()
            val completedDates = habitCompletions.map { it.dateString }.toSet()
            val isCompletedOnSelected = completedDates.contains(selectedDateStr)

            val weekCompletions = past7Days.map { completedDates.contains(it) }

            // Compute current streak
            var streak = 0
            val checkCal = Calendar.getInstance()
            val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
            val yesterdayStr = sdf.format(yesterdayCal.time)

            if (completedDates.contains(todayStr) || completedDates.contains(yesterdayStr)) {
                if (!completedDates.contains(todayStr)) {
                    checkCal.add(Calendar.DAY_OF_YEAR, -1)
                }
                while (completedDates.contains(sdf.format(checkCal.time))) {
                    streak++
                    checkCal.add(Calendar.DAY_OF_YEAR, -1)
                }
            }

            HabitWithWeeklyStatus(
                habit = habit,
                isCompletedToday = isCompletedOnSelected,
                currentStreak = streak,
                bestStreak = maxOf(streak, habitCompletions.size),
                totalCompletions = habitCompletions.size,
                weeklyHistory = weekCompletions
            )
        }

        // Filter habits based on chips
        val filteredHabits = habitsWithStatus.filter { item ->
            val matchesTime = when (timeFilter) {
                "MORNING" -> item.habit.timeOfDay == TimeOfDay.MORNING
                "AFTERNOON" -> item.habit.timeOfDay == TimeOfDay.AFTERNOON
                "EVENING" -> item.habit.timeOfDay == TimeOfDay.EVENING
                "ANYTIME" -> item.habit.timeOfDay == TimeOfDay.ANYTIME
                else -> true
            }
            val matchesCategory = if (catFilter == "All") true else item.habit.category == catFilter
            matchesTime && matchesCategory
        }

        val totalHabits = habitsWithStatus.size
        val completedCount = habitsWithStatus.count { it.isCompletedToday }
        val progressFraction = if (totalHabits > 0) completedCount.toFloat() / totalHabits.toFloat() else 0f

        val completedByDate = completions.groupBy { it.dateString }
            .mapValues { it.value.size }

        DailyFeedUiState(
            selectedDate = selectedDateStr,
            habits = filteredHabits,
            totalHabitsCount = totalHabits,
            completedCount = completedCount,
            progressFraction = progressFraction,
            selectedTimeFilter = timeFilter,
            selectedCategoryFilter = catFilter,
            completedDayMap = completedByDate
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DailyFeedUiState()
    )

    val analyticsSummary: StateFlow<AnalyticsSummary> = combine(
        allHabits,
        allCompletions
    ) { habits, completions ->
        val totalCheckIns = completions.size
        val habitCompletionMap = completions.groupBy { it.habitId }
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val todayCal = Calendar.getInstance()
        val todayStr = sdf.format(todayCal.time)

        var maxStreak = 0
        habits.forEach { habit ->
            val habitCompletions = habitCompletionMap[habit.id] ?: emptyList()
            val completedDates = habitCompletions.map { it.dateString }.toSet()
            var streak = 0
            val checkCal = Calendar.getInstance()
            val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
            val yesterdayStr = sdf.format(yesterdayCal.time)

            if (completedDates.contains(todayStr) || completedDates.contains(yesterdayStr)) {
                if (!completedDates.contains(todayStr)) {
                    checkCal.add(Calendar.DAY_OF_YEAR, -1)
                }
                while (completedDates.contains(sdf.format(checkCal.time))) {
                    streak++
                    checkCal.add(Calendar.DAY_OF_YEAR, -1)
                }
            }
            if (streak > maxStreak) maxStreak = streak
        }

        // Overall rate over past 30 days
        val past30Days = DateUtils.getPastNDays(30)
        val activeHabitCount = habits.size
        val maxPossibleCompletions = past30Days.size * (if (activeHabitCount > 0) activeHabitCount else 1)
        val actualCompletionsIn30 = completions.count { past30Days.contains(it.dateString) }
        val rate = if (maxPossibleCompletions > 0) {
            ((actualCompletionsIn30.toFloat() / maxPossibleCompletions.toFloat()) * 100f).coerceIn(0f, 100f)
        } else 0f

        // Count perfect days
        val byDate = completions.groupBy { it.dateString }
        val perfectDays = byDate.count { (_, comps) ->
            activeHabitCount > 0 && comps.size >= activeHabitCount
        }

        // Category Breakdown
        val catCounts = mutableMapOf<String, Int>()
        completions.forEach { comp ->
            val h = habits.firstOrNull { it.id == comp.habitId }
            val cat = h?.category ?: "Other"
            catCounts[cat] = (catCounts[cat] ?: 0) + 1
        }

        // Time of Day Breakdown
        val timeCounts = mutableMapOf<TimeOfDay, Int>()
        completions.forEach { comp ->
            val h = habits.firstOrNull { it.id == comp.habitId }
            val time = h?.timeOfDay ?: TimeOfDay.ANYTIME
            timeCounts[time] = (timeCounts[time] ?: 0) + 1
        }

        // Generate 70-day Heatmap Matrix (10 weeks x 7 days)
        val heatmapDays = mutableListOf<HeatmapDay>()
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -69) // Start 69 days ago (70 days total)

        val stepCal = cal.clone() as Calendar
        for (i in 0 until 70) {
            val dStr = sdf.format(stepCal.time)
            val dayCount = byDate[dStr]?.size ?: 0
            val intensity = when {
                dayCount >= 5 -> 4
                dayCount >= 3 -> 3
                dayCount >= 2 -> 2
                dayCount >= 1 -> 1
                else -> 0
            }
            val dow = (stepCal.get(Calendar.DAY_OF_WEEK) + 5) % 7 + 1 // 1 (Mon) to 7 (Sun)

            heatmapDays.add(
                HeatmapDay(
                    dateString = dStr,
                    count = dayCount,
                    intensity = intensity,
                    dayOfWeek = dow
                )
            )
            stepCal.add(Calendar.DAY_OF_YEAR, 1)
        }

        AnalyticsSummary(
            totalCheckIns = totalCheckIns,
            currentBestStreak = maxStreak,
            overallCompletionRate = rate,
            perfectDaysCount = perfectDays,
            categoryCounts = catCounts,
            timeOfDayCounts = timeCounts,
            heatmapDays = heatmapDays
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnalyticsSummary()
    )

    val tierProgress: StateFlow<TierProgress> = combine(
        allHabits,
        allCompletions,
        analyticsSummary,
        userProfile
    ) { habits, completions, analytics, profile ->
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val todayStr = sdf.format(Date())
        val past30Days = DateUtils.getPastNDays(30)
        val completionDateSet = completions.map { it.dateString }.toSet()

        // Calculate broken streaks and missed days with negative XP tracking
        var missedDaysCount = 0
        var brokenStreakCount = 0
        val penaltiesList = mutableListOf<XpPenaltyRecord>()

        if (habits.isNotEmpty()) {
            val cal = Calendar.getInstance()
            for (dateStr in past30Days) {
                // Parse day of week for rest days
                val dateObj = try { sdf.parse(dateStr) } catch (_: Exception) { null }
                val isRestDay = if (dateObj != null && profile.restDaysEnabled) {
                    cal.time = dateObj
                    val dow = cal.get(Calendar.DAY_OF_WEEK)
                    profile.designatedRestDays.contains(dow)
                } else false

                val isFrozen = profile.streakFreezeDates.contains(dateStr)

                // If day was missed and not protected by freeze or rest day
                if (!completionDateSet.contains(dateStr) && dateStr != todayStr && !isFrozen && !isRestDay) {
                    missedDaysCount++
                    if (missedDaysCount % 4 == 0) {
                        brokenStreakCount++
                        penaltiesList.add(
                            XpPenaltyRecord(
                                reason = "Broken Consistency Penalty",
                                penaltyXp = 120,
                                dateString = dateStr
                            )
                        )
                    }
                }
            }
        }

        val isTodayFrozen = profile.streakFreezeDates.contains(todayStr)

        val progress = leaderboardRepository.calculateUserTierProgress(
            totalCompletions = analytics.totalCheckIns,
            currentStreak = analytics.currentBestStreak,
            bestStreak = analytics.currentBestStreak,
            perfectDaysCount = analytics.perfectDaysCount,
            completionRate = analytics.overallCompletionRate,
            brokenStreakCount = brokenStreakCount,
            missedDaysCount = missedDaysCount,
            streakFreezesAvailable = profile.streakFreezesAvailable,
            isStreakFreezeActiveToday = isTodayFrozen,
            recentPenalties = penaltiesList
        )

        // Update home screen app widget
        MomentumWidgetProvider.updateAllWidgets(
            context = getApplication(),
            streak = analytics.currentBestStreak,
            tierTitle = "${progress.currentTier.iconEmoji} ${progress.currentTier.title}",
            completed = dailyFeedState.value.completedCount,
            total = dailyFeedState.value.totalHabitsCount,
            xp = progress.totalXp
        )

        progress
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TierProgress(
            currentTier = TierLevel.AMATEUR,
            nextTier = TierLevel.ROOKIE,
            totalXp = 0,
            tierProgressFraction = 0f,
            xpInCurrentTier = 0,
            xpNeededForNextTier = 500,
            baseCheckInXp = 0,
            streakBonusXp = 0,
            consistencyBonusXp = 0,
            perfectDayBonusXp = 0,
            negativePenaltyXp = 0
        )
    )

    val leaderboardUsers: StateFlow<List<LeaderboardUser>> = combine(
        friendDao.getAllFriends(),
        _leaderboardScope,
        _selectedRegion,
        _leaderboardTimeframe,
        analyticsSummary,
        userProfile,
        tierProgress
    ) { array ->
        @Suppress("UNCHECKED_CAST")
        val friends = array[0] as List<Friend>
        val scope = array[1] as LeaderboardScope
        val reg = array[2] as String
        val tf = array[3] as LeaderboardTimeframe
        val analytics = array[4] as AnalyticsSummary
        val profile = array[5] as UserProfile
        val tier = array[6] as TierProgress

        leaderboardRepository.getLeaderboard(
            friends = friends,
            userStreak = analytics.currentBestStreak,
            userWeeklyCompletions = analytics.totalCheckIns,
            userTotalCompletions = analytics.totalCheckIns,
            userTopHabit = "Daily Routine",
            userTotalXp = tier.totalXp,
            userTierTitle = "${tier.currentTier.iconEmoji} ${tier.currentTier.title}",
            scope = scope,
            selectedRegion = reg,
            timeframe = tf,
            userName = profile.name,
            userHandle = profile.handle,
            userAvatarEmoji = profile.avatarEmoji,
            userRegion = profile.region
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun selectDate(dateString: String) {
        _selectedDate.value = dateString
    }

    fun selectTimeFilter(filter: String) {
        _selectedTimeFilter.value = filter
    }

    fun selectCategoryFilter(category: String) {
        _selectedCategoryFilter.value = category
    }

    fun selectTab(tabIndex: Int) {
        _currentTab.value = tabIndex
    }

    fun setLeaderboardScope(scope: LeaderboardScope) {
        _leaderboardScope.value = scope
        feedbackManager.onHabitCreatedOrSaved()
    }

    fun setSelectedRegion(region: String) {
        _selectedRegion.value = region
    }

    fun setLeaderboardTimeframe(timeframe: LeaderboardTimeframe) {
        _leaderboardTimeframe.value = timeframe
    }

    fun openAddFriendDialog() {
        _editingFriend.value = null
        _showAddEditFriendDialog.value = true
    }

    fun openInviteFriendsDialog() {
        feedbackManager.onHabitCreatedOrSaved()
        _showInviteFriendsDialog.value = true
    }

    fun dismissInviteFriendsDialog() {
        _showInviteFriendsDialog.value = false
    }

    fun handleIncomingInviteUri(uriString: String) {
        val parsed = InviteUtils.parseInviteInput(uriString) ?: return
        val currentProfile = userPreferencesRepository.userProfile.value
        val newFriend = Friend(
            id = 0L,
            name = parsed.name ?: parsed.handle.removePrefix("@"),
            username = parsed.handle,
            avatarEmoji = parsed.avatarEmoji ?: "⚡",
            region = parsed.region ?: currentProfile.region,
            topHabitName = "Daily Consistency",
            streakDays = 1,
            weeklyCompletions = 4,
            cheerCount = 0,
            createdAt = System.currentTimeMillis()
        )
        _editingFriend.value = newFriend
        _showAddEditFriendDialog.value = true
        _showInviteFriendsDialog.value = false
    }

    fun openEditFriendDialog(friendId: Long) {
        viewModelScope.launch {
            val friend = friendDao.getFriendById(friendId)
            _editingFriend.value = friend
            _showAddEditFriendDialog.value = true
        }
    }

    fun dismissAddEditFriendDialog() {
        _showAddEditFriendDialog.value = false
        _editingFriend.value = null
    }

    fun saveFriend(friend: Friend) {
        viewModelScope.launch {
            if (friend.id == 0L) {
                friendDao.insertFriend(friend)
            } else {
                friendDao.updateFriend(friend)
            }
            feedbackManager.onHabitCreatedOrSaved()
            dismissAddEditFriendDialog()
        }
    }

    fun deleteFriend(friendId: Long) {
        viewModelScope.launch {
            friendDao.deleteFriend(friendId)
            feedbackManager.onHabitUncompleted()
            dismissAddEditFriendDialog()
        }
    }

    fun cheerUser(user: LeaderboardUser) {
        viewModelScope.launch {
            feedbackManager.onCheerTriggered()
            user.friendId?.let { friendId ->
                friendDao.incrementCheer(friendId)
            }
        }
    }

    fun openProfileSettingsDialog() {
        feedbackManager.onHabitCreatedOrSaved()
        _showProfileSettingsDialog.value = true
    }

    fun dismissProfileSettingsDialog() {
        _showProfileSettingsDialog.value = false
    }

    fun updateUserProfile(name: String, handle: String, avatarEmoji: String, dailyTarget: Int, region: String = userProfile.value.region) {
        userPreferencesRepository.updateProfile(name, handle, avatarEmoji, dailyTarget, region)
        feedbackManager.onHabitCreatedOrSaved()
    }

    fun completeOnboarding(
        name: String,
        handle: String,
        avatarEmoji: String,
        focusTrack: String,
        selectedHabits: List<Habit>,
        generateHistory: Boolean,
        dailyTarget: Int,
        soundEnabled: Boolean,
        hapticsEnabled: Boolean,
        region: String = "North America"
    ) {
        viewModelScope.launch {
            userPreferencesRepository.setOnboardingCompleted(
                name = name,
                handle = handle,
                avatarEmoji = avatarEmoji,
                focusTrack = focusTrack,
                dailyTarget = dailyTarget,
                soundEnabled = soundEnabled,
                hapticsEnabled = hapticsEnabled,
                region = region
            )

            _soundEnabled.value = soundEnabled
            _hapticsEnabled.value = hapticsEnabled
            feedbackManager.soundEnabled = soundEnabled
            feedbackManager.hapticsEnabled = hapticsEnabled

            repository.setupStarterHabits(selectedHabits, generateHistory)
            feedbackManager.onPerfectDayAchieved()
        }
    }

    fun rerunSetup() {
        userPreferencesRepository.resetOnboarding()
        feedbackManager.onHabitCreatedOrSaved()
    }

    fun clearAllDataAndStartFresh() {
        viewModelScope.launch {
            repository.clearAllData()
            friendDao.deleteAllFriends()
            userPreferencesRepository.clearAll()
            feedbackManager.onHabitUncompleted()
        }
    }

    fun toggleSound() {
        val nextState = !_soundEnabled.value
        _soundEnabled.value = nextState
        feedbackManager.soundEnabled = nextState
        userPreferencesRepository.setSoundEnabled(nextState)
        if (nextState) {
            feedbackManager.onCheerTriggered()
        }
    }

    fun toggleHaptics() {
        val nextState = !_hapticsEnabled.value
        _hapticsEnabled.value = nextState
        feedbackManager.hapticsEnabled = nextState
        userPreferencesRepository.setHapticsEnabled(nextState)
        if (nextState) {
            feedbackManager.onHabitCreatedOrSaved()
        }
    }

    fun openShareDialog() {
        feedbackManager.onShareTriggered()
        _showShareDialog.value = true
    }

    fun dismissShareDialog() {
        _showShareDialog.value = false
    }

    fun openAiAdviceDialog(habitStatus: HabitWithWeeklyStatus) {
        _selectedHabitForAdvice.value = habitStatus
        // If advice not yet loaded, fetch it
        if (!_taskAdviceMap.value.containsKey(habitStatus.habit.id)) {
            fetchAiAdvice(habitStatus)
        }
    }

    fun dismissAiAdviceDialog() {
        _selectedHabitForAdvice.value = null
    }

    fun fetchAiAdvice(habitStatus: HabitWithWeeklyStatus) {
        val habitId = habitStatus.habit.id
        viewModelScope.launch {
            _loadingHabitId.value = habitId
            _isLoadingAdvice.value = true

            val advice = geminiCoachService.generateAdviceForHabit(habitStatus)

            val currentMap = _taskAdviceMap.value.toMutableMap()
            currentMap[habitId] = advice
            _taskAdviceMap.value = currentMap

            _loadingHabitId.value = null
            _isLoadingAdvice.value = false
        }
    }

    fun toggleHabitCheckIn(habitId: Long) {
        viewModelScope.launch {
            val wasCompleted = repository.toggleHabitCompletion(habitId, _selectedDate.value)
            if (wasCompleted) {
                // Determine if this completion makes it a 100% Perfect Day or hits a streak milestone
                val currentHabits = dailyFeedState.value.habits
                val targetHabit = currentHabits.firstOrNull { it.habit.id == habitId }
                val completedCountAfterThis = currentHabits.count {
                    if (it.habit.id == habitId) true else it.isCompletedToday
                }
                val totalCount = currentHabits.size
                val isPerfectDay = totalCount > 0 && completedCountAfterThis >= totalCount

                val currentStreak = (targetHabit?.currentStreak ?: 0) + 1
                val isStreakMilestone = currentStreak in listOf(3, 7, 14, 21, 30, 50, 100, 365)

                if (isPerfectDay) {
                    feedbackManager.onPerfectDayAchieved()
                } else if (isStreakMilestone) {
                    feedbackManager.onStreakMilestoneAchieved()
                } else {
                    feedbackManager.onHabitCompleted()
                }
            } else {
                feedbackManager.onHabitUncompleted()
            }
        }
    }

    fun openAddHabitDialog() {
        _editingHabit.value = null
        _showAddEditDialog.value = true
    }

    fun openEditHabitDialog(habit: Habit) {
        _editingHabit.value = habit
        _showAddEditDialog.value = true
    }

    fun dismissAddEditDialog() {
        _showAddEditDialog.value = false
        _editingHabit.value = null
    }

    fun saveHabit(habit: Habit) {
        viewModelScope.launch {
            if (habit.id == 0L) {
                repository.insertHabit(habit)
            } else {
                repository.updateHabit(habit)
            }
            feedbackManager.onHabitCreatedOrSaved()
            dismissAddEditDialog()
        }
    }

    fun promptDeleteHabit(habit: Habit) {
        _habitToDelete.value = habit
    }

    fun dismissDeleteDialog() {
        _habitToDelete.value = null
    }

    fun openXpBreakdownSheet() {
        feedbackManager.onHabitCreatedOrSaved()
        _showXpBreakdownSheet.value = true
    }

    fun dismissXpBreakdownSheet() {
        _showXpBreakdownSheet.value = false
    }

    fun useStreakFreezeForToday() {
        val todayStr = DateUtils.getTodayString()
        val success = userPreferencesRepository.useStreakFreeze(todayStr)
        if (success) {
            feedbackManager.onStreakFreezeUsed()
        }
    }

    fun sendTestReminder() {
        feedbackManager.onHabitCreatedOrSaved()
        val habits = dailyFeedState.value.habits
        val firstUncompleted = habits.firstOrNull { !it.isCompletedToday }
        val habitTitle = firstUncompleted?.habit?.title ?: "Consistency Check-In"
        val habitId = firstUncompleted?.habit?.id ?: 1L

        NotificationHelper.showInstantHabitReminder(
            context = getApplication(),
            title = "Momentum: $habitTitle ⚡",
            message = "Your streak is on the line! Tap 'Quick Check-In' to earn +50 XP and protect your tier rank.",
            habitId = habitId
        )
    }

    fun updateReminderSettings(enabled: Boolean, morningTime: String, eveningTime: String) {
        userPreferencesRepository.setRemindersEnabled(enabled)
        userPreferencesRepository.setReminderTimes(morningTime, eveningTime)
        feedbackManager.onHabitCreatedOrSaved()

        val context = getApplication<Application>()
        if (enabled) {
            try {
                val morningParts = morningTime.split(":")
                val morningHour = morningParts.getOrNull(0)?.toIntOrNull() ?: 8
                val morningMin = morningParts.getOrNull(1)?.toIntOrNull() ?: 0
                NotificationHelper.scheduleDailyReminder(
                    context = context,
                    hour = morningHour,
                    minute = morningMin,
                    requestCode = NotificationHelper.REQUEST_CODE_MORNING,
                    title = "Morning Momentum 🌅",
                    message = "Start strong! Complete your morning habits to earn early streak XP."
                )

                val eveningParts = eveningTime.split(":")
                val eveningHour = eveningParts.getOrNull(0)?.toIntOrNull() ?: 20
                val eveningMin = eveningParts.getOrNull(1)?.toIntOrNull() ?: 0
                NotificationHelper.scheduleDailyReminder(
                    context = context,
                    hour = eveningHour,
                    minute = eveningMin,
                    requestCode = NotificationHelper.REQUEST_CODE_EVENING,
                    title = "Evening Habit Wrap-Up 🌙",
                    message = "Finish today's habits before midnight to lock in your +150 XP Perfect Day bonus!"
                )
            } catch (_: Exception) {}
        } else {
            NotificationHelper.cancelDailyReminder(context, NotificationHelper.REQUEST_CODE_MORNING)
            NotificationHelper.cancelDailyReminder(context, NotificationHelper.REQUEST_CODE_EVENING)
        }
    }

    fun updateRestDaysConfig(enabled: Boolean, restDays: Set<Int>) {
        userPreferencesRepository.setRestDaysConfig(enabled, restDays)
        feedbackManager.onHabitCreatedOrSaved()
    }

    fun confirmDeleteHabit() {
        val habit = _habitToDelete.value ?: return
        viewModelScope.launch {
            repository.deleteHabit(habit.id)
            dismissDeleteDialog()
        }
    }
}
