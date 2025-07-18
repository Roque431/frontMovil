package com.example.practica12.src.features.register.domain.model
import com.example.practica12.src.core.domain.model.User

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val user: User? = null,
    val token: String? = null
)
