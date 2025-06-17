package br.com.challenge.core.common

import android.util.Log
import java.util.Locale
import java.util.TimeZone

object DateUtils {

    fun formatIsoDate(
        isoDate: String,
        outputPattern: String = "dd/MM/yyyy",
    ): String? {
        return try {
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'", Locale.US)
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(isoDate)
            val outputFormat = java.text.SimpleDateFormat(outputPattern, Locale("pt", "BR"))
            outputFormat.format(date!!)
        } catch (e: Exception) {
            Log.e("PresentationError", "Erro ao executar conversão de data", e)
            null
        }
    }
}