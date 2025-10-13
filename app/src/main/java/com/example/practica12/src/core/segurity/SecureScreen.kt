package com.example.practica12.src.core.security

import androidx.compose.runtime.*
import com.example.practica12.LocalSecureScreen

@Composable
fun SecureScreen(
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val setSecureScreen = LocalSecureScreen.current

    DisposableEffect(enabled) {
        println("🛡️ SecureScreen: ${if (enabled) "ACTIVANDO" else "DESACTIVANDO"} seguridad")
        setSecureScreen(enabled)

        onDispose {
            if (enabled) {
                println("🛡️ SecureScreen: LIMPIANDO seguridad")
                setSecureScreen(false)
            }
        }
    }

    content()
}