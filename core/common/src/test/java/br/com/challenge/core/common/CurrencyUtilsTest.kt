package br.com.challenge.core.common

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName

class CurrencyUtilsTest {

    @DisplayName("formatCurrencyAbbreviated")
    @ParameterizedTest(name = " = {1}")
    @CsvSource(
        "null, null",
        "999.99, '$999,99'",
        "123456, '$123,46K'",
        "2345678, '$2,35M'",
        "1200000000, '$1,20B'",
        "5000000000000, '$5,00T'"
    )
    fun `should format currency correctly`(input: String?, expected: String) {
        val value = input?.takeIf { it != "null" }?.toDoubleOrNull()
        val actual = CurrencyUtils.formatCurrencyAbbreviated(value)
        assertEquals(expected, "$actual")
    }
}