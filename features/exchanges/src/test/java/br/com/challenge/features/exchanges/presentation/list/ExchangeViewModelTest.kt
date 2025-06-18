package br.com.challenge.features.exchanges.presentation.list

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingSource
import androidx.paging.PagingState
import br.com.challenge.core.database.entity.ExchangeEntity
import br.com.challenge.core.presentation.state.UiState
import br.com.challenge.features.exchanges.domain.usecase.FetchExchangesUseCase
import br.com.challenge.features.exchanges.domain.usecase.GetPagedExchangesUseCase
import br.com.challenge.features.exchanges.fixtures.createExchangeEntityFixture
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
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
        coEvery { fetchExchangesUseCase.invoke() } returns flowOf(Result.success(true))
        val pagingSource = mockk<PagingSource<Int, br.com.challenge.core.database.entity.ExchangeEntity>>()
        every { getPagedExchangesUseCase.invoke() } returns pagingSource

        viewModel = ExchangeViewModel(fetchExchangesUseCase, getPagedExchangesUseCase)
        advanceUntilIdle()

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
        val pagingSource = object : PagingSource<Int, ExchangeEntity>() {
            override fun getRefreshKey(state: PagingState<Int, ExchangeEntity>): Int? = null

            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ExchangeEntity> {
                return LoadResult.Page(
                    data = listOf(createExchangeEntityFixture()),
                    prevKey = null,
                    nextKey = null
                )
            }
        }

        every { getPagedExchangesUseCase.invoke() } returns pagingSource
        coEvery { fetchExchangesUseCase.invoke() } returns flowOf(Result.success(true))

        viewModel = ExchangeViewModel(fetchExchangesUseCase, getPagedExchangesUseCase)

        val differ = AsyncPagingDataDiffer(
            diffCallback = ExchangeResumeDiffCallback(),
            updateCallback = NoopListCallback(),
            mainDispatcher = StandardTestDispatcher(testScheduler),
            workerDispatcher = StandardTestDispatcher(testScheduler),
        )

        val job = launch {
            viewModel.items.collectLatest {
                differ.submitData(it)
            }
        }

        advanceUntilIdle()

        assertEquals(1, differ.itemCount)
        assertEquals("binance", differ.snapshot()[0]?.exchangeId)

        job.cancel()
    }

    @Test
    fun `retry calls fetchExchanges again and updates uiState`() = runTest {
        val pagingSource = mockk<PagingSource<Int, br.com.challenge.core.database.entity.ExchangeEntity>>()
        every { getPagedExchangesUseCase.invoke() } returns pagingSource
        coEvery { fetchExchangesUseCase.invoke() } returns flowOf(Result.success(true))
        viewModel = ExchangeViewModel(fetchExchangesUseCase, getPagedExchangesUseCase)

        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is UiState.Success)

        val error = Exception("Network fail")
        coEvery { fetchExchangesUseCase.invoke() } returns flowOf(Result.failure(error))

        viewModel.retry()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is UiState.Error)
        assertEquals(error, (viewModel.uiState.value as UiState.Error).exception)
    }
}