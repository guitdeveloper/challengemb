package br.com.challenge.core.network.interceptor

import android.content.Context
import br.com.challenge.core.network.NetworkUtils
import br.com.challenge.core.network.exception.NoConnectivityException
import okhttp3.Interceptor
import okhttp3.Response

class NetworkConnectionInterceptor(
    private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val isConnected = NetworkUtils(context).isNetworkAvailable()
        if (!isConnected) {
            throw NoConnectivityException()
        }
        return chain.proceed(chain.request())
    }
}
