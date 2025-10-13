package com.example.practica12.src.features.login.domain.repository

import com.example.practica12.src.features.login.domain.model.LoginRequest
import com.example.practica12.src.features.login.domain.model.LoginResponse

interface LoginRepository {
    suspend fun login(loginRequest: LoginRequest): Result<LoginResponse>

    suspend fun enviarPushToken(pushToken: String): Result<Unit>
}