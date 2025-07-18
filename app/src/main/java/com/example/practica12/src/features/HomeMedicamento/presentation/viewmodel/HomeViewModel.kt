package com.example.practica12.src.features.HomeMedicamento.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practica12.src.core.datastore.DataStoreManager
import com.example.practica12.src.core.domain.model.User
import com.example.practica12.src.features.HomeMedicamento.domain.model.Medicament
import com.example.practica12.src.features.HomeMedicamento.domain.usecase.DeleteMedicamentUseCase
import com.example.practica12.src.features.HomeMedicamento.domain.usecase.GetAllMedicamentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllMedicamentsUseCase: GetAllMedicamentsUseCase,
    private val deleteMedicamentUseCase: DeleteMedicamentUseCase,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _medicaments = MutableStateFlow<List<Medicament>>(emptyList())
    val medicaments: StateFlow<List<Medicament>> = _medicaments.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    init {
        loadUser()
        checkToken()
        loadMedicaments()
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
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = null
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Error desconocido"
                        )
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
                    onSuccess = {
                        // Recargar la lista después de eliminar
                        loadMedicaments()
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Error al eliminar medicamento"
                        )
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
    val errorMessage: String? = null
)