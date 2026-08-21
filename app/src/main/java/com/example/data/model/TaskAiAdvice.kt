package com.example.data.model

data class TaskAiAdvice(
    val habitId: Long,
    val habitTitle: String,
    val habitCategory: String,
    val consistencyScore: Int,
    val habitStackCue: String,
    val twoMinuteRule: String,
    val frictionReducer: String,
    val psychologyInsight: String,
    val recommendedTimeSlot: String,
    val keyObstacle: String,
    val generatedAt: Long = System.currentTimeMillis()
)
