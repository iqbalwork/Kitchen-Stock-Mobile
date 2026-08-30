package com.iqbalfauzi.kitchenstock.data.repository

import com.iqbalfauzi.kitchenstock.domain.repository.AuthRepository
import io.github.aakira.napier.Napier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Apple
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val supabase: SupabaseClient
) : AuthRepository {

    override val isUserLoggedInFlow: Flow<Boolean> = supabase.auth.sessionStatus.map { it is SessionStatus.Authenticated }

    override suspend fun signInWithEmail(email: String, password: String) {
        Napier.d("signInWithEmail: $email")
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String) {
        Napier.d("signUpWithEmail: $email")
        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun resetPasswordForEmail(email: String) {
        Napier.d("resetPasswordForEmail: $email")
        supabase.auth.resetPasswordForEmail(email)
    }

    override suspend fun signInWithGoogle() {
        Napier.d("signInWithGoogle")
        supabase.auth.signInWith(Google)
    }

    override suspend fun signInWithApple() {
        Napier.d("signInWithApple")
        supabase.auth.signInWith(Apple)
    }

    override suspend fun signInAnonymously() {
        Napier.d("signInAnonymously")
        supabase.auth.signInAnonymously()
    }

    override suspend fun signOut() {
        try {
            Napier.d("signOut starting")
            supabase.auth.signOut()
            Napier.d("signOut completed successfully")
        } catch (e: Exception) {
            Napier.e("signOut failed: ${e.message}", e)
        }
    }

    override fun isUserLoggedIn(): Boolean {
        val loggedIn = supabase.auth.currentUserOrNull() != null
        Napier.d("isUserLoggedIn: $loggedIn")
        return loggedIn
    }
}

