package com.example.practica12.src.features.HomeMedicamento.data.mappers

import com.example.practica12.src.features.HomeMedicamento.data.local.entity.MedicamentoEntity
import com.example.practica12.src.features.HomeMedicamento.data.model.MedicamentDto
import com.example.practica12.src.features.HomeMedicamento.domain.model.Medicament

// 🔁 DTO → Domain (desde API a app)
fun MedicamentDto.toDomain(): Medicament {
    return Medicament(
        id = id,
        name = name,
        dose = dose,
        time = time,
        imageUrl = imageUrl,
        imagePath = null, // No hay path local desde API, la imagen viene de una URL
        userId = userId,
        createdAt = createdAt,
        isSynced = true
    )
}

// 🔁 Entity → Domain (Room → lógica de negocio)
fun MedicamentoEntity.toDomain(): Medicament {
    return Medicament(
        id = id,
        name = nombre,
        dose = dosis,
        time = hora,
        imageUrl = imageUrl, // Ahora MedicamentoEntity también tiene imageUrl
        imagePath = imagePath,
        userId = null,
        createdAt = null,
        isSynced = isSynced
    )
}

// 🔁 Domain → Entity (guardar en Room)
fun Medicament.toEntity(isSynced: Boolean = false): MedicamentoEntity {
    return MedicamentoEntity(
        id = id,
        nombre = name,
        dosis = dose,
        hora = time,
        imagePath = imagePath,
        imageUrl = imageUrl, // Guardar imageUrl también en la entidad
        isSynced = isSynced
    )
}


