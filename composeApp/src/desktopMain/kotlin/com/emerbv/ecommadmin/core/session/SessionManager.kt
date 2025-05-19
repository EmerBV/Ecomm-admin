package com.emerbv.ecommadmin.core.session

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import com.emerbv.ecommadmin.core.navigation.NavigationState
import com.emerbv.ecommadmin.core.navigation.Screen
import com.emerbv.ecommadmin.core.utils.TokenManager
import com.emerbv.ecommadmin.core.datastore.CredentialsDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Clase de sesión centralizada para manejar la lógica de autenticación y actividad del usuario
 */
class SessionManager(
    private val tokenManager: TokenManager,
    private val navigationState: NavigationState,
    private val credentialsDataStore: CredentialsDataStore
) {
    // Estado para indicar si el usuario acaba de hacer logout
    private val _justLoggedOut = MutableStateFlow(false)
    val justLoggedOut: StateFlow<Boolean> = _justLoggedOut.asStateFlow()

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
     * Realiza un logout completo, limpiando el token, credenciales y estados relacionados,
     * y navegando a la pantalla de login.
     */
    fun logout() {
        println("=================== LOGOUT SEQUENCE START ===================")
        println("SessionManager.logout() - Before: isLoggedIn=${isLoggedIn()}")
        println("TokenManager state - Token=${tokenManager.getToken()?.take(10) ?: "null"}")

        // Marcar que acabamos de hacer logout
        _justLoggedOut.value = true

        // Clear token first
        val tokenCleared = tokenManager.clearSession()

        // Also clear any saved credentials that might cause auto-login
        credentialsDataStore.clearCredentials()

        // Double-check token is cleared
        if (tokenManager.isLoggedIn()) {
            println("ERROR: Session still active after clearing, forcing cleanup")
            tokenManager.clearSession()
        }

        println("After token/credentials clear: isLoggedIn=${isLoggedIn()}")
        println("TokenManager state - Token=${tokenManager.getToken(forceReload = true)?.take(10) ?: "null"}")

        // Force navigation to Login screen
        resetNavigation()

        println("Navigation to Login completed")
        println("=================== LOGOUT SEQUENCE END ===================")
    }

    /**
     * Resetea el estado de navegación a la pantalla de login
     */
    private fun resetNavigation() {
        navigationState.resetToLogin()
    }

    /**
     * Verifica si el usuario está autenticado
     * @return True si hay un token activo
     */
    fun isLoggedIn(): Boolean {
        // Verificación directa del almacenamiento
        val currentToken = tokenManager.getToken(forceReload = true)
        val currentUserId = tokenManager.getUserId(forceReload = true)
        return currentToken != null && currentUserId != null
    }

    /**
     * Método para indicar que se ha completado un login exitoso
     */
    fun onLoginSuccess() {
        _justLoggedOut.value = false
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
    credentialsDataStore: CredentialsDataStore,
    content: @Composable () -> Unit
) {
    val sessionManager = remember {
        SessionManager(tokenManager, navigationState, credentialsDataStore)
    }

    CompositionLocalProvider(LocalSessionManager provides sessionManager) {
        content()
    }
}