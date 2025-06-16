package br.com.challenge.core.presentation.component

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Rule
import org.junit.Test

class LoadingComponentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loadingComponent_displaysLoadingText() {
        composeTestRule.setContent {
            LoadingComponent()
        }
        composeTestRule.onNodeWithText("Carregando...").assertIsDisplayed()
    }
}