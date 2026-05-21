package com.turkcell.data.remote

import com.turkcell.data.dto.EventDto
import com.turkcell.data.dto.UserTicketDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HomeApi {
    @GET("/events")
    suspend fun getEvents(@Query("upcoming") upcoming: Boolean = true): List<EventDto>

    @GET("/events/{id}")
    suspend fun getEventDetail(@Path("id") id: String): EventDto

    @GET("/me/tickets")
    suspend fun getMyTickets(): List<UserTicketDto>

    @GET("/me/tickets/{id}")
    suspend fun getMyTicketDetail(@Path("id") id: String): UserTicketDto
}
