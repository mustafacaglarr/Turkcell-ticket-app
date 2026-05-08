package com.turkcell.data.di

import com.turkcell.core.domain.AuthRepository
import com.turkcell.data.repository.AuthRepositoryImpl
import org.koin.dsl.module

val dataModule = module {
    single<AuthRepository> {
        AuthRepositoryImpl(authApi = get())
    }
}
