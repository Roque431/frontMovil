package com.example.practica12.src.core.hardware.di


import com.example.practica12.src.core.hardware.data.CameraRepositoryImpl
import com.example.practica12.src.core.hardware.domain.CameraRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HardwareModule {

    @Binds
    @Singleton
    abstract fun bindCameraRepository(
        // El parámetro DEBE ser la clase de implementación
        // Hilt sabrá cómo crear CameraRepositoryImpl porque tiene @Inject constructor()
        repositoryImpl: CameraRepositoryImpl
    ): CameraRepository // El valor de retorno DEBE ser la interfaz
}
