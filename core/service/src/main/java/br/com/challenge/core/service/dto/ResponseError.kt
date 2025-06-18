package br.com.challenge.core.service.dto

import com.google.gson.annotations.SerializedName

data class ResponseError(
    @SerializedName("error")
    val error: String,
)