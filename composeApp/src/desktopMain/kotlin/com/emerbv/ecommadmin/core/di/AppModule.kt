package com.emerbv.ecommadmin.core.di

import com.emerbv.ecommadmin.features.auth.data.repository.AuthRepository
import com.emerbv.ecommadmin.features.auth.data.repository.AuthRepositoryImpl
import com.emerbv.ecommadmin.features.auth.domain.LoginUseCase
import com.emerbv.ecommadmin.features.auth.presentation.LoginViewModel
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    // CoroutineScope
    single { CoroutineScope(SupervisorJob() + Dispatchers.Main) }

    // Network
    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.HEADERS
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 30000
                connectTimeoutMillis = 15000
                socketTimeoutMillis = 30000
            }
        }
    }

    // Configuration
    single { "http://localhost:9091/ecommdb/api/v1" as String }

    // Repositories
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }

    // Use Cases
    factoryOf(::LoginUseCase)

    // ViewModels
    factoryOf(::LoginViewModel)
}