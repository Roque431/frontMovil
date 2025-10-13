package com.example.practica12.src.features.login.data.datasourse.remote

import com.example.practica12.src.features.login.data.model.LoginRequestDto
import com.example.practica12.src.features.login.data.model.LoginResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface LoginService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<LoginResponseDto>

    @POST("users/push-token")
    suspend fun enviarPushToken(@Body body: Map<String, String>): Response<Unit>
}