package com.emerbv.ecommadmin.features.auth.data.model

import kotlinx.serialization.Serializable

@Serializable
data class JwtResponse(
    val id: Long,
    val token: String
)
