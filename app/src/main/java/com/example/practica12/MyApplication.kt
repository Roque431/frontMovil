package com.example.practica12

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.practica12.src.core.work.NetworkWatcher
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var networkWatcher: NetworkWatcher

    override fun onCreate() {
        super.onCreate()

        // Inicializar Firebase
        FirebaseApp.initializeApp(this)

        // Obtener token de FCM
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM", "Token manual: $token")
                val prefs = getSharedPreferences("chimeup_prefs", MODE_PRIVATE)
                prefs.edit().putString("fcm_token", token).apply()
            } else {
                Log.e("FCM", "Error al obtener el token FCM", task.exception)
            }
        }

        // Registrar callback de red
        networkWatcher.registerNetworkCallback()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
}
