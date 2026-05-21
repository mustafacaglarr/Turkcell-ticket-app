package com.turkcell.core.domain

interface TicketRepository {
    suspend fun getMyTickets(): Result<List<UserTicket>>
    suspend fun getMyTicketDetail(id: String): Result<UserTicket>
}
