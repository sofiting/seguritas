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
// arrastra y selecciona son dos estados : selcciona cuando hace
// click si no hace otro click no lo desaparece el background color, arrastra cuando arrstra

// TODO cuadno aparece el proximo punto el proximo sea selected por defecto a no ser que lo cambie

// TODO click punto con id menor desaparece punto con id mayor
@Composable
fun Marker(
    puntos: List<Punto>,
    onUpdatePuntos: (List<Punto>) -> Unit,
    onSelectPunto: (Int) -> Unit
) {
    var selectedId by remember { mutableStateOf<Int?>(null) }
    var draggingId by remember { mutableStateOf<Int?>(null) }

    fun updatePuntos(updatedPunto: Punto) {
        val updatedPuntos = puntos.map {
            if (it.id == updatedPunto.id) {
                updatedPunto
            } else {
                it
            }
        }
        onUpdatePuntos(updatedPuntos)
    }

    puntos.forEach { punto ->
        val isSelected by remember { derivedStateOf { punto.id == selectedId } }
        var isDragging by remember { mutableStateOf(punto.id == draggingId) }
        var offset by remember { mutableStateOf(punto.coordenada) }

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
                            if (selectedId != punto.id) {
                                selectedId = punto.id
                                onSelectPunto(punto.id) // Inform the parent about the selection
                                draggingId = null
                                updatePuntos(punto.copy(coordenada = offset))
                            }
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            isDragging = false
                            draggingId = null
                            updatePuntos(punto.copy(coordenada = offset))
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            isDragging = true
                            draggingId = punto.id
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
}
