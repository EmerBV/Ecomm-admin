package com.emerbv.ecommadmin.features.dashboard.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun DashboardSidebar(
    onNavigate: (String) -> Unit,
    userName: String
) {
    // Estado para rastrear qué ítem está activo
    var activeRoute by remember { mutableStateOf("dashboard") }

    Column(
        modifier = Modifier
            .width(220.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colors.surface),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(Modifier.height(32.dp))
            Text(
                text = "Admin Panel",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(start = 24.dp, bottom = 32.dp)
            )

            // Elementos del sidebar
            SidebarItem(
                icon = Icons.Default.Dashboard,
                label = "Dashboard",
                isActive = activeRoute == "dashboard",
                onClick = {
                    activeRoute = "dashboard"
                    onNavigate("dashboard")
                }
            )

            SidebarItem(
                icon = Icons.Default.Inventory,
                label = "Products",
                isActive = activeRoute == "products",
                onClick = {
                    activeRoute = "products"
                    onNavigate("products")
                }
            )

            SidebarItem(
                icon = Icons.Default.Category,
                label = "Categories",
                isActive = activeRoute == "categories",
                onClick = {
                    activeRoute = "categories"
                    onNavigate("categories")
                }
            )

            SidebarItem(
                icon = Icons.Default.ShoppingCart,
                label = "Orders",
                isActive = activeRoute == "orders",
                onClick = {
                    activeRoute = "orders"
                    onNavigate("orders")
                }
            )

            SidebarItem(
                icon = Icons.Default.People,
                label = "Users",
                isActive = activeRoute == "users",
                onClick = {
                    activeRoute = "users"
                    onNavigate("users")
                }
            )

            SidebarItem(
                icon = Icons.Default.Settings,
                label = "Settings",
                isActive = activeRoute == "settings",
                onClick = {
                    activeRoute = "settings"
                    onNavigate("settings")
                }
            )
        }

        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Divider()
            Spacer(Modifier.height(16.dp))
            Text(
                text = userName,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.primary
            )
        }
    }
}

@Composable
private fun SidebarItem(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isActive) {
        MaterialTheme.colors.primary.copy(alpha = 0.12f)
    } else {
        Color.Transparent
    }

    val textColor = if (isActive) {
        MaterialTheme.colors.primary
    } else {
        MaterialTheme.colors.onSurface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = textColor
        )

        Spacer(Modifier.width(16.dp))

        Text(
            label,
            style = MaterialTheme.typography.body1,
            color = textColor
        )
    }
}