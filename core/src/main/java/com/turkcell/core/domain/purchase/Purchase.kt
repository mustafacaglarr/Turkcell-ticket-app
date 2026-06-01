package com.turkcell.core.domain.purchase

data class Purchase(
    val id: String,
    val status: PurchaseStatus,
    val totalCents: Int,
    val paidAt: String?,
    val items: List<PurchaseItem>,
    val tickets: List<Ticket>
)
