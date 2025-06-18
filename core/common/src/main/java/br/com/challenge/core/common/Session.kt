package br.com.challenge.core.common

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import br.com.challenge.core.common.Session.ApiPreferencesKeys.API_BASE_URL
import br.com.challenge.core.common.Session.ApiPreferencesKeys.API_KEY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class Session(
    private val apiPreferences: DataStore<Preferences>
) {
    private object ApiPreferencesKeys {
        val API_KEY = stringPreferencesKey("api_key")
        val API_BASE_URL = stringPreferencesKey("api_base_url")
    }

    fun getApiBaseUrl(): Flow<String> {
        return apiPreferences.data.map { preferences ->
            preferences[API_BASE_URL] ?: ""
        }
    }

    fun getApiKey(): Flow<String?> {
        return apiPreferences.data.map { preferences ->
            preferences[API_KEY] ?: ""
        }
    }

    suspend fun saveApiBaseUrl(apiBaseUrl: String) {
        apiPreferences.edit { preferences ->
            preferences[API_BASE_URL] = apiBaseUrl
        }
    }

    suspend fun saveApiKey(apiKey: String) {
        apiPreferences.edit { preferences ->
            preferences[API_KEY] = apiKey
        }
    }
}
