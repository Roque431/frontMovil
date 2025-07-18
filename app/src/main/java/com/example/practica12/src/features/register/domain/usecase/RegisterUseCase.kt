package com.example.practica12.src.features.register.domain.usecase

import com.example.practica12.src.features.register.domain.repository.RegisterRepository
import com.example.practica12.src.features.register.domain.model.RegisterRequest
import com.example.practica12.src.features.register.domain.model.RegisterResponse

class RegisterUseCase(
    private val repository: RegisterRepository
) {
    suspend operator fun invoke(name: String, email: String, password: String): Result<RegisterResponse> {
        // Validaciones para registro
        if (name.isBlank()) {
            return Result.failure(Exception("El nombre no puede estar vacío"))
        }

        if (name.length < 2) {
            return Result.failure(Exception("El nombre debe tener al menos 2 caracteres"))
        }

        if (email.isBlank()) {
            return Result.failure(Exception("El email no puede estar vacío"))
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("Email inválido"))
        }

        if (password.isBlank()) {
            return Result.failure(Exception("La contraseña no puede estar vacía"))
        }

        if (password.length < 6) {
            return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
        }

        if (!password.any { it.isDigit() }) {
            return Result.failure(Exception("La contraseña debe contener al menos un número"))
        }

        if (!password.any { it.isUpperCase() }) {
            return Result.failure(Exception("La contraseña debe contener al menos una mayúscula"))
        }

        val request = RegisterRequest(name, email, password)
        return repository.register(request)
    }
}