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
                    // Para rutas públicas, no necesitamos autenticación
                    header("Content-Type", "application/json")
                    header("Accept", "application/json")
                }
            }

            // Log de la respuesta para depuración
            println("Código de estado de respuesta: ${response.status}")
            println("Cabeceras de respuesta: ${response.headers}")

            val responseText = response.bodyAsText()
            println("Cuerpo de la respuesta: $responseText")

            if (response.status.isSuccess()) {
                val categoryResponse: CategoryResponse = response.body()
                println("Respuesta parseada: ${categoryResponse.message}")
                println("Categorías recibidas: ${categoryResponse.data?.size}")

                categoryResponse.data?.let { categories ->
                    emit(ApiResult.Success(categories))
                } ?: emit(ApiResult.Error("No se encontraron categorías: ${categoryResponse.message}"))
            } else {
                when (response.status) {
                    HttpStatusCode.Unauthorized -> {
                        emit(ApiResult.Error("Error en la solicitud", 401))
                    }
                    else -> {
                        emit(ApiResult.Error("Error en la respuesta del servidor: ${response.status}", response.status.value))
                    }
                }
            }

        } catch (e: ClientRequestException) {
            println("Error de cliente HTTP: ${e.response.status} - ${e.message}")
            val errorBody = try {
                e.response.bodyAsText()
            } catch (ex: Exception) {
                "No se pudo leer el cuerpo del error"
            }
            println("Cuerpo del error: $errorBody")

            val errorMessage = when (e.response.status) {
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
            val response = httpClient.get("$apiPath/category/$id/category") {
                headers {
                    header("Content-Type", "application/json")
                    header("Accept", "application/json")
                }
            }

            if (response.status.isSuccess()) {
                val categoryResponse: CategoryResponse = response.body()

                categoryResponse.data?.firstOrNull()?.let { category ->
                    emit(ApiResult.Success(category))
                } ?: emit(ApiResult.Error("Category not found: ${categoryResponse.message}"))
            } else {
                emit(ApiResult.Error("Error en la respuesta del servidor: ${response.status}", response.status.value))
            }

        } catch (e: ClientRequestException) {
            val errorMessage = when (e.response.status) {
                HttpStatusCode.NotFound -> "Category not found"
                else -> "Connection error: ${e.message}"
            }
            emit(ApiResult.Error(errorMessage, e.response.status.value))
        } catch (e: Exception) {
            emit(ApiResult.Error("Error getting category: ${e.message}"))
        }
    }

    // Los siguientes métodos sí pueden requerir autenticación según tu backend

    override suspend fun addCategory(name: String): Flow<ApiResult<CategoryDto>> = flow {
        emit(ApiResult.Loading)
        try {
            // Crear objeto de categoría para el cuerpo de la solicitud
            val categoryBody = mapOf("name" to name)

            val response = httpClient.post("$apiPath/add") {
                headers {
                    authHeader?.let { header("Authorization", it) }
                    contentType(ContentType.Application.Json)
                    accept(ContentType.Application.Json)
                }
                setBody(categoryBody)
            }

            if (response.status.isSuccess()) {
                val categoryResponse: CategoryResponse = response.body()

                categoryResponse.data?.firstOrNull()?.let { category ->
                    emit(ApiResult.Success(category))
                } ?: emit(ApiResult.Error("Failed to add category: ${categoryResponse.message}"))
            } else {
                emit(ApiResult.Error("Error en la respuesta del servidor: ${response.status}", response.status.value))
            }

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

            val response = httpClient.put("$apiPath/category/$id/update") {
                headers {
                    authHeader?.let { header("Authorization", it) }
                    contentType(ContentType.Application.Json)
                    accept(ContentType.Application.Json)
                }
                setBody(categoryBody)
            }

            if (response.status.isSuccess()) {
                val categoryResponse: CategoryResponse = response.body()

                categoryResponse.data?.firstOrNull()?.let { category ->
                    emit(ApiResult.Success(category))
                } ?: emit(ApiResult.Error("Failed to update category: ${categoryResponse.message}"))
            } else {
                emit(ApiResult.Error("Error en la respuesta del servidor: ${response.status}", response.status.value))
            }

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
            val response = httpClient.delete("$apiPath/category/$id/delete") {
                headers {
                    authHeader?.let { header("Authorization", it) }
                    accept(ContentType.Application.Json)
                }
            }

            if (response.status.isSuccess()) {
                val categoryResponse: CategoryResponse = response.body()

                // Si no hay error en la respuesta, consideramos que la eliminación fue exitosa
                if (response.status.isSuccess() &&
                    (categoryResponse.message.contains("deleted", ignoreCase = true) ||
                            categoryResponse.message.contains("success", ignoreCase = true))) {
                    emit(ApiResult.Success(true))
                } else {
                    emit(ApiResult.Error("Failed to delete category: ${categoryResponse.message}"))
                }
            } else {
                emit(ApiResult.Error("Error en la respuesta del servidor: ${response.status}", response.status.value))
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