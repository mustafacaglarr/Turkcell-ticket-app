package com.turkcell.ticketapp.ticketdetail

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

data class TicketDetailUiState(
    val isLoading: Boolean = false,
    val ticket: UserTicket? = null,
    val errorMessage: String? = null
)

class TicketDetailViewModel(
    private val ticketRepository: TicketRepository,
    private val strings: StringProvider
) : ViewModel() {
    private val _state = MutableStateFlow(TicketDetailUiState())
    val state: StateFlow<TicketDetailUiState> = _state.asStateFlow()

    fun loadTicket(ticketId: String) {
        val current = _state.value
        if (current.isLoading || current.ticket?.id == ticketId) return

        _state.update {
            it.copy(
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            ticketRepository.getTicket(ticketId)
                .onSuccess { ticket ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            ticket = ticket,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            ticket = null,
                            errorMessage = error.toTicketUserMessage(strings)
                        )
                    }
                }
        }
    }
}
