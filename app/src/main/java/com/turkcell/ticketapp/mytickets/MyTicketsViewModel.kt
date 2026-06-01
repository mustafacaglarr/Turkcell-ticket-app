package com.turkcell.ticketapp.mytickets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.ticket.TicketRepository
import com.turkcell.core.domain.ticket.UserTicket
import com.turkcell.ticketapp.common.StringProvider
import com.turkcell.ticketapp.common.toTicketUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MyTicketsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val tickets: List<UserTicket> = emptyList(),
    val errorMessage: String? = null
)

class MyTicketsViewModel(
    private val ticketRepository: TicketRepository,
    private val strings: StringProvider
) : ViewModel() {
    private val _state = MutableStateFlow(MyTicketsUiState())
    val state: StateFlow<MyTicketsUiState> = _state.asStateFlow()

    init {
        loadTickets()
    }

    fun loadTickets() {
        loadTickets(isRefresh = false)
    }

    fun refresh() {
        loadTickets(isRefresh = true)
    }

    private fun loadTickets(isRefresh: Boolean) {
        val current = _state.value
        if (current.isLoading || current.isRefreshing) return

        _state.update {
            it.copy(
                isLoading = !isRefresh,
                isRefreshing = isRefresh,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            ticketRepository.getMyTickets()
                .onSuccess { tickets ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            tickets = tickets,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = error.toTicketUserMessage(strings)
                        )
                    }
                }
        }
    }
}
