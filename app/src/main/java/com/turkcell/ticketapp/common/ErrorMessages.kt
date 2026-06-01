package com.turkcell.ticketapp.common

import com.turkcell.data.network.ApiException
import com.turkcell.data.network.NetworkException

internal fun Throwable.toLoginUserMessage(): String = when (this) {
    is ApiException -> when (code) {
        400, 401 -> "Email veya şifre hatalı"
        in 500..599 -> "Sunucu şu anda cevap veremiyor"
        else -> "Beklenmeyen bir hata oluştu"
    }

    is NetworkException -> "İnternet bağlantısı yok"
    else -> message ?: "Bilinmeyen bir hata oluştu."
}

internal fun Throwable.toRegisterUserMessage(): String = when (this) {
    is ApiException -> when (code) {
        400 -> "Kayıt bilgilerini kontrol et"
        409 -> "Bu email adresi zaten kayıtlı"
        in 500..599 -> "Sunucu şu anda cevap veremiyor"
        else -> "Kayıt sırasında beklenmeyen bir hata oluştu"
    }

    is NetworkException -> "İnternet bağlantısı yok"
    else -> message ?: "Bilinmeyen bir hata oluştu."
}

internal fun Throwable.toPurchaseUserMessage(): String = when (this) {
    is ApiException -> {
        val apiMessage = "${errorMessage.orEmpty()} ${message.orEmpty()}".lowercase()
        when {
            apiMessage.contains("capacity_exceeded") -> "Stok yetersiz, yenile"
            apiMessage.contains("already_paid") -> "Bu satın alım zaten ödenmiş"
            apiMessage.contains("not_purchase_owner") || code == 403 -> "Bu satın alım sana ait değil"
            code == 404 -> "Satın alım bulunamadı"
            code == 409 -> "Satın alma işlemi tamamlanamadı"
            code in 500..599 -> "Sunucu şu anda cevap veremiyor"
            else -> "Satın alma sırasında beklenmeyen bir hata oluştu"
        }
    }

    is NetworkException -> "İnternet bağlantısı yok"
    else -> message ?: "Bilinmeyen bir hata oluştu."
}

internal fun Throwable.toTicketUserMessage(): String = when (this) {
    is ApiException -> when (code) {
        403 -> "Bu bilete erişim yetkin yok"
        404 -> "Bilet bulunamadı"
        in 500..599 -> "Sunucu şu anda cevap veremiyor"
        else -> "Biletler yüklenirken beklenmeyen bir hata oluştu"
    }

    is NetworkException -> "İnternet bağlantısı yok"
    else -> message ?: "Bilinmeyen bir hata oluştu."
}
