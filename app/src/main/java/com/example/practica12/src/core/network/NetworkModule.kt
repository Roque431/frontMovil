package com.example.practica12.src.core.network

import android.content.Context
import com.example.practica12.src.features.HomeMedicamento.di.MedicamentModule
import com.example.practica12.src.core.network.interceptor.AddTokenInterceptor
import com.example.practica12.src.core.network.interceptor.TokenCaptureInterceptor
import com.example.practica12.src.core.datastore.DataStoreManager
import com.example.practica12.src.core.hardware.di.HardwareModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(
    includes = [
        MedicamentModule::class,
        HardwareModule::class
    ]
)
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        dataStoreManager: DataStoreManager
    ): OkHttpClient {


        val addTokenInterceptor = AddTokenInterceptor(dataStoreManager)
        val tokenCaptureInterceptor = TokenCaptureInterceptor(dataStoreManager)

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(tokenCaptureInterceptor)
            .addInterceptor(addTokenInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://52.201.63.254/api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}