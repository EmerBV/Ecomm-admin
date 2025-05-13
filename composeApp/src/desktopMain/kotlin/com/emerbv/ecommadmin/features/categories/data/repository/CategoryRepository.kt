package com.emerbv.ecommadmin.features.categories.data.repository

import com.emerbv.ecommadmin.core.network.ApiResult
import com.emerbv.ecommadmin.features.categories.data.model.CategoryDto
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz para el repositorio de categorías
 */
interface CategoryRepository {
    suspend fun getAllCategories(): Flow<ApiResult<List<CategoryDto>>>
    suspend fun getCategoryById(id: Long): Flow<ApiResult<CategoryDto>>
    suspend fun addCategory(name: String): Flow<ApiResult<CategoryDto>>
    suspend fun updateCategory(id: Long, name: String): Flow<ApiResult<CategoryDto>>
    suspend fun deleteCategory(id: Long): Flow<ApiResult<Boolean>>
}
