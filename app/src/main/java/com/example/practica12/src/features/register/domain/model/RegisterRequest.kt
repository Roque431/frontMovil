package com.example.practica12.src.features.register.domain.model

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)
