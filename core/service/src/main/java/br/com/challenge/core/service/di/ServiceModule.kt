package br.com.challenge.core.service.di

import br.com.challenge.core.service.api.ExchangeApiService
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import retrofit2.Retrofit

val serviceModule = module {
    factory { (baseUrl: String, headers: Map<String, String>) ->
        get<Retrofit> { parametersOf(baseUrl, headers) }
            .create(ExchangeApiService::class.java)
    }
}
