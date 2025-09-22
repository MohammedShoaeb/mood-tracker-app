package com.shadow.moodtracker.forTestingPurpose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun RadialShapeDynamic(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF7D2727),
    scale: Float = 1f
) {
    Box(Modifier.size(100.dp)) {

        Canvas(modifier = modifier) {

            val path = Path().apply {
                moveTo(32f, 18f)
                cubicTo(13f, 25f, 3.7f, 28.5f, 0.75f, 37.8f)
                cubicTo(-2.25f, 47f, 3.85f, 56f, 15.9f, 73.6f)
                lineTo(42f, 111f)
                cubicTo(50.5f, 123f, 54.6f, 130f, 60f, 132f)
                cubicTo(65.75f, 134f, 75f, 132f, 93f, 128.75f)
                cubicTo(100.459f, 127.427f, 107.356f, 126.622f, 115.084f, 126.197f)
                cubicTo(123f, 126f, 130f, 127.5f, 136.3f, 128.75f)
                cubicTo(155f, 132.5f, 165f, 134f, 170f, 132f)
                cubicTo(175.6f, 129f, 180f, 124f, 188f, 111.75f)
                lineTo(214f, 73.65f)
                cubicTo(226f, 56f, 232.4f, 47f, 229.5f, 38f)
                cubicTo(226.5f, 28.5f, 217f, 25f, 198f, 18f)
                cubicTo(173f, 9f, 142f, 0f, 115f, 0f)
                cubicTo(88f, 0f, 56f, 9f, 32f, 18f)
                close()
            }
            drawPath(path = path, color = color)

            // Get bounds and calculate offset to center the shape
            val bounds = path.getBounds()
            val offsetX = (size.width - bounds.width) / 2f - bounds.left
            val offsetY = (size.height - bounds.height) / 2f - bounds.top

            withTransform({
                translate(offsetX, offsetY)
                scale(scale)
            }) {
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRadialShapeDynamic() {
    RadialShapeDynamic(
        modifier = Modifier.size(300.dp),
    )

}





