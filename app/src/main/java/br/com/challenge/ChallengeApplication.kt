package br.com.challenge

import android.app.Application
import br.com.challenge.core.common.Session
import br.com.challenge.core.database.di.databaseModule
import br.com.challenge.core.network.di.networkModule
import br.com.challenge.core.service.di.serviceModule
import br.com.challenge.features.exchanges.di.featureExchangeModule
import br.com.challenge.di.appModule
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class ChallengeApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        applicationScope.launch {
            Session.saveApiBaseUrl(applicationContext, BuildConfig.EXCHANGE_BASE_URL)
            Session.saveApiKey(applicationContext, BuildConfig.API_KEY)
        }

        startKoin {
            androidContext(this@ChallengeApplication)
            modules(
                appModule,
                networkModule,
                databaseModule,
                serviceModule,
                featureExchangeModule,
            )
        }
    }
}
