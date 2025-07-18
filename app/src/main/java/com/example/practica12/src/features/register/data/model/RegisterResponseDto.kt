package com.example.practica12.src.features.register.data.model

import com.example.practica12.src.core.domain.model.User
import com.google.gson.annotations.SerializedName
import com.example.practica12.src.features.register.domain.model.RegisterResponse

data class RegisterResponseDto(
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

// Mapper function
fun RegisterResponseDto.toDomain(): RegisterResponse {
    return RegisterResponse(
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