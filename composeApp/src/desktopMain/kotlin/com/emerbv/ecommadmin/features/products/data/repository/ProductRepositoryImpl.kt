package com.emerbv.ecommadmin.features.products.data.repository

import com.emerbv.ecommadmin.core.network.ApiResult
import com.emerbv.ecommadmin.features.products.data.model.ProductDto
import com.emerbv.ecommadmin.features.products.data.model.ProductResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
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

            val response = httpClient.get("$baseUrl/products/all") {
                headers {
                    // Para rutas públicas, no necesitamos autenticación
                    // pero mantenemos las cabeceras de contenido
                    header("Content-Type", "application/json")
                    header("Accept", "application/json")
                }
            }

            // Imprime información detallada sobre la respuesta para depuración
            println("Código de estado de respuesta: ${response.status}")
            println("Cabeceras de respuesta: ${response.headers}")

            val responseText = response.bodyAsText()
            println("Cuerpo de la respuesta: $responseText")

            if (response.status.isSuccess()) {
                val productResponse: ProductResponse = response.body()
                println("Respuesta parseada: ${productResponse.message}")

                productResponse.data?.let {
                    println("Productos obtenidos: ${it.size}")
                    emit(ApiResult.Success(it))
                } ?: emit(ApiResult.Error("No se encontraron productos: ${productResponse.message}"))
            } else {
                // Si el estado no es exitoso, lo manejamos específicamente
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
            println("Error de cliente: ${e.message}")
            println("Estado de respuesta: ${e.response.status}")

            val errorMessage = when (e.response.status) {
                HttpStatusCode.Unauthorized -> "Error en la solicitud"
                HttpStatusCode.NotFound -> "No se encontraron productos"
                else -> "Error de conexión: ${e.message}"
            }
            emit(ApiResult.Error(errorMessage, e.response.status.value))
        } catch (e: Exception) {
            println("Error inesperado: ${e::class.simpleName} - ${e.message}")
            e.printStackTrace()
            emit(ApiResult.Error("Error inesperado: ${e.message}"))
        }
    }

    // Resto de métodos del repositorio que no requieren autenticación...

    override suspend fun getProductById(id: Long): Flow<ApiResult<ProductDto>> = flow {
        emit(ApiResult.Loading)
        try {
            val response = httpClient.get("$baseUrl/products/product/$id/product") {
                headers {
                    header("Content-Type", "application/json")
                    header("Accept", "application/json")
                }
            }

            if (response.status.isSuccess()) {
                val productResponse: ProductResponse = response.body()

                productResponse.data?.firstOrNull()?.let {
                    emit(ApiResult.Success(it))
                } ?: emit(ApiResult.Error("Producto no encontrado"))
            } else {
                emit(ApiResult.Error("Error en la respuesta del servidor: ${response.status}", response.status.value))
            }
        } catch (e: ClientRequestException) {
            val errorMessage = when (e.response.status) {
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
            val response = httpClient.get("$baseUrl/products/product/$encodedCategory/all/products") {
                headers {
                    header("Content-Type", "application/json")
                    header("Accept", "application/json")
                }
            }

            if (response.status.isSuccess()) {
                val productResponse: ProductResponse = response.body()

                productResponse.data?.let {
                    emit(ApiResult.Success(it))
                } ?: emit(ApiResult.Error("No se encontraron productos en esta categoría"))
            } else {
                emit(ApiResult.Error("Error en la respuesta del servidor: ${response.status}", response.status.value))
            }
        } catch (e: ClientRequestException) {
            val errorMessage = when (e.response.status) {
                HttpStatusCode.NotFound -> "No se encontraron productos en esta categoría"
                else -> "Error de conexión: ${e.message}"
            }
            emit(ApiResult.Error(errorMessage, e.response.status.value))
        } catch (e: Exception) {
            emit(ApiResult.Error("Error inesperado: ${e.message}"))
        }
    }

    // Método que podrían necesitar autenticación

    override suspend fun getProductsByStatus(status: String): Flow<ApiResult<List<ProductDto>>> = flow {
        emit(ApiResult.Loading)
        try {
            val response = httpClient.get("$baseUrl/products/product/$status/products") {
                headers {
                    authHeader?.let { header("Authorization", it) }
                    header("Content-Type", "application/json")
                    header("Accept", "application/json")
                }
            }

            if (response.status.isSuccess()) {
                val productResponse: ProductResponse = response.body()

                productResponse.data?.let {
                    emit(ApiResult.Success(it))
                } ?: emit(ApiResult.Error("No se encontraron productos con este estado"))
            } else {
                emit(ApiResult.Error("Error en la respuesta del servidor: ${response.status}", response.status.value))
            }
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
            val response = httpClient.get("$baseUrl/products/product/best-sellers/products") {
                headers {
                    header("Content-Type", "application/json")
                    header("Accept", "application/json")
                }
            }

            if (response.status.isSuccess()) {
                val productResponse: ProductResponse = response.body()

                productResponse.data?.let {
                    emit(ApiResult.Success(it))
                } ?: emit(ApiResult.Error("No se encontraron productos destacados"))
            } else {
                emit(ApiResult.Error("Error en la respuesta del servidor: ${response.status}", response.status.value))
            }
        } catch (e: ClientRequestException) {
            val errorMessage = when (e.response.status) {
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
            val response = httpClient.get("$baseUrl/products/product/most-desired/products") {
                headers {
                    header("Content-Type", "application/json")
                    header("Accept", "application/json")
                }
            }

            if (response.status.isSuccess()) {
                val productResponse: ProductResponse = response.body()

                productResponse.data?.let {
                    emit(ApiResult.Success(it))
                } ?: emit(ApiResult.Error("No se encontraron productos deseados"))
            } else {
                emit(ApiResult.Error("Error en la respuesta del servidor: ${response.status}", response.status.value))
            }
        } catch (e: ClientRequestException) {
            val errorMessage = when (e.response.status) {
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
            val response = httpClient.get("$baseUrl/products/product/recent/products") {
                headers {
                    header("Content-Type", "application/json")
                    header("Accept", "application/json")
                }
            }

            if (response.status.isSuccess()) {
                val productResponse: ProductResponse = response.body()

                productResponse.data?.let {
                    emit(ApiResult.Success(it))
                } ?: emit(ApiResult.Error("No se encontraron productos recientes"))
            } else {
                emit(ApiResult.Error("Error en la respuesta del servidor: ${response.status}", response.status.value))
            }
        } catch (e: ClientRequestException) {
            val errorMessage = when (e.response.status) {
                HttpStatusCode.NotFound -> "No se encontraron productos recientes"
                else -> "Error de conexión: ${e.message}"
            }
            emit(ApiResult.Error(errorMessage, e.response.status.value))
        } catch (e: Exception) {
            emit(ApiResult.Error("Error inesperado: ${e.message}"))
        }
    }
}