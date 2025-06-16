package br.com.challenge.features.exchanges.domain.usecase

import br.com.challenge.features.exchanges.domain.repository.ExchangeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FetchExchangesUseCase(
    private val repository: ExchangeRepository
) {
    operator fun invoke(): Flow<Result<Boolean>> = flow {
        try {
            val count = repository.getCount()
            if (count == 0 || repository.isCacheExpired(repository.first())) {
                emit(repository.fetchExchanges())
            }
            emit(Result.success(true))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
