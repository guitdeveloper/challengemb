package br.com.challenge.features.exchanges.presentation.detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.challenge.core.presentation.state.UiState
import br.com.challenge.features.exchanges.domain.Exchange
import br.com.challenge.features.exchanges.domain.usecase.GetExchangeDetailUseCase
import kotlinx.coroutines.launch

class ExchangeDetailViewModel(
    private val getExchangeDetailUseCase: GetExchangeDetailUseCase
) : ViewModel() {

    private val _uiState = mutableStateOf<UiState<Exchange>>(UiState.Loading)
    val uiState: State<UiState<Exchange>> = _uiState

    fun loadExchangeDetail(exchangeId: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            getExchangeDetailUseCase(exchangeId).collect {
                result ->
                    result.onSuccess { exchange ->
                        _uiState.value = UiState.Success(exchange)
                    }
                    .onFailure { exception ->
                        _uiState.value = UiState.Error(exception)
                    }
            }
        }
    }

    fun retry(exchangeId: String) {
        loadExchangeDetail(exchangeId)
    }
}
