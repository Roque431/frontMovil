package com.example.practica12.src.features.HomeMedicamento.data.repository


import com.example.practica12.src.features.HomeMedicamento.data.datasourse.remote.MedicamentService
import com.example.practica12.src.features.HomeMedicamento.data.model.MedicamentDto
import com.example.practica12.src.features.HomeMedicamento.domain.model.Medicament
import com.example.practica12.src.features.HomeMedicamento.domain.repository.MedicamentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class MedicamentRepositoryImpl @Inject constructor(
    private val medicamentService: MedicamentService
) : MedicamentRepository {

    override suspend fun getAllMedicaments(): Flow<Result<List<Medicament>>> = flow {
        try {
            val response = medicamentService.getAllMedicaments()

            if (response.isSuccessful) {
                val medicamentsDto = response.body()?.medicaments ?: emptyList()

                // ✅ LOGS DE DEBUG:
                println("🔍 ========== MEDICAMENTOS RECIBIDOS ==========")
                medicamentsDto.forEach { medicament ->
                    println("🔍 ID: ${medicament.id}")
                    println("🔍 NOMBRE: ${medicament.name}")
                    println("🔍 IMAGE_URL: '${medicament.imageUrl}'")
                    println("🔍 ==========================================")
                }

                val medicaments = medicamentsDto.map { it.toDomain() }
                emit(Result.success(medicaments))
            } else {
                emit(Result.failure(Exception("Error al obtener medicamentos: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun getMedicamentById(id: Int): Flow<Result<Medicament>> = flow {
        try {
            val response = medicamentService.getMedicamentById(id)

            if (response.isSuccessful) {
                val medicamentDto = response.body()?.medicament
                if (medicamentDto != null) {
                    emit(Result.success(medicamentDto.toDomain()))
                } else {
                    emit(Result.failure(Exception("Medicamento no encontrado")))
                }
            } else {
                emit(Result.failure(Exception("Error al obtener medicamento: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    // ✅ MÉTODO COMPLETAMENTE REESCRITO
    override suspend fun createMedicament(
        name: String,
        dose: String,
        time: String,
        imageFile: File?
    ): Flow<Result<Medicament>> = flow {
        try {
            // ✅ Crear RequestBody para campos de texto
            val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val doseBody = dose.toRequestBody("text/plain".toMediaTypeOrNull())
            val timeBody = time.toRequestBody("text/plain".toMediaTypeOrNull())

            // ✅ Crear MultipartBody.Part para imagen (si existe)
            val imagePart = imageFile?.let { file ->
                val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", file.name, requestBody)
            }

            // ✅ Llamar al API con multipart
            val response = medicamentService.createMedicament(
                name = nameBody,
                dose = doseBody,
                time = timeBody,
                image = imagePart
            )

            if (response.isSuccessful) {
                val medicamentDto = response.body()?.medicament
                if (medicamentDto != null) {
                    emit(Result.success(medicamentDto.toDomain()))
                } else {
                    emit(Result.failure(Exception("Error al crear medicamento")))
                }
            } else {
                val errorMessage = response.body()?.error ?: "Error desconocido"
                emit(Result.failure(Exception(errorMessage)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    // ✅ MÉTODO COMPLETAMENTE REESCRITO
    override suspend fun updateMedicament(
        id: Int,
        name: String,
        dose: String,
        time: String,
        imageFile: File?
    ): Flow<Result<Medicament>> = flow {
        try {
            // ✅ Crear RequestBody para campos de texto
            val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val doseBody = dose.toRequestBody("text/plain".toMediaTypeOrNull())
            val timeBody = time.toRequestBody("text/plain".toMediaTypeOrNull())

            // ✅ Crear MultipartBody.Part para imagen (si existe)
            val imagePart = imageFile?.let { file ->
                val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", file.name, requestBody)
            }

            // ✅ Llamar al API con multipart
            val response = medicamentService.updateMedicament(
                id = id,
                name = nameBody,
                dose = doseBody,
                time = timeBody,
                image = imagePart
            )

            if (response.isSuccessful) {
                val medicamentDto = response.body()?.medicament
                if (medicamentDto != null) {
                    emit(Result.success(medicamentDto.toDomain()))
                } else {
                    emit(Result.failure(Exception("Error al actualizar medicamento")))
                }
            } else {
                val errorMessage = response.body()?.error ?: "Error desconocido"
                emit(Result.failure(Exception(errorMessage)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun deleteMedicament(id: Int): Flow<Result<Boolean>> = flow {
        try {
            val response = medicamentService.deleteMedicament(id)

            if (response.isSuccessful) {
                emit(Result.success(true))
            } else {
                val errorMessage = response.body()?.error ?: "Error desconocido"
                emit(Result.failure(Exception(errorMessage)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    // Función de extensión para convertir DTO a Domain
    private fun MedicamentDto.toDomain(): Medicament {
        return Medicament(
            id = this.id,
            name = this.name,
            dose = this.dose,
            time = this.time,
            imageUrl = this.imageUrl,
            userId = this.userId,
            createdAt = this.createdAt
        )
    }
}