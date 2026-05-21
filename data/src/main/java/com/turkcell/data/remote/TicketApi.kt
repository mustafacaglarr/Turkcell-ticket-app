package com.turkcell.data.remote

import com.turkcell.data.dto.UserTicketDto
import retrofit2.http.GET
import retrofit2.http.Path

interface TicketApi {
    @GET("/me/tickets")
    suspend fun getMyTickets(): List<UserTicketDto>

    @GET("/me/tickets/{id}")
    suspend fun getMyTicketDetail(@Path("id") id: String): UserTicketDto
}
