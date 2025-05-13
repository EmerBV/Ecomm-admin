package com.emerbv.ecommadmin.features.categories.domain

import com.emerbv.ecommadmin.core.network.ApiResult
import com.emerbv.ecommadmin.features.categories.data.model.CategoryDto
import com.emerbv.ecommadmin.features.categories.data.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow

/**
 * Caso de uso para obtener todas las categorías
 */
class GetAllCategoriesUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(): Flow<ApiResult<List<CategoryDto>>> {
        return repository.getAllCategories()
    }
}

/**
 * Caso de uso para obtener una categoría por su ID
 */
class GetCategoryByIdUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(id: Long): Flow<ApiResult<CategoryDto>> {
        return repository.getCategoryById(id)
    }
}

/**
 * Caso de uso para añadir una nueva categoría
 */
class AddCategoryUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(name: String): Flow<ApiResult<CategoryDto>> {
        return repository.addCategory(name)
    }
}

/**
 * Caso de uso para actualizar una categoría existente
 */
class UpdateCategoryUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(id: Long, name: String): Flow<ApiResult<CategoryDto>> {
        return repository.updateCategory(id, name)
    }
}

/**
 * Caso de uso para eliminar una categoría
 */
class DeleteCategoryUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(id: Long): Flow<ApiResult<Boolean>> {
        return repository.deleteCategory(id)
    }
}