package com.emerbv.ecommadmin.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Componente Chip personalizado que muestra una etiqueta seleccionable con iconos opcionales
 *
 * @param onClick Callback cuando se hace clic en el chip
 * @param modifier Modificador para personalizar el aspecto
 * @param selected Si el chip está seleccionado
 * @param enabled Si el chip está habilitado
 * @param leadingIcon Icono opcional que aparece antes del contenido
 * @param trailingIcon Icono opcional que aparece después del contenido
 * @param border Borde opcional alrededor del chip
 * @param backgroundColor Color de fondo del chip
 * @param selectedBackgroundColor Color de fondo cuando el chip está seleccionado
 * @param contentColor Color del contenido del chip
 * @param selectedContentColor Color del contenido cuando el chip está seleccionado
 * @param content Contenido del chip (generalmente texto)
 */
@Composable
fun Chip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    border: BorderStroke? = BorderStroke(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.12f)),
    backgroundColor: Color = MaterialTheme.colors.surface,
    selectedBackgroundColor: Color = MaterialTheme.colors.primary.copy(alpha = 0.12f),
    contentColor: Color = MaterialTheme.colors.onSurface,
    selectedContentColor: Color = MaterialTheme.colors.primary,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        color = if (selected) selectedBackgroundColor else backgroundColor,
        contentColor = if (selected) selectedContentColor else contentColor,
        shape = RoundedCornerShape(16.dp),
        border = if (!selected) border else null,
        elevation = if (selected) 0.dp else 0.dp,
        modifier = modifier.height(32.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable(
                    onClick = onClick,
                    enabled = enabled
                )
                .padding(
                    start = if (leadingIcon != null) 8.dp else 12.dp,
                    end = if (trailingIcon != null) 8.dp else 12.dp
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(Modifier.width(4.dp))
            }

            content()

            if (trailingIcon != null) {
                Spacer(Modifier.width(4.dp))
                trailingIcon()
            }
        }
    }
}

/**
 * Versión simplificada del componente Chip que muestra solo texto
 *
 * @param onClick Callback cuando se hace clic en el chip
 * @param text Texto que se mostrará en el chip
 * @param modifier Modificador para personalizar el aspecto
 * @param selected Si el chip está seleccionado
 * @param enabled Si el chip está habilitado
 * @param leadingIcon Icono opcional que aparece antes del texto
 * @param trailingIcon Icono opcional que aparece después del texto
 */
@Composable
fun TextChip(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Chip(
        onClick = onClick,
        modifier = modifier,
        selected = selected,
        enabled = enabled,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon
    ) {
        Text(text = text, style = MaterialTheme.typography.body2)
    }
}