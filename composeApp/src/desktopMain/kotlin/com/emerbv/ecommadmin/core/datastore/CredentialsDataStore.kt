package com.emerbv.ecommadmin.core.datastore

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

/**
 * Clase para manejar el almacenamiento de credenciales y preferencias del usuario.
 * Utiliza multiplatform-settings para abstraer el almacenamiento específico de cada plataforma.
 */
class CredentialsDataStore(private val settings: Settings) {

    // Claves para acceder a las preferencias
    companion object {
        private const val KEY_EMAIL = "email"
        private const val KEY_PASSWORD = "password"
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_FONT_SIZE = "font_size"
    }

    /**
     * Guarda las credenciales del usuario si "recordarme" está activado.
     */
    fun saveCredentials(email: String, password: String, rememberMe: Boolean) {
        settings[KEY_REMEMBER_ME] = rememberMe

        if (rememberMe) {
            settings[KEY_EMAIL] = email
            settings[KEY_PASSWORD] = password
        } else {
            clearCredentials()
        }
    }

    /**
     * Guarda la información de sesión del usuario.
     */
    fun saveSession(token: String, userId: Long) {
        settings[KEY_TOKEN] = token
        settings[KEY_USER_ID] = userId.toString()
    }

    /**
     * Verifica si hay credenciales guardadas.
     */
    fun hasCredentials(): Boolean {
        return settings[KEY_REMEMBER_ME, false] &&
                settings.getStringOrNull(KEY_EMAIL) != null &&
                settings.getStringOrNull(KEY_PASSWORD) != null
    }

    /**
     * Obtiene el email guardado.
     */
    fun getEmail(): String? = settings.getStringOrNull(KEY_EMAIL)

    /**
     * Obtiene la contraseña guardada.
     */
    fun getPassword(): String? = settings.getStringOrNull(KEY_PASSWORD)

    /**
     * Obtiene el estado de "recordarme".
     */
    fun getRememberMe(): Boolean = settings[KEY_REMEMBER_ME, false]

    /**
     * Obtiene el token JWT guardado.
     */
    fun getToken(): String? = settings.getStringOrNull(KEY_TOKEN)

    /**
     * Obtiene el ID de usuario guardado.
     */
    fun getUserId(): Long? = settings.getStringOrNull(KEY_USER_ID)?.toLongOrNull()

    /**
     * Limpia las credenciales guardadas.
     */
    fun clearCredentials() {
        settings.remove(KEY_EMAIL)
        settings.remove(KEY_PASSWORD)
        settings.remove(KEY_REMEMBER_ME)
    }

    /**
     * Limpia la información de sesión.
     */
    fun clearSession() {
        settings.remove(KEY_TOKEN)
        settings.remove(KEY_USER_ID)
    }

    /**
     * Guarda la preferencia de tema oscuro.
     */
    fun setDarkMode(isDarkMode: Boolean) {
        settings[KEY_DARK_MODE] = isDarkMode
    }

    /**
     * Obtiene la preferencia de tema oscuro.
     */
    fun isDarkMode(): Boolean = settings[KEY_DARK_MODE, false]

    /**
     * Guarda la preferencia de tamaño de fuente.
     */
    fun setFontSize(size: Float) {
        settings[KEY_FONT_SIZE] = size
    }

    /**
     * Obtiene la preferencia de tamaño de fuente.
     */
    fun getFontSize(): Float = settings[KEY_FONT_SIZE, 1.0f]

    /**
     * Método de extensión para obtener un String o null.
     */
    private fun Settings.getStringOrNull(key: String): String? {
        return if (this.hasKey(key)) this.getString(key, "") else null
    }
}