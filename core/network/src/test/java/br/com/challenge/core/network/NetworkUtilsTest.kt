package br.com.challenge.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested

class NetworkUtilsTest {

    private lateinit var context: Context
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCapabilities: NetworkCapabilities
    private lateinit var network: Network

    @Nested
    inner class IsNetworkAvailable {
        @BeforeEach
        fun setup() {
            context = mockk()
            connectivityManager = mockk()
            networkCapabilities = mockk()
            network = mockk()

            every {
                context.getSystemService(Context.CONNECTIVITY_SERVICE)
            } returns connectivityManager
        }

        @AfterEach
        fun teardown() {
            unmockkAll()
        }

        @Test
        fun `should return true when connected to Wi-Fi`() {
            every { connectivityManager.activeNetwork } returns network
            every { connectivityManager.getNetworkCapabilities(network) } returns networkCapabilities
            every { networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns true
            every { networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns false

            val result = NetworkUtils(context).isNetworkAvailable()

            assertTrue(result)
        }

        @Test
        fun `should return true when connected to Cellular`() {
            every { connectivityManager.activeNetwork } returns network
            every { connectivityManager.getNetworkCapabilities(network) } returns networkCapabilities
            every { networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns false
            every { networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns true

            val result = NetworkUtils(context).isNetworkAvailable()

            assertTrue(result)
        }

        @Test
        fun `should return false when no active network`() {
            every { connectivityManager.activeNetwork } returns null

            val result = NetworkUtils(context).isNetworkAvailable()

            assertFalse(result)
        }

        @Test
        fun `should return false when no network capabilities`() {
            every { connectivityManager.activeNetwork } returns network
            every { connectivityManager.getNetworkCapabilities(network) } returns null

            val result = NetworkUtils(context).isNetworkAvailable()

            assertFalse(result)
        }

        @Test
        fun `should return false when not connected to Wi-Fi or Cellular`() {
            every { connectivityManager.activeNetwork } returns network
            every { connectivityManager.getNetworkCapabilities(network) } returns networkCapabilities
            every { networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns false
            every { networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns false

            val result = NetworkUtils(context).isNetworkAvailable()

            assertFalse(result)
        }
    }
}