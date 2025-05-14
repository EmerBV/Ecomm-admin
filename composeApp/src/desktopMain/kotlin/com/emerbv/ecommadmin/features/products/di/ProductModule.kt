package com.emerbv.ecommadmin.features.products.di

import com.emerbv.ecommadmin.core.utils.TokenManager
import com.emerbv.ecommadmin.features.products.data.repository.ProductRepository
import com.emerbv.ecommadmin.features.products.data.repository.ProductRepositoryImpl
import com.emerbv.ecommadmin.features.products.domain.*
import com.emerbv.ecommadmin.features.products.presentation.ProductListViewModel
import com.emerbv.ecommadmin.features.products.presentation.ProductFormViewModel
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val productModule = module {
    // JSON personalizado para ser más tolerante con las respuestas
    single(qualifier = org.koin.core.qualifier.named("productJson")) {
        Json {
            isLenient = true
            ignoreUnknownKeys = true
            prettyPrint = true
            coerceInputValues = true
            encodeDefaults = true
            explicitNulls = false
        }
    }

    // Cliente HTTP específico para productos
    single(qualifier = org.koin.core.qualifier.named("productClient")) {
        val baseClient = get<HttpClient>()
        baseClient.config {
            install(ContentNegotiation) {
                json(get(qualifier = org.koin.core.qualifier.named("productJson")))
            }
        }
    }

    // Repositorios
    single<ProductRepository> {
        ProductRepositoryImpl(
            httpClient = get(qualifier = org.koin.core.qualifier.named("productClient")),
            baseUrl = get(),
            tokenProvider = { get<TokenManager>().getToken() }
        )
    }

    // Casos de uso
    factoryOf(::GetAllProductsUseCase)
    factoryOf(::GetProductByIdUseCase)
    factoryOf(::GetProductsByCategoryUseCase)
    factoryOf(::GetProductsByStatusUseCase)
    factoryOf(::GetBestSellerProductsUseCase)
    factoryOf(::GetMostWishedProductsUseCase)
    factoryOf(::GetRecentProductsUseCase)
    factoryOf(::AddProductUseCase)
    factoryOf(::UpdateProductUseCase)

    // ViewModels
    factoryOf(::ProductListViewModel)
    factoryOf(::ProductFormViewModel)
}