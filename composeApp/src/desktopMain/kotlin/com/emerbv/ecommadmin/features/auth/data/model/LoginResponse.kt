package com.emerbv.ecommadmin.features.auth.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val message: String,
    val data: JwtResponse?
)
