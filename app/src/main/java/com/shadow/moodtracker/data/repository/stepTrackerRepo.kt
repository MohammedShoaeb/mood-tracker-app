package com.shadow.moodtracker.data.repository

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class StepTrackerRecordsWrapper(val records: List<StepTrackerRecord> = emptyList())

data class StepTrackerRecord(
    val day: String="",
    val color: String="",
)

data class StepCountsLegend(
    val range: IntRange,
    val label: String,
    val color: Color?
)
val stepCountLegends = listOf(
    StepCountsLegend(IntRange.EMPTY, "Empty", null)
)+listOf(
    0..3000 to  Color(0xffB5EAD7),
    3001..5000 to Color(0xffE2F0CC),
    5001..7000 to Color(0xffFFDAC0),
    7001..9000 to Color(0xffFEB7B1),
    9001..11000 to Color(0xffFF9AA2),
    11001..13000 to  Color(0xffC7CEEA),
    13001..15000 to  Color(0xffCBB6E4),
    15001..Int.MAX_VALUE to Color(0xffFF606C) ,
).map { (range, color) ->
    val label =
        if (range.last == Int.MAX_VALUE) {
        "${range.first / 1000}k+"
    } else {
        "${range.first / 1000}k - ${range.last / 1000}k"
    }
    StepCountsLegend(range, label, color)
}




@Composable
fun habitColorLegends(): List<Color> {
    return listOf(
        Color(0xffB5EAD7),

        Color(0xffE2F0CC),
        Color(0xffFFDAC0),
        Color(0xffFEB7B1),
        Color(0xffFF9AA2),
        Color(0xffC7CEEA),
        Color(0xffCBB6E4),
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer
    )
}
//Pair(0 to 3000, Color(0xFFBFA6D1)),
//Pair(3001 to 4000, Color(0xFFA6C8E1)),
//Pair(4001 to 5000, Color(0xFFD9B4B4)),
//Pair(6000 to 7000, Color(0xFFC9D6D1)),
//Pair(8000 to 9000, Color(0xFFF1D6A3)),
//Pair(10000 to 12000, Color(0xFFD6E5D3)),
//Pair(13000 to 15000, Color(0xFFF3C6D0)),
//Pair(16000 to Int.MAX_VALUE, Color(0xFFE6B7A3))