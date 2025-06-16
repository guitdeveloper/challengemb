package br.com.challenge.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.challenge.core.common.Session
import br.com.challenge.core.network.di.ApiConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainViewModel(
    private val context: Context
) : ViewModel() {

    private val _apiConfig = MutableStateFlow<ApiConfig?>(null)
    val apiConfig: StateFlow<ApiConfig?> = _apiConfig.asStateFlow()

    init {
        viewModelScope.launch {
            val authorization = Session.getApiKey(context).firstOrNull() ?: ""
            val baseUrl = Session.getApiBaseUrl(context).firstOrNull() ?: ""
            val headers = mapOf("Authorization" to authorization)
            _apiConfig.value = ApiConfig(baseUrl, headers)
        }
    }
}
