package com.turkcell.ticketapp.eventdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.event.Event
import com.turkcell.core.domain.event.EventRepository
import com.turkcell.core.domain.event.TicketType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EventDetailUiState(
    val isLoading: Boolean = false,
    val event: Event? = null,
    val errorMessage: String? = null,
    val selectedQuantities: Map<String, Int> = emptyMap()
) {
    val totalCents: Int
        get() = event?.ticketTypes.orEmpty().sumOf { ticketType ->
            (selectedQuantities[ticketType.id] ?: 0) * ticketType.priceCents
        }
}

class EventDetailViewModel(
    private val eventRepository: EventRepository
) : ViewModel() {
    private val _state = MutableStateFlow(EventDetailUiState())
    val state: StateFlow<EventDetailUiState> = _state.asStateFlow()

    fun loadEvent(eventId: String) {
        val current = _state.value
        if (current.isLoading || current.event?.id == eventId) return

        _state.update {
            it.copy(
                isLoading = true,
                errorMessage = null,
                selectedQuantities = emptyMap()
            )
        }

        viewModelScope.launch {
            eventRepository.getEvent(eventId)
                .onSuccess { event ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            event = event,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            event = null,
                            errorMessage = error.message ?: "Etkinlik detayı yüklenemedi."
                        )
                    }
                }
        }
    }

    fun increase(ticketType: TicketType) {
        _state.update { state ->
            val currentQuantity = state.selectedQuantities[ticketType.id] ?: 0
            val maxQuantity = minOf(20, ticketType.remaining)
            if (currentQuantity >= maxQuantity) {
                state
            } else {
                state.copy(
                    selectedQuantities = state.selectedQuantities + (ticketType.id to currentQuantity + 1)
                )
            }
        }
    }

    fun decrease(ticketTypeId: String) {
        _state.update { state ->
            val currentQuantity = state.selectedQuantities[ticketTypeId] ?: 0
            if (currentQuantity <= 0) {
                state
            } else {
                val nextQuantity = currentQuantity - 1
                val nextQuantities = if (nextQuantity == 0) {
                    state.selectedQuantities - ticketTypeId
                } else {
                    state.selectedQuantities + (ticketTypeId to nextQuantity)
                }
                state.copy(selectedQuantities = nextQuantities)
            }
        }
    }
}
