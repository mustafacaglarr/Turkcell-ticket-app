package com.turkcell.data.di

import com.turkcell.core.domain.AuthRepository
import com.turkcell.core.domain.HomeRepository
import com.turkcell.data.local.TokenStore
import com.turkcell.data.repository.AuthRepositoryImpl
import com.turkcell.data.repository.HomeRepositoryImpl
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

    single<HomeRepository> {
        HomeRepositoryImpl(homeApi = get())
    }
}
