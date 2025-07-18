package com.example.practica12.src.features.Calculadora.presentation.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CalculadoraViewModel : ViewModel() {

    private val _texto = MutableStateFlow("0")
    val texto: StateFlow<String> = _texto

    private val _primerNumero = MutableStateFlow("")
    val primerNumero: StateFlow<String> = _primerNumero

    private val _segundoNumero = MutableStateFlow("")
    val segundoNumero: StateFlow<String> = _segundoNumero

    private val _operacion = MutableStateFlow("")
    val operacion: StateFlow<String> = _operacion

    private val _resultado = MutableStateFlow(0.0)
    val resultado: StateFlow<Double> = _resultado

    private val _enOperacion = MutableStateFlow(false)
    val enOperacion: StateFlow<Boolean> = _enOperacion

    fun ingresarNumero(numero: String) {
        val target = if (_enOperacion.value) _segundoNumero else _primerNumero
        target.value += numero
        _texto.value = target.value
    }

    fun ingresarPunto() {
        val target = if (_enOperacion.value) _segundoNumero else _primerNumero
        if (!target.value.contains(".")) {
            target.value = if (target.value.isEmpty()) "0." else target.value + "."
            _texto.value = target.value
        }
    }

    fun seleccionarOperacion(op: String) {
        if (_primerNumero.value.isNotEmpty()) {
            if (_segundoNumero.value.isNotEmpty()) {
                calcular()
                _primerNumero.value = limpiarDecimal(_resultado.value)
                _segundoNumero.value = ""
            }
            _operacion.value = op
            _enOperacion.value = true
        }
    }

    fun calcular() {
        val num1 = _primerNumero.value.toDoubleOrNull()
        val num2 = _segundoNumero.value.toDoubleOrNull()

        if (num1 != null && num2 != null && _operacion.value.isNotEmpty()) {
            _resultado.value = when (_operacion.value) {
                "suma" -> num1 + num2
                "resta" -> num1 - num2
                "multiplicacion" -> num1 * num2
                "division" -> if (num2 != 0.0) num1 / num2 else Double.NaN
                else -> 0.0
            }

            val resultadoStr = if (_resultado.value.isNaN()) {
                "Error"
            } else {
                limpiarDecimal(_resultado.value)
            }

            _texto.value = resultadoStr
            _primerNumero.value = resultadoStr
            _segundoNumero.value = ""
            _operacion.value = ""
            _enOperacion.value = false
        }
    }

    fun limpiar() {
        _texto.value = "0"
        _primerNumero.value = ""
        _segundoNumero.value = ""
        _operacion.value = ""
        _resultado.value = 0.0
        _enOperacion.value = false
    }

    fun borrarUltimo() {
        val target = if (_enOperacion.value) _segundoNumero else _primerNumero
        if (target.value.isNotEmpty()) {
            target.value = target.value.dropLast(1)
            _texto.value = if (target.value.isEmpty()) "0" else target.value
        }
    }

    private fun limpiarDecimal(numero: Double): String {
        return if (numero == numero.toLong().toDouble()) {
            numero.toLong().toString()
        } else {
            numero.toString()
        }
    }
}
