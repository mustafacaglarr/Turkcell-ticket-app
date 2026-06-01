package com.turkcell.ticketapp.di

import com.turkcell.ticketapp.eventdetail.EventDetailViewModel
import com.turkcell.ticketapp.home.HomeViewModel
import com.turkcell.ticketapp.login.LoginViewModel
import com.turkcell.ticketapp.mytickets.MyTicketsViewModel
import com.turkcell.ticketapp.register.RegisterViewModel
import com.turkcell.ticketapp.ticketdetail.TicketDetailViewModel
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
        HomeViewModel(
            eventRepository = get(),
            ticketRepository = get()
        )
    }

    viewModel {
        EventDetailViewModel(
            eventRepository = get(),
            purchaseRepository = get()
        )
    }

    viewModel {
        MyTicketsViewModel(ticketRepository = get())
    }

    viewModel {
        TicketDetailViewModel(ticketRepository = get())
    }
}
