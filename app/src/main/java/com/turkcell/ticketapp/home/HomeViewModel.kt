package com.turkcell.ticketapp.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.Event
import com.turkcell.core.domain.HomeRepository
import com.turkcell.core.domain.UserTicket
import kotlinx.coroutines.launch

class HomeViewModel(
    private val homeRepository: HomeRepository
) : ViewModel() {
    var uiState by mutableStateOf(HomeUiState())
        private set

    init {
        loadHome()
    }

    fun loadHome() {
        if (uiState.isLoading) return

        viewModelScope.launch {
            uiState = uiState.copy(
                isLoading = true,
                eventErrorMessage = null,
                ticketErrorMessage = null
            )

            val eventsResult = homeRepository.getEvents(upcoming = true)
            val ticketsResult = homeRepository.getMyTickets()

            uiState = uiState.copy(
                isLoading = false,
                events = eventsResult.getOrElse { emptyList() },
                tickets = ticketsResult.getOrElse { emptyList() },
                eventErrorMessage = eventsResult.exceptionOrNull()?.message,
                ticketErrorMessage = ticketsResult.exceptionOrNull()?.message
            )
        }
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val events: List<Event> = emptyList(),
    val tickets: List<UserTicket> = emptyList(),
    val eventErrorMessage: String? = null,
    val ticketErrorMessage: String? = null
)
