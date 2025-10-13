package com.example.practica12.src.core.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.practica12.src.core.security.SecureScreen
import com.example.practica12.src.features.HomeMedicamento.presentation.screens.AddMedicamentScreen
import com.example.practica12.src.features.HomeMedicamento.presentation.screens.HomeScreen
import com.example.practica12.src.features.login.presentation.screen.LoginScreen

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryFlow
        .collectAsState(initial = navController.currentBackStackEntry)

    // ✅ ACTIVAR SEGURIDAD SOLO SI NO ESTAMOS EN LOGIN
    val shouldBeSecure = remember(currentRoute.value) {
        currentRoute.value?.destination?.route != "login"
    }

    // ✅ ENVOLVER TODO EN SecureScreen EXCEPTO LOGIN
    SecureScreen(enabled = shouldBeSecure) {
        NavHost(
            navController = navController,
            startDestination = "login"
        ) {
            // ❌ Login SIN protección (permite capturas)
            composable("login") {
                LoginScreen(navController = navController)
            }

            // ✅ Home protegido (seguridad manejada por NavigationWrapper)
            composable("home") {
                HomeScreen(navController = navController)
            }

            // ✅ Add Medicament protegido (seguridad manejada por NavigationWrapper)
            composable("add_medicament") {
                AddMedicamentScreen(
                    navController = navController,
                    medicamentId = null
                )
            }

            // ✅ Edit Medicament protegido (seguridad manejada por NavigationWrapper)
            composable(
                route = "edit_medicament/{medicamentId}",
                arguments = listOf(
                    navArgument("medicamentId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val medicamentId = backStackEntry.arguments?.getInt("medicamentId")
                AddMedicamentScreen(
                    navController = navController,
                    medicamentId = medicamentId
                )
            }
        }
    }
}