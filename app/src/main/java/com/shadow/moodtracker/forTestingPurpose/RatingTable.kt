package com.shadow.moodtracker.forTestingPurpose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun StarRatingGrid(ratings: List<List<Int>>) {
    Column(modifier = Modifier.padding(8.dp)) {
        // Iterate over rows
        ratings.forEach { row ->
            Row {
                // Iterate over cells in a row
                row.forEach { rating ->
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .border(1.dp, Color.Black)
                            .background(getColorForRating(rating))
                            .padding(2.dp)
                    )
                }
            }
        }
        HorizontalDivider()
        HorizontalDivider()


    }
}

// Function to map ratings to colors
fun getColorForRating(rating: Int): Color {
    return when (rating) {
        5 -> Color.Blue
        4 -> Color.Cyan
        3 -> Color.Magenta
        2 -> Color(0xFFFFA500) // Orange
        1 -> Color.Yellow
        else -> Color.Transparent
    }
}

@Preview
@Composable
fun PreviewStarRatingGrid() {
    // Example grid data
    val ratings = listOf(
        listOf(5, 4, 3, 5, 4),
        listOf(3, 2, 4, 5, 1),
        listOf(4, 5, 5, 3, 2),
        listOf(1, 3, 4, 2, 5)
    )
    StarRatingGrid(ratings = ratings)
}