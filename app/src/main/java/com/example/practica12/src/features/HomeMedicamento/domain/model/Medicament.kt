package com.example.practica12.src.features.HomeMedicamento.domain.model


data class Medicament(
    val id: Int = 0,
    val name: String,
    val dose: String,
    val time: String,
    val imageUrl: String? = null,
    val userId: Int? = null,
    val createdAt: String? = null,
    val imagePath: String? = null, // ✅ Para imagen local
    val isSynced: Boolean = false   // ✅ Nuevo campo para saber si está sincronizado
)


