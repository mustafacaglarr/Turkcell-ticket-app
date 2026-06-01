package com.turkcell.ticketapp.common

import com.turkcell.data.network.ApiException
import com.turkcell.data.network.NetworkException
import com.turkcell.ticketapp.R

internal fun Throwable.toLoginUserMessage(strings: StringProvider): String = when (this) {
    is ApiException -> when (code) {
        400, 401 -> strings.get(R.string.error_invalid_credentials)
        in 500..599 -> strings.get(R.string.error_server_unavailable)
        else -> strings.get(R.string.error_unexpected)
    }

    is NetworkException -> strings.get(R.string.error_no_connection)
    else -> message ?: strings.get(R.string.error_unknown)
}

internal fun Throwable.toRegisterUserMessage(strings: StringProvider): String = when (this) {
    is ApiException -> when (code) {
        400 -> strings.get(R.string.error_register_invalid)
        409 -> strings.get(R.string.error_email_taken)
        in 500..599 -> strings.get(R.string.error_server_unavailable)
        else -> strings.get(R.string.error_register_unexpected)
    }

    is NetworkException -> strings.get(R.string.error_no_connection)
    else -> message ?: strings.get(R.string.error_unknown)
}

internal fun Throwable.toPurchaseUserMessage(strings: StringProvider): String = when (this) {
    is ApiException -> {
        val apiMessage = "${errorMessage.orEmpty()} ${message.orEmpty()}".lowercase()
        when {
            apiMessage.contains("capacity_exceeded") -> strings.get(R.string.error_capacity_exceeded)
            apiMessage.contains("already_paid") -> strings.get(R.string.error_already_paid)
            apiMessage.contains("not_purchase_owner") || code == 403 -> strings.get(R.string.error_not_purchase_owner)
            code == 404 -> strings.get(R.string.error_purchase_not_found)
            code == 409 -> strings.get(R.string.error_purchase_failed)
            code in 500..599 -> strings.get(R.string.error_server_unavailable)
            else -> strings.get(R.string.error_purchase_unexpected)
        }
    }

    is NetworkException -> strings.get(R.string.error_no_connection)
    else -> message ?: strings.get(R.string.error_unknown)
}

internal fun Throwable.toTicketUserMessage(strings: StringProvider): String = when (this) {
    is ApiException -> when (code) {
        403 -> strings.get(R.string.error_ticket_forbidden)
        404 -> strings.get(R.string.error_ticket_not_found)
        in 500..599 -> strings.get(R.string.error_server_unavailable)
        else -> strings.get(R.string.error_ticket_unexpected)
    }

    is NetworkException -> strings.get(R.string.error_no_connection)
    else -> message ?: strings.get(R.string.error_unknown)
}
