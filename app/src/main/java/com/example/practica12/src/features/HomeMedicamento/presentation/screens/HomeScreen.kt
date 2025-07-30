package com.example.practica12.src.features.HomeMedicamento.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.practica12.src.core.hardware.data.NetworkChecker
import com.example.practica12.src.features.HomeMedicamento.presentation.components.MedicamentCard
import com.example.practica12.src.features.HomeMedicamento.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val networkChecker = remember { NetworkChecker(context) }
    val isOnline = remember { mutableStateOf(networkChecker.isOnline()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val medicaments by viewModel.medicaments.collectAsState()
    val user by viewModel.user.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    // Monitorear cambios de conexión
    LaunchedEffect(Unit) {
        while (true) {
            val connected = networkChecker.isOnline()
            if (!connected && isOnline.value) {
                isOnline.value = false
            } else if (connected && !isOnline.value) {
                isOnline.value = true
                scope.launch {
                    snackbarHostState.showSnackbar("✅ Conexión restaurada")
                    delay(1000) // Esperamos un poco para que SyncWorker termine
                    viewModel.loadMedicaments() // 🔄 Refrescamos medicamentos desde servidor
                }
            }
            delay(2000)
        }
    }


    LaunchedEffect(Unit) {
        println("🔄 HomeScreen: Recargando medicamentos...")
        viewModel.loadMedicaments()
    }

    LaunchedEffect(navController.currentBackStackEntry) {
        val refreshNeeded = navController.currentBackStackEntry
            ?.savedStateHandle
            ?.get<Boolean>("refresh_needed") == true

        if (refreshNeeded) {
            viewModel.loadMedicaments()
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.set("refresh_needed", false)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Control de Medicamentos",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6C63FF)
                    )
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.logout()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = Color(0xFF6C63FF)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_medicament") },
                containerColor = Color(0xFF6C63FF),
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar medicamento"
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { WelcomeHeader(userName = user?.name ?: "Usuario") }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "💊", fontSize = 60.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Control de Medicamentos",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D2D2D)
                    )
                }
            }

            if (!isOnline.value) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                    ) {
                        Text(
                            text = "⚠️ Sin conexión a internet. Mostrando datos locales.",
                            modifier = Modifier.padding(16.dp),
                            color = Color(0xFFEF6C00)
                        )
                    }
                }
            }

            if (uiState.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF6C63FF))
                    }
                }
            }

            uiState.errorMessage?.let { error ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(16.dp),
                            color = Color(0xFFD32F2F)
                        )
                    }
                }
            }

            if (medicaments.isEmpty() && !uiState.isLoading) {
                item { EmptyMedicamentsView() }
            } else {
                items(medicaments) { medicament ->
                    MedicamentCard(
                        medicament = medicament,
                        onCardClick = {},
                        onEditClick = { navController.navigate("edit_medicament/${it.id}") },
                        onDeleteClick = {
                            println("🗑️ HomeScreen: Eliminando medicamento ${it.id}")
                            viewModel.deleteMedicament(it.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WelcomeHeader(userName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF6C63FF)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(text = "¡Bienvenido!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = userName, fontSize = 16.sp, color = Color.White.copy(alpha = 0.9f))
        }
    }
}

@Composable
fun EmptyMedicamentsView() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "📋", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No tienes medicamentos registrados",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Toca el botón + para agregar tu primer medicamento",
                fontSize = 14.sp,
                color = Color(0xFF999999),
                textAlign = TextAlign.Center
            )
        }
    }
}