package com.example.practica12.src.features.login.domain.model
import com.example.practica12.src.core.domain.model.User

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val user: User? = null,
    val token: String? = null
)
