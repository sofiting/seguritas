package com.example.seguritas.screen

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.navigation.NavController
import com.example.seguritas.component.Marker
import com.example.seguritas.domain.Punto
import com.google.gson.Gson

@Composable
fun ImageMarkerScreen(navController: NavController) {
    var puntos by remember { mutableStateOf(listOf<Punto>()) }
    var selectedId by remember { mutableStateOf<Int?>(null) }
    var isDragging by remember { mutableStateOf(false) }
    var idCounter by remember { mutableStateOf(1) }

    val imageUrl = "https://fancyhouse-design.com/wp-content/uploads/2024/05/Weather-resistant-materials-for-outdoor-spaces-ensure-longevity-and-minimal-maintenance.jpg"

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(Color.LightGray)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )

            puntos.forEach { punto ->
                Marker(
                    punto = punto,
                    isSelected = selectedId == punto.id,
                    onPuntoUpdated = { updatedPunto ->
                        puntos = puntos.map { if (it.id == updatedPunto.id) updatedPunto else it }
                    },
                    onPuntoSelected = {
                        selectedId = punto.id
                    },
                    onDragStarted = {
                        isDragging = true
                    },
                    onDragEnded = {
                        isDragging = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(onClick = {
                puntos = puntos + Punto(
                    id = idCounter,
                    coordenada = Offset(x = 150f, y = 150f)
                )
                selectedId = idCounter
                idCounter++
            }) {
                Text("Crear Punto")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(onClick = {
                selectedId?.let { id ->
                    puntos = puntos.filter { it.id != id }
                }
            }) {
                Text("Borrar Punto")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val puntosJson = Uri.encode(Gson().toJson(puntos))
            val encodedImageUrl = Uri.encode(imageUrl)
            navController.navigate("image_detail/$encodedImageUrl/$puntosJson")
        }) {
            Text("Ver Detalle")
        }
    }
}

