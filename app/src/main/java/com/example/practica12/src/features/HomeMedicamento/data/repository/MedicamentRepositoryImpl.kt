package com.example.practica12.src.features.HomeMedicamento.data.repository

import com.example.practica12.src.core.hardware.data.NetworkChecker
import com.example.practica12.src.core.hardware.domain.CameraRepository
import com.example.practica12.src.features.HomeMedicamento.data.datasourse.remote.MedicamentService
import com.example.practica12.src.features.HomeMedicamento.data.local.dao.MedicamentoDao
import com.example.practica12.src.features.HomeMedicamento.data.mappers.toDomain
import com.example.practica12.src.features.HomeMedicamento.data.mappers.toEntity
import com.example.practica12.src.features.HomeMedicamento.domain.model.Medicament
import com.example.practica12.src.features.HomeMedicamento.domain.repository.MedicamentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOf
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class MedicamentRepositoryImpl @Inject constructor(
    private val medicamentService: MedicamentService,
    private val networkChecker: NetworkChecker,
    private val medicamentoDao: MedicamentoDao,
    private val cameraRepository: CameraRepository
) : MedicamentRepository {

    override suspend fun getAllMedicaments(): Flow<Result<List<Medicament>>> = flow {
        // Primero, emitir los medicamentos locales
        medicamentoDao.getAll().collect { localMedicaments ->
            emit(Result.success(localMedicaments.map { it.toDomain() }))
        }

        // Luego, intentar obtener de la red y actualizar la base de datos local
        try {
            if (networkChecker.isOnline()) {
                val response = medicamentService.getAllMedicaments()
                if (response.isSuccessful) {
                    val medicamentsDto = response.body()?.medicaments ?: emptyList()
                    // Limpiar la base de datos local antes de insertar los datos de la nube
                    // Esto es una estrategia simple, para apps más complejas se necesita una lógica de merge
                    // medicamentoDao.deleteAll() // Considerar si es necesario borrar

                    medicamentsDto.forEach { dto ->
                        val medicament = dto.toDomain()
                        medicamentoDao.insert(medicament.toEntity(isSynced = true))
                    }
                    // Emitir los medicamentos actualizados (locales + nube)
                    medicamentoDao.getAll().collect { updatedMedicaments ->
                        emit(Result.success(updatedMedicaments.map { it.toDomain() }))
                    }
                } else {
                    // Si falla la red, no emitir un error que sobrescriba los datos locales
                    println("Error al obtener medicamentos de la red: ${response.message()}")
                }
            }
        } catch (e: Exception) {
            println("Excepción al obtener medicamentos de la red: ${e.message}")
        }
    }
    override suspend fun getAllMedicamentsLocal(): Flow<Result<List<Medicament>>> {
        return try {
            medicamentoDao.getAll().map { list ->
                Result.success(list.map { it.toDomain() })
            }
        } catch (e: Exception) {
            flowOf(Result.failure(e))
        }
    }

    override suspend fun getMedicamentById(id: Int): Flow<Result<Medicament>> = flow {
        try {
            val response = medicamentService.getMedicamentById(id)
            val medicamentDto = response.body()?.medicament
            if (response.isSuccessful && medicamentDto != null) {
                emit(Result.success(medicamentDto.toDomain()))
            } else {
                emit(Result.failure(Exception("Medicamento no encontrado")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun createMedicament(
        name: String,
        dose: String,
        time: String,
        imageFile: File?
    ): Flow<Result<Medicament>> = flow {
        try {
            if (networkChecker.isOnline()) {
                val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
                val doseBody = dose.toRequestBody("text/plain".toMediaTypeOrNull())
                val timeBody = time.toRequestBody("text/plain".toMediaTypeOrNull())

                val imagePart = imageFile?.let {
                    val requestBody = it.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("image", it.name, requestBody)
                }

                val response = medicamentService.createMedicament(nameBody, doseBody, timeBody, imagePart)
                val medicamentDto = response.body()?.medicament

                if (response.isSuccessful && medicamentDto != null) {
                    val medicament = medicamentDto.toDomain()
                    medicamentoDao.insert(medicament.toEntity(isSynced = true))
                    emit(Result.success(medicament))
                } else {
                    // Si falla la creación en línea, guardar localmente como no sincronizado
                    val medicament = Medicament(
                        name = name,
                        dose = dose,
                        time = time,
                        imagePath = imageFile?.absolutePath,
                        imageUrl = null
                    )
                    medicamentoDao.insert(medicament.toEntity(isSynced = false))
                    emit(Result.failure(Exception("Error al crear medicamento en línea, guardado localmente.")))
                }
            } else {
                // 🧠 Guardar imagen local si existe
                val imagePath = imageFile?.let {
                    val bitmap = android.graphics.BitmapFactory.decodeFile(it.absolutePath)
                    val fileName = "med_${System.currentTimeMillis()}"
                    cameraRepository.saveBitmapToInternalStorage(fileName, bitmap)
                }

                val medicament = Medicament(
                    name = name,
                    dose = dose,
                    time = time,
                    imagePath = imagePath,
                    imageUrl = null
                )

                medicamentoDao.insert(medicament.toEntity(isSynced = false))
                emit(Result.success(medicament))
            }
        } catch (e: Exception) {
            // En caso de excepción, guardar localmente como no sincronizado
            val medicament = Medicament(
                name = name,
                dose = dose,
                time = time,
                imagePath = imageFile?.absolutePath,
                imageUrl = null
            )
            medicamentoDao.insert(medicament.toEntity(isSynced = false))
            emit(Result.failure(Exception("Excepción al crear medicamento, guardado localmente: ${e.message}")))
        }
    }


    override suspend fun updateMedicament(
        id: Int,
        name: String,
        dose: String,
        time: String,
        imageFile: File?
    ): Flow<Result<Medicament>> = flow {
        try {
            if (networkChecker.isOnline()) {
                val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
                val doseBody = dose.toRequestBody("text/plain".toMediaTypeOrNull())
                val timeBody = time.toRequestBody("text/plain".toMediaTypeOrNull())

                val imagePart = imageFile?.let {
                    val requestBody = it.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("image", it.name, requestBody)
                }

                val response = medicamentService.updateMedicament(id, nameBody, doseBody, timeBody, imagePart)
                val medicamentDto = response.body()?.medicament

                if (response.isSuccessful && medicamentDto != null) {
                    // Al actualizar online, también actualizamos la base de datos local
                    val medicament = medicamentDto.toDomain()
                    medicamentoDao.actualizar(medicament.toEntity(isSynced = true))
                    emit(Result.success(medicament))
                } else {
                    // Si falla la actualización en línea, marcar localmente como no sincronizado
                    val existingMedicament = medicamentoDao.getMedicamentById(id) // Asumiendo que existe este método
                    val medicamentToUpdate = existingMedicament?.copy(
                        nombre = name,
                        dosis = dose,
                        hora = time,
                        isSynced = false
                    ) ?: Medicament(
                        id = id,
                        name = name,
                        dose = dose,
                        time = time,
                        imagePath = imageFile?.absolutePath,
                        imageUrl = null
                    ).toEntity(isSynced = false)

                    medicamentoDao.actualizar(medicamentToUpdate)
                    emit(Result.failure(Exception("Error al actualizar medicamento en línea, marcado para sincronizar.")))
                }
            } else {
                // Sin conexión, actualizar localmente y marcar como no sincronizado
                val existingMedicament = medicamentoDao.getMedicamentById(id) // Asumiendo que existe este método
                val medicamentToUpdate = existingMedicament?.copy(
                    nombre = name,
                    dosis = dose,
                    hora = time,
                    isSynced = false
                ) ?: Medicament(
                    id = id,
                    name = name,
                    dose = dose,
                    time = time,
                    imagePath = imageFile?.absolutePath,
                    imageUrl = null
                ).toEntity(isSynced = false)

                medicamentoDao.actualizar(medicamentToUpdate)
                emit(Result.failure(Exception("Sin conexión, medicamento actualizado localmente y marcado para sincronizar.")))
            }
        } catch (e: Exception) {
            // En caso de excepción, marcar localmente como no sincronizado
            val existingMedicament = medicamentoDao.getMedicamentById(id) // Asumiendo que existe este método
            val medicamentToUpdate = existingMedicament?.copy(
                nombre = name,
                dosis = dose,
                hora = time,
                isSynced = false
            ) ?: Medicament(
                id = id,
                name = name,
                dose = dose,
                time = time,
                imagePath = imageFile?.absolutePath,
                imageUrl = null
            ).toEntity(isSynced = false)

            medicamentoDao.actualizar(medicamentToUpdate)
            emit(Result.failure(Exception("Excepción al actualizar medicamento, marcado para sincronizar: ${e.message}")))
        }
    }

    override suspend fun deleteMedicament(id: Int): Flow<Result<Boolean>> = flow {
        try {
            if (networkChecker.isOnline()) {
                val response = medicamentService.deleteMedicament(id)
                if (response.isSuccessful) {
                    // Si se elimina del servidor, también se elimina de la base de datos local
                    medicamentoDao.deleteById(id)
                    emit(Result.success(true))
                } else {
                    // Si falla la eliminación en línea, no eliminar localmente y marcar para reintentar
                    emit(Result.failure(Exception("Error al eliminar medicamento en línea, reintentar.")))
                }
            } else {
                // Sin conexión, eliminar localmente y no intentar sincronizar (asumimos que se eliminará en la próxima sincronización si existe en el servidor)
                medicamentoDao.deleteById(id)
                emit(Result.success(true))
            }
        } catch (e: Exception) {
            // En caso de excepción, no eliminar localmente y marcar para reintentar
            emit(Result.failure(e))
        }
    }
    override suspend fun syncPendingMedicaments(): Flow<Result<Unit>> = flow {
        try {
            if (networkChecker.isOnline()) {
                val pendientes = medicamentoDao.getNoSincronizados()
                pendientes.forEach { localMedicament ->
                    val nameBody = localMedicament.nombre.toRequestBody("text/plain".toMediaTypeOrNull())
                    val doseBody = localMedicament.dosis.toRequestBody("text/plain".toMediaTypeOrNull())
                    val timeBody = localMedicament.hora.toRequestBody("text/plain".toMediaTypeOrNull())

                    val imagePart = localMedicament.imagePath?.let { path ->
                        val file = File(path)
                        val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("image", file.name, requestBody)
                    }

                    val response = medicamentService.createMedicament(nameBody, doseBody, timeBody, imagePart)
                    val medicamentDto = response.body()?.medicament

                    if (response.isSuccessful && medicamentDto != null) {
                        val synced = medicamentDto.toDomain().copy(imagePath = localMedicament.imagePath)
                        medicamentoDao.actualizar(synced.toEntity(isSynced = true))
                    }
                }
                emit(Result.success(Unit))
            } else {
                emit(Result.failure(Exception("Sin conexión a internet")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}




