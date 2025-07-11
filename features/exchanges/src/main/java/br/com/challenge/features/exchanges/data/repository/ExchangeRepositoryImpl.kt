package br.com.challenge.features.exchanges.data.repository

import br.com.challenge.core.database.dao.ExchangeDao
import br.com.challenge.core.database.entity.ExchangeEntity
import br.com.challenge.core.service.api.ExchangeApiService
import br.com.challenge.core.service.dto.ExchangeIconDto
import br.com.challenge.core.service.dto.ResponseError
import br.com.challenge.features.exchanges.domain.model.Exchange
import br.com.challenge.features.exchanges.domain.mapper.toDetail
import br.com.challenge.features.exchanges.domain.repository.ExchangeRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ExchangeRepositoryImpl(
    private val apiService: ExchangeApiService,
    private val exchangeDao: ExchangeDao,
) : ExchangeRepository {

    companion object {
        private const val CACHE_EXPIRATION_TIME = 5 * 60 * 1000L
        private const val ICON_SIZE = 64
    }

    override fun getPagedItems() = exchangeDao.getPagedItems()

    override suspend fun getCount() = exchangeDao.getCount()

    override suspend fun first() = exchangeDao.first()

    override suspend fun getExchangeById(
        exchangeId: String
    ): Flow<Result<Exchange>> = flow {
        exchangeDao.getExchangeBy(exchangeId)?.let {
            emit(Result.success(it.toDetail()))
        } ?: emit(Result.failure(Exception("Exchange não encontrada para exchange ID '$exchangeId'")))
    }

    override suspend fun fetchExchanges(): Result<Boolean> {
        val exchangesResponse = apiService.getExchanges()
        val iconsResponse = apiService.getExchangeIcons(ICON_SIZE)
        return if (exchangesResponse.isSuccessful) {
            val entities = mutableListOf<ExchangeEntity>()
            var iconMap: Map<String, ExchangeIconDto>? = hashMapOf()
            if (iconsResponse.isSuccessful) {
                iconMap = iconsResponse.body()?.associateBy { it.exchangeId }
            }
            val list = exchangesResponse.body()?.map {
                val iconUrl = iconMap?.get(it.exchangeId)?.iconUrl
                entities.add(it.toDetail(iconUrl))
                it.toDetail(iconUrl)
            } ?: emptyList()
            if (list.isNotEmpty()) {
                exchangeDao.insertAll(entities)
            }
            Result.success(true)
        } else {
            var error: String? = exchangesResponse.message()
            exchangesResponse.errorBody()?.let {
                error = try {
                    Gson().fromJson(it.string(), ResponseError::class.java).error
                } catch (e: Exception) {
                    null
                }
            }
            Result.failure(Exception(error))
        }
    }

    override fun isCacheExpired(entity: ExchangeEntity): Boolean {
        val now = System.currentTimeMillis()
        return now - entity.lastUpdated < CACHE_EXPIRATION_TIME
    }
}
