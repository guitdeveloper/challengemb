package br.com.challenge.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exchanges")
data class ExchangeEntity(
    @PrimaryKey val exchangeId: String,
    val name: String?,
    val websiteUrl: String?,
    val volume1dayUsd: Double?,
    val volume1hrsUsd: Double? = null,
    val volume1mthUsd: Double? = null,
    val dataQuoteStart: String? = null,
    val dataQuoteEnd: String? = null,
    val dataOrderBookStart: String? = null,
    val dataOrderBookEnd: String? = null,
    val dataTradeStart: String? = null,
    val dataTradeEnd: String? = null,
    val dataSymbolsCount: Int? = null,
    val rank: Double? = null,
    val iconPath: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)
