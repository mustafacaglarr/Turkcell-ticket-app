package com.turkcell.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class EventDto(
    val id: String,
    val name: String,
    val description: String = "",
    val venue: String? = null,
    val place: String? = null,
    val startsAt: String = "",
    val endsAt: String? = null,
    val ticketTypes: List<TicketTypeDto> = emptyList()
)
