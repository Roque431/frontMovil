package com.example.practica12.src.features.login.data.model

import com.google.gson.annotations.SerializedName
import com.example.practica12.src.features.login.domain.model.LoginRequest

data class LoginRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

// Mapper function
fun LoginRequest.toDto(): LoginRequestDto {
    return LoginRequestDto(
        email = this.email,
        password = this.password
    )
}
