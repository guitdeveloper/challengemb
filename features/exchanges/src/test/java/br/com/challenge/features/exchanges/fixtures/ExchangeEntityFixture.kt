package br.com.challenge.features.exchanges.fixtures

import br.com.challenge.core.database.entity.ExchangeEntity

fun createExchangeEntityFixture(): ExchangeEntity {
    return ExchangeEntity(
        exchangeId = "binance",
        name = "Binance",
        volume1dayUsd = 100.0,
        volume1hrsUsd = 10.0,
        volume1mthUsd = 1000.0,
        websiteUrl = "https://binance.com",
        dataSymbolsCount = 100,
        dataQuoteStart = "2023-01-01",
        dataQuoteEnd = "2023-12-31",
        dataTradeStart = "2023-01-01",
        dataTradeEnd = "2023-12-31",
        rank = 1.0,
        iconPath = "https://cdn.exchange.com/icons/binance.png",
        lastUpdated = System.currentTimeMillis(),
    )
}