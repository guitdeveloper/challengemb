package br.com.challenge.core.service.dto

import com.google.gson.annotations.SerializedName

data class ExchangeIconDto(
    @SerializedName("exchange_id")
    val exchangeId: String,
    @SerializedName("url")
    val iconUrl: String,
)
