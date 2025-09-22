package com.shadow.moodtracker.presentationLayer.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.core.graphics.withSave
import com.shadow.moodtracker.R
import com.shadow.moodtracker.data.repository.StepTrackerRecord
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun StepTrackerChart(
    stepData: List<StepTrackerRecord>,
    days: Int,
) {
    val materialTheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .aspectRatio(1/1f)

            .padding(12.dp)

            .border(
                BorderStroke(0.75.dp, MaterialTheme.colorScheme.outlineVariant),
                shape = RoundedCornerShape(6.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .padding(10.dp)
                .aspectRatio(1f)
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val innerRadius = size.minDimension * 0.34f
            val ringWidth = size.minDimension * 0.19f
            val angleStep = 360f / days
            val textRadius = innerRadius + ringWidth - 70f

            for (dayIndex in 0 until days) {
                val startAngle = (dayIndex * angleStep) - 90f

                val ringColor = stepData.find { it.day == (dayIndex + 1).toString() }

                drawArc(
                    color = materialTheme.outline,
                    startAngle = startAngle,
                    sweepAngle = angleStep,
                    useCenter = false,
                    topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
                    size = Size(innerRadius * 2, innerRadius * 2),
                    style = Stroke(ringWidth, cap = StrokeCap.Butt)
                )
                drawArc(
                    color = if (ringColor == null) materialTheme.surface else Color(
                        ringColor.color.toColorInt()
                    ),
                    startAngle = startAngle,
                    sweepAngle = angleStep,
                    useCenter = false,
                    topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
                    size = Size(innerRadius * 2, innerRadius * 2),
                    style = Stroke(ringWidth - 5f, cap = StrokeCap.Butt)
                )
            }

            // Paint for day numbers
            val textPaintDays = Paint().apply {
                color = materialTheme.onBackground.toArgb()
                textSize = size.minDimension * 0.030f
                textAlign = Paint.Align.CENTER
                typeface = Typeface.DEFAULT_BOLD
            }

            // Draw day numbers
            for (day in 1..days) {
                val middleAngle = Math.toRadians(((day + 0.5) * angleStep - 102f))
                val textX = (center.x + textRadius * cos(middleAngle)).toFloat()
                val textY = (center.y + textRadius * sin(middleAngle)).toFloat()
                val rotationAngle = (day) * angleStep

                //            val rotationAngle = ((day - 0.5f) * angleStep)
                drawContext.canvas.nativeCanvas.apply {
                    withSave {
                        translate(textX, textY)
                        rotate(rotationAngle)
                        drawText(day.toString(), 0f, 0f, textPaintDays)
                    }
                }
            }
        }


        Image(
            modifier = Modifier
                .size(156.dp)
                .aspectRatio(1f),
            painter = painterResource(id = R.drawable.sneaker), contentDescription = null
        )

    }


}

@Preview
@Composable
fun PreviewStepTrackerWheel() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {


    }

}
