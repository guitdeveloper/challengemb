package br.com.challenge.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.challenge.core.database.dao.ExchangeDao
import br.com.challenge.core.database.entity.ExchangeEntity

@Database(
    entities = [
        ExchangeEntity::class,
   ],
    version = 1
)
abstract class ChallengeDatabase : RoomDatabase() {
    abstract fun exchangeDao(): ExchangeDao
}
