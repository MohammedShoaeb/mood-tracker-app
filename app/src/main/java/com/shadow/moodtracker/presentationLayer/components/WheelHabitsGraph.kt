package com.shadow.moodtracker.presentationLayer.components

import android.annotation.SuppressLint
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.core.graphics.withSave
import com.shadow.moodtracker.data.repository.HabitProgress
import kotlin.math.cos
import kotlin.math.sin

//@Composable
//fun WheelHabits(
//    modifier: Modifier = Modifier,
//    habits: List<String> = listOf(
//        "Practice Soccer",
//        "Go to the Gym",
//        "Sleep 8hrs+",
//        "Eat healthy",
//        "Treadmill",
//        "Make Bed",
//        "Stretch",
//        "Yoga"
//    ),
//    habitColors: List<Color> = listOf(
//        Color(0xffAC592F), Color(0xFFE89664), Color(0xFFF5B776), Color(0xFFF8BE44),
//        Color(0xFFF2A20E), Color(0xFFE2860B), Color(0xFFEA8247), Color(0xFFB84E1A)
//    ),
//    days: Int = 31,
//    habitData: List<List<Boolean>> = List(8) { List(31) { false } }
//) {
//
//
//    Box(
//        modifier = Modifier
//            .aspectRatio(1f)
//            .wrapContentSize()
//            .background(Color.White),
//        contentAlignment = Alignment.Center
//    ) {
//
//        Image(
//            modifier = Modifier
//                .size(86.dp)
//                .aspectRatio(1f),
//            painter = painterResource(id = R.drawable.paw), contentDescription = null
//        )
//
//        Canvas(
//            modifier = modifier
//                .padding(10.dp)
//                .aspectRatio(1f)
//
//        ) {
//            with(this) {
//                val center = Offset(size.width / 2, size.height / 2)
//                val innerRadius = size.minDimension * 0.18f
//                val ringWidth = size.minDimension * 0.035f
//                val angleStep = 310f / days
//                val textRadius = innerRadius + ringWidth * habits.size
//
//                for (habitIndex in habits.indices) {
//                    for (dayIndex in 0 until days) {
//                        val startAngle = dayIndex * angleStep - 90f
//                        val filled =
//                            habitData[habitIndex][dayIndex]
//                        val ringRadius =
//                            innerRadius + habitIndex * ringWidth+0.4f
//
//                        val ringColor = if (filled) habitColors[habitIndex] else Color(0xFFF5F5F6)
//
//                        drawArc(
//                            color = Color.Black,
//                            startAngle = startAngle-0.2f,
//                            sweepAngle = if(dayIndex==days-1) angleStep+0.4f else angleStep,
//                            useCenter = false,
//                            topLeft = Offset(center.x - ringRadius, center.y - ringRadius),
//                            size = Size(ringRadius * 2, ringRadius * 2),
//                            style = Stroke(ringWidth + 1, cap = StrokeCap.Butt)
//                        )
//                        drawArc(
//                            color = ringColor,
//                            startAngle = startAngle,
//                            sweepAngle = angleStep,
//                            useCenter = false,
//                            topLeft = Offset(center.x - ringRadius, center.y - ringRadius),
//                            size = Size(ringRadius * 2, ringRadius * 2),
//                            style = Stroke(ringWidth, cap = StrokeCap.Butt)
//                        )
//
//                    }
//                }
//
//                val textPaint = Paint().apply {
//                    color = android.graphics.Color.WHITE
//                    textSize =
//                        size.minDimension * 0.02f
//                    textAlign = Paint.Align.RIGHT
//                    typeface = Typeface.DEFAULT
//                    isUnderlineText = true
//                }
//
//                for (habitIndex in habits.indices) {
//                    val habitAngle = Math.toRadians((-90f).toDouble())
//                    val habitRadius =
//                        innerRadius + habitIndex * ringWidth
//                    val padding = size.minDimension*0.01
//                    val textX =
//                        (center.x + (habitRadius - 30) * cos(habitAngle) - padding).toFloat()
//                    val textAboveArc =
//                        habitRadius - (size.minDimension*0.015f)
//                    val adjustedTextY = (center.y + textAboveArc * sin(habitAngle)).toFloat()
//
//                    val textColor =
//                        if (habitData[habitIndex].contains(true)) habitColors[habitIndex] else materialTheme.onSurfaceVariant
//
//                    textPaint.color = textColor.toArgb()
//
//                    drawContext.canvas.nativeCanvas.apply {
//                        withSave {
//                            translate(textX, adjustedTextY)
//                            drawText(habits[habitIndex], 0f, 0f, textPaint)
//                        }
//                    }
//                }
//
//
//                val textPaintDays = Paint().apply {
//                    color = android.graphics.Color.BLACK
//                    textSize =
//                        size.minDimension * 0.02f
//                    textAlign = Paint.Align.CENTER
//                    typeface = Typeface.DEFAULT_BOLD
//                }
//
//                for (day in 1..days) {
//                    val middleAngle =
//                        Math.toRadians(((day - 0.5f) * angleStep - 90).toDouble())
//                    val textX = (center.x + textRadius * cos(middleAngle)).toFloat()
//                    val textY = (center.y + textRadius * sin(middleAngle)).toFloat()
//                    val rotationAngle = ((day - 0.5f) * angleStep)
//
//                    drawContext.canvas.nativeCanvas.apply {
//                        withSave {
//                            translate(textX, textY)
//                            rotate(rotationAngle)
//                            drawText(day.toString(), 0f, 0f, textPaintDays)
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Preview
//@Composable
//fun PrevWheelHabits() {
//
//    val sampleData = List(8) { List(31) { Math.random() > 0.5 } }
//      WheelHabits(habitData = sampleData)
//    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//    }
//}


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalStdlibApi::class)
@Composable
fun WheelHabitsNew(
    modifier: Modifier = Modifier,
    days: Int,
    data: List<HabitProgress>,
    today: Int
) {
    val getTheHabit = remember(data) { data.sortedBy { it.habit.order } }
    val preparedColors = remember(getTheHabit) {
        getTheHabit.map { it.habit.color.toColorInt() }
    }
    val materialTheme = MaterialTheme.colorScheme
val typo = MaterialTheme.typography
    val textPaint = remember {
        Paint().apply {
            color = android.graphics.Color.WHITE
            textAlign = Paint.Align.RIGHT
            typeface = Typeface.DEFAULT_BOLD
            isUnderlineText = false

        }
    }
    val textPaintDays = remember {
        Paint().apply {
            color = materialTheme.onSurface.toArgb()
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }
    }

    Canvas(
        modifier = modifier
            .padding(10.dp)
            .aspectRatio(1f),
    ) {
        textPaint.textSize = size.minDimension * 0.03f
        textPaintDays.textSize = size.minDimension * 0.03f

        val center = Offset(size.width / 2, size.height / 2)
        val innerRadius = size.minDimension * 0.18f
        val ringWidth = size.minDimension * 0.035f
        val angleStep = 310f / days
        val textRadius = innerRadius + ringWidth * 8
        val totalRings = 8


        val arcPathCache = mutableMapOf<Pair<Int, Int>, Path>()

        fun getArcPath(ringIndex: Int, dayIndex: Int): Path {
            val key = ringIndex to dayIndex
            return arcPathCache.getOrPut(key) {
                val startAngle = dayIndex * angleStep - 90f
                val ringRadius = innerRadius + ringIndex * ringWidth + 0.4f
                Path().apply {
                    arcTo(
                        rect = Rect(
                            center = center,
                            radius = ringRadius
                        ),
                        startAngleDegrees = startAngle,
                        sweepAngleDegrees = angleStep,
                        forceMoveTo = true
                    )
                }
            }
        }

//        if (data.isEmpty() || data.size != totalRings) {


        // Draw habit arcs
        for (habitIndex in getTheHabit.indices) {
            for (dayIndex in 0 until days) {
                val path = getArcPath(habitIndex, dayIndex)
                val currentDay = (dayIndex + 1).toString()
                val isFilled = getTheHabit[habitIndex].daysDone.contains(currentDay)
                val ringColor = if (isFilled)
                    preparedColors[habitIndex]
                else
                    materialTheme.surfaceVariant.toArgb()

                drawPath(
                    path = path,
                    color = if(isFilled) materialTheme.inverseOnSurface else materialTheme.onSurfaceVariant,
                    style = Stroke(ringWidth + 1, cap = StrokeCap.Butt)
                )
                drawPath(
                    path = path,
                    color = Color(ringColor),
                    style = Stroke(ringWidth, cap = StrokeCap.Butt)
                )
                if (dayIndex + 1 == today) {
                    drawPath(
                        path = path,
                        color = materialTheme.primary.copy(0.2f),
                        style = Stroke(ringWidth + 4, cap = StrokeCap.Butt)
                    )
                }
            }
        }

        for (habitIndex in getTheHabit.indices) {
            val habitAngle = Math.toRadians((-90f).toDouble())
            val habitRadius = innerRadius + habitIndex * ringWidth
            val padding = size.minDimension * 0.01
            val textX = (center.x + (habitRadius - 30) * cos(habitAngle) - padding).toFloat()
            val textAboveArc = habitRadius - (size.minDimension * 0.015f)
            val adjustedTextY = (center.y + textAboveArc * sin(habitAngle)).toFloat()

//            val textColor = getTheHabit[habitIndex].habit.color
//            textPaint.color = textColor.toColorInt()
            textPaint.color =materialTheme.onSurface.toArgb()
            drawContext.canvas.nativeCanvas.apply {
                withSave {
                    translate(textX, adjustedTextY)
                    drawText(getTheHabit[habitIndex].habit.name, 0f, 0f, textPaint)
                }
            }
        }

        for (day in 1..days) {
            val middleAngle = Math.toRadians(((day - 0.5f) * angleStep - 90).toDouble())
            val textX = (center.x + textRadius * cos(middleAngle)).toFloat()
            val textY = (center.y + textRadius * sin(middleAngle)).toFloat()
            val rotationAngle = ((day - 0.5f) * angleStep)

            drawContext.canvas.nativeCanvas.apply {
                withSave {
                    translate(textX, textY)
                    rotate(rotationAngle)
                    drawText(day.toString(), 0f, 0f, textPaintDays)
                }
            }
        }
    }

}

