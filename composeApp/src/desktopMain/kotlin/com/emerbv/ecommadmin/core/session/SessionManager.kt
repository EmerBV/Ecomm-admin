package com.emerbv.ecommadmin.core.session

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import com.emerbv.ecommadmin.core.navigation.NavigationState
import com.emerbv.ecommadmin.core.navigation.Screen
import com.emerbv.ecommadmin.core.utils.TokenManager
import com.emerbv.ecommadmin.core.datastore.CredentialsDataStore

/**
 * Clase de sesión centralizada para manejar la lógica de autenticación y actividad del usuario
 */
class SessionManager(
    private val tokenManager: TokenManager,
    private val navigationState: NavigationState,
    private val credentialsDataStore: CredentialsDataStore
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

    fun logout() {
        println("Logout initiated - Session status before: isLoggedIn=${isLoggedIn()}")

        // Clear token first
        tokenManager.clearSession()

        // Also clear any saved credentials that might cause auto-login
        credentialsDataStore.clearCredentials()

        // Double-check token is cleared
        if (tokenManager.isLoggedIn()) {
            println("ERROR: Session still active after clearing, forcing cleanup")
            tokenManager.clearSession()
        }

        println("Token cleared - Session status after: isLoggedIn=${tokenManager.isLoggedIn()}")

        // Force navigation to Login screen
        navigationState.navigateTo(Screen.Login)

        println("Navigation to Login completed")
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