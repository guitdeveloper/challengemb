package br.com.challenge.features.exchanges.domain.model

data class ExchangeResume(
    val exchangeId: String,
    val name: String?,
    val volume1dayUsd: Double?,
    val rank: Double?,
    val iconPath: String? = null,
)


