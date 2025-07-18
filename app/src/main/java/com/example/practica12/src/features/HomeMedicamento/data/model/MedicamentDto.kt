package com.example.practica12.src.features.HomeMedicamento.data.model

import com.google.gson.annotations.SerializedName

data class MedicamentDto(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("name")
    val name: String,
    @SerializedName("dose")
    val dose: String,
    @SerializedName("time")
    val time: String,
    @SerializedName("image_url")
    val imageUrl: String? = null,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

data class MedicamentResponseDto(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("medicament")
    val medicament: MedicamentDto? = null,
    @SerializedName("medicaments")
    val medicaments: List<MedicamentDto>? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("error")
    val error: String? = null
)


