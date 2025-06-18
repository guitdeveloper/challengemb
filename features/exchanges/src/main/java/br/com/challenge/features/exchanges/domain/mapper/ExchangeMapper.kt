package br.com.challenge.features.exchanges.domain.mapper

import br.com.challenge.core.database.entity.ExchangeEntity
import br.com.challenge.core.service.dto.ExchangeDto
import br.com.challenge.features.exchanges.domain.model.Exchange
import br.com.challenge.features.exchanges.domain.model.ExchangeResume

fun ExchangeEntity.toResume() =
    ExchangeResume(
        this.exchangeId,
        this.name,
        this.volume1dayUsd,
        this.rank,
        this.iconPath,
    )

fun ExchangeEntity.toDetail() =
    Exchange(
        this.exchangeId,
        this.name,
        this.websiteUrl,
        this.volume1dayUsd,
        this.volume1hrsUsd,
        this.volume1mthUsd,
        this.dataQuoteStart,
        this.dataQuoteEnd,
        this.dataOrderBookStart,
        this.dataOrderBookEnd,
        this.dataTradeStart,
        this.dataTradeEnd,
        this.dataSymbolsCount,
        this.rank,
        this.iconPath,
    )

fun ExchangeDto.toDetail(iconUrl: String?) =
    ExchangeEntity(
        this.exchangeId,
        this.name,
        this.websiteUrl,
        this.volume1dayUsd,
        this.volume1hrsUsd,
        this.volume1mthUsd,
        this.dataQuoteStart,
        this.dataQuoteEnd,
        this.dataOrderBookStart,
        this.dataOrderBookEnd,
        this.dataTradeStart,
        this.dataTradeEnd,
        this.dataSymbolsCount,
        this.rank,
        iconUrl,
    )
