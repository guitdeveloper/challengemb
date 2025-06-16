package br.com.challenge.features.exchanges.domain.usecase

import androidx.paging.PagingSource
import br.com.challenge.core.database.entity.ExchangeEntity
import br.com.challenge.features.exchanges.domain.repository.ExchangeRepository

class GetPagedExchangesUseCase(
    private val repository: ExchangeRepository
) {
    operator fun invoke(): PagingSource<Int, ExchangeEntity> {
        return repository.getPagedItems()
    }
}
