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
import io.ktor.http.*
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

    // JSON común con configuración muy permisiva para debugging
    single {
        Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            allowSpecialFloatingPointValues = true
            useArrayPolymorphism = true
            coerceInputValues = true
            encodeDefaults = true
            explicitNulls = false // permitir valores nulos
            classDiscriminator = "type" // para polimorfismo
        }
    }

    // Network
    single {
        val jsonConfig = get<Json>()
        HttpClient(CIO) {
            // ContentNegotiation para parsear json
            install(ContentNegotiation) {
                json(jsonConfig)
            }

            // Logging para debug
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
                sanitizeHeader { header -> header == HttpHeaders.Authorization }
            }

            // Timeouts generosos para debug
            install(HttpTimeout) {
                requestTimeoutMillis = 60000 // 1 minuto
                connectTimeoutMillis = 30000 // 30 segundos
                socketTimeoutMillis = 60000  // 1 minuto
            }

            // Configuración para manejar redirecciones
            install(HttpRedirect) {
                checkHttpMethod = false
            }

            // Reintentar peticiones en caso de error del servidor
            install(HttpRequestRetry) {
                retryOnExceptionIf(3) { request, cause ->
                    println("Excepción al realizar petición: ${cause.message}")
                    true // Reintentar para cualquier excepción
                }
                retryOnServerErrors(maxRetries = 3)
                exponentialDelay()
            }

            // Comportamiento por defecto para solicitudes
            defaultRequest {
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
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