package br.com.challenge.features.exchanges.di

import br.com.challenge.core.network.ApiConfig
import br.com.challenge.core.presentation.utils.ChromeCustomTabNavigator
import br.com.challenge.features.exchanges.data.repository.ExchangeRepositoryImpl
import br.com.challenge.features.exchanges.domain.repository.ExchangeRepository
import br.com.challenge.features.exchanges.domain.usecase.GetExchangeDetailUseCase
import br.com.challenge.features.exchanges.domain.usecase.FetchExchangesUseCase
import br.com.challenge.features.exchanges.domain.usecase.GetPagedExchangesUseCase
import br.com.challenge.features.exchanges.presentation.detail.ExchangeDetailViewModel
import br.com.challenge.features.exchanges.presentation.list.ExchangeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val featureExchangeModule = module {
    single { ChromeCustomTabNavigator() }

    factory<ExchangeRepository> { (apiConfig: ApiConfig) ->
        ExchangeRepositoryImpl(
            apiService = get { parametersOf(apiConfig.baseUrl, apiConfig.headers) },
            exchangeDao = get(),
        )
    }

    factory { (apiConfig: ApiConfig) ->
        GetPagedExchangesUseCase(
            repository = get { parametersOf(apiConfig) }
        )
    }

    factory { (apiConfig: ApiConfig) ->
        FetchExchangesUseCase(
            repository = get { parametersOf(apiConfig) }
        )
    }

    factory { (apiConfig: ApiConfig) ->
        GetExchangeDetailUseCase(
            repository = get { parametersOf(apiConfig) }
        )
    }

    viewModel { (apiConfig: ApiConfig) ->
        val useCase = get<FetchExchangesUseCase> { parametersOf(apiConfig) }
        val pagedUseCase = get<GetPagedExchangesUseCase> { parametersOf(apiConfig) }
        ExchangeViewModel(useCase, pagedUseCase)
    }

    viewModel { (apiConfig: ApiConfig) ->
        val useCase = get<GetExchangeDetailUseCase> { parametersOf(apiConfig) }
        ExchangeDetailViewModel(useCase)
    }
}
