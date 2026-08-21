package com.example.data.service

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.HabitWithWeeklyStatus
import com.example.data.model.TaskAiAdvice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiCoachService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateAdviceForHabit(habitStatus: HabitWithWeeklyStatus): TaskAiAdvice = withContext(Dispatchers.IO) {
        val habit = habitStatus.habit
        val apiKey = BuildConfig.GEMINI_API_KEY
        val completionRate = if (habitStatus.weeklyHistory.isNotEmpty()) {
            val doneCount = habitStatus.weeklyHistory.count { it }
            (doneCount.toFloat() / habitStatus.weeklyHistory.size * 100).toInt()
        } else 60

        // If no API key or placeholder, provide tailored smart behavioral advice
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateFallbackAdvice(habitStatus, completionRate)
        }

        try {
            val prompt = """
                You are an elite behavioral scientist and habit optimization coach based on Atomic Habits by James Clear and BJ Fogg's Tiny Habits.
                Analyze the following user habit and performance metrics:
                - Habit Name: "${habit.title}"
                - Description / Motivation: "${habit.description.ifEmpty { "Not specified" }}"
                - Category: ${habit.category}
                - Preferred Time of Day: ${habit.timeOfDay.label}
                - Current Streak: ${habitStatus.currentStreak} days (Best: ${habitStatus.bestStreak} days)
                - 7-Day Completion Rate: $completionRate%
                - Total Lifetime Check-ins: ${habitStatus.totalCompletions}

                Generate customized, hyper-practical advice to maximize consistency for THIS SPECIFIC TASK.
                Return ONLY a valid JSON object matching this exact schema:
                {
                    "habitStackCue": "When/Then implementation intention (e.g. Immediately after I [existing routine], I will [habit] for [X min])",
                    "twoMinuteRule": "The ultra-low barrier 2-minute version for low-energy/busy days",
                    "frictionReducer": "Physical or digital environment design change to eliminate resistance before starting",
                    "psychologyInsight": "1 concise sentence explaining the psychological reason why this routine will stick",
                    "recommendedTimeSlot": "Optimal time window or context trigger (e.g. 7:15 AM - Right after brewing coffee)",
                    "keyObstacle": "The most common psychological trap for this specific habit and how to bypass it"
                }
            """.trimIndent()

            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    val part = JSONObject().apply { put("text", prompt) }
                    val contentObj = JSONObject().apply {
                        put("parts", JSONArray().apply { put(part) })
                    }
                    put(contentObj)
                }
                put("contents", contents)
                val genConfig = JSONObject().apply {
                    put("temperature", 0.7)
                    put("topP", 0.95)
                }
                put("generationConfig", genConfig)
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.w("GeminiCoachService", "API error: ${response.code} $responseBody")
                return@withContext generateFallbackAdvice(habitStatus, completionRate)
            }

            val rootJson = JSONObject(responseBody)
            val candidates = rootJson.optJSONArray("candidates")
            val candidate = candidates?.optJSONObject(0)
            val content = candidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text") ?: ""

            // Clean markdown code blocks if wrapped in ```json ... ```
            val cleanedJsonStr = text
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val parsedJson = JSONObject(cleanedJsonStr)

            TaskAiAdvice(
                habitId = habit.id,
                habitTitle = habit.title,
                habitCategory = habit.category,
                consistencyScore = completionRate,
                habitStackCue = parsedJson.optString("habitStackCue", "After my ${habit.timeOfDay.label.lowercase()} routine, I will start ${habit.title}."),
                twoMinuteRule = parsedJson.optString("twoMinuteRule", "Commit to just 2 minutes of ${habit.title} when feeling resistant."),
                frictionReducer = parsedJson.optString("frictionReducer", "Prepare all required materials the evening prior in your primary line of sight."),
                psychologyInsight = parsedJson.optString("psychologyInsight", "Environmental cues create automatic neural loops that eliminate decision fatigue."),
                recommendedTimeSlot = parsedJson.optString("recommendedTimeSlot", "${habit.timeOfDay.label} window"),
                keyObstacle = parsedJson.optString("keyObstacle", "Perfectionism: Remember showing up for 1 minute preserves the streak identity.")
            )
        } catch (e: Exception) {
            Log.e("GeminiCoachService", "Error calling Gemini API: ${e.message}", e)
            generateFallbackAdvice(habitStatus, completionRate)
        }
    }

    private fun generateFallbackAdvice(habitStatus: HabitWithWeeklyStatus, completionRate: Int): TaskAiAdvice {
        val habit = habitStatus.habit
        val titleLower = habit.title.lowercase()

        val cue = when {
            titleLower.contains("read") || titleLower.contains("book") ->
                "Habit Stack: Immediately after sitting in bed with your night water, read 3 pages before unlocking your phone."
            titleLower.contains("water") || titleLower.contains("hydrat") ->
                "Habit Stack: After turning off your morning alarm, drink a full 500ml glass placed on your nightstand."
            titleLower.contains("meditat") || titleLower.contains("breath") ->
                "Habit Stack: After stepping out of the morning shower, sit upright on your cushion for 3 deep diaphragmatic breaths."
            titleLower.contains("gym") || titleLower.contains("workout") || titleLower.contains("pushup") ->
                "Habit Stack: After brushing your teeth, drop down and complete the first set before entering the kitchen."
            titleLower.contains("code") || titleLower.contains("study") || titleLower.contains("write") ->
                "Habit Stack: After opening your laptop and taking the first sip of coffee, open only the target workspace in Fullscreen."
            else ->
                "Habit Stack: Immediately after your ${habit.timeOfDay.label.lowercase()} routine, trigger ${habit.title} for 5 uninterrupted minutes."
        }

        val micro = when {
            titleLower.contains("read") -> "Micro-Step: Read just 1 single page or 2 paragraphs on low-energy days."
            titleLower.contains("workout") || titleLower.contains("pushup") -> "Micro-Step: Just do 5 pushups or put on your workout sneakers."
            titleLower.contains("code") || titleLower.contains("study") -> "Micro-Step: Write 1 line of clean code or review 1 flashcard."
            titleLower.contains("meditat") -> "Micro-Step: Close your eyes for 60 seconds and count 5 calm breaths."
            else -> "Micro-Step: Perform 90 seconds of the easiest starting movement to seal the habit vote."
        }

        val friction = when {
            habit.category == "Fitness" -> "Friction Reducer: Set workout apparel and shoes directly in front of the door the night before."
            habit.category == "Mind" || habit.category == "Learning" -> "Friction Reducer: Keep digital distractions blocked with Do Not Disturb scheduled 10 minutes prior."
            habit.category == "Health" -> "Friction Reducer: Fill a 1L water carafe and stage any supplements right beside your workspace."
            else -> "Friction Reducer: Clear your physical desk surface so the barrier to entry is zero."
        }

        val insight = when {
            habitStatus.currentStreak >= 7 -> "Momentum Law: With a ${habitStatus.currentStreak}-day streak, your baseline friction is 40% lower—protect this identity."
            habitStatus.currentStreak in 3..6 -> "Consolidation Phase: Days 4-10 are where resistance peaks; lower the difficulty to keep the streak alive."
            else -> "Starting Kinetic: Consistency beats intensity. Repeating the cue creates myelination in the basal ganglia."
        }

        val obstacle = when {
            habit.timeOfDay.label == "Evening" -> "Tiredness Trap: If fatigued, don't skip—switch immediately to the 2-minute micro-version."
            habit.timeOfDay.label == "Morning" -> "Snooze Friction: Remove phones from arms reach to ensure prompt morning execution."
            else -> "Decision Fatigue: Automate the exact start time so you never debate whether to do it."
        }

        return TaskAiAdvice(
            habitId = habit.id,
            habitTitle = habit.title,
            habitCategory = habit.category,
            consistencyScore = completionRate,
            habitStackCue = cue,
            twoMinuteRule = micro,
            frictionReducer = friction,
            psychologyInsight = insight,
            recommendedTimeSlot = "${habit.timeOfDay.label} (${if (habit.timeOfDay.label == "Morning") "7:00 AM - 8:30 AM" else if (habit.timeOfDay.label == "Evening") "9:00 PM - 10:00 PM" else "1:00 PM - 2:30 PM"})",
            keyObstacle = obstacle
        )
    }
}
