package com.emerbv.ecommadmin.core.activity

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Modifier que detecta la actividad del usuario (clics, movimientos del ratón)
 * y llama al callback proporcionado.
 */
fun Modifier.detectUserActivity(onActivityDetected: () -> Unit): Modifier = composed {
    this.pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Main)
                when (event.type) {
                    PointerEventType.Press,
                    PointerEventType.Move,
                    PointerEventType.Release,
                    PointerEventType.Enter -> {
                        println("ACTIVIDAD DETECTADA: ${event.type}")
                        onActivityDetected()
                    }
                    else -> {
                        println("Evento ignorado: ${event.type}")
                    }
                }
            }
        }
    }
}