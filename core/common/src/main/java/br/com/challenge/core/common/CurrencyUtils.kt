package br.com.challenge.core.common

import android.annotation.SuppressLint

object CurrencyUtils {

    @SuppressLint("DefaultLocale")
    fun formatCurrencyAbbreviated(currency: Double?): String {
        return currency?.let {
            val units = arrayOf("", "K", "M", "B", "T")
            var value = it
            var index = 0

            while (value >= 1_000 && index < units.lastIndex) {
                value /= 1_000
                index++
            }

            "$%.2f%s".format(value, units[index])
        } ?: "N/A"
    }
}
