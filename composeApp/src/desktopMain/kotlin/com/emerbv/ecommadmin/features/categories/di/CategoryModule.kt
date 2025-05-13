package com.emerbv.ecommadmin.features.categories.di

import com.emerbv.ecommadmin.core.utils.TokenManager
import com.emerbv.ecommadmin.features.categories.data.repository.CategoryRepository
import com.emerbv.ecommadmin.features.categories.data.repository.CategoryRepositoryImpl
import com.emerbv.ecommadmin.features.categories.domain.*
import com.emerbv.ecommadmin.features.categories.presentation.CategoryFormViewModel
import com.emerbv.ecommadmin.features.categories.presentation.CategoryListViewModel
import io.ktor.client.*
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val categoryModule = module {
    // Repositorio
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