package com.shadow.moodtracker.utils


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp


fun createDiagonalGradientBrush(
    startColor: Color,
    endColor: Color,
    startOffset: Offset = Offset(0f, 0f),
    endOffset: Offset = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
): Brush {
    return Brush.linearGradient(
        colors = listOf(startColor, endColor),
        start = startOffset,
        end = endOffset
    )
}


fun createHorizontalGradientBrush(
    startColor: Color,
    endColor: Color
): Brush {
    return Brush.linearGradient(
        colors = listOf(startColor, endColor),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, 0f)
    )
}

fun createVerticalGradientBrush(
    startColor: Color,
    endColor: Color
): Brush {
    return Brush.linearGradient(
        colors = listOf(startColor, endColor),
        start = Offset(0f, 0f),
        end = Offset(0f, Float.POSITIVE_INFINITY)
    )
}

@Composable
fun Loading(modifier: Modifier = Modifier){
Column(verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally) {

    CircularProgressIndicator(
        color = MaterialTheme.colorScheme.onSecondary,
        trackColor = MaterialTheme.colorScheme.primary,
        strokeWidth = 3.dp)
    Text(
        text = "Loading",
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelSmall,
    )
}

}
fun Color.toHexString(): String {
    return String.format("#%08X", toArgb())
}

