package br.com.challenge.features.exchanges.data.repository

import br.com.challenge.core.database.dao.ExchangeDao
import br.com.challenge.core.database.entity.ExchangeEntity
import br.com.challenge.core.service.api.ExchangeApiService
import br.com.challenge.core.service.dto.ExchangeDto
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExchangeRepositoryImplTest {

    private val apiService: ExchangeApiService = mockk()
    private val exchangeDao: ExchangeDao = mockk()
    private lateinit var repository: ExchangeRepositoryImpl

    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = ExchangeRepositoryImpl(apiService, exchangeDao)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit success when exchange is found by id`() = runTest {
        val entity = ExchangeEntity("bin", "Binance", "url", 543456789.0)
        coEvery { exchangeDao.getExchangeBy("bin") } returns entity

        val result = repository.getExchangeById("BIN").first()

        assertTrue(result.isSuccess)
        assertEquals("bin", result.getOrNull()?.exchangeId)
    }

    @Test
    fun `should emit failure when exchange not found by id`() = runTest {
        coEvery { exchangeDao.getExchangeBy("abc") } returns null

        val result = repository.getExchangeById("abc").first()

        assertTrue(result.isFailure)
        assertEquals("Exchange not found for ID abc", result.exceptionOrNull()?.message)
    }

    @Test
    fun `should fetch and insert exchanges when API call is successful`() = runTest {
        val dto = ExchangeDto("bin", "Binance", "url", 1000.0)

        val response = Response.success(listOf(dto))

        coEvery { apiService.getExchanges() } returns response
        coEvery { exchangeDao.removeAll() } just Runs
        coEvery { exchangeDao.insertAll(any()) } just Runs

        val result = repository.fetchExchanges()

        assertTrue(result.isSuccess)
        coVerify { exchangeDao.removeAll() }
        coVerify { exchangeDao.insertAll(withArg { assertEquals(1, it.size) }) }
    }

    @Test
    fun `should return failure when API call fails`() = runTest {
        val errorResponse = Response.error<List<ExchangeDto>>(404, mockk<ResponseBody>(relaxed = true))

        coEvery { apiService.getExchanges() } returns errorResponse

        val result = repository.fetchExchanges()

        assertTrue(result.isFailure)
        assertEquals("API fail: 404 - ", result.exceptionOrNull()?.message)
    }

    @Test
    fun `should return true when cache is not expired`() {
        val entity = ExchangeEntity("btc", "Bitcoin", "url", 12435.0, lastUpdated = System.currentTimeMillis())
        assertTrue(repository.isCacheExpired(entity))
    }

    @Test
    fun `should return false when cache is expired`() {
        val entity = ExchangeEntity("btc", "Bitcoin", "url", 123.0, lastUpdated = System.currentTimeMillis() - 5 * 60 * 1000)
        assertFalse(repository.isCacheExpired(entity))
    }
}