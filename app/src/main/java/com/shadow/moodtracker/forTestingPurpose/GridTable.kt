package com.shadow.moodtracker.forTestingPurpose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shadow.moodtracker.ui.theme.MoodTrackerTheme

@Composable
fun GridTableWithClick() {
    val colorScheme = MaterialTheme.colorScheme
    var clickedBox by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    val verticalLines = 12
    val horizontalLines = 12

    Box(modifier = Modifier.fillMaxSize().background(colorScheme.surface)) {

        // Canvas for drawing the grid
        Canvas(modifier = Modifier.padding(8.dp).aspectRatio(3/2f).fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset -> }
            }) {
// canvas here:

            val barWidthPx = 0.5.dp.toPx()

            // Draw the grid
            drawRoundRect(
                color = colorScheme.outline,
                style = Stroke(barWidthPx),
                size = size
            )

            // Draw vertical lines (grid columns)
            val verticalLines = 12
            val verticalSize = size.width / (verticalLines + 1)
            repeat(verticalLines) { index ->
                val startX = verticalSize * (index + 1)
                drawLine(colorScheme.outline, start = Offset(startX, 0f), end = Offset(startX, size.height), strokeWidth = barWidthPx)
            }

            // Draw horizontal lines (grid rows)
            val horizontalLines = 12
            val horizontalSize = size.height / (horizontalLines + 1)
            repeat(horizontalLines) { index ->
                val startY = horizontalSize * (index + 1)
                drawLine(colorScheme.outline, start = Offset(0f, startY), end = Offset(size.width, startY), strokeWidth = barWidthPx)
            }
        }


        // Display clicked box information (for debugging purposes)
        clickedBox?.let {
            Text(
                text = "Clicked Box: Row ${it.second + 1}, Column ${it.first + 1}",
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}



@Preview(name = "Nexus 5 - Small", device = "id:Nexus 5", group = "Portrait Mode")
@Preview(name = "Pixel 5 - Medium", device = "id:pixel_5", group = "Portrait Mode")
@Preview(name = "Pixel 7 Pro - Large", device = "id:pixel_5", group = "Portrait Mode")
@Preview(
    name = "Pixel Fold - Foldable",
    device = "spec:width=1800dp,height=2208dp,dpi=420",
    group = "Portrait Mode"
)
@Preview(name = "Nexus 7 - Small Tablet", device = "id:Nexus 7", group = "Portrait Mode")
@Preview(
    name = "Pixel Tablet - Large Tablet",
    device = "spec:width=2560dp,height=1600dp,dpi=274",
    group = "Portrait Mode"
)@Composable
fun GridTableWithClickPreview() {
    MoodTrackerTheme {
        GridTableWithClick()
    }
}


