package br.com.challenge.features.exchanges.presentation.list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import br.com.challenge.core.presentation.state.UiState
import br.com.challenge.features.exchanges.domain.mapper.toResume
import br.com.challenge.features.exchanges.domain.model.ExchangeResume
import br.com.challenge.features.exchanges.domain.usecase.FetchExchangesUseCase
import br.com.challenge.features.exchanges.domain.usecase.GetPagedExchangesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ExchangeViewModel(
    private val fetchExchangesUseCase: FetchExchangesUseCase,
    private val getPagedExchangesUseCase: GetPagedExchangesUseCase,
) : ViewModel() {

    private val _uiState = mutableStateOf<UiState<Boolean>>(UiState.Loading)
    val uiState: State<UiState<Boolean>> = _uiState

    val items: Flow<PagingData<ExchangeResume>> = Pager(
        config = PagingConfig(pageSize = 8),
        pagingSourceFactory = { getPagedExchangesUseCase() }
    ).flow
        .map { pagingData -> pagingData.map { it.toResume() } }
        .cachedIn(viewModelScope)

    init {
        fetchExchanges()
    }

    fun fetchExchanges() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            fetchExchangesUseCase().collect {
                result ->
                    result.onSuccess { success ->
                        _uiState.value = UiState.Success(success)
                    }
                    .onFailure { exception ->
                        _uiState.value = UiState.Error(exception)
                    }
            }
        }
    }

    fun retry() {
        fetchExchanges()
    }
}
