package com.emerbv.ecommadmin.core.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.animateColorAsState

/**
 * Botón personalizado para cambiar entre tema claro y oscuro.
 *
 * @param isDarkMode Estado actual del tema (oscuro o claro)
 * @param onToggle Función a llamar cuando se cambia el tema
 * @param modifier Modificador para personalizar el aspecto
 */
@Composable
fun ThemeToggleButton(
    isDarkMode: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isDarkMode) Color(0xFF3C4043) else Color(0xFFF1F3F4),
        animationSpec = tween(300)
    )
    val contentColor by animateColorAsState(
        targetValue = if (isDarkMode) Color(0xFFE1E3E5) else Color(0xFF5F6368),
        animationSpec = tween(300)
    )
    val alignment by animateFloatAsState(
        targetValue = if (isDarkMode) 1f else 0f,
        animationSpec = tween(300)
    )

    Box(
        modifier = modifier
            .width(56.dp)
            .height(32.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.12f), CircleShape)
            .clickable { onToggle() },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .offset(x = 24.dp * alignment)
                .padding(4.dp)
                .clip(CircleShape)
                .background(contentColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                contentDescription = if (isDarkMode) "Modo oscuro" else "Modo claro",
                tint = backgroundColor,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}