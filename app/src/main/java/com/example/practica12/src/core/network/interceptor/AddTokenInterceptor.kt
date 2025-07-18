package com.example.practica12.src.core.network.interceptor

import android.util.Log
import com.example.practica12.src.core.datastore.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AddTokenInterceptor(
    private val dataStoreManager: DataStoreManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        Log.d("AddTokenInterceptor", "🔍 Request URL: ${originalRequest.url}")


        val url = originalRequest.url.toString()
        if (url.contains("/auth/login") || url.contains("/auth/register")) {
            Log.d("AddTokenInterceptor", "⏭️ Saltando auth endpoints")
            return chain.proceed(originalRequest)
        }


        val token = runBlocking {
            dataStoreManager.getToken().first()
        }

        Log.d("AddTokenInterceptor", "🔍 Token obtenido: ${token?.take(20) ?: "NO HAY TOKEN"}")


        if (token.isNullOrEmpty()) {
            Log.d("AddTokenInterceptor", "❌ No hay token, proceeding sin Authorization")
            return chain.proceed(originalRequest)
        }


        val newRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        Log.d("AddTokenInterceptor", "✅ Token agregado a request")

        return chain.proceed(newRequest)
    }
}