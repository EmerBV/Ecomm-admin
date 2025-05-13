package com.emerbv.ecommadmin.features.products.data.repository

import com.emerbv.ecommadmin.core.network.ApiResult
import com.emerbv.ecommadmin.features.products.data.model.CategoryDto
import com.emerbv.ecommadmin.features.products.data.model.CategoryResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Interfaz para el repositorio de categorías
 */
interface CategoryRepository {
    suspend fun getAllCategories(): Flow<ApiResult<List<CategoryDto>>>
}

/**
 * Implementación del repositorio de categorías usando Ktor
 */
class CategoryRepositoryImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val tokenProvider: () -> String?
) : CategoryRepository {

    override suspend fun getAllCategories(): Flow<ApiResult<List<CategoryDto>>> = flow {
        emit(ApiResult.Loading)
        try {
            val token = tokenProvider()

            val response: CategoryResponse = httpClient.get("$baseUrl/categories/all") {
                headers {
                    token?.let {
                        header("Authorization", "Bearer $it")
                    }
                }
            }.body()

            response.data?.let { categories ->
                emit(ApiResult.Success(categories))
            } ?: emit(ApiResult.Error("No se encontraron categorías: ${response.message}"))

        } catch (e: Exception) {
            emit(ApiResult.Error("Error al obtener categorías: ${e.message}"))
        }
    }
}