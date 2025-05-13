package com.emerbv.ecommadmin.features.categories.data.repository

import com.emerbv.ecommadmin.core.network.ApiResult
import com.emerbv.ecommadmin.features.categories.data.model.CategoryDto
import com.emerbv.ecommadmin.features.categories.data.model.CategoryResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
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

            val response = httpClient.get("$apiPath/all") {
                headers {
                    authHeader?.let { header("Authorization", it) }
                }
            }

            // Log de la respuesta bruta para debuggear
            val responseText = response.bodyAsText()
            println("Respuesta bruta: $responseText")

            if (response.status.isSuccess()) {
                val categoryResponse: CategoryResponse = response.body()
                println("Respuesta parseada: ${categoryResponse.message}")
                println("Categorías recibidas: ${categoryResponse.data?.size}")

                categoryResponse.data?.let { categories ->
                    emit(ApiResult.Success(categories))
                } ?: emit(ApiResult.Error("No se encontraron categorías: ${categoryResponse.message}"))
            } else {
                emit(ApiResult.Error("Error en la respuesta del servidor: ${response.status}"))
            }

        } catch (e: ClientRequestException) {
            println("Error de cliente HTTP: ${e.response.status} - ${e.message}")
            val errorMessage = when (e.response.status) {
                HttpStatusCode.Unauthorized -> "Autenticación requerida. Por favor, inicie sesión nuevamente."
                HttpStatusCode.NotFound -> "No se encontraron categorías"
                else -> "Error de conexión: ${e.message}"
            }
            emit(ApiResult.Error(errorMessage, e.response.status.value))
        } catch (e: Exception) {
            println("Error inesperado: ${e::class.simpleName} - ${e.message}")
            e.printStackTrace()
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