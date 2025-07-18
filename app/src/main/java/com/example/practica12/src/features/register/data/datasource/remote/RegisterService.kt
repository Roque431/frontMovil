package com.example.practica12.src.features.register.data.datasource.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import com.example.practica12.src.features.register.data.model.RegisterRequestDto
import com.example.practica12.src.features.register.data.model.RegisterResponseDto

interface RegisterService {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): Response<RegisterResponseDto>
}