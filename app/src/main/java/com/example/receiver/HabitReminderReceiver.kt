package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.util.NotificationHelper

class HabitReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val title = intent?.getStringExtra(EXTRA_TITLE) ?: "Momentum Reminder ⚡"
        val message = intent?.getStringExtra(EXTRA_MESSAGE) ?: "Keep your streak active! Don't let your XP drop."

        NotificationHelper.showInstantHabitReminder(
            context = context,
            title = title,
            message = message
        )
    }

    companion object {
        const val EXTRA_TITLE = "extra_reminder_title"
        const val EXTRA_MESSAGE = "extra_reminder_message"
    }
}
