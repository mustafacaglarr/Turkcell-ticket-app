package com.turkcell.core.domain.purchase

enum class TicketStatus {
    VALID,
    USED,
    CANCELLED,
    UNKNOWN;

    companion object {
        fun fromApi(value: String): TicketStatus = entries.firstOrNull {
            it.name == value.uppercase()
        } ?: UNKNOWN
    }
}
