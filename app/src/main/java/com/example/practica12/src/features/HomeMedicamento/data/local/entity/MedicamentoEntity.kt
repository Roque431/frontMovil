package com.example.practica12.src.features.HomeMedicamento.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicamentos")
data class MedicamentoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val dosis: String,
    val hora: String,
    val imagePath: String? = null,      // 📷 Ruta local del archivo de imagen (solo si se tomó sin internet)
    val imageUrl: String? = null,      // 🌐 URL de la imagen del servidor
    val isSynced: Boolean = false       // 🔁 Indica si ya fue sincronizado con el servidor
)


