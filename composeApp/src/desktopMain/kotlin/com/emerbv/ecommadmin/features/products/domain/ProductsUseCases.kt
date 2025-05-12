package com.emerbv.ecommadmin.features.products.domain

import com.emerbv.ecommadmin.core.network.ApiResult
import com.emerbv.ecommadmin.features.products.data.model.ProductDto
import com.emerbv.ecommadmin.features.products.data.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

class GetAllProductsUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(): Flow<ApiResult<List<ProductDto>>> {
        return repository.getAllProducts()
    }
}

class GetProductByIdUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(id: Long): Flow<ApiResult<ProductDto>> {
        return repository.getProductById(id)
    }
}

class GetProductsByCategoryUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(category: String): Flow<ApiResult<List<ProductDto>>> {
        return repository.getProductsByCategory(category)
    }
}

class GetProductsByStatusUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(status: String): Flow<ApiResult<List<ProductDto>>> {
        return repository.getProductsByStatus(status)
    }
}

class GetBestSellerProductsUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(): Flow<ApiResult<List<ProductDto>>> {
        return repository.getBestSellerProducts()
    }
}

class GetMostWishedProductsUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(): Flow<ApiResult<List<ProductDto>>> {
        return repository.getMostWishedProducts()
    }
}

class GetRecentProductsUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(): Flow<ApiResult<List<ProductDto>>> {
        return repository.getRecentProducts()
    }
}