package com.emerbv.ecommadmin.features.categories.data.model

import kotlinx.serialization.Serializable

/**
 * Modelo de respuesta para las peticiones a la API de categorías
 */
@Serializable
data class CategoryResponse(
    val message: String,
    val data: List<CategoryDto>? = null
)

/**
 * DTO para representar una categoría
 */
@Serializable
data class CategoryDto(
    val id: Long,
    val name: String,
    val imageFileName: String? = null,
    val imageFileType: String? = null,
    val imageDownloadUrl: String? = null
)