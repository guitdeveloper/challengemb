package br.com.challenge.core.network.exception

import okio.IOException

class NoConnectivityException : IOException() {
    override val message: String
        get() = "Not connected"
}
