package com.turkcell.data.di

import com.turkcell.core.domain.AuthRepository
import com.turkcell.core.domain.EventRepository
import com.turkcell.core.domain.TicketRepository
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
