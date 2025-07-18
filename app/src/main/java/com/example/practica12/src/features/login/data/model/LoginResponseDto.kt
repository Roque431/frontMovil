package com.example.practica12.src.features.login.data.model

import com.example.practica12.src.features.login.domain.model.LoginResponse
import com.example.practica12.src.core.domain.model.User
import com.google.gson.annotations.SerializedName

data class LoginResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("user") val user: UserDto? = null,
    @SerializedName("token") val token: String? = null
)

data class UserDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String
)

// Mapper: DTO -> Domain
fun LoginResponseDto.toDomain(): LoginResponse {
    return LoginResponse(
        success = this.success,
        message = this.message,
        user = this.user?.let {
            User(
                id = it.id,
                name = it.name,
                email = it.email
            )
        },
        token = this.token
    )
}