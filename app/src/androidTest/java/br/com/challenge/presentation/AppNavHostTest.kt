package br.com.challenge.presentation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.challenge.core.network.ApiConfig
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.navigation.testing.TestNavHostController
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.ComposeNavigator
import br.com.challenge.core.common.di.commonModule
import br.com.challenge.core.database.dao.ExchangeDao
import br.com.challenge.core.presentation.utils.ChromeCustomTabNavigator
import br.com.challenge.core.service.api.ExchangeApiService
import br.com.challenge.features.exchanges.data.repository.ExchangeRepositoryImpl
import br.com.challenge.features.exchanges.domain.repository.ExchangeRepository
import br.com.challenge.features.exchanges.domain.usecase.FetchExchangesUseCase
import br.com.challenge.features.exchanges.domain.usecase.GetExchangeDetailUseCase
import br.com.challenge.features.exchanges.domain.usecase.GetPagedExchangesUseCase
import br.com.challenge.features.exchanges.presentation.EXCHANGE_LIST
import br.com.challenge.features.exchanges.presentation.detail.ExchangeDetailViewModel
import br.com.challenge.features.exchanges.presentation.list.ExchangeViewModel
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest

@RunWith(AndroidJUnit4::class)
class AppNavHostTest : KoinTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val mockApiConfig = mockk<ApiConfig>(relaxed = true)

    private val testModule = listOf(
        commonModule,
        module {
            single { ChromeCustomTabNavigator() }
            single { mockk<ExchangeDao>(relaxed = true) }
            single { mockk<ExchangeApiService>(relaxed = true) }
            single<ExchangeRepository> { ExchangeRepositoryImpl(get(), get()) }
            single { FetchExchangesUseCase(get()) }
            single { GetPagedExchangesUseCase(get()) }
            single { GetExchangeDetailUseCase(get()) }
            viewModel { ExchangeViewModel(get(), get()) }
            viewModel { ExchangeDetailViewModel(get()) }
        })

    @Before
    fun setUp() {
        stopKoin()
        startKoin {
            modules(testModule)
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun appNavHost_navigatesToExchangeDetailScreen() {
        lateinit var navController: TestNavHostController
        composeRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }

            AppNavHost(
                navController = navController,
                apiConfig = mockApiConfig,
            )
        }

        composeRule.onNodeWithTag(EXCHANGE_LIST, useUnmergedTree = true).assertIsDisplayed()
        composeRule.runOnUiThread {
            navController.navigate("exchange_detail/testId123")
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithText("Detalhes da Exchange").assertIsDisplayed()
    }

    @Test
    @Ignore("Verificar posteriormente")
    fun appNavHost_backNavigationFromDetailToList() {
        lateinit var navController: TestNavHostController
        composeRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }

            AppNavHost(
                navController = navController,
                apiConfig = mockApiConfig,
            )
        }

        composeRule.runOnUiThread {
            navController.navigate("exchange_detail/testBack")
        }

        composeRule.waitForIdle()
        composeRule.runOnUiThread {
            navController.popBackStack()
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag(EXCHANGE_LIST, useUnmergedTree = true).assertIsDisplayed()
    }
}