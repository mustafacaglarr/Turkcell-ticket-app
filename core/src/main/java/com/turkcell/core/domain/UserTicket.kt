package com.turkcell.core.domain

data class UserTicket(
    val id: String,
    val qrCode: String,
    val status: String,
    val ticketTypeId: String?,
    val ticketTypeName: String?,
    val eventName: String?,
    val eventVenue: String?,
    val eventStartsAt: String?
)
