package com.turkcell.ticketapp.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.turkcell.core.domain.Event
import com.turkcell.core.domain.TicketType
import com.turkcell.core.domain.UserTicket
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel()
) {
    val state = viewModel.uiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Ana Sayfa",
                style = MaterialTheme.typography.headlineMedium
            )

            OutlinedButton(
                onClick = viewModel::loadHome,
                enabled = !state.isLoading
            ) {
                Text("Yenile")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    SectionTitle(text = "Etkinlikler")
                }

                state.eventErrorMessage?.let { message ->
                    item {
                        ErrorText(message = message)
                    }
                }

                if (state.events.isEmpty() && state.eventErrorMessage == null) {
                    item {
                        EmptyText(text = "Gösterilecek etkinlik bulunamadı.")
                    }
                }

                items(state.events, key = { it.id }) { event ->
                    EventCard(event = event)
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    SectionTitle(text = "Biletlerim")
                }

                state.ticketErrorMessage?.let { message ->
                    item {
                        ErrorText(message = message)
                    }
                }

                if (state.tickets.isEmpty() && state.ticketErrorMessage == null) {
                    item {
                        EmptyText(text = "Henüz biletin yok.")
                    }
                }

                items(state.tickets, key = { it.id }) { ticket ->
                    TicketCard(ticket = ticket)
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun EventCard(event: Event) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = event.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (event.venue.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = event.venue, style = MaterialTheme.typography.bodyMedium)
            }

            if (event.startsAt.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = event.startsAt, style = MaterialTheme.typography.bodySmall)
            }

            if (event.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = event.description, style = MaterialTheme.typography.bodyMedium)
            }

            if (event.ticketTypes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                event.ticketTypes.forEach { ticketType ->
                    TicketTypeRow(ticketType = ticketType)
                }
            }
        }
    }
}

@Composable
private fun TicketTypeRow(ticketType: TicketType) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = ticketType.name, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = "${ticketType.remaining} kalan - ${ticketType.priceCents / 100} TL",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun TicketCard(ticket: UserTicket) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = ticket.eventName ?: "Bilet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            ticket.ticketTypeName?.let { ticketTypeName ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = ticketTypeName, style = MaterialTheme.typography.bodyMedium)
            }

            ticket.eventVenue?.let { venue ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = venue, style = MaterialTheme.typography.bodyMedium)
            }

            ticket.eventStartsAt?.let { startsAt ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = startsAt, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Durum: ${ticket.status}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun EmptyText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun ErrorText(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyMedium
    )
}
