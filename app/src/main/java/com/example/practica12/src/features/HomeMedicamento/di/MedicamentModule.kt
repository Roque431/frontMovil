package com.example.practica12.src.features.HomeMedicamento.di

import com.example.practica12.src.features.HomeMedicamento.data.datasourse.remote.MedicamentService
import com.example.practica12.src.features.HomeMedicamento.data.repository.MedicamentRepositoryImpl
import com.example.practica12.src.features.HomeMedicamento.domain.repository.MedicamentRepository
import com.example.practica12.src.features.HomeMedicamento.domain.usecase.CreateMedicamentUseCase
import com.example.practica12.src.features.HomeMedicamento.domain.usecase.DeleteMedicamentUseCase
import com.example.practica12.src.features.HomeMedicamento.domain.usecase.GetAllMedicamentsUseCase
import com.example.practica12.src.features.HomeMedicamento.domain.usecase.GetMedicamentByIdUseCase
import com.example.practica12.src.features.HomeMedicamento.domain.usecase.UpdateMedicamentUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MedicamentModule {

    @Provides
    @Singleton
    fun provideMedicamentService(retrofit: Retrofit): MedicamentService {
        return retrofit.create(MedicamentService::class.java)
    }

    @Provides
    @Singleton
    fun provideMedicamentRepository(
        medicamentRepositoryImpl: MedicamentRepositoryImpl
    ): MedicamentRepository {
        return medicamentRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideGetAllMedicamentsUseCase(
        repository: MedicamentRepository
    ): GetAllMedicamentsUseCase {
        return GetAllMedicamentsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetMedicamentByIdUseCase(
        repository: MedicamentRepository
    ): GetMedicamentByIdUseCase {
        return GetMedicamentByIdUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideCreateMedicamentUseCase(
        repository: MedicamentRepository
    ): CreateMedicamentUseCase {
        return CreateMedicamentUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideUpdateMedicamentUseCase(
        repository: MedicamentRepository
    ): UpdateMedicamentUseCase {
        return UpdateMedicamentUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteMedicamentUseCase(
        repository: MedicamentRepository
    ): DeleteMedicamentUseCase {
        return DeleteMedicamentUseCase(repository)
    }
}