package com.emerbv.ecommadmin.core.session

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.emerbv.ecommadmin.core.activity.detectUserActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.concurrent.TimeUnit

/**
 * Tiempo de sesión por defecto: 30 minutos
 */
private val DEFAULT_SESSION_TIMEOUT = TimeUnit.MINUTES.toMillis(1)

/**
 * Intervalo de verificación de timeout: 1 minuto
 */
private val DEFAULT_CHECK_INTERVAL = TimeUnit.MINUTES.toMillis(1)

/**
 * Un contenedor que aplica la detección de actividad y monitoreo de timeout de sesión
 * Este componente debe envolver las pantallas que requieren autenticación
 */
@Composable
fun SessionScreen(
    timeoutMillis: Long = DEFAULT_SESSION_TIMEOUT,
    content: @Composable () -> Unit
) {
    // Obtener el manejador de sesión del contexto de composición
    val sessionManager = LocalSessionManager.current
        ?: throw IllegalStateException("SessionManager no encontrado. Asegúrate de usar SessionManagerProvider en tu jerarquía de composición.")

    println("SessionScreen iniciada. Tiempo de inactividad configurado: ${timeoutMillis/1000} segundos")

    // Actualizar actividad al cargar la pantalla
    LaunchedEffect(Unit) {
        println("Actualizando marca de tiempo inicial")
        sessionManager.updateActivity()
    }

    // Efecto para verificar periódicamente si la sesión ha expirado
    LaunchedEffect(Unit) {
        while (isActive) {
            //delay(DEFAULT_CHECK_INTERVAL)

            delay(10000) // Revisar cada 10 segundos para debugging (en producción usar DEFAULT_CHECK_INTERVAL)

            val isLoggedIn = sessionManager.isLoggedIn()
            val hasTimedOut = sessionManager.hasSessionTimedOut(timeoutMillis)

            println("Verificación de timeout: isLoggedIn=$isLoggedIn, hasTimedOut=$hasTimedOut")

            if (isLoggedIn && hasTimedOut) {
                println("TIMEOUT DETECTADO: Cerrando sesión automáticamente")
                sessionManager.logout()
                break
            }
        }
    }

    // Efecto para actualizar la actividad cuando se muestra esta pantalla
    LaunchedEffect(Unit) {
        sessionManager.updateActivity()
    }

    // Aplicar detector de actividad a todo el contenido
    Box(
        modifier = Modifier
            .fillMaxSize()
            .detectUserActivity { sessionManager.updateActivity() }
    ) {
        content()
    }
}