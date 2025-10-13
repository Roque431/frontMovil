package com.example.practica12.src.core.hardware.data

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.practica12.src.core.hardware.domain.CameraRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import java.io.IOException

@Singleton
class CameraRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : CameraRepository {

    override suspend fun createTempImageUri(): Result<Uri> {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = context.cacheDir
            val tempFile = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
            val authority = "${context.packageName}.fileprovider"
            val uri = FileProvider.getUriForFile(context, authority, tempFile)
            Result.success(uri)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun isCameraAvailable(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    override fun cleanTempFiles() {
        val cacheDir = context.cacheDir
        cacheDir.listFiles()?.forEach { file ->
            if (file.name.startsWith("JPEG_")) {
                file.delete()
            }
        }
    }

    override suspend fun saveBitmapToInternalStorage(fileName: String, bitmap: Bitmap): String? {
        return try {
            val file = File(context.filesDir, "$fileName.jpg")
            FileOutputStream(file).use { stream ->
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)) {
                    throw IOException("Couldn't save bitmap.")
                }
            }
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun saveBitmapAsTempFile(bitmap: Bitmap): Result<Uri> {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = context.cacheDir
            val tempFile = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
            tempFile.outputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            val authority = "${context.packageName}.fileprovider"
            val uri = FileProvider.getUriForFile(context, authority, tempFile)
            Result.success(uri)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
