package com.emerbv.ecommadmin.features.auth.domain

import com.emerbv.ecommadmin.core.network.ApiResult
import com.emerbv.ecommadmin.features.auth.data.model.JwtResponse
import com.emerbv.ecommadmin.features.auth.data.model.LoginRequest
import com.emerbv.ecommadmin.features.auth.data.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Flow<ApiResult<JwtResponse>> {
        return repository.login(LoginRequest(email, password))
    }
}