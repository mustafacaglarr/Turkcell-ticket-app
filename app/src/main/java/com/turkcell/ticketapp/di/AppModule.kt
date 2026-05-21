package com.turkcell.ticketapp.di

import com.turkcell.ticketapp.home.HomeViewModel
import com.turkcell.ticketapp.login.LoginViewModel
import com.turkcell.ticketapp.register.RegisterViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel {
        LoginViewModel(authRepository = get())
    }

    viewModel {
        RegisterViewModel(authRepository = get())
    }

    viewModel {
        HomeViewModel(homeRepository = get())
    }
}
