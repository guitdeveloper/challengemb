package br.com.challenge.features.exchanges.presentation.detail

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import br.com.challenge.core.presentation.state.UiState
import br.com.challenge.features.exchanges.domain.Exchange
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class ExchangeDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val fakeExchange = Exchange(
        exchangeId = "123",
        name = "Fake Exchange",
        websiteUrl = "https://fake.exchange",
        volume1dayUsd = 12345.0,
        volume1hrsUsd = 1234.0,
        volume1mthUsd = 1234567.0,
        dataQuoteStart = "2023-01-01",
        dataQuoteEnd = "2023-06-01",
        dataOrderBookStart = null,
        dataOrderBookEnd = null,
        dataTradeStart = "2023-01-01",
        dataTradeEnd = "2023-06-01",
        dataSymbolsCount = 10,
        rank = 1.0
    )

    @Test
    fun testBackButtonCallsOnBackClick() {
        var backClicked = false
        composeTestRule.setContent {
            ExchangeDetailScreen(
                exchangeId = "123",
                onBackClick = { backClicked = true },
                apiConfig = mockk()
            )
        }

        // Clica no botão voltar
        composeTestRule.onNodeWithContentDescription("Voltar").performClick()
        assert(backClicked)
    }

    @Test
    fun testLoadingStateShowsLoadingComponent() {
        val viewModel = mockk<ExchangeDetailViewModel>(relaxed = true)

        coEvery { viewModel.uiState } returns mutableStateOf(UiState.Loading)

        composeTestRule.setContent {
            androidx.compose.runtime.CompositionLocalProvider(
                androidx.compose.runtime.staticCompositionLocalOf<ExchangeDetailViewModel> { viewModel }
                    .provides(viewModel)
            ) {
                ExchangeDetailScreen(
                    exchangeId = "123",
                    onBackClick = {},
                    apiConfig = mockk()
                )
            }
        }

        composeTestRule.onNodeWithText("Loading").assertExists()
    }

    @Test
    fun testSuccessStateShowsExchangeName() {
        val viewModel = mockk<ExchangeDetailViewModel>(relaxed = true)
        coEvery { viewModel.uiState } returns mutableStateOf(UiState.Success(fakeExchange))

        composeTestRule.setContent {
            androidx.compose.runtime.CompositionLocalProvider(
                androidx.compose.runtime.staticCompositionLocalOf<ExchangeDetailViewModel> { viewModel }
                    .provides(viewModel)
            ) {
                ExchangeDetailScreen(
                    exchangeId = "123",
                    onBackClick = {},
                    apiConfig = mockk()
                )
            }
        }

        composeTestRule.onNodeWithText("Fake Exchange").assertExists()
    }

    @Test
    fun testErrorStateShowsErrorMessage() {
        val viewModel = mockk<ExchangeDetailViewModel>(relaxed = true)
        coEvery {
            viewModel.uiState
        } returns mutableStateOf(UiState.Error(Exception("Error loading data")))

        composeTestRule.setContent {
            androidx.compose.runtime.CompositionLocalProvider(
                androidx.compose.runtime.staticCompositionLocalOf<ExchangeDetailViewModel> { viewModel }
                    .provides(viewModel)
            ) {
                ExchangeDetailScreen(
                    exchangeId = "123",
                    onBackClick = {},
                    apiConfig = mockk()
                )
            }
        }

        composeTestRule.onNodeWithText("Error loading data").assertExists()
    }
}