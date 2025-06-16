package br.com.challenge.core.network.interceptor

import android.content.Context
import br.com.challenge.core.network.NetworkUtils
import br.com.challenge.core.network.exception.NoConnectivityException
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.verify
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class NetworkConnectionInterceptorTest {

    private val context = mockk<Context>(relaxed = true)
    private val chain = mockk<Interceptor.Chain>(relaxed = true)
    private val request = mockk<Request>(relaxed = true)
    private val response = mockk<Response>(relaxed = true)

    @Test
    fun `intercept should proceed when network is available`() {
        mockkConstructor(NetworkUtils::class)
        every { anyConstructed<NetworkUtils>().isNetworkAvailable() } returns true
        every { chain.request() } returns request
        every { chain.proceed(request) } returns response

        val interceptor = NetworkConnectionInterceptor(context)
        val result = interceptor.intercept(chain)

        verify(exactly = 1) { chain.proceed(request) }
        assert(result == response)
    }

    @Test
    fun `intercept should throw NoConnectivityException when network is not available`() {
        mockkConstructor(NetworkUtils::class)
        every { anyConstructed<NetworkUtils>().isNetworkAvailable() } returns false

        val interceptor = NetworkConnectionInterceptor(context)

        assertThrows(NoConnectivityException::class.java) {
            interceptor.intercept(chain)
        }
    }
}