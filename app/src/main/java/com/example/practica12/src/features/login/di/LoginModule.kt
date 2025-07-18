package com.example.practica12.src.features.login.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import com.example.practica12.src.core.datastore.DataStoreManager
import com.example.practica12.src.features.login.data.datasourse.remote.LoginService
import com.example.practica12.src.features.login.data.repository.LoginRepositoryImpl
import com.example.practica12.src.features.login.domain.repository.LoginRepository
import com.example.practica12.src.features.login.domain.usecase.LoginUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoginModule {

    @Provides
    @Singleton
    fun provideLoginService(retrofit: Retrofit): LoginService {
        return retrofit.create(LoginService::class.java)
    }

    @Provides
    @Singleton
    fun provideLoginRepository(
        loginService: LoginService,
        dataStoreManager: DataStoreManager
    ): LoginRepository {
        return LoginRepositoryImpl(loginService, dataStoreManager)
    }

    @Provides
    fun provideLoginUseCase(repository: LoginRepository): LoginUseCase {
        return LoginUseCase(repository)
    }
}