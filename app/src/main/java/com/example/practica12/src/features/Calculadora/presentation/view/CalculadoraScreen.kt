package com.example.calculadora

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.practica12.src.features.Calculadora.presentation.viewModel.CalculadoraViewModel

@Composable
fun CalculadoraScreen(
    viewModel: CalculadoraViewModel = viewModel()
) {
    // Variables de estado para la interfaz
    val displayText by viewModel.texto.collectAsState(initial = "0")
    val operacion by viewModel.operacion.collectAsState(initial = "")
    val primerNumero by viewModel.primerNumero.collectAsState(initial = "")
    val segundoNumero by viewModel.segundoNumero.collectAsState(initial = "")
    val resultado by viewModel.resultado.collectAsState(initial = 0.0)
    val operacionEnCurso by viewModel.operacion.collectAsState(initial = false)

    // Diseño de la calculadora
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Pantalla de la calculadora
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
                .padding(16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = displayText,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Fila de botones para operaciones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OperacionButton(
                text = "C",
                color = Color(0xFFFF5722),
                modifier = Modifier.weight(1f)
            ) {
                viewModel.limpiar()
            }
            OperacionButton(
                text = "÷",
                color = Color(0xFF2196F3),
                modifier = Modifier.weight(1f)
            ) {
                viewModel.seleccionarOperacion("division")
            }
            OperacionButton(
                text = "×",
                color = Color(0xFF2196F3),
                modifier = Modifier.weight(1f)
            ) {
                viewModel.seleccionarOperacion("multiplicacion")
            }
            OperacionButton(
                text = "⌫",
                color = Color(0xFFFF5722),
                modifier = Modifier.weight(1f)
            ) {
                viewModel.borrarUltimo()
            }
        }

        // Filas de botones numéricos
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NumeroButton(text = "7", modifier = Modifier.weight(1f)) {
                viewModel.ingresarNumero("7")
            }
            NumeroButton(text = "8", modifier = Modifier.weight(1f)) {
                viewModel.ingresarNumero("8")
            }
            NumeroButton(text = "9", modifier = Modifier.weight(1f)) {
                viewModel.ingresarNumero("9")
            }
            OperacionButton(
                text = "-",
                color = Color(0xFF2196F3),
                modifier = Modifier.weight(1f)
            ) {
                viewModel.seleccionarOperacion("resta")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NumeroButton(text = "4", modifier = Modifier.weight(1f)) {
                viewModel.ingresarNumero("4")
            }
            NumeroButton(text = "5", modifier = Modifier.weight(1f)) {
                viewModel.ingresarNumero("5")
            }
            NumeroButton(text = "6", modifier = Modifier.weight(1f)) {
                viewModel.ingresarNumero("6")
            }
            OperacionButton(
                text = "+",
                color = Color(0xFF2196F3),
                modifier = Modifier.weight(1f)
            ) {
                viewModel.seleccionarOperacion("suma")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NumeroButton(text = "1", modifier = Modifier.weight(1f)) {
                viewModel.ingresarNumero("1")
            }
            NumeroButton(text = "2", modifier = Modifier.weight(1f)) {
                viewModel.ingresarNumero("2")
            }
            NumeroButton(text = "3", modifier = Modifier.weight(1f)) {
                viewModel.ingresarNumero("3")
            }
            OperacionButton(
                text = "=",
                color = Color(0xFF4CAF50),
                modifier = Modifier
                    .weight(1f)
                    .height(140.dp)
            ) {
                viewModel.calcular()
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NumeroButton(
                text = "0",
                modifier = Modifier.weight(2f)
            ) {
                viewModel.ingresarNumero("0")
            }
            NumeroButton(
                text = ".",
                modifier = Modifier.weight(1f)
            ) {
                viewModel.ingresarPunto()
            }
        }
    }
}

@Composable
fun NumeroButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(64.dp)
            .aspectRatio(1f),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2E2E2E),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun OperacionButton(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(64.dp)
            .aspectRatio(1f),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
