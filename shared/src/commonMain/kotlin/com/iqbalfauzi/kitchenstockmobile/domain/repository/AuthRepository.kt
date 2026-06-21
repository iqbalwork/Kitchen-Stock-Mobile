package com.iqbalfauzi.kitchenstockmobile.domain.repository

interface AuthRepository {
    suspend fun signInAnonymously()
    suspend fun signOut()
    fun isUserLoggedIn(): Boolean
}
