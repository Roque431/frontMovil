package com.example.practica12.src.features.HomeMedicamento.domain.model

data class Medicament(
    val id: Int = 0,
    val name: String,
    val dose: String,
    val time: String,
    val imageUrl: String? = null,
    val userId: Int,
    val createdAt: String? = null
)
