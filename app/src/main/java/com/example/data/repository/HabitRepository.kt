package com.example.data.repository

import com.example.data.db.HabitDao
import com.example.data.model.Habit
import com.example.data.model.HabitCompletion
import com.example.data.model.TimeOfDay
import com.example.util.DateUtils
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Random

class HabitRepository(private val habitDao: HabitDao) {

    val allHabits: Flow<List<Habit>> = habitDao.getAllActiveHabits()
    val allCompletions: Flow<List<HabitCompletion>> = habitDao.getAllCompletions()

    fun getCompletionsForDate(dateString: String): Flow<List<HabitCompletion>> {
        return habitDao.getCompletionsForDate(dateString)
    }

    suspend fun toggleHabitCompletion(habitId: Long, dateString: String): Boolean {
        val existing = habitDao.getCompletion(habitId, dateString)
        return if (existing != null) {
            habitDao.deleteCompletion(habitId, dateString)
            false
        } else {
            habitDao.insertCompletion(
                HabitCompletion(
                    habitId = habitId,
                    dateString = dateString,
                    completedAt = System.currentTimeMillis(),
                    count = 1
                )
            )
            true
        }
    }

    suspend fun insertHabit(habit: Habit): Long {
        return habitDao.insertHabit(habit)
    }

    suspend fun updateHabit(habit: Habit) {
        habitDao.updateHabit(habit)
    }

    suspend fun deleteHabit(habitId: Long) {
        habitDao.deleteHabit(habitId)
        habitDao.deleteAllCompletionsForHabit(habitId)
    }

    suspend fun clearAllData() {
        habitDao.deleteAllCompletions()
        habitDao.deleteAllHabits()
    }

    suspend fun setupStarterHabits(habits: List<Habit>, generateInitialHistory: Boolean = false) {
        clearAllData()
        if (habits.isNotEmpty()) {
            habitDao.insertHabits(habits)
            if (generateInitialHistory) {
                generateSampleHistory(habits)
            }
        }
    }

    private suspend fun generateSampleHistory(habits: List<Habit>) {
        val completions = mutableListOf<HabitCompletion>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val random = Random(42)

        for (daysAgo in 14 downTo 1) {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
            val dateStr = dateFormat.format(cal.time)

            for (habit in habits) {
                val chance = 0.85
                if (random.nextDouble() < chance) {
                    completions.add(
                        HabitCompletion(
                            habitId = habit.id,
                            dateString = dateStr,
                            completedAt = cal.timeInMillis,
                            count = 1
                        )
                    )
                }
            }
        }
        if (completions.isNotEmpty()) {
            habitDao.insertCompletions(completions)
        }
    }

    suspend fun preloadSampleDataIfNeeded() {
        // No automatic fake data preloaded. User starts fresh in Setup Onboarding.
    }
}
