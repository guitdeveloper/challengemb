package br.com.challenge.core.common

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class SessionTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun `saves and getApiKey returns the saved value`() = runTest {
        val apiKey = "api_key"
        Session.saveApiKey(context, apiKey)

        val savedKey = Session.getApiKey(context).first()
        assertEquals(apiKey, savedKey)
    }

    @Test
    fun `saves and getApiBaseUrl returns the saved value`() = runTest {
        val baseUrl = "https://api.com.br"
        Session.saveApiBaseUrl(context, baseUrl)

        val savedBaseUrl = Session.getApiBaseUrl(context).first()
        assertEquals(baseUrl, savedBaseUrl)
    }
}