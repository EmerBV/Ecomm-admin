package com.emerbv.ecommadmin.features.products.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponse(
    val message: String,
    val data: List<CategoryDto>? = null
)