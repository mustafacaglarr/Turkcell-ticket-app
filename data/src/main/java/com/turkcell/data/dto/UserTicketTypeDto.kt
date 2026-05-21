package com.turkcell.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserTicketTypeDto(
    val id: String,
    val name: String = "",
    val priceCents: Int = 0,
    val event: TicketEventDto? = null
)
