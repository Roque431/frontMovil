package com.example.palindromoverificador.src.features.palindromo.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PalindromeScreen() {
    // Estado para el texto a verificar
    var textoInput by remember { mutableStateOf("") }
    // Estado para el resultado
    var resultado by remember { mutableStateOf("El resultado se mostrará aquí") }
    // Estado para el color del resultado
    var colorResultado by remember { mutableStateOf(Color.Gray) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Primera sección: Entrada de texto (33 cm = 330dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(330.dp)
                .background(Color(0xFFE8F5E9)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Ingrese un texto para verificar si es palíndromo",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                TextField(
                    value = textoInput,
                    onValueChange = { textoInput = it },
                    placeholder = { Text("Ejemplo: anita lava la tina") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Segunda sección: Botón de verificación (33 cm = 330dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(330.dp)
                .background(Color(0xFFE3F2FD)),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    if (textoInput.trim().isEmpty()) {
                        resultado = "Por favor, ingrese un texto"
                        colorResultado = Color.Gray
                    } else {
                        // Eliminar espacios y convertir a minúsculas para la verificación
                        val textoNormalizado = textoInput.replace("\\s+".toRegex(), "").lowercase()

                        // Verificar si es palíndromo
                        val esPalindromo = textoNormalizado == textoNormalizado.reversed()

                        if (esPalindromo) {
                            resultado = "\"$textoInput\" ES un palíndromo"
                            colorResultado = Color.Green
                        } else {
                            resultado = "\"$textoInput\" NO es un palíndromo"
                            colorResultado = Color.Red
                        }
                    }
                },
                modifier = Modifier
                    .padding(16.dp)
                    .height(56.dp)
                    .width(220.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "VERIFICAR PALÍNDROMO",
                    fontSize = 16.sp
                )
            }
        }

        // Tercera sección: Resultado (33 cm = 330dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(330.dp)
                .background(Color(0xFFFFF3E0)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = resultado,
                fontSize = 20.sp,
                color = colorResultado,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}
