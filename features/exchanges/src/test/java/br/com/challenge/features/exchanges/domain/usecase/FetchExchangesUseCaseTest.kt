package br.com.challenge.features.exchanges.domain.usecase

import br.com.challenge.core.database.entity.ExchangeEntity
import br.com.challenge.features.exchanges.domain.repository.ExchangeRepository
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class FetchExchangesUseCaseTest {

    private val repository = mockk<ExchangeRepository>()
    private val useCase = FetchExchangesUseCase(repository)
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val dummyExchangeEntity = ExchangeEntity(
        exchangeId = "id",
        name = "name",
        websiteUrl = "url",
        volume1dayUsd = 0.0,
        volume1hrsUsd = 0.0,
        volume1mthUsd = 0.0,
        dataQuoteStart = "",
        dataQuoteEnd = "",
        dataOrderBookStart = "",
        dataOrderBookEnd = "",
        dataTradeStart = "",
        dataTradeEnd = "",
        dataSymbolsCount = 0,
        rank = 0.0,
        lastUpdated = System.currentTimeMillis() - 10_000L
    )

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `when count is zero then fetchExchanges is called`() = runTest {
        coEvery { repository.getCount() } returns 0
        coEvery { repository.first() } returns dummyExchangeEntity
        coEvery { repository.isCacheExpired(any()) } returns true
        coEvery { repository.fetchExchanges() } returns Result.success(true)

        val results = useCase().toList()

        assertEquals(1, results.size)
        coVerify(exactly = 1) { repository.fetchExchanges() }
    }

    @Test
    fun `when cache expired then fetchExchanges is called`() = runTest {
        coEvery { repository.getCount() } returns 1
        coEvery { repository.first() } returns dummyExchangeEntity
        coEvery { repository.isCacheExpired(any()) } returns true
        coEvery { repository.fetchExchanges() } returns Result.success(true)

        val results = useCase().toList()

        assertEquals(1, results.size)
        coVerify(exactly = 1) { repository.fetchExchanges() }
    }

    @Test
    fun `when exists exchanges in database then fetchExchanges is not called`() = runTest {
        coEvery { repository.getCount() } returns 10
        coEvery { repository.first() } returns dummyExchangeEntity
        coEvery { repository.isCacheExpired(any()) } returns false
        coEvery { repository.fetchExchanges() } returns Result.success(true)

        val results = useCase().toList()

        assertEquals(1, results.size)
        coVerify(exactly = 0) { repository.fetchExchanges() }
    }

    @Test
    fun `when exception thrown then Result failure is emitted`() = runTest {
        val exception = RuntimeException("fail")
        coEvery { repository.getCount() } throws exception

        val results = useCase().toList()

        assertEquals(1, results.size)
        assertEquals(exception, results[0].exceptionOrNull())
    }
}