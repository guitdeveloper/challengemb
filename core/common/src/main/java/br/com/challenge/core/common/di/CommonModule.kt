package br.com.challenge.core.common.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import br.com.challenge.core.common.Session
import org.koin.dsl.module

val Context.apiPreferences: DataStore<Preferences> by preferencesDataStore(name = "api_prefs")

val commonModule = module {
    single<DataStore<Preferences>> {
        get<Context>().apiPreferences
    }

    single { Session(get()) }
}