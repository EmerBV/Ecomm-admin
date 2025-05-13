package com.emerbv.ecommadmin.features.categories.data.repository

import com.emerbv.ecommadmin.core.network.ApiResult
import com.emerbv.ecommadmin.features.categories.data.model.CategoryDto
import com.emerbv.ecommadmin.features.categories.data.model.CategoryResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Implementación del repositorio de categorías usando Ktor
 */
class CategoryRepositoryImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val tokenProvider: () -> String?
) : CategoryRepository {

    private val apiPath = "$baseUrl/categories"

    private val authHeader: String?
        get() = tokenProvider()?.let { "Bearer $it" }

    override suspend fun getAllCategories(): Flow<ApiResult<List<CategoryDto>>> = flow {
        emit(ApiResult.Loading)
        try {
            // Agrega logging para depuración
            println("Realizando solicitud de categorías a: $apiPath/all")

            val response: CategoryResponse = httpClient.get("$apiPath/all") {
                headers {
                    authHeader?.let { header("Authorization", it) }
                }
            }.body()

            // Imprime la respuesta para depuración
            println("Respuesta recibida: ${response.message}")
            println("Categorías recibidas: ${response.data}")

            response.data?.let { categories ->
                emit(ApiResult.Success(categories))
            } ?: emit(ApiResult.Error("No se encontraron categorías: ${response.message}"))

        } catch (e: Exception) {
            println("Error al obtener categorías: ${e::class.simpleName} - ${e.message}")
            emit(ApiResult.Error("Error obteniendo categorías: ${e.message}"))
        }
    }

    override suspend fun getCategoryById(id: Long): Flow<ApiResult<CategoryDto>> = flow {
        emit(ApiResult.Loading)
        try {
            val response: CategoryResponse = httpClient.get("$apiPath/category/$id/category") {
                headers {
                    authHeader?.let { header("Authorization", it) }
                }
            }.body()

            response.data?.firstOrNull()?.let { category ->
                emit(ApiResult.Success(category))
            } ?: emit(ApiResult.Error("Category not found: ${response.message}"))

        } catch (e: ClientRequestException) {
            val errorMessage = when (e.response.status) {
                HttpStatusCode.Unauthorized -> "Authentication required. Please login again."
                HttpStatusCode.NotFound -> "Category not found"
                else -> "Connection error: ${e.message}"
            }
            emit(ApiResult.Error(errorMessage, e.response.status.value))
        } catch (e: Exception) {
            emit(ApiResult.Error("Error getting category: ${e.message}"))
        }
    }

    override suspend fun addCategory(name: String): Flow<ApiResult<CategoryDto>> = flow {
        emit(ApiResult.Loading)
        try {
            // Crear objeto de categoría para el cuerpo de la solicitud
            val categoryBody = mapOf("name" to name)

            val response: CategoryResponse = httpClient.post("$apiPath/add") {
                headers {
                    authHeader?.let { header("Authorization", it) }
                    contentType(ContentType.Application.Json)
                }
                setBody(categoryBody)
            }.body()

            response.data?.firstOrNull()?.let { category ->
                emit(ApiResult.Success(category))
            } ?: emit(ApiResult.Error("Failed to add category: ${response.message}"))

        } catch (e: ClientRequestException) {
            val errorMessage = when (e.response.status) {
                HttpStatusCode.Unauthorized -> "Authentication required. Please login again."
                HttpStatusCode.Conflict -> "A category with this name already exists"
                else -> "Connection error: ${e.message}"
            }
            emit(ApiResult.Error(errorMessage, e.response.status.value))
        } catch (e: Exception) {
            emit(ApiResult.Error("Error adding category: ${e.message}"))
        }
    }

    override suspend fun updateCategory(id: Long, name: String): Flow<ApiResult<CategoryDto>> = flow {
        emit(ApiResult.Loading)
        try {
            // Crear objeto de categoría para el cuerpo de la solicitud
            val categoryBody = mapOf("name" to name)

            val response: CategoryResponse = httpClient.put("$apiPath/category/$id/update") {
                headers {
                    authHeader?.let { header("Authorization", it) }
                    contentType(ContentType.Application.Json)
                }
                setBody(categoryBody)
            }.body()

            response.data?.firstOrNull()?.let { category ->
                emit(ApiResult.Success(category))
            } ?: emit(ApiResult.Error("Failed to update category: ${response.message}"))

        } catch (e: ClientRequestException) {
            val errorMessage = when (e.response.status) {
                HttpStatusCode.Unauthorized -> "Authentication required. Please login again."
                HttpStatusCode.NotFound -> "Category not found"
                HttpStatusCode.Conflict -> "A category with this name already exists"
                else -> "Connection error: ${e.message}"
            }
            emit(ApiResult.Error(errorMessage, e.response.status.value))
        } catch (e: Exception) {
            emit(ApiResult.Error("Error updating category: ${e.message}"))
        }
    }

    override suspend fun deleteCategory(id: Long): Flow<ApiResult<Boolean>> = flow {
        emit(ApiResult.Loading)
        try {
            val response: CategoryResponse = httpClient.delete("$apiPath/category/$id/delete") {
                headers {
                    authHeader?.let { header("Authorization", it) }
                }
            }.body()

            // Si no hay error en la respuesta, consideramos que la eliminación fue exitosa
            if (response.message.contains("deleted", ignoreCase = true) ||
                response.message.contains("success", ignoreCase = true)) {
                emit(ApiResult.Success(true))
            } else {
                emit(ApiResult.Error("Failed to delete category: ${response.message}"))
            }

        } catch (e: ClientRequestException) {
            val errorMessage = when (e.response.status) {
                HttpStatusCode.Unauthorized -> "Authentication required. Please login again."
                HttpStatusCode.NotFound -> "Category not found"
                else -> "Connection error: ${e.message}"
            }
            emit(ApiResult.Error(errorMessage, e.response.status.value))
        } catch (e: Exception) {
            emit(ApiResult.Error("Error deleting category: ${e.message}"))
        }
    }
}