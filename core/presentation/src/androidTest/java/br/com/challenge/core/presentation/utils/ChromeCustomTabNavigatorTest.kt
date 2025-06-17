package br.com.challenge.core.presentation.utils

import android.app.Instrumentation
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import androidx.compose.ui.graphics.Color

@RunWith(AndroidJUnit4::class)
class ChromeCustomTabNavigatorTest {

    private val navigator = ChromeCustomTabNavigator()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun openChromeCustomTab_launchesCustomTabIntent() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val testUrl = "https://www.example.com"
        val toolbarColor = Color.Red
        Intents.intending(IntentMatchers.hasAction(Intent.ACTION_VIEW))
            .respondWith(Instrumentation.ActivityResult(0, null))

        navigator.openChromeCustomTab(context, testUrl, toolbarColor)

        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasAction(Intent.ACTION_VIEW),
                IntentMatchers.hasData(testUrl)
            )
        )
    }
}