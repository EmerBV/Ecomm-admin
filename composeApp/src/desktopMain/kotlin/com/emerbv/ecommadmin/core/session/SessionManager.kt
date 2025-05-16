package com.emerbv.ecommadmin.core.session

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import com.emerbv.ecommadmin.core.navigation.NavigationState
import com.emerbv.ecommadmin.core.navigation.Screen
import com.emerbv.ecommadmin.core.utils.TokenManager

/**
 * Clase de sesión centralizada para manejar la lógica de autenticación y actividad del usuario
 */
class SessionManager(
    private val tokenManager: TokenManager,
    private val navigationState: NavigationState
) {
    /**
     * Actualiza la marca de tiempo de la última actividad del usuario
     */
    fun updateActivity() {
        tokenManager.updateLastActivityTimestamp()
    }

    /**
     * Verifica si la sesión ha expirado por inactividad
     * @param timeoutMillis Tiempo de inactividad permitido en milisegundos
     * @return True si la sesión ha expirado
     */
    fun hasSessionTimedOut(timeoutMillis: Long): Boolean {
        return tokenManager.hasSessionTimedOut(timeoutMillis)
    }

    /**
     * Ejecuta el cierre de sesión completo: limpia el token y redirige al login
     */
    fun logout() {
        println("Logout iniciado. Estado previo: ${tokenManager.isLoggedIn()}")

        // Limpiar token
        tokenManager.clearSession()

        // Verificar que realmente se limpió
        if (tokenManager.isLoggedIn()) {
            println("ERROR: El token no se eliminó correctamente")
            // Intento más agresivo si el primer intento falló
            tokenManager.clearSession()
        }

        println("Estado post-limpieza: ${tokenManager.isLoggedIn()}")

        // Navegar explícitamente a la pantalla de login
        navigationState.navigateTo(Screen.Login)

        println("Navegación a login completada")
    }

    /**
     * Verifica si el usuario está autenticado
     * @return True si hay un token activo
     */
    fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedIn()
    }
}

/**
 * Proveedor de composición local para acceder al SessionManager desde cualquier Composable
 */
val LocalSessionManager = compositionLocalOf<SessionManager?> { null }

/**
 * Composable que proporciona un SessionManager a todo su contenido
 */
@Composable
fun SessionManagerProvider(
    tokenManager: TokenManager,
    navigationState: NavigationState,
    content: @Composable () -> Unit
) {
    val sessionManager = remember {
        SessionManager(tokenManager, navigationState)
    }

    CompositionLocalProvider(LocalSessionManager provides sessionManager) {
        content()
    }
}