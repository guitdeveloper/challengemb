package br.com.challenge.core.network.exception

import okio.IOException

class NoConnectivityException : IOException() {
    override val message: String
        get() = "Sem conexão com a Internet. Verifique sua conexão Wi-Fi ou Dados Móveis."
}
