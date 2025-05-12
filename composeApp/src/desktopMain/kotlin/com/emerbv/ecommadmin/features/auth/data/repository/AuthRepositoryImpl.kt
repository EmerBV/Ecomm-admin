package com.emerbv.ecommadmin.features.auth.data.repository

import com.emerbv.ecommadmin.core.network.ApiResult
import com.emerbv.ecommadmin.features.auth.data.model.JwtResponse
import com.emerbv.ecommadmin.features.auth.data.model.LoginRequest
import com.emerbv.ecommadmin.features.auth.data.model.LoginResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : AuthRepository {
    override suspend fun login(request: LoginRequest): Flow<ApiResult<JwtResponse>> = flow {
        emit(ApiResult.Loading)
        try {
            val response: LoginResponse = httpClient.post("$baseUrl/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            response.data?.let {
                emit(ApiResult.Success(it))
            } ?: emit(ApiResult.Error(response.message))
        } catch (e: ClientRequestException) {
            val errorMessage = when (e.response.status) {
                HttpStatusCode.Unauthorized -> "Credenciales incorrectas"
                else -> "Error de conexión: ${e.message}"
            }
            emit(ApiResult.Error(errorMessage, e.response.status.value))
        } catch (e: Exception) {
            emit(ApiResult.Error("Error inesperado: ${e.message}"))
        }
    }
}