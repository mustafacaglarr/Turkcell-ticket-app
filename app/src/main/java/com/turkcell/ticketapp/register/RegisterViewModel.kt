package com.turkcell.ticketapp.register

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.AuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun onEmailChange(value: String) {
        email = value
    }

    fun onPasswordChange(value: String) {
        password = value
    }

    fun register(onSuccess: () -> Unit) {
        if (isLoading) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            authRepository.register(email, password)
                .onSuccess {
                    Log.d("RegisterScreen", "Kayit yapildi")
                    onSuccess()
                }
                .onFailure { error ->
                    errorMessage = error.message ?: "Kayit basarisiz oldu"
                }

            isLoading = false
        }
    }
}
