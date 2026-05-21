package com.turkcell.data.repository

import com.turkcell.core.domain.Event
import com.turkcell.core.domain.EventRepository
import com.turkcell.data.mapper.toDomain
import com.turkcell.data.remote.EventApi
import com.turkcell.data.util.runCatchingApi

class EventRepositoryImpl(
    private val eventApi: EventApi
) : EventRepository {
    override suspend fun getEvents(upcoming: Boolean): Result<List<Event>> = runCatchingApi {
        eventApi.getEvents(upcoming = upcoming)
    }.map { events -> events.map { it.toDomain() } }

    override suspend fun getEventDetail(id: String): Result<Event> = runCatchingApi {
        eventApi.getEventDetail(id)
    }.map { event -> event.toDomain() }
}
