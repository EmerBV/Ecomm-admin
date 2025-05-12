package com.emerbv.ecommadmin.features.auth.data.repository

import com.emerbv.ecommadmin.core.network.ApiResult
import com.emerbv.ecommadmin.features.auth.data.model.JwtResponse
import com.emerbv.ecommadmin.features.auth.data.model.LoginRequest
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(request: LoginRequest): Flow<ApiResult<JwtResponse>>
}