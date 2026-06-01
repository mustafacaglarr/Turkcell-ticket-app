package com.turkcell.ticketapp.di

import com.turkcell.ticketapp.eventdetail.EventDetailViewModel
import com.turkcell.ticketapp.common.StringProvider
import com.turkcell.ticketapp.home.HomeViewModel
import com.turkcell.ticketapp.login.LoginViewModel
import com.turkcell.ticketapp.mytickets.MyTicketsViewModel
import com.turkcell.ticketapp.register.RegisterViewModel
import com.turkcell.ticketapp.ticketdetail.TicketDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        StringProvider(context = get())
    }

    viewModel {
        LoginViewModel(
            authRepository = get(),
            strings = get()
        )
    }

    viewModel {
        RegisterViewModel(
            authRepository = get(),
            strings = get()
        )
    }

    viewModel {
        HomeViewModel(
            eventRepository = get(),
            authRepository = get(),
            strings = get()
        )
    }

    viewModel {
        EventDetailViewModel(
            eventRepository = get(),
            purchaseRepository = get(),
            strings = get()
        )
    }

    viewModel {
        MyTicketsViewModel(
            ticketRepository = get(),
            strings = get()
        )
    }

    viewModel {
        TicketDetailViewModel(
            ticketRepository = get(),
            strings = get()
        )
    }
}
