package com.turkcell.core.util

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

private val turkishMonthsShort = arrayOf(
    "Oca", "Şub", "Mar", "Nis", "May", "Haz", "Tem", "Ağu", "Eyl", "Eki", "Kas", "Ara"
)

object DateFormatter {
    private val inputFormats = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
    ).onEach { format ->
        format.timeZone = TimeZone.getTimeZone("UTC")
    }

    fun format(value: String): String {
        val date = inputFormats.firstNotNullOfOrNull { format ->
            runCatching { format.parse(value) }.getOrNull()
        } ?: return value

        val output = SimpleDateFormat("dd MM yyyy HH:mm", Locale.US)
        val formatted = output.format(date)
        val parts = formatted.split(" ")
        val monthIndex = parts.getOrNull(1)?.toIntOrNull()?.minus(1)
        val month = monthIndex?.let { turkishMonthsShort.getOrNull(it) } ?: return value

        return "${parts[0]} $month ${parts[2]} ${parts[3]}"
    }
}
