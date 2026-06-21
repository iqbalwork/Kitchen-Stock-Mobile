package com.iqbalfauzi.kitchenstockmobile.data.repository

import com.iqbalfauzi.kitchenstockmobile.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth

class AuthRepositoryImpl(
    private val supabase: SupabaseClient
) : AuthRepository {

    override suspend fun signInAnonymously() {
        supabase.auth.signInAnonymously()
    }

    override suspend fun signOut() {
        supabase.auth.signOut()
    }

    override fun isUserLoggedIn(): Boolean {
        return supabase.auth.currentUserOrNull() != null
    }
}
