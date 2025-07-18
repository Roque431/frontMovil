package com.example.practica12.src.features.HomeMedicamento.domain.usecase

import com.example.practica12.src.features.HomeMedicamento.domain.model.Medicament
import com.example.practica12.src.features.HomeMedicamento.domain.repository.MedicamentRepository
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

class GetAllMedicamentsUseCase @Inject constructor(
    private val repository: MedicamentRepository
) {
    suspend operator fun invoke(): Flow<Result<List<Medicament>>> {
        return repository.getAllMedicaments()
    }
}

class GetMedicamentByIdUseCase @Inject constructor(
    private val repository: MedicamentRepository
) {
    suspend operator fun invoke(id: Int): Flow<Result<Medicament>> {
        return repository.getMedicamentById(id)
    }
}

// ✅ CAMBIO: imageUrl: String? → imageFile: File?
class CreateMedicamentUseCase @Inject constructor(
    private val repository: MedicamentRepository
) {
    suspend operator fun invoke(
        name: String,
        dose: String,
        time: String,
        imageFile: File?
    ): Flow<Result<Medicament>> {
        // Validaciones antes de crear
        if (name.isBlank()) {
            return kotlinx.coroutines.flow.flow {
                emit(Result.failure(Exception("El nombre del medicamento es requerido")))
            }
        }

        if (dose.isBlank()) {
            return kotlinx.coroutines.flow.flow {
                emit(Result.failure(Exception("La dosis es requerida")))
            }
        }

        if (time.isBlank()) {
            return kotlinx.coroutines.flow.flow {
                emit(Result.failure(Exception("La hora es requerida")))
            }
        }

        return repository.createMedicament(name, dose, time, imageFile)
    }
}

// ✅ CAMBIO: imageUrl: String? → imageFile: File?
class UpdateMedicamentUseCase @Inject constructor(
    private val repository: MedicamentRepository
) {
    suspend operator fun invoke(
        id: Int,
        name: String,
        dose: String,
        time: String,
        imageFile: File?
    ): Flow<Result<Medicament>> {
        // Validaciones antes de actualizar
        if (name.isBlank()) {
            return kotlinx.coroutines.flow.flow {
                emit(Result.failure(Exception("El nombre del medicamento es requerido")))
            }
        }

        if (dose.isBlank()) {
            return kotlinx.coroutines.flow.flow {
                emit(Result.failure(Exception("La dosis es requerida")))
            }
        }

        if (time.isBlank()) {
            return kotlinx.coroutines.flow.flow {
                emit(Result.failure(Exception("La hora es requerida")))
            }
        }

        return repository.updateMedicament(id, name, dose, time, imageFile)
    }
}

class DeleteMedicamentUseCase @Inject constructor(
    private val repository: MedicamentRepository
) {
    suspend operator fun invoke(id: Int): Flow<Result<Boolean>> {
        return repository.deleteMedicament(id)
    }
}