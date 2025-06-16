package br.com.challenge.core.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import br.com.challenge.core.common.Session.ApiPreferencesKeys.API_BASE_URL
import br.com.challenge.core.common.Session.ApiPreferencesKeys.API_KEY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.apiPreferences: DataStore<Preferences> by preferencesDataStore(name = "api_prefs")

object Session {
    private object ApiPreferencesKeys {
        val API_KEY = stringPreferencesKey("api_key")
        val API_BASE_URL = stringPreferencesKey("api_base_url")
    }

    fun getApiBaseUrl(context: Context): Flow<String> {
        return context.apiPreferences.data.map { preferences ->
            preferences[API_BASE_URL] ?: ""
        }
    }

    fun getApiKey(context: Context): Flow<String?> {
        return context.apiPreferences.data.map { preferences ->
            preferences[API_KEY] ?: ""
        }
    }

    suspend fun saveApiBaseUrl(context: Context, apiBaseUrl: String) {
        context.apiPreferences.edit { preferences ->
            preferences[API_BASE_URL] = apiBaseUrl
        }
    }

    suspend fun saveApiKey(context: Context, apiKey: String) {
        context.apiPreferences.edit { preferences ->
            preferences[API_KEY] = apiKey
        }
    }
}
