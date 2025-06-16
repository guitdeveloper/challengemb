package br.com.challenge.features.exchanges.presentation.list

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavHostController
import androidx.paging.PagingData
import br.com.challenge.core.network.di.ApiConfig
import br.com.challenge.core.presentation.state.UiState
import br.com.challenge.features.exchanges.domain.ExchangeResume
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ExchangeListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: NavHostController
    private val apiConfig = ApiConfig("https://fake.api", mapOf("Authorization" to ""))

    @Before
    fun setUp() {
        navController = mockk(relaxed = true)
    }

    @Test
    fun showLoading_whenUiStateIsLoading() {
        val viewModel = mockExchangeViewModel(UiState.Loading, PagingData.empty())

        composeTestRule.setContent {
            ExchangeListScreen(
                navController = navController,
                apiConfig = apiConfig,
                viewModel = viewModel
            )
        }

        composeTestRule.onNodeWithTag("loading").assertExists()
    }

    @Test
    fun showError_whenUiStateIsError() {
        val viewModel = mockExchangeViewModel(UiState.Error(Exception("Falha")), PagingData.empty())

        composeTestRule.setContent {
            ExchangeListScreen(
                navController = navController,
                apiConfig = apiConfig,
                viewModel = viewModel
            )
        }

        composeTestRule.onNodeWithText("Falha").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tentar novamente").assertExists()
    }

    @Test
    fun showItems_whenUiStateIsSuccess() {
        val items = listOf(
            ExchangeResume("binance", "Binance", 1000000.0, 1.0),
            ExchangeResume("coinbase", "Coinbase", 500000.0, 1.0),
        )
        val viewModel = mockExchangeViewModel(UiState.Success(true), PagingData.from(items))

        composeTestRule.setContent {
            ExchangeListScreen(
                navController = navController,
                apiConfig = apiConfig,
                viewModel = viewModel
            )
        }

        composeTestRule.onNodeWithText("Binance").assertIsDisplayed()
        composeTestRule.onNodeWithText("Coinbase").assertIsDisplayed()
    }

    @Test
    fun retryCalled_whenRefreshButtonClicked() {
        val viewModel = spyk(mockExchangeViewModel(UiState.Success(true), PagingData.empty()))

        composeTestRule.setContent {
            ExchangeListScreen(
                navController = navController,
                apiConfig = apiConfig,
                viewModel = viewModel
            )
        }

        composeTestRule.onNodeWithContentDescription("Atualizar").performClick()
        verify { viewModel.retry() }
    }

    @Test
    fun navigateToDetail_whenItemClicked() {
        val exchange = ExchangeResume("kraken", "Kraken", 12345.0, 1.0)
        val viewModel = mockExchangeViewModel(UiState.Success(true), PagingData.from(listOf(exchange)))

        composeTestRule.setContent {
            ExchangeListScreen(
                navController = navController,
                apiConfig = apiConfig,
                viewModel = viewModel
            )
        }

        composeTestRule.onNodeWithText("Kraken").performClick()

        verify { navController.navigate("exchange_detail/kraken") }
    }

    // Helper
    private fun mockExchangeViewModel(
        state: UiState<Boolean>,
        pagingData: PagingData<ExchangeResume>
    ): ExchangeViewModel {
        val viewModel = mockk<ExchangeViewModel>(relaxed = true)
        every { viewModel.uiState } returns mutableStateOf(UiState.Loading)
        every { viewModel.items } returns flowOf(pagingData)
        return viewModel
    }
}