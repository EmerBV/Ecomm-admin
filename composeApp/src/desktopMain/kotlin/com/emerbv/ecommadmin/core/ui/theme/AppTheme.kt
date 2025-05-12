package com.emerbv.ecommadmin.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import com.emerbv.ecommadmin.core.datastore.CredentialsDataStore

/**
 * Composable que proporciona un tema para la aplicación con soporte para modo oscuro.
 *
 * @param darkTheme Indica si se debe usar el tema oscuro
 * @param content Contenido a mostrar con el tema aplicado
 */
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Usamos EcommAdminTheme que ya está definido
    EcommAdminTheme(darkTheme = darkTheme, content = content)
}

/**
 * Clase para gestionar el estado del tema de la aplicación.
 */
class ThemeState(
    private val credentialsDataStore: CredentialsDataStore,
    initialDarkMode: Boolean = false,
    initialFontScale: Float = 1.0f
) {
    // Estado para modo oscuro
    private val _isDarkMode = mutableStateOf(initialDarkMode)
    val isDarkMode: State<Boolean> = _isDarkMode

    // Estado para escala de fuente
    private val _fontScale = mutableStateOf(initialFontScale)
    val fontScale: State<Float> = _fontScale

    /**
     * Cambia entre modo claro y oscuro.
     */
    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
        credentialsDataStore.setDarkMode(_isDarkMode.value)
    }

    /**
     * Establece el modo oscuro.
     */
    fun setDarkMode(darkMode: Boolean) {
        _isDarkMode.value = darkMode
        credentialsDataStore.setDarkMode(darkMode)
    }

    /**
     * Aumenta el tamaño de la fuente.
     */
    fun increaseFontSize() {
        if (_fontScale.value < 1.5f) {
            _fontScale.value += 0.1f
            credentialsDataStore.setFontSize(_fontScale.value)
        }
    }

    /**
     * Disminuye el tamaño de la fuente.
     */
    fun decreaseFontSize() {
        if (_fontScale.value > 0.8f) {
            _fontScale.value -= 0.1f
            credentialsDataStore.setFontSize(_fontScale.value)
        }
    }

    /**
     * Restablece el tamaño de la fuente al valor predeterminado.
     */
    fun resetFontSize() {
        _fontScale.value = 1.0f
        credentialsDataStore.setFontSize(1.0f)
    }
}

/**
 * Composable para recordar el estado del tema de la aplicación.
 */
@Composable
fun rememberThemeState(
    credentialsDataStore: CredentialsDataStore
): ThemeState {
    return remember {
        ThemeState(
            credentialsDataStore = credentialsDataStore,
            initialDarkMode = credentialsDataStore.isDarkMode(),
            initialFontScale = credentialsDataStore.getFontSize()
        )
    }
}
