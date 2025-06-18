package br.com.challenge.presentation

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun mainScreen_showsLoadingOrNavHost() {
        composeTestRule.waitForIdle()

        composeTestRule.onRoot().assertExists()
        composeTestRule.onNodeWithTag(NAV_HOST, useUnmergedTree = true).assertExists()
    }
}