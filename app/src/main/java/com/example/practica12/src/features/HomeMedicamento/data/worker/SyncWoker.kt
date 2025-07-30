package com.example.practica12.src.features.HomeMedicamento.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.practica12.src.core.hardware.data.NetworkChecker
import com.example.practica12.src.features.HomeMedicamento.data.datasourse.remote.MedicamentService
import com.example.practica12.src.features.HomeMedicamento.data.local.dao.MedicamentoDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val medicamentoDao: MedicamentoDao,
    private val medicamentService: MedicamentService,
    private val networkChecker: NetworkChecker
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            if (!networkChecker.isOnline()) {
                println("📡 Sin conexión, se reintentará luego.")
                return@withContext Result.retry()
            }

            val pendientes = medicamentoDao.getNoSincronizados()
            var sincronizados = 0

            pendientes.forEach { localMed ->
                try {
                    val nameBody = localMed.nombre.toRequestBody("text/plain".toMediaTypeOrNull())
                    val doseBody = localMed.dosis.toRequestBody("text/plain".toMediaTypeOrNull())
                    val timeBody = localMed.hora.toRequestBody("text/plain".toMediaTypeOrNull())

                    val imagePart: MultipartBody.Part? = localMed.imagePath?.let { path ->
                        val file = File(path)
                        if (file.exists()) {
                            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                            MultipartBody.Part.createFormData("image", file.name, requestFile)
                        } else {
                            println("⚠️ Imagen no encontrada en $path")
                            null
                        }
                    }

                    val response = medicamentService.createMedicament(
                        name = nameBody,
                        dose = doseBody,
                        time = timeBody,
                        image = imagePart
                    )

                    if (response.isSuccessful) {
                        val serverMed = response.body()?.medicament
                        if (serverMed != null) {
                            println("✅ Medicamento sincronizado: ${serverMed.name}")
                            medicamentoDao.actualizar(
                                localMed.copy(
                                    id = serverMed.id,
                                    isSynced = true
                                )
                            )
                            sincronizados++
                        }
                    } else {
                        println("❌ Falló la sincronización de ${localMed.nombre}")
                    }

                } catch (e: Exception) {
                    println("🚨 Error sincronizando ${localMed.nombre}: ${e.message}")
                }
            }

            if (sincronizados > 0) {
                val prefs = applicationContext.getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)
                prefs.edit().putBoolean("synced_recently", true).apply()
                println("☁️ Se sincronizaron $sincronizados medicamento(s) local(es).")
            } else {
                println("📭 No hubo medicamentos nuevos para sincronizar.")
            }

            return@withContext Result.success()

        } catch (e: Exception) {
            println("❌ Error general en SyncWorker: ${e.message}")
            return@withContext Result.failure()
        }
    }
}
