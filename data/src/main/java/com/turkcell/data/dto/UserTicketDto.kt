package com.turkcell.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserTicketDto(
    val id: String,
    val qrCode: String = "",
    val status: String = "",
    val usedAt: String? = null,
    val checkedInBy: String? = null,
    val ticketTypeId: String? = null,
    val ticketType: UserTicketTypeDto? = null
)
