package com.iqbalfauzi.kitchenstock.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signInWithEmail(email: String, password: String)
    suspend fun signUpWithEmail(email: String, password: String)
    suspend fun resetPasswordForEmail(email: String)
    suspend fun signInWithGoogle()
    suspend fun signInWithApple()
    suspend fun signInAnonymously()
    suspend fun signOut()
    fun isUserLoggedIn(): Boolean
    val isUserLoggedInFlow: Flow<Boolean>
}

