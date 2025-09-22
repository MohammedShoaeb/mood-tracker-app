package com.shadow.moodtracker.data.repository

data class Series(
    val name: String = "",
    val dateStarted: String = "",
    val dateFinished: String = "",
    val seasons: List<Season> = listOf()
)

data class Season(
    val seasonNumber: Int = 1,
    val episodes: List<Episode> = listOf()
)

data class Episode(
    val watched: Boolean = false,
    val note: String = ""
)
