package com.emerbv.ecommadmin.features.dashboard.presentation

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

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

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

    var selectedTabIndex by remember { mutableStateOf(0) }

    val navigationItems = listOf(
        NavigationItem("Dashboard", Icons.Default.Dashboard, "dashboard"),
        NavigationItem("Productos", Icons.Default.Inventory, "products"),
        NavigationItem("Pedidos", Icons.Default.ShoppingCart, "orders"),
        NavigationItem("Usuarios", Icons.Default.People, "users"),
        NavigationItem("Reportes", Icons.Default.BarChart, "reports"),
        NavigationItem("Configuración", Icons.Default.Settings, "settings")
    )

    EcommAdminTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Ecomm Admin Panel") },
                    actions = {
                        IconButton(onClick = {
                            // Cerrar sesión
                            tokenManager.clearSession()
                            navigationState.navigateTo(Screen.Login)
                        }) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Cerrar sesión"
                            )
                        }
                    }
                )
            },
            bottomBar = {
                BottomNavigation {
                    navigationItems.forEachIndexed { index, item ->
                        BottomNavigationItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTabIndex) {
                    0 -> DashboardContent()
                    else -> PlaceholderContent(navigationItems[selectedTabIndex].title)
                }
            }
        }
    }
}

@Composable
fun DashboardContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Bienvenido al panel de administración",
            style = MaterialTheme.typography.h5
        )

        Text(
            "Resumen del sistema",
            style = MaterialTheme.typography.subtitle1
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                title = "Productos",
                value = "324",
                icon = Icons.Default.Inventory,
                modifier = Modifier.weight(1f)
            )

            StatCard(
                title = "Pedidos",
                value = "42",
                icon = Icons.Default.ShoppingCart,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                title = "Usuarios",
                value = "1,256",
                icon = Icons.Default.People,
                modifier = Modifier.weight(1f)
            )

            StatCard(
                title = "Ingresos",
                value = "€15,420",
                icon = Icons.Default.AttachMoney,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Actividad reciente",
            style = MaterialTheme.typography.subtitle1
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                RecentActivityItem(
                    title = "Nuevo pedido #1042",
                    subtitle = "Hace 5 minutos",
                    icon = Icons.Default.ShoppingCart
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                RecentActivityItem(
                    title = "Producto agotado: Laptop XPS",
                    subtitle = "Hace 20 minutos",
                    icon = Icons.Default.Warning
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                RecentActivityItem(
                    title = "Nuevo usuario registrado",
                    subtitle = "Hace 1 hora",
                    icon = Icons.Default.PersonAdd
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.h6
            )

            Text(
                text = title,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun RecentActivityItem(
    title: String,
    subtitle: String,
    icon: ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.padding(end = 16.dp)
        )

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.subtitle2
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun PlaceholderContent(screenName: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Construction,
                contentDescription = "En construcción",
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Sección $screenName",
                style = MaterialTheme.typography.h6
            )

            Text(
                text = "En desarrollo",
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}