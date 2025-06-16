package br.com.challenge.features.exchanges.presentation.list

import androidx.paging.PagingData
import androidx.paging.PagingSource
import app.cash.turbine.test
import br.com.challenge.core.presentation.state.UiState
import br.com.challenge.features.exchanges.domain.ExchangeResume
import br.com.challenge.features.exchanges.domain.usecase.FetchExchangesUseCase
import br.com.challenge.features.exchanges.domain.usecase.GetPagedExchangesUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExchangeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fetchExchangesUseCase = mockk<FetchExchangesUseCase>()
    private val getPagedExchangesUseCase = mockk<GetPagedExchangesUseCase>()

    private lateinit var viewModel: ExchangeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init triggers fetchExchanges and sets uiState to Success on success`() = runTest {
        // Mock do use case para emitir Result.success(true)
        coEvery { fetchExchangesUseCase.invoke() } returns flowOf(Result.success(true))

        // Mock do pagingSource para o Pager funcionar
        val pagingSource = mockk<PagingSource<Int, br.com.challenge.core.database.entity.ExchangeEntity>>()
        every { getPagedExchangesUseCase.invoke() } returns pagingSource

        // Cria o ViewModel (chamará init -> fetchExchanges)
        viewModel = ExchangeViewModel(fetchExchangesUseCase, getPagedExchangesUseCase)

        // Avança as coroutines pendentes
        advanceUntilIdle()

        // Verifica se o estado UI foi atualizado para Success
        assertTrue(viewModel.uiState.value is UiState.Success)
        assertEquals(true, (viewModel.uiState.value as UiState.Success).data)
    }

    @Test
    fun `init triggers fetchExchanges and sets uiState to Error on failure`() = runTest {
        val exception = Exception("API error")

        coEvery { fetchExchangesUseCase.invoke() } returns flowOf(Result.failure(exception))
        val pagingSource = mockk<PagingSource<Int, br.com.challenge.core.database.entity.ExchangeEntity>>()
        every { getPagedExchangesUseCase.invoke() } returns pagingSource

        viewModel = ExchangeViewModel(fetchExchangesUseCase, getPagedExchangesUseCase)

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is UiState.Error)
        assertEquals(exception, (viewModel.uiState.value as UiState.Error).exception)
    }

    @Test
    fun `items flow emits PagingData`() = runTest {
        val pagingSource = mockk<PagingSource<Int, br.com.challenge.core.database.entity.ExchangeEntity>>()
        every { getPagedExchangesUseCase.invoke() } returns pagingSource

        coEvery { fetchExchangesUseCase.invoke() } returns flowOf(Result.success(true))

        viewModel = ExchangeViewModel(fetchExchangesUseCase, getPagedExchangesUseCase)

        // Testa se o flow "items" emite algum PagingData (não necessariamente dados, só não nulo)
        viewModel.items.test {
            val emission = awaitItem()
            // emission é PagingData<ExchangeResume>
            assertTrue(emission is PagingData)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `retry calls fetchExchanges again and updates uiState`() = runTest {
        val pagingSource = mockk<PagingSource<Int, br.com.challenge.core.database.entity.ExchangeEntity>>()
        every { getPagedExchangesUseCase.invoke() } returns pagingSource

        // Primeiro, o fetch retorna sucesso
        coEvery { fetchExchangesUseCase.invoke() } returns flowOf(Result.success(true))
        viewModel = ExchangeViewModel(fetchExchangesUseCase, getPagedExchangesUseCase)

        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is UiState.Success)

        // Agora mocka um erro para a segunda chamada (retry)
        val error = Exception("Network fail")
        coEvery { fetchExchangesUseCase.invoke() } returns flowOf(Result.failure(error))

        viewModel.retry()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is UiState.Error)
        assertEquals(error, (viewModel.uiState.value as UiState.Error).exception)
    }
}