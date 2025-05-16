package com.emerbv.ecommadmin.core.utils

import com.emerbv.ecommadmin.features.auth.data.model.JwtResponse
import com.russhwolf.settings.Settings

class TokenManager(private val settings: Settings) {
    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
    }

    fun saveUserSession(jwtResponse: JwtResponse) {
        settings.putString(KEY_TOKEN, jwtResponse.token)
        settings.putString(KEY_USER_ID, jwtResponse.id.toString())
        // Log para depuración
        println("Token saved: ${jwtResponse.token.take(15)}...")
    }

    fun getToken(): String? {
        val token = settings.getStringOrNull(KEY_TOKEN)
        // Log para depuración
        println("Retrieved token: ${token?.take(15)}...")
        return token
    }

    fun getUserId(): Long? = settings.getStringOrNull(KEY_USER_ID)?.toLongOrNull()

    fun clearSession() {
        settings.remove(KEY_TOKEN)
        settings.remove(KEY_USER_ID)
        println("Session cleared")
    }

    fun isLoggedIn(): Boolean = getToken() != null && getUserId() != null

    // Helper para depuración
    fun printTokenStatus() {
        println("TokenManager status: logged in = ${isLoggedIn()}, token = ${getToken()?.take(10) ?: "null"}")
    }

    // Extensión de Settings para facilitar el acceso a valores String
    private fun Settings.getStringOrNull(key: String): String? {
        return if (this.hasKey(key)) this.getString(key, "") else null
    }
}