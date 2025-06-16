package br.com.challenge.features.exchanges.domain.usecase

import br.com.challenge.features.exchanges.domain.Exchange
import br.com.challenge.features.exchanges.domain.repository.ExchangeRepository
import kotlinx.coroutines.flow.Flow

class GetExchangeDetailUseCase(
    private val repository: ExchangeRepository
) {
    suspend operator fun invoke(exchangeId: String): Flow<Result<Exchange>> {
        return repository.getExchangeById(exchangeId)
    }
}
