package br.com.challenge.core.network.interceptor

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.Interceptor
import okhttp3.Request
import org.junit.jupiter.api.Test

class HeaderInterceptorTest {

    @Test
    fun `intercept should add headers to the request`() {
        val headers = mapOf("Authorization" to "Bearer token", "Accept" to "application/json")
        val interceptor = HeaderInterceptor(headers)
        val originalRequest = mockk<Request>(relaxed = true)
        val requestBuilder = mockk<Request.Builder>(relaxed = true)
        val builtRequest = mockk<Request>(relaxed = true)
        val chain = mockk<Interceptor.Chain>()
        every { chain.request() } returns originalRequest
        every { originalRequest.newBuilder() } returns requestBuilder
        every { requestBuilder.addHeader(any(), any()) } returns requestBuilder
        every { requestBuilder.build() } returns builtRequest
        every { chain.proceed(builtRequest) } returns mockk(relaxed = true)

        interceptor.intercept(chain)

        headers.forEach { (key, value) ->
            verify(exactly = 1) { requestBuilder.addHeader(key, value) }
        }
        verify(exactly = 1) { requestBuilder.build() }
        verify(exactly = 1) { chain.proceed(builtRequest) }
    }
}