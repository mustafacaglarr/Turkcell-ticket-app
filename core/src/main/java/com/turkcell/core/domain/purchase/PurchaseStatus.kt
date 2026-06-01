package com.turkcell.core.domain.purchase

enum class PurchaseStatus {
    PENDING,
    PAID,
    UNKNOWN;

    companion object {
        fun fromApi(value: String): PurchaseStatus = entries.firstOrNull {
            it.name == value.uppercase()
        } ?: UNKNOWN
    }
}
