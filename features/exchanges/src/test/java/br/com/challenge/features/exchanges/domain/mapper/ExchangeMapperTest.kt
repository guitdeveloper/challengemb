package br.com.challenge.features.exchanges.domain.mapper

import br.com.challenge.core.database.entity.ExchangeEntity
import br.com.challenge.core.service.dto.ExchangeDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExchangeMapperTest {

    private val fakeExchangeId = "binance"
    private val fakeName = "Binance"
    private val fakeUrl = "https://binance.com"
    private val fakeVol1Day = 12345.67
    private val fakeVol1Hr = 123.45
    private val fakeVol1Mth = 999999.99
    private val fakeQuoteStart = "2020-01-01"
    private val fakeQuoteEnd = "2023-12-31"
    private val fakeOrderBookStart = "2020-01-02"
    private val fakeOrderBookEnd = "2023-12-30"
    private val fakeTradeStart = "2020-02-01"
    private val fakeTradeEnd = "2023-11-30"
    private val fakeSymbolsCount = 150
    private val fakeRank = 1.00

    @Test
    fun `should mapper ExchangeEntity to ExchangeResume`() {
        val entity = ExchangeEntity(
            exchangeId = fakeExchangeId,
            name = fakeName,
            websiteUrl = fakeUrl,
            volume1dayUsd = fakeVol1Day,
            volume1hrsUsd = fakeVol1Hr,
            volume1mthUsd = fakeVol1Mth,
            dataQuoteStart = fakeQuoteStart,
            dataQuoteEnd = fakeQuoteEnd,
            dataOrderBookStart = fakeOrderBookStart,
            dataOrderBookEnd = fakeOrderBookEnd,
            dataTradeStart = fakeTradeStart,
            dataTradeEnd = fakeTradeEnd,
            dataSymbolsCount = fakeSymbolsCount,
            rank = fakeRank,
        )

        val resume = entity.toResume()

        assertEquals(fakeExchangeId, resume.exchangeId)
        assertEquals(fakeName, resume.name)
        assertEquals(fakeVol1Day, resume.volume1dayUsd)
        assertEquals(fakeRank, resume.rank)
    }

    @Test
    fun `should mapper ExchangeEntity to Exchange`() {
        val entity = ExchangeEntity(
            exchangeId = fakeExchangeId,
            name = fakeName,
            websiteUrl = fakeUrl,
            volume1dayUsd = fakeVol1Day,
            volume1hrsUsd = fakeVol1Hr,
            volume1mthUsd = fakeVol1Mth,
            dataQuoteStart = fakeQuoteStart,
            dataQuoteEnd = fakeQuoteEnd,
            dataOrderBookStart = fakeOrderBookStart,
            dataOrderBookEnd = fakeOrderBookEnd,
            dataTradeStart = fakeTradeStart,
            dataTradeEnd = fakeTradeEnd,
            dataSymbolsCount = fakeSymbolsCount,
            rank = fakeRank,
        )

        val detail = entity.toDetail()

        assertEquals(fakeExchangeId, detail.exchangeId)
        assertEquals(fakeVol1Hr, detail.volume1hrsUsd)
        assertEquals(fakeSymbolsCount, detail.dataSymbolsCount)
    }

    @Test
    fun `should mapper ExchangeDto to ExchangeEntity`() {
        val dto = ExchangeDto(
            exchangeId = fakeExchangeId,
            name = fakeName,
            websiteUrl = fakeUrl,
            volume1dayUsd = fakeVol1Day,
            volume1hrsUsd = fakeVol1Hr,
            volume1mthUsd = fakeVol1Mth,
            dataQuoteStart = fakeQuoteStart,
            dataQuoteEnd = fakeQuoteEnd,
            dataOrderBookStart = fakeOrderBookStart,
            dataOrderBookEnd = fakeOrderBookEnd,
            dataTradeStart = fakeTradeStart,
            dataTradeEnd = fakeTradeEnd,
            dataSymbolsCount = fakeSymbolsCount,
            rank = fakeRank,
        )

        val entity = dto.toDetail("url")

        assertEquals(fakeExchangeId, entity.exchangeId)
        assertEquals(fakeTradeStart, entity.dataTradeStart)
        assertEquals(fakeVol1Mth, entity.volume1mthUsd)
    }
}
