package com.emerbv.ecommadmin.core.utils

import com.emerbv.ecommadmin.features.auth.data.model.JwtResponse
import com.russhwolf.settings.Settings

class TokenManager(private val settings: Settings) {
    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_LAST_ACTIVITY = "last_activity_timestamp"
    }

    fun saveUserSession(jwtResponse: JwtResponse) {
        settings.putString(KEY_TOKEN, jwtResponse.token)
        settings.putString(KEY_USER_ID, jwtResponse.id.toString())
        // Actualizar timestamp de última actividad
        updateLastActivityTimestamp()
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
        // Limpiar todos los datos de sesión
        settings.remove(KEY_TOKEN)
        settings.remove(KEY_USER_ID)
        settings.remove(KEY_LAST_ACTIVITY)
        println("Session cleared completely")
    }

    fun isLoggedIn(): Boolean = getToken() != null && getUserId() != null

    // Funciones para gestionar el timeout por inactividad
    fun updateLastActivityTimestamp() {
        settings.putLong(KEY_LAST_ACTIVITY, System.currentTimeMillis())
        println("Last activity timestamp updated: ${System.currentTimeMillis()}")
    }

    fun getLastActivityTimestamp(): Long {
        return settings.getLong(KEY_LAST_ACTIVITY, System.currentTimeMillis())
    }

    fun hasSessionTimedOut(timeoutMillis: Long): Boolean {
        if (!isLoggedIn()) return false

        val lastActivity = getLastActivityTimestamp()
        val currentTime = System.currentTimeMillis()
        val timeSinceLastActivity = currentTime - lastActivity

        println("Time since last activity: ${timeSinceLastActivity / 1000} seconds")
        return timeSinceLastActivity > timeoutMillis
    }

    // Helper para depuración
    fun printTokenStatus() {
        println("TokenManager status: logged in = ${isLoggedIn()}, token = ${getToken()?.take(10) ?: "null"}")
    }

    // Extensión de Settings para facilitar el acceso a valores String
    private fun Settings.getStringOrNull(key: String): String? {
        return if (this.hasKey(key)) this.getString(key, "") else null
    }
}