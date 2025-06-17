package br.com.challenge.features.exchanges.data.repository

import androidx.paging.PagingSource
import br.com.challenge.core.database.dao.ExchangeDao
import br.com.challenge.core.database.entity.ExchangeEntity
import br.com.challenge.core.service.api.ExchangeApiService
import br.com.challenge.core.service.dto.ExchangeDto
import br.com.challenge.core.service.dto.ExchangeIconDto
import br.com.challenge.features.exchanges.domain.Exchange
import br.com.challenge.features.exchanges.domain.mapper.toDetail
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class ExchangeRepositoryImplTest {

    private val apiService: ExchangeApiService = mockk()
    private val dao: ExchangeDao = mockk()
    private lateinit var repository: ExchangeRepositoryImpl

    @BeforeEach
    fun setup() {
        repository = ExchangeRepositoryImpl(apiService, dao)
    }

    @Test
    fun `getPagedItems returns flow from dao`() {
        val pagingSource = mockk<PagingSource<Int, ExchangeEntity>>()
        every { dao.getPagedItems() } returns pagingSource

        val result = repository.getPagedItems()
        assertEquals(pagingSource, result)
    }

    @Test
    fun `getCount returns count from dao`() = runTest {
        coEvery { dao.getCount() } returns 42

        val count = repository.getCount()
        assertEquals(42, count)
    }

    @Test
    fun `first returns first entity from dao`() = runTest {
        val entity = mockk<ExchangeEntity>()
        coEvery { dao.first() } returns entity

        val result = repository.first()
        assertEquals(entity, result)
    }

    @Test
    fun `getExchangeById emits success when found`() = runTest {
        val entity = mockk<ExchangeEntity>()
        val exchange = mockk<Exchange>()
        every { entity.toDetail() } returns exchange
        coEvery { dao.getExchangeBy("binance") } returns entity

        val result = repository.getExchangeById("binance").first()
        assertTrue(result.isSuccess)
        assertEquals(exchange, result.getOrNull())
    }

    @Test
    fun `getExchangeById emits failure when not found`() = runTest {
        coEvery { dao.getExchangeBy("kraken") } returns null

        val result = repository.getExchangeById("kraken").first()
        assertTrue(result.isFailure)
        assertEquals("Exchange não encontrada para exchange ID 'kraken'", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getExchangeById emits failure when dao throws exception`() = runTest {
        coEvery { dao.getExchangeBy("bitfinex") } throws RuntimeException("Erro no banco")

        val result = repository.getExchangeById("bitfinex").first()
        assertTrue(result.isFailure)
        assertEquals("Erro no banco", result.exceptionOrNull()?.message)
    }

    @Test
    fun `fetchExchanges updates DB when api responses are successful`() = runTest {
        val dto = ExchangeDto("id1", "Binance", "https://binance.com", volume1dayUsd = 100.0)
        val icon = ExchangeIconDto("id1", "https://icon.com/test.png")
        val entity = mockk<ExchangeEntity>()

        coEvery { apiService.getExchanges() } returns Response.success(listOf(dto))
        coEvery { apiService.getExchangeIcons(64) } returns Response.success(listOf(icon))
        every { dto.toDetail("https://icon.com") } returns entity
        coEvery { dao.removeAll() } just Runs
        coEvery { dao.insertAll(listOf(entity)) } just Runs

        val result = repository.fetchExchanges()

        assertTrue(result.isSuccess)
        coVerify { dao.removeAll() }
        coVerify { dao.insertAll(listOf(entity)) }
    }

    @Test
    fun `fetchExchanges returns failure when exchanges API fails`() = runTest {
        val errorResponse = Response.error<List<ExchangeDto>>(
            500,
            ResponseBody.create(null, "Erro")
        )
        coEvery { apiService.getExchanges() } returns errorResponse

        val result = repository.fetchExchanges()
        assertTrue(result.isFailure)
        assertEquals("Falha na API: 500 - null", result.exceptionOrNull()?.message)
    }

    @Test
    fun `fetchExchanges skips icons when icon API fails`() = runTest {
        val dto = ExchangeDto("id1", "Binance", "https://binance.com", volume1dayUsd = 73333.32)
        val entity = mockk<ExchangeEntity>()

        coEvery { apiService.getExchanges() } returns Response.success(listOf(dto))
        coEvery { apiService.getExchangeIcons(64) } returns Response.error(404, ResponseBody.create(null, "Não encontrado"))
        every { dto.toDetail(null) } returns entity
        coEvery { dao.removeAll() } just Runs
        coEvery { dao.insertAll(listOf(entity)) } just Runs

        val result = repository.fetchExchanges()

        assertTrue(result.isSuccess)
        coVerify { dao.insertAll(listOf(entity)) }
    }

    @Test
    fun `fetchExchanges does not insert when list is empty`() = runTest {
        coEvery { apiService.getExchanges() } returns Response.success(emptyList())
        coEvery { apiService.getExchangeIcons(64) } returns Response.success(emptyList())

        val result = repository.fetchExchanges()

        assertTrue(result.isSuccess)
        coVerify(exactly = 0) { dao.insertAll(any()) }
    }

    @Test
    fun `isCacheExpired returns false when updated recently`() {
        val entity = ExchangeEntity("id1", "Binance", "binance.com", volume1dayUsd = 94534.90, lastUpdated = System.currentTimeMillis())
        val result = repository.isCacheExpired(entity)
        assertFalse(result)
    }

    @Test
    fun `isCacheExpired returns true when outdated`() {
        val oldTimestamp = System.currentTimeMillis() - (11 * 60 * 1000)
        val entity = ExchangeEntity("id1", "Binance", "binance.com", volume1dayUsd = 94534.90, lastUpdated = oldTimestamp)
        val result = repository.isCacheExpired(entity)
        assertTrue(result)
    }
}