package br.com.challenge

import android.app.Application
import br.com.challenge.core.common.di.commonModule
import br.com.challenge.core.database.di.databaseModule
import br.com.challenge.core.network.di.networkModule
import br.com.challenge.core.service.di.serviceModule
import br.com.challenge.features.exchanges.di.featureExchangeModule
import br.com.challenge.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class ChallengeApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@ChallengeApplication)
            modules(
                appModule,
                commonModule,
                networkModule,
                databaseModule,
                serviceModule,
                featureExchangeModule,
            )
        }
    }
}
