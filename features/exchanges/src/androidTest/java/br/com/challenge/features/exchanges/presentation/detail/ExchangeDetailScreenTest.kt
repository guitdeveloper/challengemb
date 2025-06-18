package br.com.challenge.features.exchanges.presentation.detail

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.challenge.core.network.ApiConfig
import br.com.challenge.core.presentation.state.UiState
import br.com.challenge.core.presentation.utils.ChromeCustomTabNavigator
import br.com.challenge.features.exchanges.domain.model.Exchange
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest

@RunWith(AndroidJUnit4::class)
class ExchangeDetailScreenTest : KoinTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val fakeExchange = Exchange(
        exchangeId = "binance",
        name = "Binance",
        websiteUrl = "https://binance.com",
        volume1dayUsd = 123456789.0,
        volume1hrsUsd = 123456.0,
        volume1mthUsd = 987654321.0,
        dataSymbolsCount = 100,
        dataQuoteStart = "2020-01-01T00:00:00Z",
        dataQuoteEnd = "2024-12-31T23:59:59Z",
        dataTradeStart = "2020-02-01T00:00:00Z",
        dataTradeEnd = "2024-11-30T23:59:59Z"
    )

    private lateinit var viewModel: ExchangeDetailViewModel

    @Before
    fun setUp() {
        viewModel = mockk(relaxed = true)
        every { viewModel.uiState } returns mutableStateOf(UiState.Success(fakeExchange))
    }

    @Test
    @Ignore("Verificar posteriormente")
    fun exchangeDetailScreen_shouldDisplayTopAppBar_andContent() {
        composeRule.setContent {
            ExchangeDetailScreen(
                exchangeId = "binance",
                onBackClick = {},
                apiConfig = ApiConfig("", mapOf()),
                viewModel = viewModel
            )
        }

        composeRule
            .onNodeWithText("Exchange Details")
            .assertExists()

        composeRule
            .onNodeWithText("Binance")
            .assertExists()

        composeRule
            .onNodeWithText("ID: binance")
            .assertExists()
    }

    @Test
    fun volumeInformation_shouldDisplayFormattedValues() {
        composeRule.setContent {
            VolumeInformation(exchange = fakeExchange)
        }

        composeRule.onNodeWithText("Volume (24h)").assertExists()
        composeRule.onNodeWithText("\$123.46M").assertExists()
        composeRule.onNodeWithText("\$123.46K").assertExists()
        composeRule.onNodeWithText("\$987.65M").assertExists()
    }

    @Test
    fun dataInformation_shouldDisplayDataCorrectly() {
        composeRule.setContent {
            DataInformation(exchange = fakeExchange)
        }

        composeRule.onNodeWithText("100").assertExists()
        composeRule.onNodeWithText("01/01/2020").assertExists()
        composeRule.onNodeWithText("31/12/2024").assertExists()
        composeRule.onNodeWithText("01/02/2020").assertExists()
        composeRule.onNodeWithText("30/11/2024").assertExists()
    }

    @Test
    fun whenUiStateIsLoading_shouldShowLoadingComponent() {
        every { viewModel.uiState } returns mutableStateOf(UiState.Loading)

        composeRule.setContent {
            ExchangeDetailScreen(
                exchangeId = "binance",
                onBackClick = {},
                apiConfig = ApiConfig("", mapOf()),
                viewModel = viewModel
            )
        }

        composeRule.onNodeWithContentDescription("Loading").assertExists()
    }

    @Test
    fun whenUiStateIsError_shouldShowErrorComponent_andRetry() {
        every { viewModel.uiState } returns mutableStateOf(UiState.Error(Exception("Erro de rede")))
        every { viewModel.retry("binance") } just Runs

        composeRule.setContent {
            ExchangeDetailScreen(
                exchangeId = "binance",
                onBackClick = {},
                apiConfig = ApiConfig("", mapOf()),
                viewModel = viewModel
            )
        }

        composeRule.onNodeWithText("Erro de rede").assertExists()
        composeRule.onNodeWithText("Tentar novamente").performClick()
        verify { viewModel.retry("binance") }
    }

    @Test
    fun whenWebsiteClicked_shouldTriggerChromeCustomTab() {
        val navigator = mockk<ChromeCustomTabNavigator>(relaxed = true)
        val context = ApplicationProvider.getApplicationContext<Context>()

        composeRule.setContent {
            CompositionLocalProvider(
                LocalContext provides context
            ) {
                Header(exchange = fakeExchange, webNavigator = navigator)
            }
        }

        composeRule.onNodeWithText("https://binance.com").performClick()
        verify { navigator.openChromeCustomTab(any(), "https://binance.com", any()) }
    }
}