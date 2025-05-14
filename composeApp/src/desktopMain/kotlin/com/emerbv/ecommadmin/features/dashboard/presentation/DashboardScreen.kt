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
import com.emerbv.ecommadmin.features.dashboard.presentation.components.PopularProductsList
import com.emerbv.ecommadmin.features.dashboard.presentation.components.LowInventoryAlertList
import com.emerbv.ecommadmin.features.dashboard.presentation.components.InventoryStatusRow
import com.emerbv.ecommadmin.features.dashboard.presentation.components.KpiCard

@Composable
fun DashboardScreen(
    userData: JwtResponse,
    navigationState: NavigationState,
    tokenManager: TokenManager,
    viewModel: DashboardViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    // Refresh data when screen is shown
    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }

    EcommAdminTheme {
        Row(Modifier.fillMaxSize()) {
            // Sidebar
            DashboardSidebar(
                onNavigate = { route ->
                    when (route) {
                        "dashboard" -> {}
                        "products" -> navigationState.navigateTo(Screen.ProductList(userData))
                        "categories" -> navigationState.navigateTo(Screen.CategoryList(userData))
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

                // Loading indicator
                if (uiState.isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Error message if any
                if (uiState.errorMessage != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = MaterialTheme.colors.error.copy(alpha = 0.1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = "Error",
                                tint = MaterialTheme.colors.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = uiState.errorMessage ?: "",
                                color = MaterialTheme.colors.error
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(Modifier.height(24.dp))

                // KPIs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    KpiCard(
                        title = "Total Products",
                        value = uiState.totalProducts.toString(),
                        icon = Icons.Default.Inventory,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Total Categories",
                        value = uiState.totalCategories.toString(),
                        icon = Icons.Default.Category,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Total Inventory",
                        value = "${uiState.totalInventory} units",
                        icon = Icons.Default.ViewList,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Pre-Orders",
                        value = uiState.preOrderCount.toString(),
                        icon = Icons.Default.ShoppingCart,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Quick Access
                Text(
                    text = "Quick Access",
                    style = MaterialTheme.typography.h6
                )

                Spacer(Modifier.height(16.dp))

                // Quick access cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    QuickAccessCard(
                        icon = Icons.Default.Inventory,
                        title = "Products",
                        description = "Manage your products",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            navigationState.navigateTo(Screen.ProductList(userData))
                        }
                    )

                    QuickAccessCard(
                        icon = Icons.Default.Category,
                        title = "Categories",
                        description = "Manage your categories",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            navigationState.navigateTo(Screen.CategoryList(userData))
                        }
                    )

                    QuickAccessCard(
                        icon = Icons.Default.ShoppingCart,
                        title = "Orders",
                        description = "View recent orders",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            // Implementar navegación a órdenes
                        }
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Main content lists
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    Column(Modifier.weight(1f)) {
                        // Use real data for popular products
                        PopularProductsList(products = uiState.popularProducts)
                    }
                    Column(Modifier.weight(1f)) {
                        // Use real data for low inventory alerts
                        LowInventoryAlertList(alerts = uiState.lowInventoryAlerts)
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Inventory status with real data
                InventoryStatusRow(
                    inStockCount = uiState.inStockCount,
                    outOfStockCount = uiState.outOfStockCount,
                    preOrderCount = uiState.preOrderCount
                )
            }
        }
    }
}

@Composable
fun QuickAccessCard(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable(onClick = onClick),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.h6
            )

            Text(
                text = description,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}