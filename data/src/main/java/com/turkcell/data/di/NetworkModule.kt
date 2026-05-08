package com.turkcell.data.di

import com.turkcell.data.remote.AuthApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import org.koin.dsl.module

private const val BASE_URL = "https://tickets-api.halitkalayci.com/"

val networkModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
        }
    }

    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .build()
    }

    single<AuthApi> {
        get<Retrofit>().create(AuthApi::class.java)
    }
}
