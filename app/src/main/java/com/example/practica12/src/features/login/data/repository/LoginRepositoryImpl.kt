package com.example.practica12.src.features.login.data.repository

import com.example.practica12.src.features.login.domain.repository.LoginRepository
import com.example.practica12.src.features.login.domain.model.LoginRequest
import com.example.practica12.src.features.login.domain.model.LoginResponse
import com.example.practica12.src.features.login.data.model.toDto
import com.example.practica12.src.features.login.data.model.toDomain
import com.example.practica12.src.core.datastore.DataStoreManager
import com.example.practica12.src.features.login.data.datasourse.remote.LoginService

class LoginRepositoryImpl(
    private val loginService: LoginService,
    private val dataStoreManager: DataStoreManager
) : LoginRepository {

    override suspend fun login(loginRequest: LoginRequest): Result<LoginResponse> {
        return try {
            val response = loginService.login(loginRequest.toDto())

            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!.toDomain()

                // Guardar en DataStore si el login es exitoso
                if (loginResponse.success && loginResponse.token != null && loginResponse.user != null) {
                    dataStoreManager.saveToken(loginResponse.token)
                    dataStoreManager.saveUser(loginResponse.user)
                }

                Result.success(loginResponse)
            } else {
                Result.failure(Exception("Error en el servidor: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
}