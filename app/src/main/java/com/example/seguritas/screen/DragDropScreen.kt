package com.example.seguritas.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap

import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.seguritas.ui.theme.SeguritasTheme
import com.example.seguritas.utils.dashedBorder
import com.example.seguritas.utils.isIntersecting
import kotlin.math.roundToInt

@Composable
fun DragDropScreen(
    modifier: Modifier = Modifier,
    onImagePick: () -> Unit,
    imageBitmap: ImageBitmap?,
    onRemoveImage: () -> Unit,
    onNavigateToPdf: () -> Unit,
    onNavigateToImageMarker: () -> Unit
) {
    var textOffset by remember { mutableStateOf(IntOffset(0, 0)) }
    var imageOffset by remember { mutableStateOf(IntOffset(0, 0)) }
    var isTextDroppedInZone by remember { mutableStateOf(false) }
    var isImageDroppedInZone by remember { mutableStateOf(false) }

    var dropZoneBounds by remember { mutableStateOf(Rect.Zero) }
    var textBounds by remember { mutableStateOf(Rect.Zero) }
    var imageBounds by remember { mutableStateOf(Rect.Zero) }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(
                    color = if (isTextDroppedInZone || isImageDroppedInZone) Color.Green else Color.White,
                    shape = RoundedCornerShape(13.dp)
                )

                .dashedBorder(
                    color = Color.Black,
                    width = 2.dp,
                    on = 10.dp,
                    off = 5.dp,
                    cornerRadius = 16.dp
                )
                .onGloballyPositioned { coordinates ->
                    dropZoneBounds = coordinates.boundsInWindow()
                }
                .align(Alignment.Center)
        ) {
            if (isTextDroppedInZone || isImageDroppedInZone) {
                BasicText(
                    text = "Dropped!",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .offset { textOffset }
                    .size(120.dp, 60.dp)
                    .padding(10.dp)
                    .dashedBorder(
                        color = Color.Black,
                        width = 2.dp,
                        on = 10.dp,
                        off = 5.dp,
                        cornerRadius = 16.dp
                    )
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            textOffset = IntOffset(
                                (textOffset.x + dragAmount.x).roundToInt(),
                                (textOffset.y + dragAmount.y).roundToInt()
                            )
                        }
                    }
                    .onGloballyPositioned { coordinates ->
                        textBounds = coordinates.boundsInWindow()
                        isTextDroppedInZone = textBounds.isIntersecting(dropZoneBounds)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Drag me",
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(5.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { onImagePick() },
                    modifier = Modifier.height(46.dp),
                ) {
                    Text("Add image")
                }

                Button(
                    onClick = { onNavigateToPdf() },
                    modifier = Modifier.height(46.dp),
                ) {
                    Text("PDF")
                }
                Button(
                    onClick = { onNavigateToImageMarker() },
                    modifier = Modifier.height(46.dp),
                ) {
                    Text("Image Marker")
                }
            }

            imageBitmap?.let {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(70.dp)
                        .offset { imageOffset }
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                imageOffset = IntOffset(
                                    (imageOffset.x + dragAmount.x).roundToInt(),
                                    (imageOffset.y + dragAmount.y).roundToInt()
                                )
                            }
                        }
                        .onGloballyPositioned { coordinates ->
                            imageBounds = coordinates.boundsInWindow()
                            isImageDroppedInZone = imageBounds.isIntersecting(dropZoneBounds)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = it,
                        contentDescription = "Selected image",
                        modifier = Modifier.size(100.dp),
                        contentScale = ContentScale.Crop,
                    )
                    IconButton(
                        onClick = onRemoveImage,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(Color.Black)
                            .padding(5.dp)
                            .size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Remove Image",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun DragDropScreenPreview() {
    SeguritasTheme {
        DragDropScreen(
            onImagePick = { /* No-op */ },
            imageBitmap = null,
            onRemoveImage = { /* No-op */ },
            onNavigateToPdf = { /* No-op */ },
            onNavigateToImageMarker = { /* No-op */ },
        )
    }
}

