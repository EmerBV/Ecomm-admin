package com.emerbv.ecommadmin.features.dashboard.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DashboardSidebar(
    onNavigate: (String) -> Unit,
    userName: String
) {
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
            SidebarItem(Icons.Default.Dashboard, "Dashboard") { onNavigate("dashboard") }
            SidebarItem(Icons.Default.Inventory, "Productos") { onNavigate("products") }
            SidebarItem(Icons.Default.Category, "Categorías") { onNavigate("categories") }
            SidebarItem(Icons.Default.ShoppingCart, "Pedidos") { onNavigate("orders") }
            SidebarItem(Icons.Default.People, "Usuarios") { onNavigate("users") }
            SidebarItem(Icons.Default.Settings, "Configuración") { onNavigate("settings") }
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
private fun SidebarItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = label, tint = MaterialTheme.colors.primary)
        Spacer(Modifier.width(16.dp))
        Text(label, style = MaterialTheme.typography.body1)
    }
} 