package com.shadow.moodtracker.forTestingPurpose
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.xr.runtime.math.toRadians
import kotlin.math.cos
import kotlin.math.sin

data class RadialMenuItem(
    val value: Float,
    val color: Color,
    val icon: ImageVector,
    val label: String
)


@Composable
fun RadialMenuSlice(
    modifier: Modifier = Modifier,
    color: Color,
    startAngle: Float, // The start angle for this specific slice (already adjusted for gap)
    sweepAngle: Float, // The sweep angle for this specific slice
    arcThickness: Float, // Thickness of the arc
    icon: ImageVector,
    label: String,
    iconDistanceFromCenter: Float, // Where to place the icon/text radially
    totalRadius: Float, // The overall outer radius of the circular menu (for Path calculations)
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(onClick = onClick)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = totalRadius
            val innerRadius = outerRadius - arcThickness

            // Rectangles for arc segments
            val outerRect = Rect(center = center, radius = outerRadius)
            val innerRect = Rect(center = center, radius = innerRadius)

            // Radius for the rounded caps (half of the arc thickness)
            val capRadius = arcThickness / 2f

            val path = Path().apply {
                // Calculate angles in radians for trigonometric functions
                val startAngleRad = toRadians(startAngle).toFloat()
                val endAngleRad = toRadians(startAngle + sweepAngle).toFloat()

                // Calculate the midpoint radius for the center of the caps
                val midCapRadius = (outerRadius + innerRadius) / 2f

                // 1. Move to the start point of the outer arc
                val startOuterX = center.x + outerRadius * cos(startAngleRad)
                val startOuterY = center.y + outerRadius * sin(startAngleRad)
                moveTo(startOuterX, startOuterY)

                // 2. Draw the outer arc segment
                arcTo(
                    rect = outerRect,
                    startAngleDegrees = startAngle,
                    sweepAngleDegrees = sweepAngle,
                    forceMoveTo = false // Don't move, we're already at startOuterX,Y
                )

                // 3. Draw the End Cap (a semi-circle connecting outer and inner arcs)
                // Center point of the end cap's circular arc
                val endCapCenterX = center.x + midCapRadius * cos(endAngleRad)
                val endCapCenterY = center.y + midCapRadius * sin(endAngleRad)
                arcTo(
                    rect = Rect(center = Offset(endCapCenterX, endCapCenterY), radius = capRadius),
                    startAngleDegrees = startAngle + sweepAngle, // Angle for the outer point of the cap
                    sweepAngleDegrees = 180f, // Half-circle
                    forceMoveTo = false
                )

                // 4. Draw the Inner Arc segment (in reverse direction)
                arcTo(
                    rect = innerRect,
                    startAngleDegrees = startAngle + sweepAngle, // Start from the end of the sweep
                    sweepAngleDegrees = -sweepAngle, // Sweep backwards to the start
                    forceMoveTo = false
                )

                // 5. Draw the Start Cap (a semi-circle connecting inner and outer arcs)
                // Center point of the start cap's circular arc
                val startCapCenterX = center.x + midCapRadius * cos(startAngleRad)
                val startCapCenterY = center.y + midCapRadius * sin(startAngleRad)
                arcTo(
                    rect = Rect(center = Offset(startCapCenterX.toFloat(), startCapCenterY.toFloat()), radius = capRadius),
                    startAngleDegrees = startAngle + 180f, // Angle for the inner point of the cap (180 deg from outer)
                    sweepAngleDegrees = 180f, // Half-circle
                    forceMoveTo = false
                )

                close() // Close the path to form a complete shape
            }
            drawPath(path, color = color) // Draw the filled path
        }

        // Content (Icon and Text) for the slice
        val midAngleOfContent = startAngle + (sweepAngle / 2f)
        val midAngleRadOfContent = toRadians(midAngleOfContent.toDouble().toFloat())

        val xOffset = iconDistanceFromCenter * cos(midAngleRadOfContent)
        val yOffset = iconDistanceFromCenter * sin(midAngleRadOfContent)

        Column(
            modifier = Modifier
                .offset(x = xOffset.dp, y = yOffset.dp)
                .align(Alignment.Center)
                .size(60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(28.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                color = Color.White,
                fontSize = 11.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
fun RadialMenu(
    modifier: Modifier = Modifier,
    menuItems: List<RadialMenuItem>,
    sliceGap: Float = 12f, // Gap in degrees between slices
    arcThickness: Float = 80f, // Thickness of each arc
    centralCircleSizeFraction: Float = 2.5f, // Factor for central circle size relative to totalSize
    onSliceClick: (RadialMenuItem) -> Unit = {}
) {
    val totalSize = 250.dp // Overall size of the radial menu

    val outerRadius = totalSize.value / 2f
    val innerRadius = outerRadius - arcThickness
    val iconDistanceFromCenter = (outerRadius + innerRadius) / 2f

    Box(
        modifier = modifier
            .size(totalSize)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        var currentStartAngle = -90f // Start from the top (12 o'clock)
        val adjustedTotalAngle = 360f - (menuItems.size * sliceGap)

        menuItems.forEach { item ->
            val sweepAngle = adjustedTotalAngle * item.value

            // Pass totalRadius to RadialMenuSlice for Path calculations
            RadialMenuSlice(
                modifier = Modifier.fillMaxSize(),
                color = item.color,
                startAngle = currentStartAngle + (sliceGap / 2f),
                sweepAngle = sweepAngle,
                arcThickness = arcThickness,
                icon = item.icon,
                label = item.label,
                iconDistanceFromCenter = iconDistanceFromCenter,
                totalRadius = outerRadius, // Pass the total outer radius to the slice
                onClick = { onSliceClick(item) }
            )

            currentStartAngle += sweepAngle + sliceGap
        }

        // Draw the central circle
        Box(
            modifier = Modifier
                .size(totalSize / centralCircleSizeFraction)
                .aspectRatio(1f)
                .background(Color.DarkGray, MaterialTheme.shapes.extraLarge),
            contentAlignment = Alignment.Center
        ) {
            Image(
                imageVector = Icons.Default.Settings,
                contentDescription = "All Devices",
                modifier = Modifier.size(36.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RadialMenuPreview() {
    val menuItems = listOf(
        RadialMenuItem(0.25f, Color(0xFFEF5350), Icons.Default.Star, "Heating"),
        RadialMenuItem(0.15f, Color(0xFF66BB6A), Icons.Default.Star, "Soundbox"),
        RadialMenuItem(0.30f, Color(0xFF42A5F5), Icons.Default.Star, "Fan"),
        RadialMenuItem(0.30f, Color(0xFFFFA726), Icons.Default.Star, "TV")
    )

    RadialMenu(
        menuItems = menuItems,
        sliceGap = 12f,
        arcThickness = 80f,
        onSliceClick = { item ->
            println("Clicked on: ${item.label}")
        }
    )
}