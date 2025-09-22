package com.shadow.moodtracker.model

import androidx.compose.ui.graphics.Color


data class MoodOption(
    val color: Color?,
    val label: String
)
data class MoodStats(
    val happyDaysCount: Int,
    val totalDays: Int,
    val mostFrequentMood: String,
    val moodStability: String
)


data class AnxietyOptions(
    val color: Color?,
    val label: String
)
data class AnxietyStats(
    val mostFrequentFeeling: String,
    val totalDaysTracked: Int,
    val calmDaysCount: Int
)