package com.iqbalfauzi.kitchenstockmobile.di

import com.iqbalfauzi.kitchenstockmobile.BuildKonfig
import com.iqbalfauzi.kitchenstockmobile.data.repository.AuthRepositoryImpl
import com.iqbalfauzi.kitchenstockmobile.data.repository.InventoryRepositoryImpl
import com.iqbalfauzi.kitchenstockmobile.db.DbDriverFactory
import com.iqbalfauzi.kitchenstockmobile.db.KitchenDatabase
import com.iqbalfauzi.kitchenstockmobile.domain.repository.AuthRepository
import com.iqbalfauzi.kitchenstockmobile.domain.repository.InventoryRepository
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.MemorySessionManager
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import org.koin.dsl.module

val dataModule = module {
    single {
        val driver = get<DbDriverFactory>().createDriver()
        KitchenDatabase(driver)
    }

    single {
        createSupabaseClient(
            supabaseUrl = BuildKonfig.SUPABASE_URL,
            supabaseKey = BuildKonfig.SUPABASE_ANON_KEY
        ) {
            install(Postgrest)
            install(Auth) {
                sessionManager = MemorySessionManager()
            }
            install(Realtime)
        }
    }

    single<InventoryRepository> {
        InventoryRepositoryImpl(get(), get())
    }

    single<AuthRepository> {
        AuthRepositoryImpl(get())
    }
}
