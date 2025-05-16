package com.emerbv.ecommadmin.features.products.data.repository

import com.emerbv.ecommadmin.core.network.ApiResult
import com.emerbv.ecommadmin.features.products.data.model.ProductDto
import com.emerbv.ecommadmin.features.products.data.model.VariantDto
import com.emerbv.ecommadmin.features.products.data.model.ProductResponse
import com.emerbv.ecommadmin.features.products.data.model.VariantResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.*

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
            val response = httpClient.get("$baseUrl/products/all") {
                headers {
                    header("Content-Type", "application/json")
                    header("Accept", "application/json")
                }
            }

            if (response.status.isSuccess()) {
                val productResponse: ProductResponse = response.body()
                val products = productResponse.getProducts()

                emit(ApiResult.Success(products))
            } else {
                emit(ApiResult.Error("Error en la respuesta del servidor: ${response.status}", response.status.value))
            }
        } catch (e: Exception) {
            emit(ApiResult.Error("Error inesperado: ${e.message}"))
        }
    }

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

                val product = productResponse.getProduct()
                if (product != null) {
                    emit(ApiResult.Success(product))
                } else {
                    emit(ApiResult.Error("Producto no encontrado"))
                }
            } else {
                emit(ApiResult.Error("Error en la respuesta del servidor: ${response.status}", response.status.value))
            }
        } catch (e: SerializationException) {
            println("Error de serialización: ${e.message}")
            emit(ApiResult.Error("Error al procesar la respuesta del servidor: ${e.message}"))
        } catch (e: ClientRequestException) {
            val errorMessage = when (e.response.status) {
                HttpStatusCode.NotFound -> "Producto no encontrado"
                else -> "Error de conexión: ${e.message}"
            }
            emit(ApiResult.Error(errorMessage, e.response.status.value))
        } catch (e: Exception) {
            println("Error inesperado: ${e::class.simpleName} - ${e.message}")
            e.printStackTrace()
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
                val products = productResponse.getProducts()

                emit(ApiResult.Success(products))
            } else {
                emit(ApiResult.Error("Error en la respuesta del servidor: ${response.status}", response.status.value))
            }
        } catch (e: SerializationException) {
            println("Error de serialización: ${e.message}")
            emit(ApiResult.Error("Error al procesar la respuesta del servidor: ${e.message}"))
        } catch (e: ClientRequestException) {
            val errorMessage = when (e.response.status) {
                HttpStatusCode.NotFound -> "No se encontraron productos en esta categoría"
                else -> "Error de conexión: ${e.message}"
            }
            emit(ApiResult.Error(errorMessage, e.response.status.value))
        } catch (e: Exception) {
            println("Error inesperado: ${e::class.simpleName} - ${e.message}")
            e.printStackTrace()
            emit(ApiResult.Error("Error inesperado: ${e.message}"))
        }
    }

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
                val products = productResponse.getProducts()

                emit(ApiResult.Success(products))
            } else {
                emit(ApiResult.Error("Error en la respuesta del servidor: ${response.status}", response.status.value))
            }
        } catch (e: SerializationException) {
            println("Error de serialización: ${e.message}")
            emit(ApiResult.Error("Error al procesar la respuesta del servidor: ${e.message}"))
        } catch (e: ClientRequestException) {
            val errorMessage = when (e.response.status) {
                HttpStatusCode.Unauthorized -> "No autorizado. Por favor, inicie sesión nuevamente."
                HttpStatusCode.NotFound -> "No se encontraron productos con este estado"
                else -> "Error de conexión: ${e.message}"
            }
            emit(ApiResult.Error(errorMessage, e.response.status.value))
        } catch (e: Exception) {
            println("Error inesperado: ${e::class.simpleName} - ${e.message}")
            e.printStackTrace()
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
                val products = productResponse.getProducts()

                emit(ApiResult.Success(products))
            } else {
                emit(ApiResult.Error("Error en la respuesta del servidor: ${response.status}", response.status.value))
            }
        } catch (e: SerializationException) {
            println("Error de serialización: ${e.message}")
            emit(ApiResult.Error("Error al procesar la respuesta del servidor: ${e.message}"))
        } catch (e: ClientRequestException) {
            val errorMessage = when (e.response.status) {
                HttpStatusCode.NotFound -> "No se encontraron productos destacados"
                else -> "Error de conexión: ${e.message}"
            }
            emit(ApiResult.Error(errorMessage, e.response.status.value))
        } catch (e: Exception) {
            println("Error inesperado: ${e::class.simpleName} - ${e.message}")
            e.printStackTrace()
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
                val products = productResponse.getProducts()

                emit(ApiResult.Success(products))
            } else {
                emit(ApiResult.Error("Error en la respuesta del servidor: ${response.status}", response.status.value))
            }
        } catch (e: SerializationException) {
            println("Error de serialización: ${e.message}")
            emit(ApiResult.Error("Error al procesar la respuesta del servidor: ${e.message}"))
        } catch (e: ClientRequestException) {
            val errorMessage = when (e.response.status) {
                HttpStatusCode.NotFound -> "No se encontraron productos deseados"
                else -> "Error de conexión: ${e.message}"
            }
            emit(ApiResult.Error(errorMessage, e.response.status.value))
        } catch (e: Exception) {
            println("Error inesperado: ${e::class.simpleName} - ${e.message}")
            e.printStackTrace()
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
                val products = productResponse.getProducts()

                emit(ApiResult.Success(products))
            } else {
                emit(ApiResult.Error("Error en la respuesta del servidor: ${response.status}", response.status.value))
            }
        } catch (e: SerializationException) {
            println("Error de serialización: ${e.message}")
            emit(ApiResult.Error("Error al procesar la respuesta del servidor: ${e.message}"))
        } catch (e: ClientRequestException) {
            val errorMessage = when (e.response.status) {
                HttpStatusCode.NotFound -> "No se encontraron productos recientes"
                else -> "Error de conexión: ${e.message}"
            }
            emit(ApiResult.Error(errorMessage, e.response.status.value))
        } catch (e: Exception) {
            println("Error inesperado: ${e::class.simpleName} - ${e.message}")
            e.printStackTrace()
            emit(ApiResult.Error("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun addProduct(product: ProductDto): Flow<ApiResult<ProductDto>> = flow {
        emit(ApiResult.Loading)
        try {
            // Crear un ProductDto modificado, similar al que recibimos del servidor
            val productToSend = product.copy(
                id = 0L, // El id será asignado por el servidor
                variants = emptyList(),
                images = emptyList(),
                salesCount = 0,
                wishCount = 0,
                createdAt = null
            )

            val response = httpClient.post("$baseUrl/products/add") {
                headers {
                    authHeader?.let { header("Authorization", it) }
                    contentType(ContentType.Application.Json)
                    accept(ContentType.Application.Json)
                }
                setBody(productToSend)
            }

            if (response.status.isSuccess()) {
                val productResponse: ProductResponse = response.body()

                // Usar la nueva función getProduct para extraer el producto
                val newProduct = productResponse.getProduct()

                if (newProduct != null) {
                    emit(ApiResult.Success(newProduct))
                } else {
                    emit(ApiResult.Error("No se pudo crear el producto: ${productResponse.message}"))
                }
            } else {
                emit(ApiResult.Error("Error en la respuesta del servidor: ${response.status}", response.status.value))
            }
        } catch (e: SerializationException) {
            // Log detallado para errores de serialización
            println("Error de serialización: ${e.message}")
            emit(ApiResult.Error("Error al procesar la respuesta del servidor: ${e.message}"))
        } catch (e: ClientRequestException) {
            val errorMessage = when (e.response.status) {
                HttpStatusCode.Unauthorized -> "No autorizado. Por favor, inicie sesión nuevamente."
                HttpStatusCode.Conflict -> "Un producto con este nombre y marca ya existe"
                else -> "Error de conexión: ${e.message}"
            }
            emit(ApiResult.Error(errorMessage, e.response.status.value))
        } catch (e: Exception) {
            // Log para errores generales
            println("Error inesperado: ${e::class.simpleName} - ${e.message}")
            e.printStackTrace()
            emit(ApiResult.Error("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun updateProduct(product: ProductDto): Flow<ApiResult<ProductDto>> = flow {
        emit(ApiResult.Loading)
        try {
            // Enviar directamente el ProductDto, sin convertirlo a un mapa
            val response = httpClient.put("$baseUrl/products/product/${product.id}/update") {
                headers {
                    authHeader?.let { header("Authorization", it) }
                    contentType(ContentType.Application.Json)
                    accept(ContentType.Application.Json)
                }
                setBody(product)
            }

            if (response.status.isSuccess()) {
                val productResponse: ProductResponse = response.body()

                val updatedProduct = productResponse.getProduct()
                if (updatedProduct != null) {
                    emit(ApiResult.Success(updatedProduct))
                } else {
                    emit(ApiResult.Error("No se pudo actualizar el producto: ${productResponse.message}"))
                }
            } else {
                emit(ApiResult.Error("Error en la respuesta del servidor: ${response.status}", response.status.value))
            }
        } catch (e: SerializationException) {
            println("Error de serialización: ${e.message}")
            emit(ApiResult.Error("Error al procesar la respuesta del servidor: ${e.message}"))
        } catch (e: ClientRequestException) {
            val errorMessage = when (e.response.status) {
                HttpStatusCode.Unauthorized -> "No autorizado. Por favor, inicie sesión nuevamente."
                HttpStatusCode.NotFound -> "Producto no encontrado"
                else -> "Error de conexión: ${e.message}"
            }
            emit(ApiResult.Error(errorMessage, e.response.status.value))
        } catch (e: Exception) {
            println("Error inesperado: ${e::class.simpleName} - ${e.message}")
            e.printStackTrace()
            emit(ApiResult.Error("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun deleteProduct(productId: Long): Flow<ApiResult<Boolean>> = flow {
        emit(ApiResult.Loading)
        try {
            val token = tokenProvider()
            println("DEBUG: Token for delete operation: ${token?.take(10) ?: "null"}")

            if (token.isNullOrBlank()) {
                println("DEBUG: Authentication token not available")
                emit(ApiResult.Error("Authentication token not available. Please login again.", 401))
                return@flow
            }

            println("DEBUG: Sending delete request for product ID: $productId")
            val response = httpClient.delete("$baseUrl/products/product/$productId/delete") {
                headers {
                    header("Authorization", "Bearer $token")
                    accept(ContentType.Application.Json)
                }
            }

            println("DEBUG: Delete response status: ${response.status}")

            if (response.status.isSuccess()) {
                println("DEBUG: Product deletion successful")
                emit(ApiResult.Success(true))
            } else {
                val errorText = response.bodyAsText()
                println("DEBUG: Error response body: $errorText")

                when (response.status) {
                    HttpStatusCode.Forbidden -> {
                        emit(ApiResult.Error("You do not have permission to delete this product. Only admin users can delete products.", 403))
                    }
                    else -> {
                        emit(ApiResult.Error("Server error: ${response.status} - $errorText", response.status.value))
                    }
                }
            }
        } catch (e: ClientRequestException) {
            val errorMessage = when (e.response.status) {
                HttpStatusCode.Unauthorized -> "Not authorized. Please login again with an admin account."
                HttpStatusCode.Forbidden -> "You do not have permission to delete products. Admin privileges required."
                HttpStatusCode.NotFound -> "Product not found"
                else -> "Connection error: ${e.message}"
            }
            println("DEBUG: ClientRequestException: $errorMessage")
            emit(ApiResult.Error(errorMessage, e.response.status.value))
        } catch (e: Exception) {
            println("DEBUG: Exception in deleteProduct: ${e::class.simpleName} - ${e.message}")
            e.printStackTrace()
            emit(ApiResult.Error("Unexpected error: ${e.message}"))
        }
    }

    override suspend fun addProductVariant(productId: Long, variant: VariantDto): Flow<ApiResult<VariantDto>> = flow {
        emit(ApiResult.Loading)
        try {
            val response = httpClient.post("$baseUrl/variants/variant/add?productId=$productId") {
                headers {
                    authHeader?.let { header("Authorization", it) }
                    contentType(ContentType.Application.Json)
                    accept(ContentType.Application.Json)
                }
                setBody(variant)
            }

            if (response.status.isSuccess()) {
                val variantResponse: VariantResponse = response.body()
                val data = variantResponse.data

                if (data != null) {
                    emit(ApiResult.Success(data))
                } else {
                    emit(ApiResult.Error("Error al añadir la variante: ${variantResponse.message}"))
                }
            } else {
                val errorText = response.bodyAsText()
                emit(ApiResult.Error("Error del servidor: ${response.status} - $errorText", response.status.value))
            }
        } catch (e: SerializationException) {
            emit(ApiResult.Error("Error de formato: ${e.message}"))
        } catch (e: ClientRequestException) {
            val errorMessage = when (e.response.status) {
                HttpStatusCode.Unauthorized -> "No autorizado. Por favor, inicie sesión nuevamente."
                HttpStatusCode.Conflict -> "Ya existe una variante con este nombre para este producto"
                else -> "Error de conexión: ${e.message}"
            }
            emit(ApiResult.Error(errorMessage, e.response.status.value))
        } catch (e: Exception) {
            emit(ApiResult.Error("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun updateProductVariant(productId: Long, variant: VariantDto): Flow<ApiResult<VariantDto>> = flow {
        emit(ApiResult.Loading)
        try {
            val response = httpClient.put("$baseUrl/variants/variant/${variant.id}/update") {
                headers {
                    authHeader?.let { header("Authorization", it) }
                    contentType(ContentType.Application.Json)
                    accept(ContentType.Application.Json)
                }
                setBody(variant)
            }

            if (response.status.isSuccess()) {
                val variantResponse: VariantResponse = response.body()
                val data = variantResponse.data

                if (data != null) {
                    emit(ApiResult.Success(data))
                } else {
                    emit(ApiResult.Error("Error al actualizar la variante: ${variantResponse.message}"))
                }
            } else {
                val errorText = response.bodyAsText()
                emit(ApiResult.Error("Error del servidor: ${response.status} - $errorText", response.status.value))
            }
        } catch (e: SerializationException) {
            emit(ApiResult.Error("Error de formato: ${e.message}"))
        } catch (e: ClientRequestException) {
            val errorMessage = when (e.response.status) {
                HttpStatusCode.Unauthorized -> "No autorizado. Por favor, inicie sesión nuevamente."
                HttpStatusCode.NotFound -> "No se encontró la variante"
                HttpStatusCode.Conflict -> "Ya existe una variante con este nombre para este producto"
                else -> "Error de conexión: ${e.message}"
            }
            emit(ApiResult.Error(errorMessage, e.response.status.value))
        } catch (e: Exception) {
            emit(ApiResult.Error("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun deleteProductVariant(productId: Long, variantId: Long): Flow<ApiResult<Boolean>> = flow {
        emit(ApiResult.Loading)
        try {
            val response = httpClient.delete("$baseUrl/variants/variant/$variantId/delete") {
                headers {
                    authHeader?.let { header("Authorization", it) }
                    accept(ContentType.Application.Json)
                }
            }

            if (response.status.isSuccess()) {
                emit(ApiResult.Success(true))
            } else {
                val errorText = response.bodyAsText()
                emit(ApiResult.Error("Error del servidor: ${response.status} - $errorText", response.status.value))
            }
        } catch (e: ClientRequestException) {
            val errorMessage = when (e.response.status) {
                HttpStatusCode.Unauthorized -> "No autorizado. Por favor, inicie sesión nuevamente."
                HttpStatusCode.NotFound -> "No se encontró la variante"
                else -> "Error de conexión: ${e.message}"
            }
            emit(ApiResult.Error(errorMessage, e.response.status.value))
        } catch (e: Exception) {
            emit(ApiResult.Error("Error inesperado: ${e.message}"))
        }
    }

}