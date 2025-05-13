package com.emerbv.ecommadmin.features.products.data.model

import com.emerbv.ecommadmin.features.categories.data.model.CategoryDto
import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val message: String,
    val data: List<ProductDto>? = null
)

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

@Serializable
data class VariantDto(
    val id: Long,
    val name: String,
    val price: Double,
    val inventory: Int = 0
)
