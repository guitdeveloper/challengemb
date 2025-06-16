package br.com.challenge.core.network.di

import br.com.challenge.core.network.interceptor.HeaderInterceptor
import br.com.challenge.core.network.interceptor.NetworkConnectionInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    factory { (headers: Map<String, String>) -> HeaderInterceptor(headers) }

    factory { (headers: Map<String, String>) ->
        OkHttpClient.Builder()
            .addInterceptor(NetworkConnectionInterceptor(get()))
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(get<HeaderInterceptor> { parametersOf(headers) })
            .build()
    }

    factory { (baseUrl: String, headers: Map<String, String>) ->
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get { parametersOf(headers) })
            .build()
    }
}
