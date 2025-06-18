package br.com.challenge.features.exchanges.domain.repository

import androidx.paging.PagingSource
import br.com.challenge.core.database.entity.ExchangeEntity
import br.com.challenge.features.exchanges.domain.model.Exchange
import kotlinx.coroutines.flow.Flow

interface ExchangeRepository {
    fun getPagedItems(): PagingSource<Int, ExchangeEntity>
    suspend fun getCount(): Int
    suspend fun first(): ExchangeEntity
    suspend fun fetchExchanges(): Result<Boolean>
    suspend fun getExchangeById(exchangeId: String): Flow<Result<Exchange>>
    fun isCacheExpired(entity: ExchangeEntity): Boolean
}
