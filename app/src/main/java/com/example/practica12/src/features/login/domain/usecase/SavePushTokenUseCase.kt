package com.example.practica12.src.features.login.domain.usecase

import com.example.practica12.src.features.login.domain.repository.LoginRepository

class EnviarPushTokenUseCase(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(pushToken: String): Result<Unit> {
        return repository.enviarPushToken(pushToken)
    }
}
