package br.com.challenge.core.service.dto

import com.google.gson.annotations.SerializedName

data class ExchangeDto(
    @SerializedName("exchange_id")
    val exchangeId: String,
    val name: String?,
    @SerializedName("website")
    val websiteUrl: String?,
    @SerializedName("volume_1day_usd")
    val volume1dayUsd: Double?,
    @SerializedName("volume_1hrs_usd")
    val volume1hrsUsd: Double? = null,
    @SerializedName("volume_1mth_usd")
    val volume1mthUsd: Double? = null,
    @SerializedName("data_quote_start")
    val dataQuoteStart: String? = null,
    @SerializedName("data_quote_end")
    val dataQuoteEnd: String? = null,
    @SerializedName("data_orderbook_start")
    val dataOrderBookStart: String? = null,
    @SerializedName("data_orderbook_end")
    val dataOrderBookEnd: String? = null,
    @SerializedName("data_trade_start")
    val dataTradeStart: String? = null,
    @SerializedName("data_trade_end")
    val dataTradeEnd: String? = null,
    @SerializedName("data_symbols_count")
    val dataSymbolsCount: Int? = null,
    val rank: Double? = null,
)
