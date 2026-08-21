package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.example.data.db.AppDatabase
import com.example.data.model.HabitCompletion
import com.example.util.DateUtils
import com.example.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HabitNotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == ACTION_QUICK_CHECK_IN) {
            val habitId = intent.getLongExtra(EXTRA_HABIT_ID, 1L)
            val todayStr = DateUtils.getTodayString()

            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val existing = db.habitDao().getCompletion(habitId, todayStr)
                    if (existing == null) {
                        db.habitDao().insertCompletion(
                            HabitCompletion(
                                habitId = habitId,
                                dateString = todayStr,
                                completedAt = System.currentTimeMillis(),
                                count = 1
                            )
                        )
                    }

                    // Dismiss the reminder notification
                    NotificationManagerCompat.from(context).cancel(2001)

                    // Post confirmation notification
                    NotificationHelper.showInstantHabitReminder(
                        context = context,
                        title = "Habit Completed! ⚡ (+50 XP)",
                        message = "Great consistency! Your streak is preserved and XP increased.",
                        habitId = habitId
                    )
                } catch (_: Exception) {
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }

    companion object {
        const val ACTION_QUICK_CHECK_IN = "com.example.momentum.ACTION_QUICK_CHECK_IN"
        const val EXTRA_HABIT_ID = "extra_habit_id"
    }
}
