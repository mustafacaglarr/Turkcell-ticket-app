package com.turkcell.ticketapp.eventdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.event.Event
import com.turkcell.core.domain.event.EventRepository
import com.turkcell.core.domain.event.TicketType
import com.turkcell.core.domain.purchase.CreatePurchaseItem
import com.turkcell.core.domain.purchase.Purchase
import com.turkcell.core.domain.purchase.PurchaseRepository
import com.turkcell.ticketapp.common.toPurchaseUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EventDetailUiState(
    val isLoading: Boolean = false,
    val event: Event? = null,
    val errorMessage: String? = null,
    val selectedQuantities: Map<String, Int> = emptyMap(),
    val isCreatingPurchase: Boolean = false,
    val isPaying: Boolean = false,
    val purchase: Purchase? = null,
    val purchaseErrorMessage: String? = null,
    val showPaymentDialog: Boolean = false,
    val paymentCompleted: Boolean = false
) {
    val totalCents: Int
        get() = event?.ticketTypes.orEmpty().sumOf { ticketType ->
            (selectedQuantities[ticketType.id] ?: 0) * ticketType.priceCents
        }

    val hasSelection: Boolean
        get() = selectedQuantities.any { it.value > 0 }
}

class EventDetailViewModel(
    private val eventRepository: EventRepository,
    private val purchaseRepository: PurchaseRepository
) : ViewModel() {
    private val _state = MutableStateFlow(EventDetailUiState())
    val state: StateFlow<EventDetailUiState> = _state.asStateFlow()

    fun loadEvent(eventId: String, force: Boolean = false) {
        val current = _state.value
        if (current.isLoading || (!force && current.event?.id == eventId)) return

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
                    selectedQuantities = state.selectedQuantities + (ticketType.id to currentQuantity + 1),
                    purchaseErrorMessage = null
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
                state.copy(
                    selectedQuantities = nextQuantities,
                    purchaseErrorMessage = null
                )
            }
        }
    }

    fun createPurchase() {
        val current = _state.value
        if (current.isCreatingPurchase || current.isPaying || !current.hasSelection) return

        val items = current.selectedQuantities
            .filter { it.value > 0 }
            .map { (ticketTypeId, quantity) ->
                CreatePurchaseItem(ticketTypeId = ticketTypeId, quantity = quantity)
            }

        if (items.isEmpty()) return

        _state.update {
            it.copy(
                isCreatingPurchase = true,
                purchaseErrorMessage = null,
                purchase = null,
                showPaymentDialog = false,
                paymentCompleted = false
            )
        }

        viewModelScope.launch {
            purchaseRepository.createPurchase(items)
                .onSuccess { purchase ->
                    _state.update {
                        it.copy(
                            isCreatingPurchase = false,
                            purchase = purchase,
                            showPaymentDialog = true,
                            purchaseErrorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    val eventId = _state.value.event?.id
                    _state.update {
                        it.copy(
                            isCreatingPurchase = false,
                            purchaseErrorMessage = error.toPurchaseUserMessage()
                        )
                    }
                    if (eventId != null) {
                        loadEvent(eventId, force = true)
                    }
                }
        }
    }

    fun dismissPaymentDialog() {
        if (_state.value.isPaying) return
        _state.update { it.copy(showPaymentDialog = false) }
    }

    fun payPurchase() {
        val purchaseId = _state.value.purchase?.id ?: return
        if (_state.value.isPaying) return

        _state.update {
            it.copy(
                isPaying = true,
                purchaseErrorMessage = null,
                paymentCompleted = false
            )
        }

        viewModelScope.launch {
            purchaseRepository.pay(purchaseId)
                .onSuccess { purchase ->
                    _state.update {
                        it.copy(
                            isPaying = false,
                            purchase = purchase,
                            showPaymentDialog = false,
                            paymentCompleted = true,
                            purchaseErrorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isPaying = false,
                            purchaseErrorMessage = error.toPurchaseUserMessage()
                        )
                    }
                }
        }
    }
}
