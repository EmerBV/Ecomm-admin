package com.emerbv.ecommadmin.features.dashboard.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.emerbv.ecommadmin.core.navigation.NavigationState
import com.emerbv.ecommadmin.core.navigation.Screen
import com.emerbv.ecommadmin.core.ui.theme.EcommAdminTheme
import com.emerbv.ecommadmin.core.utils.TokenManager
import com.emerbv.ecommadmin.features.auth.data.model.JwtResponse
import com.emerbv.ecommadmin.features.dashboard.presentation.components.DashboardSidebar
import com.emerbv.ecommadmin.features.dashboard.presentation.components.KpiCardsRow
import com.emerbv.ecommadmin.features.dashboard.presentation.components.PopularProductsList
import com.emerbv.ecommadmin.features.dashboard.presentation.components.LowInventoryAlertList
import com.emerbv.ecommadmin.features.dashboard.presentation.components.InventoryStatusRow

@Composable
fun DashboardScreen(
    userData: JwtResponse,
    navigationState: NavigationState,
    tokenManager: TokenManager
) {
    // Guardar datos de sesión
    LaunchedEffect(userData) {
        tokenManager.saveUserSession(userData)
    }

    EcommAdminTheme {
        Row(Modifier.fillMaxSize()) {
            // Sidebar
            DashboardSidebar(
                onNavigate = { route ->
                    when (route) {
                        "dashboard" -> {}
                        "products" -> navigationState.navigateTo(Screen.ProductList(userData))
                        // Agrega más rutas según sea necesario
                    }
                },
                userName = "Admin"
            )
            // Contenido principal
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(32.dp)
            ) {
                // TopBar simulada (nombre usuario y notificaciones)
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Admin", style = MaterialTheme.typography.body1)
                    IconButton(onClick = { /* Notificaciones */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
                    }
                    IconButton(onClick = {
                        tokenManager.clearSession()
                        navigationState.navigateTo(Screen.Login)
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión")
                    }
                }
                Spacer(Modifier.height(24.dp))
                // KPIs
                KpiCardsRow()
                Spacer(Modifier.height(24.dp))
                // Listas principales
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    Column(Modifier.weight(1f)) {
                        PopularProductsList()
                    }
                    Column(Modifier.weight(1f)) {
                        LowInventoryAlertList()
                    }
                }
                Spacer(Modifier.height(24.dp))
                // Estado de inventario
                InventoryStatusRow()
            }
        }
    }
}