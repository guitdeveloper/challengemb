package br.com.challenge.core.network

data class ApiConfig(
    val baseUrl: String,
    val headers: Map<String, String>
)
