package com.iqbalfauzi.kitchenstockmobile.di

import com.iqbalfauzi.kitchenstockmobile.BuildKonfig
import com.iqbalfauzi.kitchenstockmobile.data.repository.AuthRepositoryImpl
import com.iqbalfauzi.kitchenstockmobile.data.repository.InventoryRepositoryImpl
import com.iqbalfauzi.kitchenstockmobile.db.DbDriverFactory
import com.iqbalfauzi.kitchenstockmobile.db.KitchenDatabase
import com.iqbalfauzi.kitchenstockmobile.domain.repository.AuthRepository
import com.iqbalfauzi.kitchenstockmobile.domain.repository.InventoryRepository
import com.iqbalfauzi.kitchenstockmobile.domain.repository.ShoppingRepository
import com.iqbalfauzi.kitchenstockmobile.data.repository.ShoppingRepositoryImpl
import com.russhwolf.settings.Settings
import io.github.aakira.napier.Napier
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SettingsSessionManager
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.logging.LogLevel
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import org.koin.dsl.module

val dataModule = module {
    single<Settings> { Settings() }

    single {
        val driver = get<DbDriverFactory>().createDriver()
        KitchenDatabase(driver)
    }

    @OptIn(SupabaseInternal::class)
    single {
        createSupabaseClient(
            supabaseUrl = BuildKonfig.SUPABASE_URL,
            supabaseKey = BuildKonfig.SUPABASE_ANON_KEY
        ) {
            install(Postgrest)
            install(Auth) {
                sessionManager = SettingsSessionManager(get())
            }
            install(Realtime)

            defaultLogLevel = LogLevel.DEBUG

            httpConfig {
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            Napier.d(tag = "SupabaseNetwork") { message }
                        }
                    }
                    level = io.ktor.client.plugins.logging.LogLevel.ALL
                }
            }
        }
    }

    single<InventoryRepository> {
        InventoryRepositoryImpl(get(), get())
    }

    single<AuthRepository> {
        AuthRepositoryImpl(get())
    }

    single<ShoppingRepository> {
        ShoppingRepositoryImpl(get(), get())
    }
}

