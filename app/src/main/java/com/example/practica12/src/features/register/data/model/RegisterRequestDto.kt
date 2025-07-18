package com.example.practica12.src.features.register.data.model

import com.google.gson.annotations.SerializedName
import com.example.practica12.src.features.register.domain.model.RegisterRequest

data class RegisterRequestDto(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

// Mapper function
fun RegisterRequest.toDto(): RegisterRequestDto {
    return RegisterRequestDto(
        name = this.name,
        email = this.email,
        password = this.password
    )
}