package br.com.challenge.presentation

import br.com.challenge.core.common.Session
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val session = mockk<Session>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `apiConfig emits expected values from Session`() = runTest {
        val expectedApiKey = "api_123"
        val expectedBaseUrl = "https://api.test.com"
        coEvery { session.getApiKey() } returns flowOf(expectedApiKey)
        coEvery { session.getApiBaseUrl() } returns flowOf(expectedBaseUrl)
        coEvery { session.saveApiBaseUrl(any()) } returns Unit
        coEvery { session.saveApiKey(any()) } returns Unit

        val viewModel = MainViewModel(session, testDispatcher)
        advanceUntilIdle()

        val config = viewModel.apiConfig.value!!
        assertEquals(expectedBaseUrl, config.baseUrl)
        assertEquals(expectedApiKey, config.headers["Authorization"])
    }
}
