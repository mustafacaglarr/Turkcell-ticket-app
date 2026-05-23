package com.turkcell.data.repository

import com.turkcell.core.domain.auth.AuthRepository
import com.turkcell.core.domain.auth.AuthSession
import com.turkcell.core.domain.auth.User
import com.turkcell.core.domain.auth.UserRole
import com.turkcell.data.dto.CredentialsDto
import com.turkcell.data.dto.TokenPairDto
import com.turkcell.data.local.TokenStore
import com.turkcell.data.remote.AuthApi
import com.turkcell.data.util.runCatchingApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val tokenStore: TokenStore
) : AuthRepository {
    override val isLoggedIn: Flow<Boolean> = tokenStore.accessToken.map { !it.isNullOrBlank() }

    override suspend fun login(
        email: String,
        password: String
    ): Result<AuthSession> = runCatchingApi {
        authApi.login(CredentialsDto(email = email, password = password))
    }.onSuccess {
        tokenStore.save(it.accessToken, it.refreshToken)
    }
        .map {
            tokenPair -> tokenPair.toAuthSession()
        }


    override suspend fun register(
        email: String,
        password: String
    ): Result<AuthSession> = runCatchingApi {
        authApi.register(CredentialsDto(email = email, password = password))
    }.map { tokenPair -> tokenPair.toAuthSession() }

    override suspend fun logout(): Result<Unit> = runCatching {
        tokenStore.clear()
    }

    private fun TokenPairDto.toAuthSession(): AuthSession =
        AuthSession(
            user = User(
                user.id,
                user.email,
                UserRole.fromApi(user.role)
            ),
            accessToken = accessToken,
            refreshToken = refreshToken
        )
}
