package com.example.practica12.src.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.practica12.src.features.login.presentation.screen.LoginScreen
import com.example.practica12.src.features.register.presentation.screen.RegisterScreen
import com.example.practica12.src.features.HomeMedicamento.presentation.screens.HomeScreen
import com.example.practica12.src.features.HomeMedicamento.presentation.screens.AddMedicamentScreen

@Composable
fun NavigationWrapper(startDestination: String = "login") {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("register") {
            RegisterScreen(navController = navController)
        }

        composable("home") {
            HomeScreen(navController = navController) // 🆕 AHORA SÍ CONECTADO
        }

        // 🆕 RUTAS DE MEDICAMENTOS
        composable("medicamentos") {
            HomeScreen(navController = navController)
        }

        composable("add_medicament") {
            AddMedicamentScreen(navController = navController)
        }

        composable("edit_medicament/{medicamentId}") { backStackEntry ->
            val medicamentId = backStackEntry.arguments?.getString("medicamentId")?.toIntOrNull()
            AddMedicamentScreen(
                navController = navController,
                medicamentId = medicamentId // ✅ Pasar ID para editar
            )
        }

        composable("counter") {

        }

        composable("flashlight") {

        }

        composable("rickandmorty") {

        }
    }
}