package br.com.challenge.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(private val headers: Map<String, String>) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder().apply {
            headers.forEach { (key, value) ->
                addHeader(key, value)
            }
        }
        return chain.proceed(requestBuilder.build())
    }
}
