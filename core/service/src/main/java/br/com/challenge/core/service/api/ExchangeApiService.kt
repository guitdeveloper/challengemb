package br.com.challenge.core.service.api

import br.com.challenge.core.service.dto.ExchangeDto
import br.com.challenge.core.service.dto.ExchangeIconDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ExchangeApiService {
    @GET("v1/exchanges")
    suspend fun getExchanges(
        @Query("filter_exchange_id") exchangeIds: String? = null,
    ): Response<List<ExchangeDto>>

    @GET("v1/exchanges/{exchange_id}")
    suspend fun getExchangeById(
        @Path("exchange_id") exchangeId: String,
    ): Response<List<ExchangeDto>>

    @GET("v1/exchanges/icons/{icon_size}")
    suspend fun getExchangeIcons(
        @Path("icon_size") iconSize: Int,
    ): Response<List<ExchangeIconDto>>
}
