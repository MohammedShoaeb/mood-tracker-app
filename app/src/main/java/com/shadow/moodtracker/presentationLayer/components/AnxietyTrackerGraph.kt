package com.shadow.moodtracker.presentationLayer.components

import android.annotation.SuppressLint
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.DefaultStrokeLineWidth
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.toColorInt
import com.shadow.moodtracker.data.repository.AnxietyTrackerRecord

@SuppressLint("SuspiciousIndentation")
@Composable
fun AnxietyTrackerGraph(
    daysInMonth: Int,
    selectedDay: Int,
    firstDayOfMonthIndex: Int,
    onDayClicked: (Int) -> Unit,
    onDaySelected: (Int) -> Unit = {},
    records: List<AnxietyTrackerRecord> =emptyList(),
    column: Int = 7,
    row: Int = 5,
    padding: Float = 4f
) {
    val materialTheme = MaterialTheme.colorScheme
    var canvasSize by remember { mutableStateOf(Offset(0f, 0f)) }
    val HEADER_ROWS = 1

//    val viewModel.totalDaysInMonth



        /////////////////////////////////////////////////////
        Canvas(modifier = Modifier
            .aspectRatio(4/3f)
            .onGloballyPositioned { coordinates ->
                canvasSize = Offset(
                    coordinates.size.width.toFloat()-padding,
                    coordinates.size.height.toFloat()-padding
                )
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val cellWidth = canvasSize.x / column
                    val cellHeight = canvasSize.y / (row + HEADER_ROWS)
                    val colIndex = (offset.x / cellWidth).toInt()
                    val rowIndex = (offset.y / cellHeight).toInt()

                    // Ignore taps in the header row
                    if (rowIndex < HEADER_ROWS) return@detectTapGestures

                    val dayIndex = (rowIndex - HEADER_ROWS) * column + colIndex
                    if (dayIndex in 0 until daysInMonth) {
                        val day = dayIndex + 1
                        onDayClicked(day)
                        onDaySelected(day)

                    }
                }
            }
        ) {
            var day = 1
            val canvasHeight = size.height
            val canvasWidth = size.width
            val verticalSpace = canvasHeight / (row + 1)
            val horizontalSpace = canvasWidth / column
            val roundRectSize = Size(canvasWidth, canvasHeight)
            val cornerRadius = CornerRadius(10f, 10f)
            val textPaint = Paint().apply {
                color = materialTheme.onBackground.toArgb()
                textSize = 40f
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) // Make text bold
            }


            drawRoundRect(
                color = materialTheme.surface,
                size = if (daysInMonth <= 28) {
                    Size(roundRectSize.width, roundRectSize.height - verticalSpace)
                } else {
                    roundRectSize
                },
                topLeft = Offset(0f, 0f),
                cornerRadius = cornerRadius
            )

            drawRoundRect(
                color = materialTheme.outlineVariant,
                style = Stroke(DefaultStrokeLineWidth),
                size = if (daysInMonth <= 28) {
                    Size(roundRectSize.width, roundRectSize.height - verticalSpace)
                } else {
                    roundRectSize
                },
                topLeft = Offset(0f, 0f),
                cornerRadius = cornerRadius,

                )

            val allDays = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            val firstDayIndex = firstDayOfMonthIndex
            val rotatedHeaders = allDays.drop(firstDayIndex) + allDays.take(firstDayIndex)
            repeat(column) { j ->
                val textX = horizontalSpace * j + horizontalSpace / 2
                val textY = verticalSpace / 2

                drawContext.canvas.nativeCanvas.drawText(
                    rotatedHeaders[j],
                    textX,
                    textY,
                    textPaint
                )
            }

            val totalCells = row * column
            val paddedHorizontalSpace = horizontalSpace - 2 * padding
            val paddedVerticalSpace = verticalSpace - 2 * padding

            repeat(totalCells) { index ->
                if (day > daysInMonth) return@repeat

                val i = index / column + 1
                val j = index % column

                val centerX = horizontalSpace * j + horizontalSpace / 2
                val centerY = verticalSpace * i + verticalSpace / 2

                val result= records.find{it.day==((index+1).toString()) }
                drawRoundRect(
                    color = (if(result==null) materialTheme.surfaceContainerHigh else Color(result.anxietyLevel.toColorInt())),
                    topLeft = Offset(
                        x = horizontalSpace * j + padding,
                        y = verticalSpace * i + padding
                    ),

                    size = Size(paddedHorizontalSpace, paddedVerticalSpace),
                    cornerRadius = CornerRadius(x = 20f, y = 20f)
                )
                textPaint.color = if (result != null) android.graphics.Color.BLACK else materialTheme.onBackground.toArgb()

                // Draw the day number
                drawContext.canvas.nativeCanvas.drawText(
                    day.toString(),
                    centerX,
                    centerY + 10f,
                    textPaint
                )

                day++
            }
/// Canvas ends here
        }








}

@Preview(name = "Pixel 5 - Medium", device = "id:pixel_5", group = "Portrait Mode")
@Composable
fun AnxietyTrackerGraphPrev() {
    MaterialTheme {
        // RateMyDayGraph()
    }
}