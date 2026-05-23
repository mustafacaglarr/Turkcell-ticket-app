package com.turkcell.data.di

import com.turkcell.core.domain.auth.AuthRepository
import com.turkcell.core.domain.event.EventRepository
import com.turkcell.core.domain.ticket.TicketRepository
import com.turkcell.data.local.TokenStore
import com.turkcell.data.repository.AuthRepositoryImpl
import com.turkcell.data.repository.EventRepositoryImpl
import com.turkcell.data.repository.TicketRepositoryImpl
import org.koin.dsl.module

val dataModule = module {
    single {
        TokenStore(context = get())
    }

    single<AuthRepository> {
        AuthRepositoryImpl(
            authApi = get(),
            tokenStore = get()
        )
    }

    single<EventRepository> {
        EventRepositoryImpl(eventApi = get())
    }

    single<TicketRepository> {
        TicketRepositoryImpl(ticketApi = get())
    }
}
