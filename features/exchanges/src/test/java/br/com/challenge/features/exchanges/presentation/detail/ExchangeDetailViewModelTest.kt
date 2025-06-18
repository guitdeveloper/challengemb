package br.com.challenge.features.exchanges.presentation.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.challenge.core.presentation.state.UiState
import br.com.challenge.features.exchanges.domain.model.Exchange
import br.com.challenge.features.exchanges.domain.usecase.GetExchangeDetailUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExchangeDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ExchangeDetailViewModel
    private val getExchangeDetailUseCase = mockk<GetExchangeDetailUseCase>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadExchangeDetail updates uiState with Success when use case returns success`() = runTest {
        val exchangeId = "123"
        val exchange = Exchange(
            exchangeId = exchangeId,
            name = "ExchangeName",
            websiteUrl = "https://exchange.com",
            volume1dayUsd = 1000.0,
            volume1hrsUsd = 100.0,
            volume1mthUsd = 30000.0,
            dataQuoteStart = "2023-01-01",
            dataQuoteEnd = "2023-06-01",
            dataOrderBookStart = "2023-01-01",
            dataOrderBookEnd = "2023-06-01",
            dataTradeStart = "2023-01-01",
            dataTradeEnd = "2023-06-01",
            dataSymbolsCount = 200,
            rank = 1.0
        )
        coEvery { getExchangeDetailUseCase.invoke(exchangeId) } returns flow {
            emit(Result.success(exchange))
        }
        viewModel = ExchangeDetailViewModel(getExchangeDetailUseCase)

        viewModel.loadExchangeDetail(exchangeId)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals(exchange, (state as UiState.Success).data)
    }

    @Test
    fun `loadExchangeDetail updates uiState with Error when use case returns failure`() = runTest {
        val exchangeId = "123"
        val exception = Exception("Error loading exchange")
        coEvery { getExchangeDetailUseCase.invoke(exchangeId) } returns flow {
            emit(Result.failure(exception))
        }
        viewModel = ExchangeDetailViewModel(getExchangeDetailUseCase)

        viewModel.loadExchangeDetail(exchangeId)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Error)
        assertEquals(exception, (state as UiState.Error).exception)
    }

    @Test
    fun `retry calls loadExchangeDetail`() = runTest {
        val exchangeId = "123"
        coEvery { getExchangeDetailUseCase.invoke(exchangeId) } returns flow {
            emit(Result.success(mockk()))
        }
        viewModel = ExchangeDetailViewModel(getExchangeDetailUseCase)

        viewModel.loadExchangeDetail(exchangeId)
        testScheduler.advanceUntilIdle()

        val firstState = viewModel.uiState.value
        assertTrue(firstState is UiState.Success)

        viewModel.retry(exchangeId)
        testScheduler.advanceUntilIdle()

        val secondState = viewModel.uiState.value
        assertTrue(secondState is UiState.Success)
    }
}