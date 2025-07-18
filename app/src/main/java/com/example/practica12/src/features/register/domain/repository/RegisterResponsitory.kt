package com.example.practica12.src.features.register.domain.repository

import com.example.practica12.src.features.register.domain.model.RegisterRequest
import com.example.practica12.src.features.register.domain.model.RegisterResponse

interface RegisterRepository {
    suspend fun register(registerRequest: RegisterRequest): Result<RegisterResponse>
}