package com.example.practica12.src.core.hardware.domain

import android.net.Uri

interface CameraRepository {
    suspend fun createTempImageUri(): Result<Uri>
    fun hasPermission(): Boolean
    fun isCameraAvailable(): Boolean
    fun cleanTempFiles()
    suspend fun saveBitmapAsTempFile(bitmap: android.graphics.Bitmap): Result<Uri> // <-- 🆕 AÑADE ESTA LÍNEA
}