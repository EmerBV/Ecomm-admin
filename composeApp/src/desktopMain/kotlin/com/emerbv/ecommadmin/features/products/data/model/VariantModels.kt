package com.emerbv.ecommadmin.features.products.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VariantResponse(
    val message: String,
    val data: VariantDto? = null
)

@Serializable
data class VariantDto(
    val id: Long,
    val name: String,
    val price: Double,
    val inventory: Int = 0
)