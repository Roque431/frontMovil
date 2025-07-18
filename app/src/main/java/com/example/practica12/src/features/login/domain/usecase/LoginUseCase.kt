package com.example.practica12.src.features.login.domain.usecase

import com.example.practica12.src.features.login.domain.model.LoginRequest
import com.example.practica12.src.features.login.domain.model.LoginResponse
import com.example.practica12.src.features.login.domain.repository.LoginRepository

class LoginUseCase(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<LoginResponse> {
        // Validaciones
        if (email.isBlank()) {
            return Result.failure(Exception("El email no puede estar vacío"))
        }

        if (password.isBlank()) {
            return Result.failure(Exception("La contraseña no puede estar vacía"))
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("Email inválido"))
        }

        val request = LoginRequest(email, password)
        return repository.login(request)
    }
}
