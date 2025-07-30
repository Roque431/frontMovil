package com.example.practica12.src.core.hardware.di

import android.content.Context
import com.example.practica12.src.core.hardware.data.CameraRepositoryImpl
import com.example.practica12.src.core.hardware.data.NetworkChecker
import com.example.practica12.src.core.hardware.domain.CameraRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HardwareModule {

    @Binds
    @Singleton
    abstract fun bindCameraRepository(
        repositoryImpl: CameraRepositoryImpl
    ): CameraRepository

    companion object {

        @Provides
        @Singleton
        fun provideNetworkChecker(
            @ApplicationContext context: Context
        ): NetworkChecker {
            return NetworkChecker(context)
        }
    }
}
