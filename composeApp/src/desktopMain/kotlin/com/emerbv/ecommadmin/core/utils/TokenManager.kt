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
        println("Clearing session - Before: Token=${getToken()?.take(10) ?: "null"}, UserId=${getUserId()}")

        // Use direct removal with clear keys
        settings.remove(KEY_TOKEN)
        settings.remove(KEY_USER_ID)
        settings.remove(KEY_LAST_ACTIVITY)

        // Try to force settings to save immediately if possible
        try {
            // This approach works with most Settings implementations
            // without requiring explicit type checking
            val settingsClass = settings.javaClass
            val flushMethod = settingsClass.methods.find { it.name == "flush" || it.name == "commit" }
            flushMethod?.invoke(settings)
        } catch (e: Exception) {
            println("Info: No flush/commit method available: ${e.message}")
            // Normal for some implementations, not an error
        }

        // Verification
        if (settings.hasKey(KEY_TOKEN) || settings.hasKey(KEY_USER_ID)) {
            println("WARNING: Failed to remove session keys, attempting aggressive cleanup")
            try {
                // More aggressive approach - clear everything if specific keys failed
                settings.clear()
            } catch (e: Exception) {
                println("ERROR: Even aggressive cleanup failed: ${e.message}")
            }
        }

        println("Clearing session - After: Token=${getToken()?.take(10) ?: "null"}, UserId=${getUserId()}")
    }

    fun isLoggedIn(): Boolean = getToken() != null && getUserId() != null

    // Funciones para gestionar el timeout por inactividad
    fun updateLastActivityTimestamp() {
        val timestamp = System.currentTimeMillis()
        settings.putLong(KEY_LAST_ACTIVITY, timestamp)
        println("Última actividad actualizada: ${java.util.Date(timestamp)}")
    }

    fun getLastActivityTimestamp(): Long {
        val timestamp = settings.getLong(KEY_LAST_ACTIVITY, System.currentTimeMillis())
        println("Última actividad registrada: ${java.util.Date(timestamp)}")
        return timestamp
    }

    fun hasSessionTimedOut(timeoutMillis: Long): Boolean {
        if (!isLoggedIn()) return false

        val lastActivity = getLastActivityTimestamp()
        val currentTime = System.currentTimeMillis()
        val timeSinceLastActivity = currentTime - lastActivity
        val hasTimedOut = timeSinceLastActivity > timeoutMillis

        println("Time since last activity: ${timeSinceLastActivity / 1000} seconds")
        return hasTimedOut
    }

    // Helper para depuración
    fun printTokenStatus() {
        println("TokenManager status: logged in = ${isLoggedIn()}, token = ${getToken()?.take(10) ?: "null"}")
    }

    // Extensión de Settings para facilitar el acceso a valores String
    private fun Settings.getStringOrNull(key: String): String? {
        return if (this.hasKey(key)) this.getString(key, "") else null
    }

    private fun Settings.getLongOrNull(key: String): Long? {
        return if (this.hasKey(key)) this.getLong(key, 0) else null
    }

}