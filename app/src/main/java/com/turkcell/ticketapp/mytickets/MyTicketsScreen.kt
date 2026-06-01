package com.turkcell.ticketapp.mytickets

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.turkcell.core.domain.ticket.UserTicket
import com.turkcell.core.util.DateFormatter
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTicketsScreen(
    onTicketClick: (String) -> Unit,
    viewModel: MyTicketsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Biletlerim",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold
            )

            OutlinedButton(
                onClick = viewModel::refresh,
                enabled = !state.isLoading && !state.isRefreshing
            ) {
                Text("Yenile")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                state.isLoading -> LoadingContent()
                state.errorMessage != null -> MessageContent(
                    message = state.errorMessage.orEmpty(),
                    isError = true
                )

                state.tickets.isEmpty() -> MessageContent(
                    message = "Henüz biletin yok.",
                    isError = false
                )

                else -> TicketList(
                    tickets = state.tickets,
                    onTicketClick = onTicketClick
                )
            }
        }
    }
}

@Composable
private fun TicketList(
    tickets: List<UserTicket>,
    onTicketClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp)
    ) {
        items(tickets, key = { it.id }) { ticket ->
            TicketCard(
                ticket = ticket,
                onClick = { onTicketClick(ticket.id) }
            )
        }
    }
}

@Composable
private fun TicketCard(
    ticket: UserTicket,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = ticket.eventName ?: "Bilet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            ticket.eventStartsAt?.let { startsAt ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = DateFormatter.format(startsAt),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            ticket.ticketTypeName?.let { ticketTypeName ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = ticketTypeName, style = MaterialTheme.typography.bodyMedium)
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
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun MessageContent(
    message: String,
    isError: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
