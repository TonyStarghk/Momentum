package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R

class MomentumWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            streak: Int = 14,
            tierTitle: String = "🏆 Champion",
            completed: Int = 3,
            total: Int = 5,
            xp: Int = 2450
        ) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val views = RemoteViews(context.packageName, R.layout.widget_momentum).apply {
                setTextViewText(R.id.widget_streak_count, streak.toString())
                setTextViewText(R.id.widget_tier_badge, tierTitle)
                setTextViewText(R.id.widget_progress_text, "$completed / $total Done")
                setTextViewText(R.id.widget_xp_text, "+$xp XP")
                setOnClickPendingIntent(R.id.widget_root, pendingIntent)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateAllWidgets(
            context: Context,
            streak: Int,
            tierTitle: String,
            completed: Int,
            total: Int,
            xp: Int
        ) {
            try {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val thisWidget = ComponentName(context, MomentumWidgetProvider::class.java)
                val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

                for (id in allWidgetIds) {
                    updateAppWidget(context, appWidgetManager, id, streak, tierTitle, completed, total, xp)
                }
            } catch (_: Exception) {
                // Graceful fallback if widget manager unavailable
            }
        }
    }
}
