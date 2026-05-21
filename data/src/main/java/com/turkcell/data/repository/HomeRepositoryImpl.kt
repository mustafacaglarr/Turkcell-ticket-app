package com.turkcell.data.repository

import com.turkcell.core.domain.Event
import com.turkcell.core.domain.HomeRepository
import com.turkcell.core.domain.TicketType
import com.turkcell.core.domain.UserTicket
import com.turkcell.data.dto.EventDto
import com.turkcell.data.dto.TicketTypeDto
import com.turkcell.data.dto.UserTicketDto
import com.turkcell.data.remote.HomeApi
import com.turkcell.data.util.runCatchingApi

class HomeRepositoryImpl(
    private val homeApi: HomeApi
) : HomeRepository {
    override suspend fun getEvents(upcoming: Boolean): Result<List<Event>> = runCatchingApi {
        homeApi.getEvents(upcoming = upcoming)
    }.map { events -> events.map { it.toDomain() } }

    override suspend fun getEventDetail(id: String): Result<Event> = runCatchingApi {
        homeApi.getEventDetail(id)
    }.map { event -> event.toDomain() }

    override suspend fun getMyTickets(): Result<List<UserTicket>> = runCatchingApi {
        homeApi.getMyTickets()
    }.map { tickets -> tickets.map { it.toDomain() } }

    override suspend fun getMyTicketDetail(id: String): Result<UserTicket> = runCatchingApi {
        homeApi.getMyTicketDetail(id)
    }.map { ticket -> ticket.toDomain() }

    private fun EventDto.toDomain(): Event = Event(
        id = id,
        name = name,
        description = description,
        venue = venue,
        startsAt = startsAt,
        endsAt = endsAt,
        ticketTypes = ticketTypes.map { it.toDomain() }
    )

    private fun TicketTypeDto.toDomain(): TicketType = TicketType(
        id = id,
        name = name,
        priceCents = priceCents,
        capacity = capacity,
        soldCount = soldCount,
        remaining = remaining
    )

    private fun UserTicketDto.toDomain(): UserTicket = UserTicket(
        id = id,
        qrCode = qrCode,
        status = status,
        ticketTypeId = ticketTypeId ?: ticketType?.id,
        ticketTypeName = ticketType?.name,
        eventName = ticketType?.event?.name,
        eventVenue = ticketType?.event?.venue,
        eventStartsAt = ticketType?.event?.startsAt
    )
}
