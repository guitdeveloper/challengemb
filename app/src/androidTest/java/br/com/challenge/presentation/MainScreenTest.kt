package br.com.challenge.presentation

import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.challenge.core.network.ApiConfig
import br.com.challenge.core.presentation.component.LOADING
import br.com.challenge.features.exchanges.presentation.EXCHANGE_LIST
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: MainViewModel

    @Test
    fun whenApiConfigIsLoaded_showsAppNavHost() {
        val apiConfig = ApiConfig("https://api.example.com", mapOf("Authorization" to "abc"))
        viewModel = mockk(relaxed = true)
        every { viewModel.apiConfig } returns MutableStateFlow(apiConfig)

        composeTestRule.setContent {
            MainScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText(EXCHANGE_LIST, useUnmergedTree = true).assertExists()
    }

    @Test
    fun whenApiConfigIsNull_showsLoadingIndicator() {
        viewModel = mockk(relaxed = true)
        every { viewModel.apiConfig } returns MutableStateFlow(null)

        composeTestRule.setContent {
            MainScreen(viewModel = viewModel)
        }

        composeTestRule
            .onNode(hasAnyAncestor(isRoot()) and hasTestTag(LOADING))
            .assertExists()
    }
}