package com.vinyl.app.ui.nowplaying.components

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vinyl.app.domain.model.VinylStyle
import com.vinyl.app.domain.model.VinylTexture
import com.vinyl.app.ui.theme.LocalVinylTheme
import kotlin.math.pow

@Composable
fun VinylDisc(
    albumArt: Bitmap?,
    rotation: Float,
    discSize: Dp,
    modifier: Modifier = Modifier,
    style: VinylStyle = VinylStyle.CLASSIC,
    texture: VinylTexture = VinylTexture.STANDARD
) {
    val theme = LocalVinylTheme.current

    Canvas(
        modifier = modifier
            .size(discSize)
            .graphicsLayer {
                rotationZ = rotation
                this.clip = true
            }
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        val discRadius = size.minDimension / 2
        val outerGrooveRadius = discRadius * 0.95f
        val labelRadius = discRadius * 0.40f

        // 1. Vinyl body
        val bodyColor = when (texture) {
            VinylTexture.CARBON_FIBER -> Color(0xFF0D0D10)
            VinylTexture.COLORED -> theme.labelRingColor.copy(
                alpha = 0.3f
            ).let { Color(it.red * 0.1f, it.green * 0.1f, it.blue * 0.1f, 1f) }
            else -> theme.vinylBody
        }
        drawCircle(
            color = bodyColor,
            radius = discRadius
        )

        // 2. Groove rings (logarithmic distribution)
        val grooveCount = 22
        val grooveStartAlpha = theme.vinylGrooveStart.alpha
        val grooveEndAlpha = theme.vinylGrooveEnd.alpha
        for (i in 0 until grooveCount) {
            val t = (i.toFloat() / grooveCount).pow(0.7f)
            val r = outerGrooveRadius * t
            val alpha = grooveStartAlpha + (grooveEndAlpha - grooveStartAlpha) * t
            drawCircle(
                color = Color.White.copy(alpha = alpha.coerceIn(0f, 1f)),
                radius = r,
                style = Stroke(width = 0.5.dp.toPx())
            )
        }

        // 3. Label area
        if (albumArt != null) {
            val labelRect = androidx.compose.ui.geometry.Rect(
                center.x - labelRadius,
                center.y - labelRadius,
                center.x + labelRadius,
                center.y + labelRadius
            )
            val artBitmap = albumArt.asImageBitmap()
            drawContext.canvas.save()
            drawContext.canvas.clipPath(
                Path().apply { addOval(labelRect) },
                clipOp = ClipOp.Intersect
            )
            drawImage(
                image = artBitmap,
                dstSize = IntSize((labelRadius * 2).toInt(), (labelRadius * 2).toInt()),
                dstOffset = IntOffset(
                    (center.x - labelRadius).toInt(),
                    (center.y - labelRadius).toInt()
                )
            )
            drawContext.canvas.restore()
        } else {
            // Placeholder: dark circle with subtle ring
            drawCircle(
                color = Color(0xFF1A1A1A),
                radius = labelRadius
            )
        }

        // Label ring glow
        if (theme.labelRingColor != Color.Transparent && theme.labelRingOpacity > 0f) {
            drawCircle(
                color = theme.labelRingColor.copy(alpha = theme.labelRingOpacity),
                radius = labelRadius + 1.dp.toPx(),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }

        // 5. Spindle (center)
        val spindleRadius = 4.dp.toPx()
        drawCircle(color = Color(0xFF555555), radius = spindleRadius)
        drawCircle(
            color = Color(0xFF888888),
            radius = spindleRadius * 0.7f
        )
        drawCircle(
            color = Color(0xFFAAAAAA),
            radius = spindleRadius * 0.4f
        )

        // 6. Specular highlight (top-right arc)
        drawArc(
            color = Color.White.copy(alpha = 0.08f),
            startAngle = -45f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(center.x - discRadius * 0.6f, center.y - discRadius * 0.8f),
            size = Size(discRadius * 1.2f, discRadius * 1.2f),
            style = Stroke(width = discRadius * 0.15f)
        )
    }
}
