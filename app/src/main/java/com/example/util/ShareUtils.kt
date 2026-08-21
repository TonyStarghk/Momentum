package com.example.util

import android.content.Context
import android.content.Intent
import com.example.data.model.AnalyticsSummary
import com.example.data.model.HabitWithWeeklyStatus

object ShareUtils {

    fun generateShareText(
        streakDays: Int,
        totalCompletions: Int,
        completionRate: Int,
        topHabit: String,
        perfectDays: Int
    ): String {
        return """
            ⚡ MOMENTUM HABIT REPORT ⚡
            
            🔥 Current Streak: $streakDays Days
            📊 Consistency Rate: $completionRate%
            🎯 Total Check-ins: $totalCompletions
            ⭐ Perfect Days: $perfectDays
            🏆 Top Habit: $topHabit
            
            Building atomic habits every day on Momentum! 🚀
            Track with me: ${InviteUtils.APP_WEB_BASE_URL}
            #MomentumHabits #AtomicHabits #Consistency
        """.trimIndent()
    }

    fun shareProgressIntent(
        context: Context,
        streakDays: Int,
        totalCompletions: Int,
        completionRate: Int,
        topHabit: String,
        perfectDays: Int
    ) {
        val shareBody = generateShareText(
            streakDays = streakDays,
            totalCompletions = totalCompletions,
            completionRate = completionRate,
            topHabit = topHabit,
            perfectDays = perfectDays
        )

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareBody)
            putExtra(Intent.EXTRA_TITLE, "My Habit Consistency Streak")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Share your Habit Momentum")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }
}
