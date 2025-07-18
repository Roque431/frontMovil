package com.example.practica12.src.core.network.interceptor

import android.util.Log
import com.example.practica12.src.core.datastore.DataStoreManager
import com.example.practica12.src.core.datastore.PreferenceKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject

class TokenCaptureInterceptor(
    private val dataStoreManager: DataStoreManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)


        val url = request.url.toString()
        if (url.contains("/auth/login") || url.contains("/auth/register")) {

            Log.d("TokenCaptureInterceptor", "🔍 Interceptando respuesta de auth")


            val responseBody = response.peekBody(Long.MAX_VALUE)
            val responseString = responseBody.string()

            try {
                val jsonObject = JSONObject(responseString)


                if (jsonObject.optBoolean("success", false)) {
                    val token = jsonObject.optString("token")
                    val userObject = jsonObject.optJSONObject("user")

                    Log.d("TokenCaptureInterceptor", "🔍 Token encontrado: ${token.take(20)}...")

                    if (token.isNotEmpty()) {

                        CoroutineScope(Dispatchers.IO).launch {
                            dataStoreManager.saveKey(PreferenceKeys.TOKEN, token)
                            Log.d("TokenCaptureInterceptor", "✅ Token guardado en DataStore")

                            userObject?.let { user ->
                                val userId = user.optString("id")
                                val userName = user.optString("name")
                                val userEmail = user.optString("email")

                                if (userId.isNotEmpty()) {
                                    dataStoreManager.saveKey(PreferenceKeys.USER_ID, userId)
                                }
                                if (userName.isNotEmpty()) {
                                    dataStoreManager.saveKey(PreferenceKeys.USER_NAME, userName)
                                }
                                if (userEmail.isNotEmpty()) {
                                    dataStoreManager.saveKey(PreferenceKeys.USER_EMAIL, userEmail)
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("TokenCaptureInterceptor", "Error al parsear respuesta", e)
            }
        }

        return response
    }
}