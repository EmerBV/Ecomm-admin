package com.emerbv.ecommadmin.features.products.domain

import com.emerbv.ecommadmin.core.network.ApiResult
import com.emerbv.ecommadmin.features.products.data.model.VariantDto
import com.emerbv.ecommadmin.features.products.data.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

/**
 * Caso de uso para añadir una variante a un producto
 */
class AddProductVariantUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(productId: Long, variant: VariantDto): Flow<ApiResult<VariantDto>> {
        return repository.addProductVariant(productId, variant)
    }
}

/**
 * Caso de uso para actualizar una variante existente
 */
class UpdateProductVariantUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(productId: Long, variant: VariantDto): Flow<ApiResult<VariantDto>> {
        return repository.updateProductVariant(productId, variant)
    }
}

/**
 * Caso de uso para eliminar una variante
 */
class DeleteProductVariantUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(productId: Long, variantId: Long): Flow<ApiResult<Boolean>> {
        return repository.deleteProductVariant(productId, variantId)
    }
}