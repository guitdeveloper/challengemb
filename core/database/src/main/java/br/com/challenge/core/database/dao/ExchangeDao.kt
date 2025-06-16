package br.com.challenge.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.challenge.core.database.entity.ExchangeEntity

@Dao
interface ExchangeDao {
    @Query("SELECT * FROM exchanges ORDER BY volume1dayUsd DESC")
    fun getPagedItems(): PagingSource<Int, ExchangeEntity>

    @Query("SELECT COUNT(*) FROM exchanges")
    suspend fun getCount(): Int

    @Query("SELECT * FROM exchanges LIMIT 1")
    suspend fun first(): ExchangeEntity

    @Query("SELECT * FROM exchanges WHERE exchangeid = :exchangeId")
    suspend fun getExchangeBy(exchangeId: String): ExchangeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exchangeList: List<ExchangeEntity>)

    @Query("DELETE FROM exchanges")
    suspend fun removeAll()
}
