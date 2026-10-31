package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "admin_settings")

class AdminManager(private val context: Context) {
    private val passwordKey = stringPreferencesKey("admin_password")

    val adminPassword: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[passwordKey]
    }

    suspend fun setPassword(password: String) {
        context.dataStore.edit { preferences ->
            preferences[passwordKey] = password
        }
    }
}
