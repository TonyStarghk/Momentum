package com.example.util

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.receiver.HabitNotificationActionReceiver
import com.example.receiver.HabitReminderReceiver
import java.util.Calendar

object NotificationHelper {

    const val CHANNEL_HABIT_REMINDERS = "momentum_habit_reminders"
    const val CHANNEL_STREAK_SHIELD = "momentum_streak_shield"

    const val REQUEST_CODE_MORNING = 1001
    const val REQUEST_CODE_EVENING = 1002

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val reminderChannel = NotificationChannel(
                CHANNEL_HABIT_REMINDERS,
                "Habit Consistency Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily smart prompts to complete habits and maintain your streak"
                enableLights(true)
                enableVibration(true)
            }

            val shieldChannel = NotificationChannel(
                CHANNEL_STREAK_SHIELD,
                "Streak Shield & XP Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical alerts before streak breaks or negative XP penalties"
                enableLights(true)
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(reminderChannel)
            notificationManager.createNotificationChannel(shieldChannel)
        }
    }

    fun showInstantHabitReminder(
        context: Context,
        title: String = "Momentum Check-In ⚡",
        message: String = "Keep your streak alive! Complete your scheduled habits to earn +50 XP and tier up.",
        habitId: Long = 1L
    ) {
        createNotificationChannels(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action Intent for Quick Check-In
        val checkInIntent = Intent(context, HabitNotificationActionReceiver::class.java).apply {
            action = HabitNotificationActionReceiver.ACTION_QUICK_CHECK_IN
            putExtra(HabitNotificationActionReceiver.EXTRA_HABIT_ID, habitId)
        }
        val checkInPendingIntent = PendingIntent.getBroadcast(
            context,
            (System.currentTimeMillis() % 10000).toInt(),
            checkInIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_HABIT_REMINDERS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openAppPendingIntent)
            .addAction(R.drawable.ic_launcher_foreground, "✓ Quick Check-In (+50 XP)", checkInPendingIntent)
            .addAction(R.drawable.ic_launcher_foreground, "Open App", openAppPendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(2001, notification)
        } catch (_: SecurityException) {
            // Graceful fallback if permission not granted
        }
    }

    fun scheduleDailyReminder(
        context: Context,
        hour: Int,
        minute: Int,
        requestCode: Int,
        title: String,
        message: String
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, HabitReminderReceiver::class.java).apply {
            putExtra(HabitReminderReceiver.EXTRA_TITLE, title)
            putExtra(HabitReminderReceiver.EXTRA_MESSAGE, message)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } catch (_: Exception) {
            // Fallback for newer Android battery optimizations
        }
    }

    fun cancelDailyReminder(context: Context, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, HabitReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
