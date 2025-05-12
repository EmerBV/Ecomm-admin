package com.emerbv.ecommadmin.features.auth.presentation

import com.emerbv.ecommadmin.features.auth.data.model.JwtResponse

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null,
    val jwtResponse: JwtResponse? = null
)
