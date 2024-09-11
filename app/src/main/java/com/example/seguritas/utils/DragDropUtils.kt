package com.example.seguritas.utils


import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.drawBehind

fun Rect.isIntersecting(other: Rect): Boolean {
    return this.left < other.right &&
            this.right > other.left &&
            this.top < other.bottom &&
            this.bottom > other.top
}

fun Modifier.dashedBorder(
    color: Color,
    width: Dp = 1.dp,
    on: Dp = 10.dp,
    off: Dp = 5.dp,
    cornerRadius: Dp = 0.dp
) = this.then(
    Modifier.drawBehind {
        val strokeWidth = width.toPx()
        val dashArray = floatArrayOf(on.toPx(), off.toPx())
        val pathEffect = PathEffect.dashPathEffect(dashArray, 0f)

        drawRoundRect(
            color = color,
            size = size,
            cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx()),
            style = Stroke(
                width = strokeWidth,
                pathEffect = pathEffect
            )
        )
    }
)
