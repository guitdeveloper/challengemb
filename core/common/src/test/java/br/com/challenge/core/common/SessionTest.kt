package br.com.challenge.core.common

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
class SessionTest {

    private lateinit var session: Session

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val testFile = context.preferencesDataStoreFile("test_prefs_${UUID.randomUUID()}")

        val dataStore = PreferenceDataStoreFactory.create(
            produceFile = { testFile }
        )

        session = Session(dataStore)
    }

    @Test
    fun `should save and retrieve API key correctly`() = runBlocking {
        val expectedApiKey = "my-secret-key"

        session.saveApiKey(expectedApiKey)
        val actualApiKey = session.getApiKey().first()

        assertEquals(expectedApiKey, actualApiKey)
    }

    @Test
    fun `should save and retrieve API base URL correctly`() = runBlocking {
        val expectedUrl = "https://api.example.com"

        session.saveApiBaseUrl(expectedUrl)
        val actualUrl = session.getApiBaseUrl().first()

        assertEquals(expectedUrl, actualUrl)
    }

    @Test
    fun `should return empty string if API base URL not set`() = runBlocking {
        val url = session.getApiBaseUrl().first()

        assertEquals("", url)
    }

    @Test
    fun `should return empty string if API key not set`() = runBlocking {
        val key = session.getApiKey().first()

        assertEquals("", key)
    }
}
