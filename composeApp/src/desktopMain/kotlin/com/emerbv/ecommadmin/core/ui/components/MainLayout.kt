package com.emerbv.ecommadmin.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emerbv.ecommadmin.core.session.LocalSessionManager
import com.emerbv.ecommadmin.features.dashboard.presentation.components.DashboardSidebar

@Composable
fun MainLayout(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    userName: String,
    onLogout: () -> Unit,
    title: String = "",
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    topBarActions: @Composable RowScope.() -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    content: @Composable () -> Unit
) {
    // Acceder al gestor de sesión desde el contexto de composición
    val sessionManager = LocalSessionManager.current
        ?: throw IllegalStateException("SessionManager no encontrado. Asegúrate de usar SessionManagerProvider.")

    Scaffold(
        topBar = {
            if (title.isNotEmpty() || showBackButton || topBarActions != {}) {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = if (showBackButton) {
                        {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    "Back"
                                )
                            }
                        }
                    } else null,
                    actions = topBarActions,
                    backgroundColor = MaterialTheme.colors.background,
                    elevation = 0.dp
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Row(Modifier.fillMaxSize()) {
            // Barra lateral
            DashboardSidebar(
                currentRoute = currentRoute,
                onNavigate = onNavigate,
                userName = userName,
                onLogout = { sessionManager.logout() }
            )

            // Contenido principal
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(paddingValues)
            ) {
                content()
            }
        }
    }
}