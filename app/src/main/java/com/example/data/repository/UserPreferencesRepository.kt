package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

data class UserProfile(
    val name: String = "New Challenger",
    val handle: String = "@challenger",
    val avatarEmoji: String = "🌱",
    val region: String = "North America",
    val focusTrack: String = "Habit Builder",
    val dailyTargetHabits: Int = 3,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val isOnboardingCompleted: Boolean = false,
    val streakFreezesAvailable: Int = 2,
    val streakFreezeDates: Set<String> = emptySet(),
    val remindersEnabled: Boolean = true,
    val morningReminderTime: String = "08:00",
    val eveningReminderTime: String = "20:00",
    val restDaysEnabled: Boolean = false,
    val designatedRestDays: Set<Int> = setOf(Calendar.SUNDAY) // default Sunday as rest day option
)

class UserPreferencesRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("momentum_user_prefs", Context.MODE_PRIVATE)

    init {
        val launchVersion = prefs.getInt("fresh_launch_version", 0)
        if (launchVersion < 1) {
            prefs.edit()
                .clear()
                .putInt("fresh_launch_version", 1)
                .putBoolean(KEY_ONBOARDING_DONE, false)
                .apply()
        }
    }

    private val _userProfile = MutableStateFlow(loadProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private fun loadProfile(): UserProfile {
        val freezeDatesRaw = prefs.getStringSet(KEY_FREEZE_DATES, emptySet()) ?: emptySet()
        val restDaysRaw = prefs.getStringSet(KEY_REST_DAYS, setOf("1")) ?: setOf("1")
        val restDaysInt = restDaysRaw.mapNotNull { it.toIntOrNull() }.toSet()

        return UserProfile(
            name = prefs.getString(KEY_NAME, "New Challenger") ?: "New Challenger",
            handle = prefs.getString(KEY_HANDLE, "@challenger") ?: "@challenger",
            avatarEmoji = prefs.getString(KEY_AVATAR, "🌱") ?: "🌱",
            region = prefs.getString(KEY_REGION, "North America") ?: "North America",
            focusTrack = prefs.getString(KEY_FOCUS_TRACK, "Habit Builder") ?: "Habit Builder",
            dailyTargetHabits = prefs.getInt(KEY_DAILY_TARGET, 3),
            soundEnabled = prefs.getBoolean(KEY_SOUND, true),
            hapticsEnabled = prefs.getBoolean(KEY_HAPTICS, true),
            isOnboardingCompleted = prefs.getBoolean(KEY_ONBOARDING_DONE, false),
            streakFreezesAvailable = prefs.getInt(KEY_FREEZES_AVAILABLE, 2),
            streakFreezeDates = freezeDatesRaw,
            remindersEnabled = prefs.getBoolean(KEY_REMINDERS_ENABLED, true),
            morningReminderTime = prefs.getString(KEY_REMINDER_MORNING, "08:00") ?: "08:00",
            eveningReminderTime = prefs.getString(KEY_REMINDER_EVENING, "20:00") ?: "20:00",
            restDaysEnabled = prefs.getBoolean(KEY_REST_DAYS_ENABLED, false),
            designatedRestDays = if (restDaysInt.isEmpty()) setOf(Calendar.SUNDAY) else restDaysInt
        )
    }

    fun setOnboardingCompleted(
        name: String,
        handle: String,
        avatarEmoji: String,
        focusTrack: String,
        dailyTarget: Int,
        soundEnabled: Boolean,
        hapticsEnabled: Boolean,
        region: String = "North America"
    ) {
        prefs.edit()
            .putString(KEY_NAME, name.ifBlank { "New Challenger" })
            .putString(KEY_HANDLE, if (handle.startsWith("@")) handle else "@$handle".ifBlank { "@challenger" })
            .putString(KEY_AVATAR, avatarEmoji.ifBlank { "🌱" })
            .putString(KEY_REGION, region.ifBlank { "North America" })
            .putString(KEY_FOCUS_TRACK, focusTrack)
            .putInt(KEY_DAILY_TARGET, dailyTarget)
            .putBoolean(KEY_SOUND, soundEnabled)
            .putBoolean(KEY_HAPTICS, hapticsEnabled)
            .putBoolean(KEY_ONBOARDING_DONE, true)
            .putInt(KEY_FREEZES_AVAILABLE, 2)
            .putBoolean(KEY_REMINDERS_ENABLED, true)
            .apply()

        _userProfile.value = loadProfile()
    }

    fun updateProfile(
        name: String,
        handle: String,
        avatarEmoji: String,
        dailyTarget: Int,
        region: String = _userProfile.value.region
    ) {
        prefs.edit()
            .putString(KEY_NAME, name.ifBlank { "New Challenger" })
            .putString(KEY_HANDLE, if (handle.startsWith("@")) handle else "@$handle".ifBlank { "@challenger" })
            .putString(KEY_AVATAR, avatarEmoji.ifBlank { "🌱" })
            .putString(KEY_REGION, region.ifBlank { "North America" })
            .putInt(KEY_DAILY_TARGET, dailyTarget)
            .apply()

        _userProfile.value = loadProfile()
    }

    fun useStreakFreeze(dateString: String): Boolean {
        val current = _userProfile.value
        if (current.streakFreezeDates.contains(dateString)) {
            // Already frozen
            return true
        }
        if (current.streakFreezesAvailable <= 0) {
            return false
        }
        val updatedDates = current.streakFreezeDates + dateString
        val updatedCount = (current.streakFreezesAvailable - 1).coerceAtLeast(0)

        prefs.edit()
            .putStringSet(KEY_FREEZE_DATES, updatedDates)
            .putInt(KEY_FREEZES_AVAILABLE, updatedCount)
            .apply()

        _userProfile.value = loadProfile()
        return true
    }

    fun addStreakFreezes(amount: Int) {
        val current = _userProfile.value
        val updated = current.streakFreezesAvailable + amount
        prefs.edit().putInt(KEY_FREEZES_AVAILABLE, updated).apply()
        _userProfile.value = loadProfile()
    }

    fun setRemindersEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_REMINDERS_ENABLED, enabled).apply()
        _userProfile.value = _userProfile.value.copy(remindersEnabled = enabled)
    }

    fun setReminderTimes(morning: String, evening: String) {
        prefs.edit()
            .putString(KEY_REMINDER_MORNING, morning)
            .putString(KEY_REMINDER_EVENING, evening)
            .apply()
        _userProfile.value = loadProfile()
    }

    fun setRestDaysConfig(enabled: Boolean, restDays: Set<Int>) {
        val stringSet = restDays.map { it.toString() }.toSet()
        prefs.edit()
            .putBoolean(KEY_REST_DAYS_ENABLED, enabled)
            .putStringSet(KEY_REST_DAYS, stringSet)
            .apply()
        _userProfile.value = loadProfile()
    }

    fun setRegion(region: String) {
        prefs.edit().putString(KEY_REGION, region).apply()
        _userProfile.value = _userProfile.value.copy(region = region)
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND, enabled).apply()
        _userProfile.value = _userProfile.value.copy(soundEnabled = enabled)
    }

    fun setHapticsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTICS, enabled).apply()
        _userProfile.value = _userProfile.value.copy(hapticsEnabled = enabled)
    }

    fun resetOnboarding() {
        prefs.edit()
            .putBoolean(KEY_ONBOARDING_DONE, false)
            .apply()
        _userProfile.value = loadProfile()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
        _userProfile.value = loadProfile()
    }

    companion object {
        private const val KEY_NAME = "user_name"
        private const val KEY_HANDLE = "user_handle"
        private const val KEY_AVATAR = "user_avatar"
        private const val KEY_REGION = "user_region"
        private const val KEY_FOCUS_TRACK = "user_focus_track"
        private const val KEY_DAILY_TARGET = "user_daily_target"
        private const val KEY_SOUND = "sound_enabled"
        private const val KEY_HAPTICS = "haptics_enabled"
        private const val KEY_ONBOARDING_DONE = "onboarding_completed"
        private const val KEY_FREEZES_AVAILABLE = "streak_freezes_available"
        private const val KEY_FREEZE_DATES = "streak_freeze_dates"
        private const val KEY_REMINDERS_ENABLED = "reminders_enabled"
        private const val KEY_REMINDER_MORNING = "reminder_morning"
        private const val KEY_REMINDER_EVENING = "reminder_evening"
        private const val KEY_REST_DAYS_ENABLED = "rest_days_enabled"
        private const val KEY_REST_DAYS = "designated_rest_days"
    }
}
