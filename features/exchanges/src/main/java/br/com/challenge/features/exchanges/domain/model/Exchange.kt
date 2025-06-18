package br.com.challenge.features.exchanges.domain.model

data class Exchange(
    val exchangeId: String,
    val name: String? = null,
    val websiteUrl: String? = null,
    val volume1dayUsd: Double? = null,
    val volume1hrsUsd: Double? = null,
    val volume1mthUsd: Double? = null,
    val dataQuoteStart: String? = null,
    val dataQuoteEnd: String? = null,
    val dataOrderBookStart: String? = null,
    val dataOrderBookEnd: String? = null,
    val dataTradeStart: String? = null,
    val dataTradeEnd: String? = null,
    val dataSymbolsCount: Int? = null,
    val rank: Double? = null,
    val iconPath: String? = null,
)
