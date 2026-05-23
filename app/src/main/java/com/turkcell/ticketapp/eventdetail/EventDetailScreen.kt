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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.turkcell.core.domain.event.Event
import com.turkcell.core.domain.event.TicketType
import com.turkcell.core.util.DateFormatter
import org.koin.androidx.compose.koinViewModel

@Composable
fun EventDetailScreen(
    eventId: String,
    viewModel: EventDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    when {
        state.isLoading -> LoadingContent()
        state.errorMessage != null -> ErrorContent(message = state.errorMessage.orEmpty())
        state.event != null -> EventContent(
            event = state.event,
            selectedQuantities = state.selectedQuantities,
            totalCents = state.totalCents,
            onIncrease = viewModel::increase,
            onDecrease = viewModel::decrease
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
    onIncrease: (TicketType) -> Unit,
    onDecrease: (String) -> Unit
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
                text = "Bilet Türleri",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (event.ticketTypes.isEmpty()) {
            item {
                Text(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = "Bu etkinlik için bilet türü bulunamadı.",
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
                    text = "Toplam: ${totalCents / 100} TL",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Satın Al")
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
                        text = "${ticketType.remaining} / ${ticketType.capacity} kalan",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${ticketType.priceCents / 100} TL",
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
