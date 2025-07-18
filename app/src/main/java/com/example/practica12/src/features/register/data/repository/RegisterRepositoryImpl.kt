package com.example.practica12.src.features.register.data.repository

import com.example.practica12.src.features.register.domain.repository.RegisterRepository
import com.example.practica12.src.features.register.domain.model.RegisterRequest
import com.example.practica12.src.features.register.domain.model.RegisterResponse
import com.example.practica12.src.features.register.data.datasource.remote.RegisterService
import com.example.practica12.src.features.register.data.model.toDto
import com.example.practica12.src.features.register.data.model.toDomain
import com.example.practica12.src.core.datastore.DataStoreManager

class RegisterRepositoryImpl(
    private val registerService: RegisterService,
    private val dataStoreManager: DataStoreManager
) : RegisterRepository {

    override suspend fun register(registerRequest: RegisterRequest): Result<RegisterResponse> {
        return try {
            val response = registerService.register(registerRequest.toDto())

            if (response.isSuccessful && response.body() != null) {
                val registerResponse = response.body()!!.toDomain()

                // Auto-login después del registro exitoso
                if (registerResponse.success && registerResponse.token != null && registerResponse.user != null) {
                    dataStoreManager.saveToken(registerResponse.token)
                    dataStoreManager.saveUser(registerResponse.user)
                }

                Result.success(registerResponse)
            } else {
                Result.failure(Exception("Error en el servidor: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
}