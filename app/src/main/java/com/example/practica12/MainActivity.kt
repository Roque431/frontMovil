package com.example.practica12

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.practica12.src.core.navigation.NavigationWrapper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var secureScreenEnabled = false

    fun setSecureScreen(enable: Boolean) {
        if (enable && !secureScreenEnabled) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
            secureScreenEnabled = true
            println("🛡️ MainActivity: SEGURIDAD ACTIVADA")
        } else if (!enable && secureScreenEnabled) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
            secureScreenEnabled = false
            println("🛡️ MainActivity: SEGURIDAD DESACTIVADA")
        } else {
            println("🛡️ MainActivity: No se realizó cambio. Estado actual: $secureScreenEnabled, solicitado: $enable")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CompositionLocalProvider(
                LocalSecureScreen provides { enable -> setSecureScreen(enable) }
            ) {
                NavigationWrapper()
            }
        }
    }
}

val LocalSecureScreen = compositionLocalOf<(Boolean) -> Unit> { { } }