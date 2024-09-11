package com.example.seguritas.domain

import com.example.seguritas.domain.Punto

data class Escenario(
    val id: Int,
    val imageUrl: String,
    val puntos: MutableList<Punto>
)