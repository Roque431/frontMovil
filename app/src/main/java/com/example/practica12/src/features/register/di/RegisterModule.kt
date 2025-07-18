package com.example.practica12.src.features.register.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import com.example.practica12.src.core.datastore.DataStoreManager
import com.example.practica12.src.features.register.data.datasource.remote.RegisterService
import com.example.practica12.src.features.register.data.repository.RegisterRepositoryImpl
import com.example.practica12.src.features.register.domain.repository.RegisterRepository
import com.example.practica12.src.features.register.domain.usecase.RegisterUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RegisterModule {

    @Provides
    @Singleton
    fun provideRegisterService(retrofit: Retrofit): RegisterService {
        return retrofit.create(RegisterService::class.java)
    }

    @Provides
    @Singleton
    fun provideRegisterRepository(
        registerService: RegisterService,
        dataStoreManager: DataStoreManager
    ): RegisterRepository {
        return RegisterRepositoryImpl(registerService, dataStoreManager)
    }

    @Provides
    fun provideRegisterUseCase(repository: RegisterRepository): RegisterUseCase {
        return RegisterUseCase(repository)
    }
}