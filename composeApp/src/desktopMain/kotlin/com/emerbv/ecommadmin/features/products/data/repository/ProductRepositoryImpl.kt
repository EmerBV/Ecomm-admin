package com.emerbv.ecommadmin.features.products.data.repository

import com.emerbv.ecommadmin.core.network.ApiResult
import com.emerbv.ecommadmin.features.products.data.model.ProductDto
import com.emerbv.ecommadmin.features.products.data.model.ProductResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProductRepositoryImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val tokenProvider: () -> String?
) : ProductRepository {

    private val authHeader: String?
        get() = tokenProvider()?.let { "Bearer $it" }

    override suspend fun getAllProducts(): Flow<ApiResult<List<ProductDto>>> = flow {
        emit(ApiResult.Loading)
        try {
            println("Realizando solicitud de productos a: $baseUrl/products/all")
            val response: ProductResponse = httpClient.get("$baseUrl/products/all") {
                headers {
                    authHeader?.let { header("Authorization", it) }
                }
            }.body()

            println("Respuesta recibida: ${response.message}")

            response.data?.let {
                println("Productos obtenidos: ${it.size}")
                emit(ApiResult.Success(it))
            } ?: emit(ApiResult.Error("No se encontraron productos: ${response.message}"))
        } catch (e: ClientRequestException) {
            println("Error de cliente: ${e.message}")
            val errorMessage = when (e.response.status) {
                HttpStatusCode.Unauthorized -> "No autorizado. Por favor, inicie sesión nuevamente."
                HttpStatusCode.NotFound -> "No se encontraron productos"
                else -> "Error de conexión: ${e.message}"
            }
            emit(ApiResult.Error(errorMessage, e.response.status.value))
        } catch (e: Exception) {
            println("Error inesperado: ${e::class.simpleName} - ${e.message}")
            emit(ApiResult.Error("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun getProductById(id: Long): Flow<ApiResult<ProductDto>> = flow {
        emit(ApiResult.Loading)
        try {
            val response: ProductResponse = httpClient.get("$baseUrl/products/product/$id/product") {
                headers {
                    authHeader?.let { header("Authorization", it) }
                }
            }.body()

            response.data?.firstOrNull()?.let {
                emit(ApiResult.Success(it))
            } ?: emit(ApiResult.Error("Producto no encontrado"))
        } catch (e: ClientRequestException) {
            val errorMessage = when (e.response.status) {
                HttpStatusCode.Unauthorized -> "No autorizado. Por favor, inicie sesión nuevamente."
                HttpStatusCode.NotFound -> "Producto no encontrado"
                else -> "Error de conexión: ${e.message}"
            }
            emit(ApiResult.Error(errorMessage, e.response.status.value))
        } catch (e: Exception) {
            emit(ApiResult.Error("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun getProductsByCategory(category: String): Flow<ApiResult<List<ProductDto>>> = flow {
        emit(ApiResult.Loading)
        try {
            val encodedCategory = java.net.URLEncoder.encode(category, "UTF-8")
            val response: ProductResponse = httpClient.get("$baseUrl/products/product/$encodedCategory/all/products") {
                headers {
                    authHeader?.let { header("Authorization", it) }
                }
            }.body()

            response.data?.let {
                emit(ApiResult.Success(it))
            } ?: emit(ApiResult.Error("No se encontraron productos en esta categoría"))
        } catch (e: ClientRequestException) {
            val errorMessage = when (e.response.status) {
                HttpStatusCode.Unauthorized -> "No autorizado. Por favor, inicie sesión nuevamente."
                HttpStatusCode.NotFound -> "No se encontraron productos en esta categoría"
                else -> "Error de conexión: ${e.message}"
            }
            emit(ApiResult.Error(errorMessage, e.response.status.value))
        } catch (e: Exception) {
            emit(ApiResult.Error("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun getProductsByStatus(status: String): Flow<ApiResult<List<ProductDto>>> = flow {
        emit(ApiResult.Loading)
        try {
            val response: ProductResponse = httpClient.get("$baseUrl/products/product/$status/products") {
                headers {
                    authHeader?.let { header("Authorization", it) }
                }
            }.body()

            response.data?.let {
                emit(ApiResult.Success(it))
            } ?: emit(ApiResult.Error("No se encontraron productos con este estado"))
        } catch (e: ClientRequestException) {
            val errorMessage = when (e.response.status) {
                HttpStatusCode.Unauthorized -> "No autorizado. Por favor, inicie sesión nuevamente."
                HttpStatusCode.NotFound -> "No se encontraron productos con este estado"
                else -> "Error de conexión: ${e.message}"
            }
            emit(ApiResult.Error(errorMessage, e.response.status.value))
        } catch (e: Exception) {
            emit(ApiResult.Error("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun getBestSellerProducts(): Flow<ApiResult<List<ProductDto>>> = flow {
        emit(ApiResult.Loading)
        try {
            val response: ProductResponse = httpClient.get("$baseUrl/products/product/best-sellers/products") {
                headers {
                    authHeader?.let { header("Authorization", it) }
                }
            }.body()

            response.data?.let {
                emit(ApiResult.Success(it))
            } ?: emit(ApiResult.Error("No se encontraron productos destacados"))
        } catch (e: ClientRequestException) {
            val errorMessage = when (e.response.status) {
                HttpStatusCode.Unauthorized -> "No autorizado. Por favor, inicie sesión nuevamente."
                HttpStatusCode.NotFound -> "No se encontraron productos destacados"
                else -> "Error de conexión: ${e.message}"
            }
            emit(ApiResult.Error(errorMessage, e.response.status.value))
        } catch (e: Exception) {
            emit(ApiResult.Error("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun getMostWishedProducts(): Flow<ApiResult<List<ProductDto>>> = flow {
        emit(ApiResult.Loading)
        try {
            val response: ProductResponse = httpClient.get("$baseUrl/products/product/most-desired/products") {
                headers {
                    authHeader?.let { header("Authorization", it) }
                }
            }.body()

            response.data?.let {
                emit(ApiResult.Success(it))
            } ?: emit(ApiResult.Error("No se encontraron productos deseados"))
        } catch (e: ClientRequestException) {
            val errorMessage = when (e.response.status) {
                HttpStatusCode.Unauthorized -> "No autorizado. Por favor, inicie sesión nuevamente."
                HttpStatusCode.NotFound -> "No se encontraron productos deseados"
                else -> "Error de conexión: ${e.message}"
            }
            emit(ApiResult.Error(errorMessage, e.response.status.value))
        } catch (e: Exception) {
            emit(ApiResult.Error("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun getRecentProducts(): Flow<ApiResult<List<ProductDto>>> = flow {
        emit(ApiResult.Loading)
        try {
            val response: ProductResponse = httpClient.get("$baseUrl/products/product/recent/products") {
                headers {
                    authHeader?.let { header("Authorization", it) }
                }
            }.body()

            response.data?.let {
                emit(ApiResult.Success(it))
            } ?: emit(ApiResult.Error("No se encontraron productos recientes"))
        } catch (e: ClientRequestException) {
            val errorMessage = when (e.response.status) {
                HttpStatusCode.Unauthorized -> "No autorizado. Por favor, inicie sesión nuevamente."
                HttpStatusCode.NotFound -> "No se encontraron productos recientes"
                else -> "Error de conexión: ${e.message}"
            }
            emit(ApiResult.Error(errorMessage, e.response.status.value))
        } catch (e: Exception) {
            emit(ApiResult.Error("Error inesperado: ${e.message}"))
        }
    }
}