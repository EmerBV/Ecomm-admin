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

    fun getToken(forceReload: Boolean = false): String? {
        val token = if (forceReload) {
            // Leer directamente del almacenamiento, ignorando posibles cachés
            if (settings.hasKey(KEY_TOKEN)) settings.getString(KEY_TOKEN, "") else null
        } else {
            settings.getStringOrNull(KEY_TOKEN)
        }

        // Log para depuración
        println("Retrieved token: ${token?.take(15)}...")
        return token
    }

    fun getUserId(forceReload: Boolean = false): Long? =
        if (forceReload) {
            if (settings.hasKey(KEY_USER_ID))
                settings.getString(KEY_USER_ID, "").toLongOrNull()
            else null
        } else {
            settings.getStringOrNull(KEY_USER_ID)?.toLongOrNull()
        }

    fun clearSession(): Boolean {
        println("Clearing session - Before: Token=${getToken()?.take(10) ?: "null"}, UserId=${getUserId()}")

        // Eliminar token y datos de usuario
        settings.remove(KEY_TOKEN)
        settings.remove(KEY_USER_ID)
        settings.remove(KEY_LAST_ACTIVITY)

        // Intentar forzar guardado
        try {
            val settingsClass = settings.javaClass
            val flushMethod = settingsClass.methods.find { it.name == "flush" || it.name == "commit" }
            flushMethod?.invoke(settings)
        } catch (e: Exception) {
            println("Info: No flush/commit method available: ${e.message}")
            // Normal para algunas implementaciones
        }

        // Verificar si se eliminó
        val stillHasToken = settings.hasKey(KEY_TOKEN)
        val stillHasUserId = settings.hasKey(KEY_USER_ID)

        // Si aún existe alguna clave, intentar limpieza agresiva
        if (stillHasToken || stillHasUserId) {
            println("WARNING: Failed to remove session keys, attempting aggressive cleanup")
            try {
                // Enfoque más agresivo - limpiar todo si fallan las claves específicas
                settings.clear()
            } catch (e: Exception) {
                println("ERROR: Even aggressive cleanup failed: ${e.message}")
                return false
            }
        }

        println("Clearing session - After: Token=${getToken(forceReload = true)?.take(10) ?: "null"}, UserId=${getUserId(forceReload = true)}")

        // Verificación final
        return !settings.hasKey(KEY_TOKEN) && !settings.hasKey(KEY_USER_ID)
    }

    fun isLoggedIn(): Boolean {
        val hasToken = getToken() != null
        val hasUserId = getUserId() != null
        println("TokenManager.isLoggedIn() - hasToken=$hasToken, hasUserId=$hasUserId")
        return hasToken && hasUserId
    }

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