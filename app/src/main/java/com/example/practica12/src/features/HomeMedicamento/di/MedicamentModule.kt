package com.example.practica12.features.HomeMedicamento.di

import android.content.Context
import androidx.room.Room
import com.example.practica12.src.core.hardware.data.NetworkChecker
import dagger.hilt.android.qualifiers.ApplicationContext
import com.example.practica12.src.features.HomeMedicamento.data.datasourse.remote.MedicamentService
import com.example.practica12.src.features.HomeMedicamento.data.local.dao.MedicamentoDao
import com.example.practica12.src.features.HomeMedicamento.data.local.database.AppDatabase
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

    // 🌐 Retrofit: Servicio remoto
    @Provides
    @Singleton
    fun provideMedicamentService(retrofit: Retrofit): MedicamentService {
        return retrofit.create(MedicamentService::class.java)
    }

    // 🧩 Room: Base de datos
    @Provides
    @Singleton
    fun provideDatabase( @ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "medicamentos_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    // 🧩 Room: DAO
    @Provides
    fun provideMedicamentoDao(db: AppDatabase): MedicamentoDao {
        return db.medicamentoDao()
    }

    // 🔁 Repositorio
    @Provides
    @Singleton
    fun provideMedicamentRepository(
        medicamentRepositoryImpl: MedicamentRepositoryImpl
    ): MedicamentRepository {
        return medicamentRepositoryImpl
    }

    // 🧠 Use Cases
    @Provides
    @Singleton
    fun provideGetAllMedicamentsUseCase(
        repository: MedicamentRepository,
        networkChecker: NetworkChecker
    ): GetAllMedicamentsUseCase {
        return GetAllMedicamentsUseCase(repository, networkChecker)
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
