package com.emerbv.ecommadmin.features.categories.di

import com.emerbv.ecommadmin.core.utils.TokenManager
import com.emerbv.ecommadmin.features.categories.data.repository.CategoryRepository
import com.emerbv.ecommadmin.features.categories.data.repository.CategoryRepositoryImpl
import com.emerbv.ecommadmin.features.categories.domain.*
import com.emerbv.ecommadmin.features.categories.presentation.CategoryFormViewModel
import com.emerbv.ecommadmin.features.categories.presentation.CategoryListViewModel
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val categoryModule = module {
    // JSON personalizado para ser más tolerante con las respuestas
    single(qualifier = org.koin.core.qualifier.named("categoryJson")) {
        Json {
            isLenient = true
            ignoreUnknownKeys = true
            prettyPrint = true
            coerceInputValues = true
            encodeDefaults = true
            explicitNulls = false
            allowSpecialFloatingPointValues = true
            useArrayPolymorphism = true
        }
    }

    // Cliente HTTP específico para categorías
    single(qualifier = org.koin.core.qualifier.named("categoryClient")) {
        val baseClient = get<HttpClient>()
        baseClient.config {
            install(ContentNegotiation) {
                json(get(qualifier = org.koin.core.qualifier.named("categoryJson")))
            }
        }
    }

    // Repositorio
    single<CategoryRepository> {
        CategoryRepositoryImpl(
            httpClient = get(qualifier = org.koin.core.qualifier.named("categoryClient")),
            baseUrl = get(),
            tokenProvider = { get<TokenManager>().getToken() }
        )
    }

    // Casos de uso
    factoryOf(::GetAllCategoriesUseCase)
    factoryOf(::GetCategoryByIdUseCase)
    factoryOf(::AddCategoryUseCase)
    factoryOf(::UpdateCategoryUseCase)
    factoryOf(::DeleteCategoryUseCase)

    // ViewModels
    factoryOf(::CategoryListViewModel)
    factoryOf(::CategoryFormViewModel)
}