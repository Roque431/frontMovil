package com.example.practica12.src.features.HomeMedicamento.presentation.screens

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.practica12.src.features.HomeMedicamento.presentation.viewmodel.AddMedicamentViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicamentScreen(
    navController: NavController,
    viewModel: AddMedicamentViewModel = hiltViewModel(),
    medicamentId: Int? = null // ✅ Para editar medicamento
) {
    var medicamentName by remember { mutableStateOf("") }
    var dose by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val uiState by viewModel.uiState.collectAsState()
    val isEditMode = medicamentId != null

    // ✅ CARGAR DATOS SI ES MODO EDICIÓN
    LaunchedEffect(medicamentId) {
        if (medicamentId != null) {
            println("✏️ AddMedicamentScreen: Cargando medicamento para editar ID: $medicamentId")
            viewModel.loadMedicamentForEdit(medicamentId)
        }
    }

    // ✅ OBSERVAR DATOS PRECARGADOS
    LaunchedEffect(uiState.medicamentToEdit) {
        uiState.medicamentToEdit?.let { medicament ->
            println("✏️ AddMedicamentScreen: Datos cargados para editar: ${medicament.name}")
            medicamentName = medicament.name
            dose = medicament.dose
            time = medicament.time
            // Si hay una imagen local, cargarla para la vista previa
            medicament.imagePath?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    imageUri = Uri.fromFile(file)
                    viewModel.processImageForPreview(imageUri!!)
                }
            }
        }
    }

    // Launchers para imagen
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri = it
            viewModel.processImageForPreview(it)
        }
    }

    val cameraPreviewLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val tempUri = viewModel.saveBitmapAsTempFile(bitmap)
            imageUri = tempUri
            tempUri?.let { viewModel.processImageForPreview(it) }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraPreviewLauncher.launch(null)
        }
    }

    // ✅ MANEJO DE ÉXITO MEJORADO
    if (uiState.isSuccess) {
        LaunchedEffect(uiState.isSuccess) {
            println("✅ AddMedicamentScreen: Operación exitosa, notificando refresh")

            // ✅ NOTIFICAR AL HOME SCREEN QUE DEBE REFRESCAR
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("refresh_needed", true)

            // ✅ RESETEAR ESTADO ANTES DE NAVEGAR
            viewModel.resetSuccess()

            // ✅ NAVEGAR DE VUELTA
            navController.navigateUp()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Editar Medicamento" else "Registrar Medicamento",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6C63FF)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color(0xFF6C63FF)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campos de texto
            OutlinedTextField(
                value = medicamentName,
                onValueChange = { medicamentName = it },
                label = { Text("NOMBRE MEDICAMENTO") },
                placeholder = { Text("antibióticos") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6C63FF),
                    focusedLabelColor = Color(0xFF6C63FF)
                )
            )

            OutlinedTextField(
                value = dose,
                onValueChange = { dose = it },
                label = { Text("DOSIS") },
                placeholder = { Text("1 tableta") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6C63FF),
                    focusedLabelColor = Color(0xFF6C63FF)
                )
            )

            OutlinedTextField(
                value = time,
                onValueChange = { time = it },
                label = { Text("HORA DEL DÍA") },
                placeholder = { Text("12:00 pm") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6C63FF),
                    focusedLabelColor = Color(0xFF6C63FF)
                )
            )

            // Sección de imagen
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Botones para subir foto
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            galleryLauncher.launch("image/*")
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6C63FF)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Subir foto",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Subir foto")
                    }

                    Button(
                        onClick = {
                            if (viewModel.hasPermission()) {
                                cameraPreviewLauncher.launch(null)
                            } else {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6C63FF)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "Tomar foto",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tomar foto")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Preview de imagen
                Card(
                    modifier = Modifier.size(120.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            imageUri != null -> {
                                AsyncImage(
                                    model = imageUri,
                                    contentDescription = "Vista previa",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            isEditMode && uiState.medicamentToEdit?.imageUrl != null -> {
                                AsyncImage(
                                    model = uiState.medicamentToEdit?.imageUrl,
                                    contentDescription = "Imagen actual",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            isEditMode && uiState.medicamentToEdit?.imagePath != null -> {
                                AsyncImage(
                                    model = File(uiState.medicamentToEdit?.imagePath!!),
                                    contentDescription = "Imagen actual",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            else -> {
                                Text(
                                    text = "💊",
                                    fontSize = 48.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Guardar/Actualizar
            Button(
                onClick = {
                    if (isEditMode) {
                        println("✏️ AddMedicamentScreen: Actualizando medicamento ID: $medicamentId")
                        viewModel.updateMedicament(
                            id = medicamentId,
                            name = medicamentName,
                            dose = dose,
                            time = time
                        )
                    } else {
                        println("➕ AddMedicamentScreen: Creando nuevo medicamento")
                        viewModel.saveMedicament(
                            name = medicamentName,
                            dose = dose,
                            time = time
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6C63FF)
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = medicamentName.isNotBlank() && dose.isNotBlank() && time.isNotBlank()
            ) {
                Text(
                    text = if (isEditMode) "Actualizar" else "Guardar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Estados de carga y error
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF6C63FF))
                }
            }

            uiState.errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFFD32F2F)
                    )
                }
            }
        }
    }
}

