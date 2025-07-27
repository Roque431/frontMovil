package com.example.practica12.src.features.HomeMedicamento.data.datasourse.remote

import com.example.practica12.src.features.HomeMedicamento.data.model.MedicamentResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface MedicamentService {

    @GET("medicaments")
    suspend fun getAllMedicaments(): Response<MedicamentResponseDto>

    @GET("medicaments/{id}")
    suspend fun getMedicamentById(@Path("id") id: Int): Response<MedicamentResponseDto>

    @Multipart
    @POST("medicaments")
    suspend fun createMedicament(
        @Part("name") name: RequestBody,
        @Part("dose") dose: RequestBody,
        @Part("time") time: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<MedicamentResponseDto>

    @Multipart
    @PUT("medicaments/{id}")
    suspend fun updateMedicament(
        @Path("id") id: Int,
        @Part("name") name: RequestBody,
        @Part("dose") dose: RequestBody,
        @Part("time") time: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<MedicamentResponseDto>

    @DELETE("medicaments/{id}")
    suspend fun deleteMedicament(@Path("id") id: Int): Response<MedicamentResponseDto>  // ✅ CAMBIO
}