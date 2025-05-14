package com.emerbv.ecommadmin.features.products.data.repository

import com.emerbv.ecommadmin.core.network.ApiResult
import com.emerbv.ecommadmin.features.products.data.model.ProductDto
import com.emerbv.ecommadmin.features.products.data.model.VariantDto
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun getAllProducts(): Flow<ApiResult<List<ProductDto>>>
    suspend fun getProductById(id: Long): Flow<ApiResult<ProductDto>>
    suspend fun getProductsByCategory(category: String): Flow<ApiResult<List<ProductDto>>>
    suspend fun getProductsByStatus(status: String): Flow<ApiResult<List<ProductDto>>>
    suspend fun getBestSellerProducts(): Flow<ApiResult<List<ProductDto>>>
    suspend fun getMostWishedProducts(): Flow<ApiResult<List<ProductDto>>>
    suspend fun getRecentProducts(): Flow<ApiResult<List<ProductDto>>>
    suspend fun addProduct(product: ProductDto): Flow<ApiResult<ProductDto>>
    suspend fun updateProduct(product: ProductDto): Flow<ApiResult<ProductDto>>

    suspend fun addProductVariant(productId: Long, variant: VariantDto): Flow<ApiResult<VariantDto>>
    suspend fun updateProductVariant(productId: Long, variant: VariantDto): Flow<ApiResult<VariantDto>>
    suspend fun deleteProductVariant(productId: Long, variantId: Long): Flow<ApiResult<Boolean>>
}