package com.example.seguritas.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seguritas.domain.Punto

// TODO cambio id de puntos despues de eliminación, pero luego cuando recupera
// los puntos en la imagen si que está bien

@Composable
fun Marker(
    punto: Punto,
    isSelected: Boolean,
    onPuntoUpdated: (Punto) -> Unit,
    onPuntoSelected: () -> Unit,
    onDragStarted: () -> Unit,
    onDragEnded: () -> Unit
) {
    var offset by remember { mutableStateOf(punto.coordenada) }
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .offset(offset.x.dp, offset.y.dp)
            .size(26.dp)
            .background(
                color = when {
                    isDragging -> Color.Green
                    isSelected -> Color.Cyan
                    else -> Color.White
                },
                shape = RoundedCornerShape(8.dp)
            )
            .border(2.dp, Color.Black, shape = RoundedCornerShape(8.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onPuntoSelected()
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        onDragStarted()
                    },
                    onDragEnd = {
                        isDragging = false
                        onDragEnded()
                        onPuntoUpdated(punto.copy(coordenada = offset))
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        isDragging = true
                        val newOffset = Offset(
                            x = (offset.x + dragAmount.x).coerceIn(0f, 300f - 26f),
                            y = (offset.y + dragAmount.y).coerceIn(0f, 300f - 26f)
                        )
                        offset = newOffset
                    }
                )
            }
    ) {
        Text(
            text = punto.id.toString(),
            fontSize = 10.sp,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}