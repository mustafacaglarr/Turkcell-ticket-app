package com.turkcell.ticketapp.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.turkcell.core.domain.event.Event
import com.turkcell.core.domain.event.TicketType
import com.turkcell.core.util.DateFormatter
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    onEventClick: (String) -> Unit,
    onMyTicketsClick: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Ana Sayfa",
                style = MaterialTheme.typography.headlineMedium
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onMyTicketsClick) {
                    Text("Biletlerim")
                }

                OutlinedButton(
                    onClick = viewModel::loadEvents,
                    enabled = !state.isEventsLoading
                ) {
                    Text("Yenile")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                SectionHeader(text = "Etkinlikler")
                Spacer(modifier = Modifier.height(8.dp))
                EventsRow(
                    isLoading = state.isEventsLoading,
                    error = state.eventsError,
                    events = state.events,
                    onEventClick = onEventClick
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        modifier = Modifier.padding(horizontal = 24.dp),
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun EventsRow(
    isLoading: Boolean,
    error: String?,
    events: List<Event>,
    onEventClick: (String) -> Unit
) {
    when {
        isLoading -> LoadingBox(height = 220)
        error != null -> ErrorText(message = error)
        events.isEmpty() -> EmptyText(text = "Şimdilik hiçbir etkinlik yok.")
        else -> {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) {
                items(items = events, key = { it.id }) { event ->
                    EventCard(
                        event = event,
                        onClick = { onEventClick(event.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EventCard(
    event: Event,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .height(300.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = event.name.take(1).uppercase().ifBlank { "?" },
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Text(
                    text = event.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2
                )

                if (event.venue.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = event.venue, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                }

                if (event.startsAt.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = DateFormatter.format(event.startsAt), style = MaterialTheme.typography.bodySmall, maxLines = 1)
                }

                if (event.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = event.description, style = MaterialTheme.typography.bodySmall, maxLines = 3)
                }

                if (event.ticketTypes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TicketTypeSummary(ticketType = event.ticketTypes.first())
                }
            }
        }
    }
}

@Composable
private fun TicketTypeSummary(ticketType: TicketType) {
    Text(
        text = "${ticketType.name}: ${ticketType.remaining} kalan - ${ticketType.priceCents / 100} TL",
        style = MaterialTheme.typography.bodySmall,
        maxLines = 1
    )
}

@Composable
private fun LoadingBox(height: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyText(text: String) {
    Text(
        modifier = Modifier.padding(horizontal = 24.dp),
        text = text,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun ErrorText(message: String) {
    Text(
        modifier = Modifier.padding(horizontal = 24.dp),
        text = message,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyMedium
    )
}
