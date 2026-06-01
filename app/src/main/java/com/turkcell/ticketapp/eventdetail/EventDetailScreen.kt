package com.turkcell.ticketapp.eventdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.turkcell.core.domain.event.Event
import com.turkcell.core.domain.event.TicketType
import com.turkcell.core.util.DateFormatter
import com.turkcell.ticketapp.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun EventDetailScreen(
    eventId: String,
    onPaymentSuccess: () -> Unit,
    viewModel: EventDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    LaunchedEffect(state.paymentCompleted) {
        if (state.paymentCompleted) {
            onPaymentSuccess()
        }
    }

    if (state.showPaymentDialog) {
        PaymentConfirmDialog(
            totalCents = state.purchase?.totalCents ?: state.totalCents,
            isPaying = state.isPaying,
            onDismiss = viewModel::dismissPaymentDialog,
            onConfirm = viewModel::payPurchase
        )
    }

    when {
        state.isLoading -> LoadingContent()
        state.errorMessage != null -> ErrorContent(message = state.errorMessage.orEmpty())
        state.event != null -> EventContent(
            event = state.event,
            selectedQuantities = state.selectedQuantities,
            totalCents = state.totalCents,
            purchaseErrorMessage = state.purchaseErrorMessage,
            isCreatingPurchase = state.isCreatingPurchase,
            isPaying = state.isPaying,
            canPurchase = state.hasSelection,
            onIncrease = viewModel::increase,
            onDecrease = viewModel::decrease,
            onCreatePurchase = viewModel::createPurchase
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun EventContent(
    event: Event?,
    selectedQuantities: Map<String, Int>,
    totalCents: Int,
    purchaseErrorMessage: String?,
    isCreatingPurchase: Boolean,
    isPaying: Boolean,
    canPurchase: Boolean,
    onIncrease: (TicketType) -> Unit,
    onDecrease: (String) -> Unit,
    onCreatePurchase: () -> Unit
) {
    if (event == null) return

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = event.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold
                )

                if (event.venue.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = event.venue, style = MaterialTheme.typography.bodyLarge)
                }

                if (event.startsAt.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = DateFormatter.format(event.startsAt),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (event.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = event.description, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        item {
            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = stringResource(R.string.ticket_types_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (event.ticketTypes.isEmpty()) {
            item {
                Text(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = stringResource(R.string.empty_ticket_types),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        items(event.ticketTypes, key = { it.id }) { ticketType ->
            TicketTypeRow(
                ticketType = ticketType,
                quantity = selectedQuantities[ticketType.id] ?: 0,
                onIncrease = { onIncrease(ticketType) },
                onDecrease = { onDecrease(ticketType.id) }
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = stringResource(R.string.total_price, formatPrice(totalCents)),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                if (purchaseErrorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = purchaseErrorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onCreatePurchase,
                    enabled = canPurchase && !isCreatingPurchase && !isPaying,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isCreatingPurchase) {
                        CircularProgressIndicator()
                    } else {
                        Text(stringResource(R.string.buy))
                    }
                }
            }
        }
    }
}

@Composable
private fun TicketTypeRow(
    ticketType: TicketType,
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    val maxQuantity = minOf(20, ticketType.remaining)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ticketType.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(
                            R.string.remaining_capacity,
                            ticketType.remaining,
                            ticketType.capacity
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = formatPrice(ticketType.priceCents),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = onDecrease,
                        enabled = quantity > 0
                    ) {
                        Text("-")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    OutlinedButton(
                        onClick = onIncrease,
                        enabled = quantity < maxQuantity
                    ) {
                        Text("+")
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentConfirmDialog(
    totalCents: Int,
    isPaying: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.payment_confirm_title))
        },
        text = {
            Text(stringResource(R.string.payment_confirm_message, formatPrice(totalCents)))
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !isPaying
            ) {
                Text(
                    if (isPaying) {
                        stringResource(R.string.paying)
                    } else {
                        stringResource(R.string.pay)
                    }
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isPaying
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

private fun formatPrice(priceCents: Int): String =
    "${priceCents / 100} TL"
