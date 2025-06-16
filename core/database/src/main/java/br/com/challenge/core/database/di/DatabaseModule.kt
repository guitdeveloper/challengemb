package br.com.challenge.core.database.di

import androidx.room.Room
import br.com.challenge.core.database.ChallengeDatabase
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(get(), ChallengeDatabase::class.java, "challenge_db")
            .fallbackToDestructiveMigration(false)
            .build()
    }

    single { get<ChallengeDatabase>().exchangeDao() }
}
