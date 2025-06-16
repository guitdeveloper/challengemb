package br.com.challenge.features.exchanges.domain.usecase

import br.com.challenge.features.exchanges.domain.Exchange
import br.com.challenge.features.exchanges.domain.repository.ExchangeRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetExchangeDetailUseCaseTest {

    private val repository = mockk<ExchangeRepository>()
    private val useCase = GetExchangeDetailUseCase(repository)
    private val dummyExchange = Exchange(
        exchangeId = "123",
        name = "Test Exchange",
        websiteUrl = "https://test.exchange",
        volume1dayUsd = 1000.0,
        volume1hrsUsd = 50.0,
        volume1mthUsd = 30000.0,
        dataQuoteStart = "2023-01-01",
        dataQuoteEnd = "2023-12-31",
        dataOrderBookStart = "2023-01-01",
        dataOrderBookEnd = "2023-12-31",
        dataTradeStart = "2023-01-01",
        dataTradeEnd = "2023-12-31",
        dataSymbolsCount = 100,
        rank = 1.0
    )

    @Test
    fun `invoke should emit success result from repository`() = runTest {
        val flowResult = flow {
            emit(Result.success(dummyExchange))
        }
        coEvery { repository.getExchangeById("123") } returns flowResult

        val results = useCase.invoke("123").toList()

        assertEquals(1, results.size)
        val result = results[0]
        assertTrue(result.isSuccess)
        assertEquals(dummyExchange, result.getOrNull())
    }

    @Test
    fun `invoke should emit failure result from repository`() = runTest {
        val exception = Exception("Not found")
        val flowResult = flow {
            emit(Result.failure<Exchange>(exception))
        }
        coEvery { repository.getExchangeById("123") } returns flowResult

        val results = useCase.invoke("123").toList()

        assertEquals(1, results.size)
        val result = results[0]
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}