package com.emerbv.ecommadmin.features.products.data.model

import com.emerbv.ecommadmin.features.categories.data.model.CategoryDto
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

@Serializable
data class ProductResponse(
    val message: String,
    val data: JsonElement? = null
) {
    // Función para extraer un solo producto
    fun getProduct(): ProductDto? {
        return when (data) {
            is JsonObject -> Json.decodeFromJsonElement<ProductDto>(data)
            is JsonArray -> {
                if (data.size > 0) {
                    Json.decodeFromJsonElement<ProductDto>(data[0])
                } else {
                    null
                }
            }

            else -> null
        }
    }

    // Función para extraer una lista de productos
    fun getProducts(): List<ProductDto> {
        return when (data) {
            is JsonArray -> Json.decodeFromJsonElement<List<ProductDto>>(data)
            is JsonObject -> listOf(Json.decodeFromJsonElement<ProductDto>(data))
            else -> emptyList()
        }
    }
}

@Serializable
data class ProductDto(
    val id: Long,
    val name: String,
    val brand: String,
    val price: Double,
    val inventory: Int,
    val description: String = "",
    val category: CategoryDto,
    val discountPercentage: Int = 0,
    val status: String = "IN_STOCK",
    val salesCount: Int = 0,
    val wishCount: Int = 0,
    val preOrder: Boolean = false,
    val createdAt: String? = null,
    val images: List<ImageDto>? = null,
    val variants: List<VariantDto>? = null
)


@Serializable
data class ImageDto(
    val id: Long,
    val fileName: String,
    val fileType: String? = null,
    val downloadUrl: String? = null
)

