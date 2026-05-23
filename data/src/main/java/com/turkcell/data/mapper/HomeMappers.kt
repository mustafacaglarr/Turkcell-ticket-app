package com.turkcell.data.mapper

import com.turkcell.core.domain.event.Event
import com.turkcell.core.domain.event.TicketType
import com.turkcell.core.domain.ticket.UserTicket
import com.turkcell.data.dto.EventDto
import com.turkcell.data.dto.TicketTypeDto
import com.turkcell.data.dto.UserTicketDto

internal fun EventDto.toDomain(): Event = Event(
    id = id,
    name = name,
    description = description,
    venue = venue ?: place.orEmpty(),
    startsAt = startsAt,
    endsAt = endsAt,
    ticketTypes = ticketTypes.map { it.toDomain() }
)

internal fun TicketTypeDto.toDomain(): TicketType = TicketType(
    id = id,
    name = name,
    priceCents = priceCents,
    capacity = capacity,
    soldCount = soldCount,
    remaining = remaining
)

internal fun UserTicketDto.toDomain(): UserTicket = UserTicket(
    id = id,
    qrCode = qrCode,
    status = status,
    ticketTypeId = ticketTypeId ?: ticketType?.id,
    ticketTypeName = ticketType?.name,
    eventName = ticketType?.event?.name,
    eventVenue = ticketType?.event?.venue,
    eventStartsAt = ticketType?.event?.startsAt
)
