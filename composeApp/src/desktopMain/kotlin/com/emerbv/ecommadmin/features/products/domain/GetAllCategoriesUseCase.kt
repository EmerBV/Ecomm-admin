package com.emerbv.ecommadmin.features.products.domain

import com.emerbv.ecommadmin.core.network.ApiResult
import com.emerbv.ecommadmin.features.products.data.model.CategoryDto
import com.emerbv.ecommadmin.features.products.data.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow

/**
 * Caso de uso para obtener todas las categorías
 */
class GetAllCategoriesUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(): Flow<ApiResult<List<CategoryDto>>> {
        return repository.getAllCategories()
    }
}