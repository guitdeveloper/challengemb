package br.com.challenge.features.exchanges.presentation.list

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.challenge.features.exchanges.domain.ExchangeResume
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExchangeItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val fakeExchange = ExchangeResume(
        exchangeId = "binance",
        name = "Binance",
        volume1dayUsd = 1000000.0,
        rank = 1.0
    )

    @Test
    fun exchangeItem_displaysCorrectInfo() {
        composeTestRule.setContent {
            ExchangeItem(exchange = fakeExchange, onClick = {})
        }

        composeTestRule.onNodeWithText("Binance").assertIsDisplayed()
        composeTestRule.onNodeWithText("ID: binance").assertIsDisplayed()
        composeTestRule.onNodeWithText("Volume 24h (USD)").assertIsDisplayed()
        composeTestRule.onNodeWithText("\$1.00M").assertIsDisplayed() // formatCurrencyAbbreviated(1_000_000.0)
    }

    @Test
    fun exchangeItem_triggersOnClick() {
        var clicked = false

        composeTestRule.setContent {
            ExchangeItem(exchange = fakeExchange, onClick = { clicked = true })
        }

        // Clica em qualquer texto dentro do Card
        composeTestRule.onNodeWithText("Binance").performClick()

        assert(clicked)
    }
}