package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector

object HabitIcons {
    val iconMap = mapOf(
        "water" to (Icons.Filled.WaterDrop to "Hydration"),
        "code" to (Icons.Filled.Code to "Code / Dev"),
        "meditation" to (Icons.Filled.SelfImprovement to "Mindfulness"),
        "runner" to (Icons.Filled.DirectionsRun to "Running / Walk"),
        "dumbbell" to (Icons.Filled.FitnessCenter to "Fitness"),
        "book" to (Icons.Filled.MenuBook to "Reading"),
        "sparkles" to (Icons.Filled.AutoAwesome to "Habit"),
        "sun" to (Icons.Filled.WbSunny to "Morning / Sun"),
        "moon" to (Icons.Filled.Bedtime to "Night / Sleep"),
        "coffee" to (Icons.Filled.LocalCafe to "Coffee / Routine"),
        "heart" to (Icons.Filled.Favorite to "Health / Heart"),
        "smile" to (Icons.Filled.SentimentSatisfied to "Mood / Gratitude")
    )

    fun getIcon(name: String): ImageVector {
        return iconMap[name]?.first ?: Icons.Filled.AutoAwesome
    }

    val availableIcons = iconMap.keys.toList()

    val availableColors = listOf(
        "#00F0FF", // Neon Cyan
        "#10B981", // Emerald
        "#F59E0B", // Amber
        "#EF4444", // Rose / Red
        "#8B5CF6", // Purple
        "#3B82F6", // Electric Blue
        "#EC4899", // Pink
        "#14B8A6"  // Teal
    )

    val availableCategories = listOf(
        "Health",
        "Fitness",
        "Mind",
        "Productivity",
        "Learning",
        "Creativity",
        "Lifestyle"
    )
}
