package com.example.practica12.src.features.HomeMedicamento.domain.repository

import com.example.practica12.src.features.HomeMedicamento.domain.model.Medicament
import kotlinx.coroutines.flow.Flow
import java.io.File

interface MedicamentRepository {
    suspend fun getAllMedicaments(): Flow<Result<List<Medicament>>>
    suspend fun getMedicamentById(id: Int): Flow<Result<Medicament>>

    // ✅ CAMBIO: imageUrl: String? → imageFile: File?
    suspend fun createMedicament(
        name: String,
        dose: String,
        time: String,
        imageFile: File?
    ): Flow<Result<Medicament>>

    // ✅ CAMBIO: imageUrl: String? → imageFile: File?
    suspend fun updateMedicament(
        id: Int,
        name: String,
        dose: String,
        time: String,
        imageFile: File?
    ): Flow<Result<Medicament>>

    suspend fun deleteMedicament(id: Int): Flow<Result<Boolean>>
}