package br.com.challenge.core.common

import android.util.Log
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.Locale

class DateUtilsTest {

    @Nested
    inner class FormatIsoDate {

        @Test
        fun `should return formatted date in default pattern`() {
            val isoDate = "2023-06-15T14:30:00.0000000Z"
            val expected = "15/06/2023"
            val actual = DateUtils.formatIsoDate(isoDate)
            assertEquals(expected, actual)
        }

        @Test
        fun `should return formatted date in custom pattern`() {
            val isoDate = "2023-06-15T14:30:00.0000000Z"
            val pattern = "dd MMM yyyy"
            val expected = "15 jun. 2023"

            val actual = DateUtils.formatIsoDate(isoDate, pattern)
            assertEquals(expected, actual.lowercase(Locale("pt", "BR")))
        }

        @Test
        fun `should return 'Data inválida' when given invalid date`() {
            val isoDate = "invalid-date"
            val expected = "Data inválida"
            mockkStatic(Log::class)
            every { Log.e(any(), any(), any()) } returns 0
            val actual = DateUtils.formatIsoDate(isoDate)
            assertEquals(expected, actual)
        }
    }
}