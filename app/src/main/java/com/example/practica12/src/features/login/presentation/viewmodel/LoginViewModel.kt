package com.example.practica12.src.features.login.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practica12.src.core.datastore.DataStoreManager
import com.example.practica12.src.core.domain.model.User
import com.example.practica12.src.features.login.domain.usecase.LoginUseCase
import com.example.practica12.src.features.login.domain.usecase.EnviarPushTokenUseCase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val enviarPushTokenUseCase: EnviarPushTokenUseCase,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    init {
        checkIfLoggedIn()
    }

    private fun checkIfLoggedIn() {
        viewModelScope.launch {
            val token = dataStoreManager.getToken().first()
            if (!token.isNullOrEmpty()) {
                val user = dataStoreManager.getUser().first()
                _uiState.value = _uiState.value.copy(
                    isSuccess = true,
                    user = user,
                    token = token
                )
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            loginUseCase(email, password).fold(
                onSuccess = { response ->
                    if (response.success && response.user != null) {
                        // Guardar en DataStore
                        response.token?.let { token ->
                            viewModelScope.launch {
                                dataStoreManager.saveToken(token)
                                response.user.let { user ->
                                    dataStoreManager.saveUser(user)
                                }
                            }
                        }

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            user = response.user,
                            token = response.token,
                            error = null
                        )

                        // ✅ Enviar token FCM al backend
                        FirebaseMessaging.getInstance().token.addOnSuccessListener { pushToken ->
                            viewModelScope.launch {
                                enviarPushTokenUseCase(pushToken).fold(
                                    onSuccess = {
                                        Log.d("FCM", "✅ Token FCM enviado al backend")
                                    },
                                    onFailure = { e ->
                                        Log.e("FCM", "❌ Error al enviar token FCM: ${e.message}")
                                    }
                                )
                            }
                        }

                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = false,
                            error = response.message
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        error = exception.message ?: "Error desconocido"
                    )
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            dataStoreManager.logout()
            _uiState.value = LoginUiState()
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val user: User? = null,
    val token: String? = null,
    val error: String? = null
)
