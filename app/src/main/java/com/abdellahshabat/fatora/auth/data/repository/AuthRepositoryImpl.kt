package com.abdellahshabat.fatora.auth.data.repository

import com.abdellahshabat.fatora.auth.data.datasource.FirebaseAuthDataSource
import com.abdellahshabat.fatora.auth.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val dataSource: FirebaseAuthDataSource
) : AuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): Result<Unit> {
        return dataSource.login(email, password)
    }

    override suspend fun register(
        email: String,
        password: String
    ): Result<Unit> {
        return dataSource.register(email, password)
    }

    override fun logout() {
        dataSource.logout()
    }

    override fun isUserLoggedIn(): Boolean {
        return dataSource.isUserLoggedIn()
    }

    override fun getCurrentUserId(): String? {
        return dataSource.getCurrentUserId()
    }
}