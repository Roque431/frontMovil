package com.example.practica12.src.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.practica12.src.core.domain.model.User
import kotlinx.coroutines.flow.first
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {


    suspend fun saveKey(key: Preferences.Key<String>, value: String) {
        context.dataStore.edit { prefs ->
            prefs[key] = value
        }
    }

    fun getKey(key: Preferences.Key<String>): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[key]
        }
    }

    suspend fun removeKey(key: Preferences.Key<String>) {
        context.dataStore.edit { prefs ->
            prefs.remove(key)
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }


    suspend fun saveToken(token: String) {
        saveKey(PreferenceKeys.TOKEN, token)
    }

    fun getToken(): Flow<String?> {
        return getKey(PreferenceKeys.TOKEN)
    }

    suspend fun clearToken() {
        removeKey(PreferenceKeys.TOKEN)
    }


    suspend fun saveUser(user: User) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.USER_ID] = user.id
            prefs[PreferenceKeys.USER_NAME] = user.name
            prefs[PreferenceKeys.USER_EMAIL] = user.email
        }
    }

    fun getUser(): Flow<User?> {
        return context.dataStore.data.map { prefs ->
            val id = prefs[PreferenceKeys.USER_ID]
            val name = prefs[PreferenceKeys.USER_NAME]
            val email = prefs[PreferenceKeys.USER_EMAIL]

            if (id != null && name != null && email != null) {
                User(id = id, name = name, email = email)
            } else {
                null
            }
        }
    }

    suspend fun clearUser() {
        context.dataStore.edit { prefs ->
            prefs.remove(PreferenceKeys.USER_ID)
            prefs.remove(PreferenceKeys.USER_NAME)
            prefs.remove(PreferenceKeys.USER_EMAIL)
        }
    }



    suspend fun logout() {
        clearToken()
        clearUser()
    }


    suspend fun isLoggedIn(): Boolean {
        return getToken().map { !it.isNullOrEmpty() }.first()
    }
}