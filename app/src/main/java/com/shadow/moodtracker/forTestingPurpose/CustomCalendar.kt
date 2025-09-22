package com.shadow.moodtracker.forTestingPurpose


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class DayRating(val day: Int, val month: Int, val rating: Int)

@Composable
fun RateMyDayGrid(dayRatings: List<DayRating>) {
    val ratingColors = mapOf(
        5 to Color(0xFF1E88E5), // Dark Blue
        4 to Color(0xFF81D4FA), // Light Blue
        3 to Color(0xFFF06292), // Pink
        2 to Color(0xFFFF7043), // Orange
        1 to Color(0xFFFFD54F)  // Yellow
    )

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            (1..12).forEach { month ->
                Text(
                    text = monthAbbreviation(month),
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 4.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
        }

        // Grid with Day Labels
        Row {
            // Day Numbers (Left column)
            Column(
                modifier = Modifier.padding(end = 4.dp)
            ) {
                (1..31).forEach { day ->
                    Text(
                        text = "$day",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .height(20.dp)
                            .padding(end = 4.dp)
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(12),
                modifier = Modifier
                    .height(620.dp)
                    .fillMaxWidth()
            ) {
                items(31 * 12) { index ->
                    val day = (index % 31) + 1
                    val month = (index / 31) + 1
                    val rating = dayRatings.firstOrNull { it.day == day && it.month == month }?.rating

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(1.dp)
                            .drawBehind {
                                drawRect(color = Color(0xFF797C76), style = Stroke(0.1f)) // Add border
                            }
                            .background(ratingColors[rating] ?: Color.LightGray, RoundedCornerShape(2.dp))
                    )
                }
            }
        }
    }
}

fun monthAbbreviation(month: Int): String {
    return listOf(
        "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
        "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
    )[month - 1]
}





@Preview
@Composable
fun ExampleRateMyDay() {
    val dayRatings = mutableListOf<DayRating>().apply {
        for (month in 1..12) {
            for (day in 1..31) {
                add(DayRating(day, month, (1..5).random())) // Random rating
            }
        }
    }
    RateMyDayGrid(dayRatings)
}



