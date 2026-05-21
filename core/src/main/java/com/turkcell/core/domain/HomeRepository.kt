package com.turkcell.core.domain

interface HomeRepository {
    suspend fun getEvents(upcoming: Boolean = true): Result<List<Event>>
    suspend fun getEventDetail(id: String): Result<Event>
    suspend fun getMyTickets(): Result<List<UserTicket>>
    suspend fun getMyTicketDetail(id: String): Result<UserTicket>
}
