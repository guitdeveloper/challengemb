package br.com.challenge.core.presentation.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class DetailRowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun detailRow_displaysLabelAndValue() {
        val testLabel = "Nome"
        val testValue = "Guilherme"

        composeTestRule.setContent {
            MaterialTheme {
                DetailRow(
                    label = testLabel,
                    value = testValue
                )
            }
        }

        composeTestRule.onNodeWithText(testLabel).assertIsDisplayed()
        composeTestRule.onNodeWithText(testValue).assertIsDisplayed()
    }
}