package com.example.practica12.src.features.HomeMedicamento.presentation.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practica12.src.features.HomeMedicamento.domain.model.Medicament
import com.example.practica12.src.features.HomeMedicamento.domain.usecase.CreateMedicamentUseCase
import com.example.practica12.src.features.HomeMedicamento.domain.usecase.GetMedicamentByIdUseCase
import com.example.practica12.src.features.HomeMedicamento.domain.usecase.UpdateMedicamentUseCase
import com.example.practica12.src.core.hardware.domain.CameraRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AddMedicamentViewModel @Inject constructor(
    private val createMedicamentUseCase: CreateMedicamentUseCase,
    private val updateMedicamentUseCase: UpdateMedicamentUseCase,
    private val getMedicamentByIdUseCase: GetMedicamentByIdUseCase,
    private val cameraRepository: CameraRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddMedicamentUiState())
    val uiState: StateFlow<AddMedicamentUiState> = _uiState.asStateFlow()

    // MÉTODOS DE CÁMARA
    fun createCameraUri(): Uri? {
        return runBlocking {
            cameraRepository.createTempImageUri().getOrNull()
        }
    }

    fun hasPermission(): Boolean = cameraRepository.hasPermission()

    fun isCameraAvailable(): Boolean = cameraRepository.isCameraAvailable()

    fun saveBitmapAsTempFile(bitmap: Bitmap): Uri? {
        return runBlocking {
            cameraRepository.saveBitmapAsTempFile(bitmap).getOrNull()
        }
    }

    // ✅ CARGAR MEDICAMENTO PARA EDITAR
    fun loadMedicamentForEdit(medicamentId: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                getMedicamentByIdUseCase(medicamentId).collect { result ->
                    result.fold(
                        onSuccess = { medicament ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                medicamentToEdit = medicament,
                                errorMessage = null
                            )
                        },
                        onFailure = { exception ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = "Error al cargar medicamento: ${exception.message}"
                            )
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error: ${e.message}"
                )
            }
        }
    }

    // PROCESAR IMAGEN
    fun processImageForPreview(imageUri: Uri) {
        viewModelScope.launch {
            try {
                println("🔥 PROCESANDO IMAGEN: $imageUri")

                val imageFile = saveImagePermanently(imageUri)

                println("🔥 ARCHIVO CREADO: ${imageFile.absolutePath}")
                println("🔥 ARCHIVO EXISTE: ${imageFile.exists()}")
                println("🔥 TAMAÑO ARCHIVO: ${imageFile.length()} bytes")

                _uiState.value = _uiState.value.copy(
                    imageUri = imageUri,
                    imageFile = imageFile
                )

                println("🔥 ESTADO ACTUALIZADO - imageFile guardado")

            } catch (e: Exception) {
                println("❌ ERROR AL PROCESAR: ${e.message}")
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al procesar imagen: ${e.message}"
                )
            }
        }
    }

    private suspend fun saveImagePermanently(uri: Uri): File {
        return withContext(Dispatchers.IO) {
            println("🔥 GUARDANDO IMAGEN PERMANENTEMENTE DESDE: $uri")

            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = context.getExternalFilesDir(null) // Usar almacenamiento externo para persistencia
            val permanentFile = File(storageDir, "MEDICAMENT_${timeStamp}.jpg")

            println("🔥 RUTA ARCHIVO PERMANENTE: ${permanentFile.absolutePath}")

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(permanentFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            println("🔥 ARCHIVO PERMANENTE GUARDADO EXITOSAMENTE")
            permanentFile
        }
    }

    // ✅ CREAR MEDICAMENTO
    fun saveMedicament(
        name: String,
        dose: String,
        time: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                isSuccess = false
            )

            try {
                val imageFile = _uiState.value.imageFile

                println("🔍 ========== GUARDANDO MEDICAMENTO ==========")
                println("🔍 name: $name")
                println("🔍 dose: $dose")
                println("🔍 time: $time")
                println("🔍 imageFile: ${imageFile?.absolutePath}")
                println("🔍 imageFile exists: ${imageFile?.exists()}")
                println("🔍 imageFile size: ${imageFile?.length()} bytes")
                println("🔍 ==========================================")

                createMedicamentUseCase(
                    name = name.trim(),
                    dose = dose.trim(),
                    time = time.trim(),
                    imageFile = imageFile
                ).collect { result ->
                    result.fold(
                        onSuccess = { medicament ->
                            println("✅ ========== MEDICAMENTO GUARDADO ==========")
                            println("✅ ID: ${medicament.id}")
                            println("✅ NAME: ${medicament.name}")
                            println("✅ IMAGE URL: ${medicament.imageUrl}")
                            println("✅ ==========================================")

                            cleanupTempFiles()
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isSuccess = true,
                                errorMessage = null
                            )
                        },
                        onFailure = { exception ->
                            println("❌ ========== ERROR AL GUARDAR ==========")
                            println("❌ ERROR: ${exception.message}")
                            exception.printStackTrace()
                            println("❌ ====================================")

                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isSuccess = false,
                                errorMessage = exception.message ?: "Error al guardar medicamento"
                            )
                        }
                    )
                }
            } catch (e: Exception) {
                println("❌ ========== EXCEPCIÓN EN SAVE ==========")
                println("❌ EXCEPCIÓN: ${e.message}")
                e.printStackTrace()
                println("❌ ====================================")

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = false,
                    errorMessage = "Error: ${e.message}"
                )
            }
        }
    }

    // ✅ ACTUALIZAR MEDICAMENTO
    fun updateMedicament(
        id: Int,
        name: String,
        dose: String,
        time: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                isSuccess = false
            )

            try {
                val imageFile = _uiState.value.imageFile

                println("🔍 ========== ACTUALIZANDO MEDICAMENTO ==========")
                println("🔍 id: $id")
                println("🔍 name: $name")
                println("🔍 dose: $dose")
                println("🔍 time: $time")
                println("🔍 imageFile: ${imageFile?.absolutePath}")
                println("🔍 ===============================================")

                updateMedicamentUseCase(
                    id = id,
                    name = name.trim(),
                    dose = dose.trim(),
                    time = time.trim(),
                    imageFile = imageFile
                ).collect { result ->
                    result.fold(
                        onSuccess = { medicament ->
                            println("✅ ========== MEDICAMENTO ACTUALIZADO ==========")
                            println("✅ ID: ${medicament.id}")
                            println("✅ NAME: ${medicament.name}")
                            println("✅ IMAGE URL: ${medicament.imageUrl}")
                            println("✅ ============================================")

                            cleanupTempFiles()
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isSuccess = true,
                                errorMessage = null
                            )
                        },
                        onFailure = { exception ->
                            println("❌ ========== ERROR AL ACTUALIZAR ==========")
                            println("❌ ERROR: ${exception.message}")
                            exception.printStackTrace()
                            println("❌ ======================================")

                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isSuccess = false,
                                errorMessage = exception.message ?: "Error al actualizar medicamento"
                            )
                        }
                    )
                }
            } catch (e: Exception) {
                println("❌ ========== EXCEPCIÓN EN UPDATE ==========")
                println("❌ EXCEPCIÓN: ${e.message}")
                e.printStackTrace()
                println("❌ =====================================")

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = false,
                    errorMessage = "Error: ${e.message}"
                )
            }
        }
    }

    private fun cleanupTempFiles() {
        try {
            println("🧹 LIMPIANDO ARCHIVOS TEMPORALES...")
            val deleted = _uiState.value.imageFile?.delete()
            println("🧹 ARCHIVO TEMPORAL ELIMINADO: $deleted")
            cameraRepository.cleanTempFiles()
            println("🧹 LIMPIEZA COMPLETADA")
        } catch (e: Exception) {
            println("⚠️ ERROR EN LIMPIEZA: ${e.message}")
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun resetSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }
}

// ✅ UI STATE ACTUALIZADO
data class AddMedicamentUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val imageUri: Uri? = null,
    val imageFile: File? = null,
    val medicamentToEdit: Medicament? = null // ✅ NUEVO: Para modo edición
)

