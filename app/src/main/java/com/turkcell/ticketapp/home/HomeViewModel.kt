package com.turkcell.ticketapp.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.event.Event
import com.turkcell.core.domain.event.EventRepository
import com.turkcell.core.domain.ticket.TicketRepository
import com.turkcell.core.domain.ticket.UserTicket
import com.turkcell.ticketapp.common.toTicketUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isEventsLoading: Boolean = false,
    val isTicketsLoading: Boolean = false,
    val events: List<Event> = emptyList(),
    val tickets: List<UserTicket> = emptyList(),
    val eventsError: String? = null,
    val ticketsError: String? = null
) {
    val isLoading: Boolean
        get() = isEventsLoading || isTicketsLoading
}

class HomeViewModel(
    private val eventRepository: EventRepository,
    private val ticketRepository: TicketRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        loadHome()
    }

    fun loadHome() {
        loadEvents()
        loadTickets()
    }

    private fun loadEvents() {
        if (_state.value.isEventsLoading) return

        _state.update { it.copy(isEventsLoading = true, eventsError = null) }

        viewModelScope.launch {
            eventRepository.getEvents().fold(
                onSuccess = { events ->
                    _state.update {
                        it.copy(
                            isEventsLoading = false,
                            events = events,
                            eventsError = null
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isEventsLoading = false,
                            eventsError = error.message ?: "Etkinlikler yüklenemedi."
                        )
                    }
                }
            )
        }
    }

    private fun loadTickets() {
        if (_state.value.isTicketsLoading) return

        _state.update { it.copy(isTicketsLoading = true, ticketsError = null) }

        viewModelScope.launch {
            ticketRepository.getMyTickets().fold(
                onSuccess = { tickets ->
                    _state.update {
                        it.copy(
                            isTicketsLoading = false,
                            tickets = tickets,
                            ticketsError = null
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isTicketsLoading = false,
                            ticketsError = error.toTicketUserMessage()
                        )
                    }
                }
            )
        }
    }
}
