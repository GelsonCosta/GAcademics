package com.gelsoncosta.gacademics.helpers

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class DataStoreHelper(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")

    }

    suspend fun saveSession(token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    suspend fun getToken(): String? {
        return context.dataStore.data.map { prefs -> prefs[TOKEN_KEY] }.first()
    }



    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}
