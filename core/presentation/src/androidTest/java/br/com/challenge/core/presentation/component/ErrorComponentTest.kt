package br.com.challenge.core.presentation.component

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class ErrorComponentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun errorComponent_displaysCorrectTexts() {
        val testMessage = "Erro ao carregar dados"
        composeTestRule.setContent {
            ErrorComponent(
                message = testMessage,
                onRetry = {}
            )
        }
        composeTestRule.onNodeWithText("Ops! Algo deu errado").assertExists()
        composeTestRule.onNodeWithText(testMessage).assertExists()
        composeTestRule.onNodeWithText("Tentar Novamente").assertExists()
    }

    @Test
    fun errorComponent_callsOnRetry_whenButtonClicked() {
        var clicked = false
        composeTestRule.setContent {
            ErrorComponent(
                message = "Qualquer mensagem",
                onRetry = { clicked = true }
            )
        }
        composeTestRule.onNodeWithText("Tentar Novamente").performClick()
        assert(clicked)
    }
}