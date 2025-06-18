package br.com.challenge.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.challenge.BuildConfig
import br.com.challenge.core.common.Session
import br.com.challenge.core.network.ApiConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainViewModel(
    private val session: Session,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val _apiConfig = MutableStateFlow<ApiConfig?>(null)
    val apiConfig: StateFlow<ApiConfig?> = _apiConfig.asStateFlow()

    init {
        viewModelScope.launch(dispatcher) {
            session.saveApiBaseUrl(BuildConfig.EXCHANGE_BASE_URL)
            session.saveApiKey(BuildConfig.API_KEY)
            val authorization = session.getApiKey().firstOrNull() ?: ""
            val baseUrl = session.getApiBaseUrl().firstOrNull() ?: ""
            val headers = mapOf("Authorization" to authorization)
            _apiConfig.value = ApiConfig(baseUrl, headers)
        }
    }
}
