package com.example.practica12.src.features.HomeMedicamento.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.practica12.src.core.datastore.DataStoreManager
import com.example.practica12.src.core.domain.model.User
import com.example.practica12.src.core.hardware.domain.GpsStatus
import com.example.practica12.src.core.hardware.domain.LocationProvider
import com.example.practica12.src.core.hardware.data.NetworkChecker
import com.example.practica12.src.features.HomeMedicamento.data.work.SyncWorker
import com.example.practica12.src.features.HomeMedicamento.domain.model.Medicament
import com.example.practica12.src.features.HomeMedicamento.domain.usecase.DeleteMedicamentUseCase
import com.example.practica12.src.features.HomeMedicamento.domain.usecase.GetAllMedicamentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    private val getAllMedicamentsUseCase: GetAllMedicamentsUseCase,
    private val deleteMedicamentUseCase: DeleteMedicamentUseCase,
    private val dataStoreManager: DataStoreManager,
    private val networkChecker: NetworkChecker,
    private val locationProvider: LocationProvider // <-- Inyección del nuevo proveedor
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _medicaments = MutableStateFlow<List<Medicament>>(emptyList())
    val medicaments: StateFlow<List<Medicament>> = _medicaments.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    // Nuevo StateFlow para el estado del GPS
    private val _gpsStatus = MutableStateFlow(GpsStatus.CHECKING)
    val gpsStatus: StateFlow<GpsStatus> = _gpsStatus.asStateFlow()

    init {
        loadUser()
        checkToken()

        // Observar el estado de la red
        viewModelScope.launch {
            networkChecker.networkStatus.collect { isOnline ->
                _uiState.value = _uiState.value.copy(isOnline = isOnline)
                Log.d("NetworkStatus", "📡 ¿Online? $isOnline")
                if (isOnline) {
                    scheduleSyncWorker()
                    loadMedicaments()
                } else {
                    loadMedicamentsLocal()
                }
            }
        }
        loadMedicamentsLocal()


        viewModelScope.launch {
            while (true) {
                locationProvider.checkLocationStatus()

                _gpsStatus.value = locationProvider.gpsStatus.value
                Log.d("GpsStatus", " Estado del GPS actualizado a: ${_gpsStatus.value}")
                delay(5000) //
            }
        }
    }

    // --- El resto de tus funciones permanecen igual ---

    private fun scheduleSyncWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val syncWork = OneTimeWorkRequestBuilder<SyncWorker>().setConstraints(constraints).build()
        WorkManager.getInstance(getApplication()).enqueue(syncWork)
        Log.d("SyncWorker", "🚀 Se lanzó el Worker para sincronizar")
    }

    private fun checkToken() {
        viewModelScope.launch {
            dataStoreManager.getToken().collect { token ->
                Log.d("HomeViewModel", "🔍 Token guardado: ${token ?: "NO HAY TOKEN"}")
            }
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            dataStoreManager.getUser().collect { user ->
                _user.value = user
            }
        }
    }

    fun loadMedicaments() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            getAllMedicamentsUseCase().collect { result ->
                result.fold(
                    onSuccess = { medicaments ->
                        _medicaments.value = medicaments
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = exception.message)
                    }
                )
            }
        }
    }

    fun loadMedicamentsLocal() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            getAllMedicamentsUseCase().collect { result ->
                result.fold(
                    onSuccess = { medicaments ->
                        _medicaments.value = medicaments
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = exception.message)
                    }
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            dataStoreManager.logout()
        }
    }

    fun deleteMedicament(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            deleteMedicamentUseCase(id).collect { result ->
                result.fold(
                    onSuccess = { loadMedicaments() },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = exception.message)
                    }
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isOnline: Boolean = true
)
