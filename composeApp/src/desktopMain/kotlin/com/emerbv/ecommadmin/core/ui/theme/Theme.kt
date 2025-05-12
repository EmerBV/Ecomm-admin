package com.emerbv.ecommadmin.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColors(
    primary = Color(0xFF1E88E5),
    primaryVariant = Color(0xFF005CB2),
    secondary = Color(0xFF00BCD4),
    secondaryVariant = Color(0xFF008BA3),
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    error = Color(0xFFB00020),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF121212),
    onSurface = Color(0xFF121212),
    onError = Color.White
)

private val DarkColors = darkColors(
    primary = Color(0xFF90CAF9),
    primaryVariant = Color(0xFF64B5F6),
    secondary = Color(0xFF80DEEA),
    secondaryVariant = Color(0xFF4DD0E1),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    error = Color(0xFFCF6679),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.Black
)

@Composable
fun EcommAdminTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}