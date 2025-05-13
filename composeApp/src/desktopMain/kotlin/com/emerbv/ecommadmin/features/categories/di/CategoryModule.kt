package com.emerbv.ecommadmin.features.categories.di

import com.emerbv.ecommadmin.core.utils.TokenManager
import com.emerbv.ecommadmin.features.categories.data.repository.CategoryRepository
import com.emerbv.ecommadmin.features.categories.data.repository.CategoryRepositoryImpl
import com.emerbv.ecommadmin.features.categories.domain.*
import com.emerbv.ecommadmin.features.categories.presentation.CategoryFormViewModel
import com.emerbv.ecommadmin.features.categories.presentation.CategoryListViewModel
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val categoryModule = module {
    // JSON personalizado para ser más tolerante con las respuestas
    /*
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

    single<CategoryRepository> {
        CategoryRepositoryImpl(
            httpClient = get(qualifier = org.koin.core.qualifier.named("productClient")),
            baseUrl = get(),
            tokenProvider = { get<TokenManager>().getToken() }
        )
    }
     */

    // Repositorio
    /*
    single<CategoryRepository> {
        CategoryRepositoryImpl(
            httpClient = get(),
            baseUrl = get(),
            tokenProvider = { get<TokenManager>().getToken() }
        )
    }

    single(qualifier = org.koin.core.qualifier.named("productClient")) {
        val baseClient = get<HttpClient>()
        baseClient.config {
            install(ContentNegotiation) {
                json(get(qualifier = org.koin.core.qualifier.named("productJson")))
            }
        }
    }

     */

    single<CategoryRepository> {
        CategoryRepositoryImpl(
            httpClient = get(),
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