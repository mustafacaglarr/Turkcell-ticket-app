package com.turkcell.ticketapp.ticketdetail

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Color
import android.view.WindowManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.turkcell.core.domain.ticket.UserTicket
import com.turkcell.core.util.DateFormatter
import org.koin.androidx.compose.koinViewModel

@Composable
fun TicketDetailScreen(
    ticketId: String,
    viewModel: TicketDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    MaxBrightnessEffect()

    LaunchedEffect(ticketId) {
        viewModel.loadTicket(ticketId)
    }

    when {
        state.isLoading -> LoadingContent()
        state.errorMessage != null -> ErrorContent(message = state.errorMessage.orEmpty())
        state.ticket != null -> TicketDetailContent(ticket = state.ticket)
    }
}

@Composable
private fun TicketDetailContent(ticket: UserTicket?) {
    if (ticket == null) return

    val qrBitmap = remember(ticket.qrCode) {
        createQrBitmap(content = ticket.qrCode)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Bilet Detayı",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = ticket.eventName ?: "Bilet",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                ticket.ticketTypeName?.let {
                    Text(text = "Bilet türü: $it", style = MaterialTheme.typography.bodyMedium)
                }

                ticket.eventVenue?.let {
                    Text(text = "Mekan: $it", style = MaterialTheme.typography.bodyMedium)
                }

                ticket.eventStartsAt?.let {
                    Text(
                        text = "Tarih: ${DateFormatter.format(it)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Text(text = "Durum: ${ticket.status}", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (ticket.qrCode.isNotBlank()) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "Bilet QR kodu",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = ticket.qrCode,
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    Text(
                        text = "Bu bilet için QR kod bulunamadı.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
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
private fun MaxBrightnessEffect() {
    val context = LocalContext.current

    DisposableEffect(context) {
        val window = context.findActivity()?.window
        val originalBrightness = window?.attributes?.screenBrightness
            ?: WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE

        if (window != null) {
            val attributes = window.attributes
            attributes.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
            window.attributes = attributes
        }

        onDispose {
            if (window != null) {
                val attributes = window.attributes
                attributes.screenBrightness = originalBrightness
                window.attributes = attributes
            }
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun createQrBitmap(
    content: String,
    size: Int = 900
): Bitmap {
    val matrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size)
    val pixels = IntArray(size * size)

    for (y in 0 until size) {
        for (x in 0 until size) {
            pixels[y * size + x] = if (matrix[x, y]) Color.BLACK else Color.WHITE
        }
    }

    return Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).apply {
        setPixels(pixels, 0, size, 0, 0, size, size)
    }
}
